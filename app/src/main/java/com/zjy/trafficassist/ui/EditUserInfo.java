package com.zjy.trafficassist.ui;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.zjy.trafficassist.R;
import com.zjy.trafficassist.UserStatus;
import com.zjy.trafficassist.utils.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserInfo extends AppCompatPreferenceActivity {

    private final String REAL_NAME = "1";

    private final String PHONE_NUM = "2";

    private final String DRIVER_LICENSE_NUM = "3";

    private final String DRIVER_LICENSE_TYPE = "4";

    private final String CAR_NUM = "5";

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {

            if (preference instanceof EditTextPreference) {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                String infoType = "";
                switch (preference.getKey()) {
                    case "user_real_name":
                        infoType = REAL_NAME;
                        break;
                    case "user_phone_number":
                        infoType = PHONE_NUM;
                        break;
                    case "user_driver_number":
                        infoType = DRIVER_LICENSE_NUM;
                        break;
                    case "user_driver_type":
                        infoType = DRIVER_LICENSE_TYPE;
                        break;
                    case "user_car_number":
                        infoType = CAR_NUM;
                        break;
                }
                String stringValue = o.toString();
                preference.setSummary(stringValue);

                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("username", UserStatus.USER.getUsername());
                userInfo.put("infotype", infoType);
                userInfo.put("info", stringValue);
//                HttpUtil.create().modifyUserInfo(userInfo).enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        try {
//                            String res = response.body().string();
//                            if (HttpUtil.stateCode(res) == HttpUtil.SUCCESS) {
//                                Toast.makeText(EditUserInfo.this, "修改成功", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(EditUserInfo.this, "修改失败", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        Toast.makeText(EditUserInfo.this, "连接失败", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.pref_edit_user_info);

        initPreferences();
    }

    public void initPreferences() {
        bindPreferenceSummaryToValue(findPreference("user_real_name"));
        bindPreferenceSummaryToValue(findPreference("user_phone_number"));
        bindPreferenceSummaryToValue(findPreference("user_driver_number"));
        bindPreferenceSummaryToValue(findPreference("user_driver_type"));
        bindPreferenceSummaryToValue(findPreference("user_car_number"));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
