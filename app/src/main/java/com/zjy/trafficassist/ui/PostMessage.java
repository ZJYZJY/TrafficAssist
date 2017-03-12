package com.zjy.trafficassist.ui;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zjy.trafficassist.*;
import com.zjy.trafficassist.model.AlarmHistory;
import com.zjy.trafficassist.utils.TransForm;
import com.zjy.trafficassist.widget.tagFlow.FlowLayout;
import com.zjy.trafficassist.widget.tagFlow.TagAdapter;
import com.zjy.trafficassist.widget.tagFlow.TagFlowLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

import me.iwf.photopicker.widget.MultiPickResultView;

public class PostMessage extends BaseActivity implements TagFlowLayout.OnTagClickListener, TagFlowLayout.OnSelectListener {

    private Button btn_commit;
    private Uri imageUri;
    private Bitmap bitmap;
    private File imgFile;
    private ArrayList<File> imgFiles;
    //Snackbar的容器
    private CoordinatorLayout container;

    /**
     * 自定义组件
     */
    private TagFlowLayout mTagFlowLayout;
    private MultiPickResultView recyclerView;

    private AlarmHistory mHistory;

    private String accidentTags = "";
    private String mNames[] = {
            "轻微擦碰","人员受伤","人员死亡",
            "不影响交通","造成交通堵塞","追尾事故",
            "有危险品车","酒后驾驶","无证驾驶",
            "事故责任清晰"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        container = (CoordinatorLayout) findViewById(R.id.post_container);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        mTagFlowLayout = (TagFlowLayout) findViewById(R.id.tag_flow);
        mTagFlowLayout.setOnTagClickListener(this);
        mTagFlowLayout.setOnSelectListener(this);
        final LayoutInflater mInflater = LayoutInflater.from(this);
        mTagFlowLayout.setAdapter(new TagAdapter<String>(mNames) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.item_tag,
                        mTagFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        recyclerView = (MultiPickResultView) findViewById(R.id.image_picker);
        recyclerView.init(this, MultiPickResultView.ACTION_SELECT, null);

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!accidentTags.equals("")){
                    final ProgressDialog mPDialog = new ProgressDialog(PostMessage.this);
                    mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mPDialog.setMessage(getResources().getString(R.string.now_upload_history));
                    mPDialog.setCancelable(true);
                    mPDialog.show();
                    final ArrayList<String> paths = recyclerView.getPhotos();
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            // 获取选择的图片对应的File对象
                            imgFiles = getImageFiles(paths);
                            mHistory = new AlarmHistory(
                                    accidentTags,
                                    UserStatus.user.getNickname(),
                                    UserStatus.user.getUsername(),
                                    imgFiles,
                                    UserStatus.user.getLocation());
                            Log.d("accTags", accidentTags);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            new AsyncTask<Void, Void, Boolean>() {

                                String ReturnCode;
                                @Override
                                protected Boolean doInBackground(Void... params) {
                                    ReturnCode = WebService.UploadHistory(mHistory);
                                    if(ReturnCode != null)
                                        Log.d("postMsg", ReturnCode);
                                    return Boolean.parseBoolean(ReturnCode);
                                }

                                @Override
                                protected void onPostExecute(final Boolean success) {
                                    super.onPostExecute(success);
                                    mPDialog.dismiss();
                                    if (success) {
                                        Snackbar.make(container, "报警成功", Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Snackbar.make(container, "报警失败", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            }.execute();
                        }
                    }.execute();
                }else{
                    Toast.makeText(PostMessage.this, "请至少选择一个标签", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recyclerView.onActivityResult(requestCode,resultCode,data);

//        // 获取选择的图片对应的File对象
//        imgFiles = getImageFiles(recyclerView.getPhotos());
    }

    public ArrayList<File> getImageFiles(ArrayList<String> paths) {
        String dir = "/storage/emulated/0/TrafficAssist/uploadTmp/";
        File Dir = new File(dir);
        if(!Dir.exists())
            Dir.mkdirs();
        ArrayList<File> imgFiles = new ArrayList<>();
        ArrayList<Bitmap> compedBitmaps = new ArrayList<>();

        for(int i = 0; i < paths.size(); i++) {
            imgFiles.add(new File(dir + TransForm.DateFileName("IMG") + ".jpg"));
//            compedBitmaps.add(TransForm.compressImage(BitmapFactory.decodeFile(paths.get(i))));
            compedBitmaps.add(TransForm.compressImage(paths.get(i)));
        }
        for(int i = 0; i < paths.size(); i++) {
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgFiles.get(i)));
                compedBitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            File file = new File(paths.get(i));
//            imgFiles.add(file);
        }
        return imgFiles;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 事故标签事件的监听函数
    @Override
    public boolean onTagClick(View view, int position, FlowLayout parent) {
        return false;
    }

    @Override
    public void onSelected(Set<Integer> selectPosSet) {
        String tags = "";
        Object[] id = selectPosSet.toArray();
        if(id.length > 0){
            tags = mNames[Integer.valueOf(id[0].toString())];
        }
        for (int i = 1; i < id.length; i++) {
            tags += "/" + mNames[Integer.valueOf(id[i].toString())];
        }
//        Integer[] id = (Integer[])selectPosSet.toArray();
//        for (Integer anId : id) {
//            tags += mNames[anId] + "/";
//        }
        accidentTags = tags;
        Log.d("accTag", "choose:" + selectPosSet.toString());
    }
}
