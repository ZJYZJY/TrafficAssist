package com.zjy.trafficassist;

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
        JSONObject historyInfo = new JSONObject(json);
        JSONArray details = historyInfo.getJSONArray("allDetails");
        for (int i = 0; i < details.length(); i++) {
            JSONObject detail = (JSONObject) details.get(i);
            String str_detail = (String) detail.get("detail");
            alarmHistories.add(new AlarmHistory(true, str_detail, UserStatus.user.getNickname()));
        }
        return alarmHistories;
    }
}
