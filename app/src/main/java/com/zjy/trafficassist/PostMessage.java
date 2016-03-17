package com.zjy.trafficassist;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PostMessage extends AppCompatActivity {

    public static final int TAKE_PHOTO = 0;
    public static final int BROWSE_PHOTO = 1;
    private ImageView add_pic;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_post_message);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_bar);

        add_pic = (ImageView)findViewById(R.id.add_picture);
        findViewById(R.id.add_picture).setOnClickListener(new View.OnClickListener() {
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
                        try{
                            if(image.exists()){
                                image.delete();
                            }
                            image.createNewFile();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                        imageUri = Uri.fromFile(image);
                        switch (which) {
                            case 0:
                                Intent camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                camera.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                                startActivityForResult(camera, TAKE_PHOTO);
                                break;
                            case 1:
                                Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                picture.setType("image/*");
                                //picture.putExtra();
                                //picture.putExtra();
                                picture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(picture, BROWSE_PHOTO);
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case TAKE_PHOTO:
                try{
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    add_pic.setImageBitmap(bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                break;
            case BROWSE_PHOTO:
                try{
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    add_pic.setImageBitmap(bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                break;
        }
    }
}
