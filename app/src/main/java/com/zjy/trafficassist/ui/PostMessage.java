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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zjy.trafficassist.*;
import com.zjy.trafficassist.model.AlarmHistory;
import com.zjy.trafficassist.utils.HttpUtil;
import com.zjy.trafficassist.utils.LogUtil;
import com.zjy.trafficassist.utils.TransForm;

import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.widget.MultiPickResultView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zjy.trafficassist.UserStatus.USER;
import static com.zjy.trafficassist.utils.HttpUtil.SUCCESS;

public class PostMessage extends BaseActivity {

    private Button btn_commit;
    private Uri imageUri;
    private Bitmap bitmap;
    private File imgFile;
    private ArrayList<File> imgFiles;

    //Snackbar的容器
    private CoordinatorLayout container;
    private RadioGroup tag_car_type;
    private RadioGroup tag_people_effect;
    private RadioGroup tag_car_crash;

    /**
     * 自定义组件
     */
    private MultiPickResultView recyclerView;

    private AlarmHistory mHistory;

    private String accidentTags = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        container = (CoordinatorLayout) findViewById(R.id.post_container);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        tag_car_type = (RadioGroup) findViewById(R.id.tag_car_type);
        tag_people_effect = (RadioGroup) findViewById(R.id.tag_people_effect);
        tag_car_crash = (RadioGroup) findViewById(R.id.tag_car_crash);

        recyclerView = (MultiPickResultView) findViewById(R.id.image_picker);
        recyclerView.init(this, MultiPickResultView.ACTION_SELECT, null);

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag_car_type.getCheckedRadioButtonId() != -1
                        && tag_people_effect.getCheckedRadioButtonId() != -1
                        && tag_car_crash.getCheckedRadioButtonId() != -1){
                    final ProgressDialog mPDialog = new ProgressDialog(PostMessage.this);
                    mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mPDialog.setMessage(getResources().getString(R.string.now_upload_history));
                    mPDialog.setCancelable(true);
                    mPDialog.show();
                    RadioButton btn1 = (RadioButton) findViewById(tag_car_type.getCheckedRadioButtonId());
                    RadioButton btn2 = (RadioButton) findViewById(tag_people_effect.getCheckedRadioButtonId());
                    RadioButton btn3 = (RadioButton) findViewById(tag_car_crash.getCheckedRadioButtonId());
                    accidentTags = btn1.getText().toString() + "/" + btn2.getText().toString() + "/" + btn3.getText().toString();
                    LogUtil.e(accidentTags);
                    final ArrayList<String> paths = recyclerView.getPhotos();

//                    new AsyncTask<Void, Void, Void>() {
//                        @Override
//                        protected Void doInBackground(Void... params) {
//                            // 获取选择的图片对应的File对象
//                            imgFiles = getImageFiles(paths);
//                            mHistory = new AlarmHistory(
//                                    accidentTags,
//                                    UserStatus.USER.getNickname(),
//                                    UserStatus.USER.getUsername(),
//                                    imgFiles,
//                                    UserStatus.USER.getLocation());
//                            Log.d("accTags", accidentTags);
//                            return null;
//                        }
//
//                        @Override
//                        protected void onPostExecute(Void aVoid) {
//                            super.onPostExecute(aVoid);
//                            new AsyncTask<Void, Void, Boolean>() {
//
//                                String ReturnCode;
//                                @Override
//                                protected Boolean doInBackground(Void... params) {
//                                    ReturnCode = WebService.UploadHistory(mHistory);
//                                    if(ReturnCode != null)
//                                        Log.d("postMsg", ReturnCode);
//                                    return Boolean.parseBoolean(ReturnCode);
//                                }
//
//                                @Override
//                                protected void onPostExecute(final Boolean success) {
//                                    super.onPostExecute(success);
//                                    mPDialog.dismiss();
//                                    if (success) {
//                                         Snackbar.make(container, "报警成功", Snackbar.LENGTH_LONG).show();
//                                    } else {
//                                        Snackbar.make(container, "报警失败", Snackbar.LENGTH_LONG).show();
//                                    }
//                                }
//                            }.execute();
//                        }
//                    }.execute();

                    imgFiles = getImageFiles(paths);
                    String NamesString = imgFiles.get(0).getName();
                    for(int i = 1; i < imgFiles.size(); i++) {
                        NamesString += "/" + imgFiles.get(i).getName();
                    }
                    List<MultipartBody.Part> parts = HttpUtil.files2Parts("image", paths);
                    HttpUtil.addTextPart(parts, "accidentTags", accidentTags, parts.size());
                    HttpUtil.addTextPart(parts, "nickname", USER.getNickname(), parts.size());
                    HttpUtil.addTextPart(parts, "username", USER.getUsername(), parts.size());
                    HttpUtil.addTextPart(parts, "longitude", USER.getLocation().longitude+"", parts.size());
                    HttpUtil.addTextPart(parts, "latitude", USER.getLocation().latitude+"", parts.size());
                    HttpUtil.addTextPart(parts, "filenames", NamesString, parts.size());
//                    HttpUtil.create().uploadHistory(parts, historyInfo).enqueue(new Callback<ResponseBody>() {
                    HttpUtil.create().uploadHistory(parts).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String res = response.body().string();
                                JSONObject json = new JSONObject(res);
                                if(HttpUtil.stateCode(res) == SUCCESS){
                                    Snackbar.make(container, "报警成功", Snackbar.LENGTH_LONG).show();
                                }else {
                                    Snackbar.make(container, "报警失败", Snackbar.LENGTH_LONG).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Snackbar.make(container, "连接失败", Snackbar.LENGTH_LONG).show();
                        }
                    });

//                    imgFiles = getImageFiles(paths);
//                    String NamesString = imgFiles.get(0).getName();
//                    for(int i = 1; i < imgFiles.size(); i++) {
//                        NamesString += "/" + imgFiles.get(i).getName();
//                    }
//                    Map<String, RequestBody> photos = new HashMap<>();
//                    Map<String, String> historyInfo = new HashMap<>();
//                    historyInfo.put("accidentTags", accidentTags);
//                    historyInfo.put("nickname", USER.getNickname());
//                    historyInfo.put("username", USER.getUsername());
//                    historyInfo.put("longitude", USER.getLocation().longitude+"");
//                    historyInfo.put("latitude", USER.getLocation().latitude+"");
//                    historyInfo.put("filenames", NamesString);
//                    for(int i = 0; i < imgFiles.size(); i++){
//                        File file = imgFiles.get(i);
//                        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/png"), file);
//                        photos.put("image", photoRequestBody);
//                    }
//                    HttpUtil.create().uploadHistory(photos, historyInfo).enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            try {
//                                String res = response.body().string();
//                                JSONObject json = new JSONObject(res);
//                                if(HttpUtil.stateCode(res) == SUCCESS){
//                                    Snackbar.make(container, "报警成功", Snackbar.LENGTH_LONG).show();
//                                }else {
//                                    Snackbar.make(container, "报警失败", Snackbar.LENGTH_LONG).show();
//                                }
//                            } catch (JSONException | IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            Snackbar.make(container, "连接失败", Snackbar.LENGTH_LONG).show();
//                        }
//                    });
                }else{
                    Toast.makeText(PostMessage.this, "请完整填写信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recyclerView.onActivityResult(requestCode,resultCode,data);

    }

    /**
     * 将要上传的文件保存到本地临时文件夹
     * @param paths 图片文件的原始路径
     * @return imgFiles 转存后的图片文件对象
     */
    public ArrayList<File> getImageFiles(ArrayList<String> paths) {
        String dir = "/storage/emulated/0/TrafficAssist/uploadTmp/";
        File Dir = new File(dir);
        if(!Dir.exists())
            Dir.mkdirs();
        ArrayList<File> imgFiles = new ArrayList<>();
        ArrayList<Bitmap> compedBitmaps = new ArrayList<>();

        for(int i = 0; i < paths.size(); i++) {
            imgFiles.add(new File(dir + TransForm.DateFileName("IMG") + ".jpg"));
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
}
