package com.zjy.trafficassist.ui;

import android.app.ProgressDialog;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import me.iwf.photopicker.widget.MultiPickResultView;

public class PostMessage extends BaseActivity implements TagFlowLayout.OnTagClickListener, TagFlowLayout.OnSelectListener {

    private Button btn_commit;
    private Uri imageUri;
    private Bitmap bitmap;
    private File imgFile;
    private ArrayList<File> imgFiles;
    //Snackbar的容器
    private CoordinatorLayout container;

    private AlarmHistory mHistory;
    private TagFlowLayout mTagFlowLayout;
    private MultiPickResultView recyclerView;

    private String mNames[] = {
            "welcome","android","TextView",
            "apple","jamy","kobe bryant",
            "jordan","layout","viewgroup",
            "margin","padding","text",
            "name","type","search","logcat"
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

                mHistory = new AlarmHistory(
                        true,
                        "accident_tags",
                        UserStatus.user.getNickname(),
                        UserStatus.user.getUsername(),
                        imgFiles,
                        UserStatus.user.getLocation());

                final ProgressDialog mPDialog = new ProgressDialog(PostMessage.this);
                mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mPDialog.setMessage(getResources().getString(R.string.now_upload_history));
                mPDialog.setCancelable(true);
                mPDialog.show();
                new AsyncTask<Void, Void, Boolean>() {

                    String ReturnCode;
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        ReturnCode = WebService.UploadHistory(mHistory);
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
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recyclerView.onActivityResult(requestCode,resultCode,data);
        // 获取选择的图片对应的File对象
        imgFiles = getImageFiles(recyclerView.getPhotos());
    }

    public ArrayList<File> getImageFiles(ArrayList<String> paths) {
        ArrayList<File> imgFiles = new ArrayList<>();
        for(int i = 0; i < paths.size(); i++) {
            File file = new File(paths.get(i));
            imgFiles.add(file);
        }
        return imgFiles;
    }

    public File getImageFile(Bitmap mBitmap)  {
        String path = "/storage/emulated/0/TrafficAssist/uploadTmp/";
        File Dir = new File(path);
        if(!Dir.exists())
            Dir.mkdirs();
        File file = new File(path + TransForm.DateFileName("IMG") + ".jpg");
        FileOutputStream fOut = null;

        try {
            fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getImageByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
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

    }
}
