package com.zjy.trafficassist.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zjy.trafficassist.UserStatus;
import com.zjy.trafficassist.WebService;
import com.zjy.trafficassist.model.AlarmHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created 2016/5/5.
 *
 * @author 郑家烨.
 */
public class TransForm {

    // 解析从服务器传回的JSON数据
    public static ArrayList<AlarmHistory> DownloadHistory(String json)
            throws UnsupportedEncodingException {
        if (json == null)
            return null;
        ArrayList<AlarmHistory> alarmHistories = new ArrayList<>();
        JSONObject historyInfo = null;
        try {
            historyInfo = new JSONObject(json);
            JSONArray histories = historyInfo.getJSONArray("allDetails");
            for (int i = 0; i < histories.length(); i++) {
                JSONObject detail = (JSONObject) histories.get(i);
                String str_detail = (String) detail.get("detail");
                String filename = (String) detail.get("fileName");
//                System.out.println(filename);
                Bitmap bitmap = WebService.GetLocalOrNetBitmap("http://192.168.31.100/TrafficAssist/AccidentImage/" + filename);
                alarmHistories.add(new AlarmHistory(true,
                        str_detail,
                        UserStatus.user.getNickname(),
                        UserStatus.user.getUsername(),
                        bitmap));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    return alarmHistories;
}

    /**
     * 用当前系统时间为文件命名
     */
    public static String DateFileName(String fileType) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        return fileType + dateFormat.format(date);
    }

    /**
     * 将字节数组转换为Bitmap对象
     */
    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        //判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            //重置baos即清空baos
            baos.reset();
            //这里压缩50%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 1920f;//这里设置高度为1920f
        float ww = 1080f;//这里设置宽度为1080f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        System.out.println("压缩前" + baos.toByteArray().length);
        //循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();//重置baos即清空baos
            //这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;//每次都减少10
        }
        System.out.println("压缩后" + baos.toByteArray().length);
        //把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }
}
