package com.zjy.trafficassist;

import com.zjy.trafficassist.model.AlarmHistory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

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
    private static String server_IP = "192.168.31.100:8080";

    /**
     * 与HTTP服务器通信，进行登录
     */
    public static String Login(String username, String password) {
        path = "http://" + server_IP + "/TrafficAssistSever/LoginLet";
        path = path + "?username=" + username + "&password=" + password;
        return Connect();
    }

    /**
     * 与HTTP服务器通信，进行注册
     */
    public static String Register(String username, String password) {
        path = "http://" + server_IP + "/TrafficAssistSever/RegisterLet";
        path = path + "?username=" + username + "&password=" + password;
        return Connect();
    }

    /**
     * 与HTTP服务器通信，将报警信息上传到个人历史记录中
     */
    public static String UploadHistory(AlarmHistory alarmHistory) {
        path = "http://" + server_IP + "/TrafficAssistSever/UploadHistory";
        path = path + "?username=" + alarmHistory.getUsername() + "&nickname=" + alarmHistory.getNickname() + "&detail="
                + alarmHistory.getDetail() + "&pictures=" + Arrays.toString(alarmHistory.getPic());
//        Map<String, String> history = new HashMap<>();
//        history.put("nickname", alarmHistory.getNickname());
//        history.put("detail", alarmHistory.getDetail());
        return Connect();
    }

    /**
     * 与HTTP服务器通信，获取报警信息到本地
     */
    public static String DownloadHistory() {
        path = "http://" + server_IP + "/TrafficAssistSever/DownloadHistory";
        path = path + "?username=" + UserStatus.user.getUsername();
        return Connect();
    }

    private static String Connect() {

        HttpURLConnection conn = null;
        InputStream is = null;
        InputStreamReader isr;
        BufferedReader br;

        try {
            conn = (HttpURLConnection) new URL(path).openConnection();

            conn.setConnectTimeout(5000); // 设置超时时间
            conn.setReadTimeout(5000);
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
                return info;
                //return parseInfo(is);
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