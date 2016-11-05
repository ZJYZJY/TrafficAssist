package com.zjy.trafficassist.ui;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zjy.trafficassist.*;
import com.zjy.trafficassist.model.AlarmHistory;
import com.zjy.trafficassist.utils.TransForm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PostMessage extends BaseActivity {

    public static final int TAKE_PHOTO = 0;
    public static final int BROWSE_PHOTO = 1;
    private int PIC_SELECTED = 0;

    private EditText accident_edit;
    private CheckBox isSerious;
    private ImageView add_pic;
    private Button btn_commit;
    private Uri imageUri;
    private Bitmap bitmap;
    private File imgFile;
    //Snackbar的容器
    private CoordinatorLayout container;

    private AlarmHistory mHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accident_edit = (EditText) findViewById(R.id.accident_edit);
        isSerious = (CheckBox) findViewById(R.id.isSerious);
        container = (CoordinatorLayout) findViewById(R.id.post_container);
        add_pic = (ImageView) findViewById(R.id.add_picture);
        btn_commit = (Button) findViewById(R.id.btn_commit);

        add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PIC_SELECTED == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostMessage.this);
                    final String[] ways = {"拍照", "从手机相册中选择"};
                    //    设置一个下拉的列表选择项
                    builder.setItems(ways, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(PostMessage.this, "选择为：" + ways[which], Toast.LENGTH_SHORT).show();
                            //创建File对象，用于存储照片
//                            File image = new File(Environment.getExternalStorageDirectory()
//                                        + File.separator + Environment.DIRECTORY_DCIM, "1.jpg");
                            File image = new File("/storage/emulated/0/TrafficAssist/uploadTmp/"
                                    + TransForm.DateFileName("IMG") + ".jpg");
                            try {
                                if (image.exists()) {
                                    image.delete();
                                }
                                image.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            imageUri = Uri.fromFile(image);
                            switch (which) {
                                case 0:
                                    Intent camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                    startActivityForResult(camera, TAKE_PHOTO);
                                    break;
                                case 1:
                                    Intent picture = new Intent(Intent.ACTION_GET_CONTENT);
                                    picture.setType("image/*");
                                    startActivityForResult(picture, BROWSE_PHOTO);
                                    break;
                            }
                        }
                    });
                    builder.show();
                } else {
                    LayoutInflater inflater = LayoutInflater.from(PostMessage.this);
                    View imgEntryView = inflater.inflate(R.layout.large_image, null); // 加载自定义的布局文件
                    final AlertDialog dialog = new AlertDialog.Builder(PostMessage.this).create();
                    ImageView large_image = (ImageView) imgEntryView.findViewById(R.id.large_imageview);
                    large_image.setImageBitmap(bitmap);
                    dialog.setView(imgEntryView); // 自定义dialog
                    dialog.show();
                    // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
                    imgEntryView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View paramView) {
                            dialog.cancel();
                        }
                    });
                }
            }
        });

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mHistory = new AlarmHistory(
                        isSerious.isChecked(),
                        accident_edit.getText().toString(),
                        UserStatus.user.getNickname(),
                        UserStatus.user.getUsername(),
                        imgFile,
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));
                        //Bitmap mini = TransForm.comp(bitmap);
                        imgFile = getImageFile(bitmap);
                        add_pic.setImageBitmap(bitmap);
                        PIC_SELECTED = PIC_SELECTED + 1;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case BROWSE_PHOTO:
                    //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
                    ContentResolver resolver = getContentResolver();
                    try {
                        //获得图片的uri
                        Uri uri = data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
                        Bitmap mini = TransForm.comp(bitmap);
                        imgFile = getImageFile(bitmap);
                        add_pic.setImageBitmap(mini);
                        PIC_SELECTED = PIC_SELECTED + 1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public File getImageFile(Bitmap mBitmap)  {
        String path = "/storage/emulated/0/TrafficAssist/uploadTmp";
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
}
