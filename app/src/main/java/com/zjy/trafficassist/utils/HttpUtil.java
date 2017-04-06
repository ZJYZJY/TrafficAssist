package com.zjy.trafficassist.utils;

import com.zjy.trafficassist.model.AlarmHistory;
import com.zjy.trafficassist.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * com.zjy.trafficassist.utils
 * Created by 73958 on 2017/4/6.
 */

public class HttpUtil {
    private static final String SERVER_IP = "120.27.130.203";

    private static final String PORT = "8001";

    private static final String PATH = "http://" + SERVER_IP + ":" + PORT + "/";

    public static final int SUCCESS = 200;

    public static final int FAIL = 400;

    public static final int EMPTY = 20;

    private static Retrofit instance;

    public static synchronized APIService create() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(PATH)
                    .addConverterFactory(GsonConverterFactory.create())  // Gson转换器
                    .build();
        }
        return instance.create(APIService.class);
    }

    public static int stateCode(String str){
        try {
            JSONObject json = new JSONObject(str);
            return json.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return FAIL;
    }

    public interface APIService {

        /**
         * 用户登录接口
         * @param user 用户对象
         */
        @POST("trafficassist/user/login.php")
        Call<ResponseBody> login(@Body User user);

        /**
         * 用户注册接口
         * @param user 用户对象
         */
        @POST("trafficassist/user/signup.php")
        Call<ResponseBody> signup(@Body User user);

        /**
         * 历史记录接口
         */
        @GET("trafficassist/user/downloadHistory.php")
        Call<ResponseBody> history(@Query("username") String username);
    }
}
