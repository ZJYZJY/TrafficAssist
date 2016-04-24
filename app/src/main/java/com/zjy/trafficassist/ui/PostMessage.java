package com.zjy.trafficassist.ui;

import android.content.ContentResolver;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.zjy.trafficassist.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PostMessage extends AppCompatActivity {

    public static final int TAKE_PHOTO = 0;
    public static final int BROWSE_PHOTO = 1;
    private int PIC_SELECTED = 0;
    private ImageView add_pic;
    private Button btn_commit;
    private Uri imageUri;
    private Bitmap bitmap;
    //Snackbar的容器
    private CoordinatorLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        container = (CoordinatorLayout) findViewById(R.id.post_container);
        add_pic = (ImageView) findViewById(R.id.add_picture);
        btn_commit = (Button) findViewById(R.id.btn_commit);

        if (PIC_SELECTED == 0) {
            add_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostMessage.this);
                    final String[] ways = {"拍照", "从手机相册中选择"};
                    //    设置一个下拉的列表选择项
                    builder.setItems(ways, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(PostMessage.this, "选择为：" + ways[which], Toast.LENGTH_SHORT).show();
                            //创建File对象，用于存储照片
                            File image = new File(Environment.getExternalStorageDirectory(), "1.jpg");
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
                }
            });
        } else {
            add_pic.setOnClickListener(new View.OnClickListener() { // 点击放大
                public void onClick(View paramView) {
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
            });
        }

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(container, "正在提交报案信息中...", Snackbar.LENGTH_LONG).setAction("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(container, "取消成功", Snackbar.LENGTH_LONG).show();
                    }
                }).show();
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
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
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
                        Uri uri = data.getData();        //获得图片的uri
                        bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
                        add_pic.setImageBitmap(bitmap);
                        PIC_SELECTED = PIC_SELECTED + 1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
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
