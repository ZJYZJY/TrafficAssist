package com.zjy.trafficassist.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.zjy.trafficassist.App;
import com.zjy.trafficassist.WebService;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * com.zjy.trafficassist.utils
 * Created by 73958 on 2017/3/21.
 */

public class ConnectIMServer {

    /**
     * 单例模式
     * @return sInstance
     */
    public static ConnectIMServer getInstance(){
        return ConnectIMServerHolder.sInstance;
    }

    private static class ConnectIMServerHolder{
        private static final ConnectIMServer sInstance = new ConnectIMServer();
    }

    public void connectIMServer(final Context context, String token) {

        if (context.getApplicationInfo().packageName.equals(App.getCurProcessName(context.getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查
                 * 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 * 2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    LogUtil.d("IMServer", "--onTokenIncorrect--");

                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            return WebService.getUserIMToken();
                        }

                        @Override
                        protected void onPostExecute(String token) {
                            super.onPostExecute(token);
                            if(token != null)
                                ConnectIMServer.getInstance().connectIMServer(context, token);
                            LogUtil.d("token from server:" + token);
                        }
                    }.execute();
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    LogUtil.d("IMServer", "--onSuccess-- " + userid);
                    Toast.makeText(context, userid + " 登陆成功", Toast.LENGTH_SHORT).show();
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtil.d("IMServer", "--onError-- " + errorCode);
                }
            });
        }
    }
}
