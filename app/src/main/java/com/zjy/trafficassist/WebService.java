package com.zjy.trafficassist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ZJY on 2016/4/22.
 */
public class WebService {

    /**
     * 执行登录操作的代码
     */
    public static final int LOGIN = 0;
    /**
     * 执行注册操作的代码
     */
    public static final int REGISTER = 1;
    /**
     * servlet的URL
     */
    private static String path;

    // 与HTTP服务器通信，进行登录或注册
    public static String Login_Register(String username, String password, int OP_Type) {

        HttpURLConnection conn = null;
        InputStream is = null;
        InputStreamReader isr;
        BufferedReader br;

        try {
            String server_IP = "192.168.199.223:8080";
            if(OP_Type == LOGIN) {
                path = "http://" + server_IP + "/TrafficAssistSever/LoginLet";
            }else if(OP_Type == REGISTER) {
                path = "http://" + server_IP + "/TrafficAssistSever/RegisterLet";
            }
            path = path + "?username=" + username + "&password=" + password;
            
            conn = (HttpURLConnection) new URL(path).openConnection();
//            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
//            BufferedWriter bw = new BufferedWriter(osw);
//            bw.write("username=" + username + "&password=" + password);
//            bw.flush();
            
            conn.setConnectTimeout(5000); // 设置超时时间
            conn.setReadTimeout(5000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                String line, info = "";
                while((line = br.readLine()) != null) {
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
//    /**
//     * 将输入流转化为 String 型
//     */
//    private static String parseInfo(InputStream inStream) throws Exception {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//        int len = 0;
//        while ((len = inStream.read(buffer)) != -1) {
//            outputStream.write(buffer, 0, len);
//        }
//        inStream.close();
//        //转化为byte类型
//        byte[] data = outputStream.toByteArray();
//        // 转化为字符串
//        return new String(data, "UTF-8");
//    }
}