package com.zjy.trafficassist;

import android.content.SharedPreferences;

import com.zjy.trafficassist.model.User;

/**
 * Created 2016/5/1.
 *
 * @author 郑家烨.
 * @function
 */
public class UserStatus {

    public static boolean Login_status = false;
    public static User user = new User();
    public static boolean first_show = true;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
}
