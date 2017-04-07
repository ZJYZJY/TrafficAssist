package com.zjy.trafficassist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.zjy.trafficassist.model.AlarmHistory;
import com.zjy.trafficassist.model.User;
import com.zjy.trafficassist.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created on 2016/4/20.
 *
 * @author 郑家烨.
 */
public class WebService {

    /**
     * 对应servlet的URL
     */
    private static String path;
    private static String server_IP = "120.27.130.203:8001";

    /**
     * 与HTTP服务器通信，进行登录
     */
    @Deprecated
    public static String Login(User user) {
        path = "http://" + server_IP + "/trafficassist/userApi/login.php";
        path = path + "?username=" + user.getUsername() + "&password=" + user.getPassword();
        return Connect();
    }

    /**
     * 将图片上传至服务器
     */
    public static ArrayList<String> UploadImage(ArrayList<File> files, String username) {
        path = "http://" + server_IP + "/trafficassist/userApi/uploadImage.php?username=" + username;
        ArrayList<String> fileNames = new ArrayList<>();
        try {
            for(int i = 0; i < files.size(); i++) {
                HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);
                conn.setDoInput(true); //允许输入流
                conn.setDoOutput(true); //允许输出流
                conn.setUseCaches(false); //不允许使用缓存
                conn.setRequestMethod("POST"); //请求方式
                conn.setRequestProperty("Charset", "UTF-8");//设置编码
                if (files.get(i) != null) {
                    /** 当文件不为空，把文件包装并且上传 */
                    Log.e("image_size", files.get(i).length()+"");
                    OutputStream outputSteam = conn.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(outputSteam);
                    InputStream is = new FileInputStream(files.get(i));
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len = is.read(bytes)) != -1) {
                        dos.write(bytes, 0, len);
                    }
                    is.close();
                    dos.flush();
                    /** 当响应成功，获取响应的流 */
                    //Log.e(TAG, "response code:"+res);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream is2;
                        InputStreamReader isr;
                        BufferedReader br;
                        is2 = conn.getInputStream();
                        isr = new InputStreamReader(is2);
                        br = new BufferedReader(isr);
                        String line, filename = "";
                        while ((line = br.readLine()) != null) {
                            filename = filename + line;
                        }
                        br.close();
                        isr.close();
                        is2.close();
                        fileNames.add(filename);
                    }
                }
            }
            return fileNames;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("IOException", "gg");
        }
        return null;
    }

    /**
     * 与HTTP服务器通信，将报警信息上传到个人历史记录中
     */
    public static String UploadHistory(AlarmHistory alarmHistory) {

        HttpURLConnection connection = null;
        InputStream is = null;
        InputStreamReader isr;
        BufferedReader br;
        JSONObject json = new JSONObject();
        ArrayList<String> filenames = new ArrayList<>();

        try {
            /**
             * 上传记录时首先上传图片
             */
            if(alarmHistory != null)
                if(alarmHistory.getFiles().size() > 0)
                    filenames =  UploadImage(alarmHistory.getFiles(), alarmHistory.getUsername());
            if (filenames == null || filenames.size() <= 0) {
                Log.e("uploadhistory", "图片上传失败");
                return null;
            }

            path = "http://" + server_IP + "/trafficassist/userApi/uploadHistory.php";
            connection = (HttpURLConnection) new URL(path).openConnection();
            connection.setConnectTimeout(10000); // 设置超时时间
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST"); // 设置获取信息方式
            connection.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式

            String NamesString = "";
            NamesString = filenames.get(0);
            for(int i = 1; i < filenames.size(); i++) {
                NamesString += "/" + filenames.get(i);
            }
            Log.d("fileNames", NamesString);
            json.put("accidentTags", alarmHistory.getaccidentTags());
            json.put("nickname", alarmHistory.getNickname());
            json.put("username", alarmHistory.getUsername());
            json.put("longitude", alarmHistory.getLocation().longitude);
            json.put("latitude", alarmHistory.getLocation().latitude);
            json.put("filenames", NamesString);

            OutputStream os = connection.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));//ISO-8859-1
            os.flush();
            os.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                String line, info = "";
                while ((line = br.readLine()) != null) {
                    info = info + line;
                }
                br.close();
                isr.close();
                is.close();
                Log.d("uploadhistory_return", info);
                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (connection != null) {
                connection.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static String Connect() {

        HttpURLConnection conn = null;
        InputStream is = null;
        InputStreamReader isr;
        BufferedReader br;

        try {
            conn = (HttpURLConnection) new URL(path).openConnection();

            conn.setConnectTimeout(60000); // 设置超时时间
            conn.setReadTimeout(60000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                String line, info = "";
                while ((line = br.readLine()) != null) {
                    info = info + line;
                }
                br.close();
                isr.close();
                is.close();
                LogUtil.e("请求成功，result-->" + info);
                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}