package com.zjy.trafficassist;

import com.zjy.trafficassist.model.AlarmHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created ${Date}.
 *
 * @author 郑家烨.
 * @function 解析从服务器传回的JSON数据
 */
public class JSONParser {

    public static ArrayList<AlarmHistory> DownloadHistory(String json) throws JSONException {
        if (json == null)
            return null;
        ArrayList<AlarmHistory> alarmHistories = new ArrayList<>();
//        ArrayList<byte[]> pictures = new ArrayList<>();
        byte[] pic;
        JSONObject historyInfo = new JSONObject(json);

        JSONArray histories = historyInfo.getJSONArray("allDetails");
        for (int i = 0; i < histories.length(); i++) {
            JSONObject detail = (JSONObject) histories.get(i);
            String str_detail = (String) detail.get("detail");
            String str_pic = (String) detail.get("picture");
//            pictures.add(str_pic.getBytes());
            alarmHistories.add(new AlarmHistory(true, str_detail,
                    UserStatus.user.getNickname(), UserStatus.user.getUsername(), str_pic.getBytes()));
        }
        return alarmHistories;
    }
}
