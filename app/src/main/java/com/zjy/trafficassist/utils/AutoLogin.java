package com.zjy.trafficassist.utils;

import android.content.Context;

import com.zjy.trafficassist.UserStatus;
import com.zjy.trafficassist.model.User;

import static android.content.Context.MODE_PRIVATE;
import static com.zjy.trafficassist.UserStatus.EDITOR;
import static com.zjy.trafficassist.UserStatus.SP;

/**
 * com.zjy.trafficassist.utils
 * Created by 73958 on 2017/3/22.
 */

public class AutoLogin {

    private static AutoLogin instance;

    public static AutoLogin getInstance(){
        if(instance == null){
            return new AutoLogin();
        }
        return instance;
    }

    public void login(Context context){
        SP = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        // 自动登录
        if(SP.getString("USER_NAME", null) != null
                && SP.getString("PASSWORD", null) != null){
            String username = SP.getString("USER_NAME", "");
            String password = SP.getString("PASSWORD", "");
            User user = new User(username, password);
            LogUtil.i("SP  " + user.getUsername() + "  " + user.getPassword());

//            UserLoginTask mAuthTask = new UserLoginTask(context, user);
//            mAuthTask.execute();
            LoginCheck loginCheck = new LoginCheck(context, user);
            loginCheck.login();
        }
    }

    public void saveUserInfo(Context context){
        // 保存用户名和密码
        SP = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
        if(UserStatus.USER != null){
            EDITOR = SP.edit();
            EDITOR.putString("USER_NAME", UserStatus.USER.getUsername());
            EDITOR.putString("PASSWORD", UserStatus.USER.getPassword());
            EDITOR.commit();
            LogUtil.i("userInfo save sucessfully");
        }
    }
}