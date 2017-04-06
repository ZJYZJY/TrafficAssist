package com.zjy.trafficassist.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.zjy.trafficassist.UserStatus;
import com.zjy.trafficassist.WebService;
import com.zjy.trafficassist.model.User;

import org.json.JSONObject;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * com.zjy.trafficassist.utils
 * Created by 73958 on 2017/3/22.
 *
 * 后台线程进行登陆操作
 */
@Deprecated
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private User user;
    private Context context;
    private ProgressDialog mPDialog;

    public UserLoginTask(Context context, User user){
        this.context = context;
        this.user = user;
    }

    public UserLoginTask(Context context, User user, ProgressDialog mPDialog){
        this.context = context;
        this.user = user;
        this.mPDialog = mPDialog;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String ReturnCode = WebService.Login(user);
        if (ReturnCode != null) {
            return Boolean.parseBoolean(ReturnCode);
        }
        return false;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        super.onPostExecute(success);
        if(mPDialog != null)
            mPDialog.dismiss();
        if (success) {
            UserStatus.LOGIN_STATUS = true;
            UserStatus.USER = user;
            // 保存登录信息
            AutoLogin.getInstance().saveUserInfo(context);

            // IM服务器登录
            SharedPreferences preferences = context.getSharedPreferences("RongKitConfig", MODE_PRIVATE);
            String token = preferences.getString("token", "");
            ConnectIMServer.getInstance().connectIMServer(context, token);

            Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();

            // 如果是LoginActivity登录的话，则关闭activity
            String contextString = context.toString();
            String AtyName = contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
            LogUtil.e(AtyName);
            if(Objects.equals(AtyName, "LoginActivity")){
                LogUtil.e("in");
                ((Activity)context).finish();
            }
        } else {
            Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
        }
    }
}
