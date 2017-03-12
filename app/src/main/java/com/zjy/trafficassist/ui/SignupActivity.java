package com.zjy.trafficassist.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zjy.trafficassist.BaseActivity;
import com.zjy.trafficassist.DatabaseManager;
import com.zjy.trafficassist.R;
import com.zjy.trafficassist.model.User;
import com.zjy.trafficassist.UserStatus;
import com.zjy.trafficassist.WebService;

public class SignupActivity extends BaseActivity {

    private DatabaseManager DBManager;
    private User user;

    private CoordinatorLayout container;
    private EditText new_username;
    private EditText new_passname;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        container = (CoordinatorLayout) findViewById(R.id.reg_container);
        new_username = (EditText) findViewById(R.id.username);
        new_passname = (EditText) findViewById(R.id.password);
        signUp = (Button) findViewById(R.id.sign_up_button);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(m.isActive()){
                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                attemptSignUp();
            }
        });
        DBManager = new DatabaseManager(this);
    }

    private void attemptSignUp(){

        /**
         * 初始化User对象
         */
        user = new User(new_username.getText().toString(), new_passname.getText().toString());
        //MapActivity.user = user;

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(user.getPassword()) && !isPasswordValid(user.getPassword())) {
            new_passname.setError(getString(R.string.error_invalid_password));
            focusView = new_passname;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(user.getUsername())) {
            new_username.setError(getString(R.string.error_field_required));
            focusView = new_username;
            cancel = true;
        } else if (!isUsernameValid(user.getUsername())) {
            new_username.setError(getString(R.string.error_invalid_username));
            focusView = new_username;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // perform the user login attempt.
            final ProgressDialog mPDialog = new ProgressDialog(SignupActivity.this);
            mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPDialog.setMessage(getResources().getString(R.string.now_user_register));
            mPDialog.setCancelable(true);
            mPDialog.show();
            new AsyncTask<Void, Void, Boolean>(){
                String ReturnCode;

                @Override
                protected Boolean doInBackground(Void... params) {
                    ReturnCode = WebService.Register(user.getUsername(), user.getPassword());
                    System.out.println(ReturnCode);
                    return Boolean.parseBoolean(ReturnCode);
                }

                @Override
                protected void onPostExecute(final Boolean success) {
                    super.onPostExecute(success);
                    mPDialog.dismiss();
                    if (success) {
                        Toast.makeText(SignupActivity.this, "注册成功：" + ReturnCode, Toast.LENGTH_SHORT).show();
                        UserStatus.Login_status = true;
                        UserStatus.user = user;
                        finish();
                        startActivity(new Intent(SignupActivity.this, MapActivity.class));
                    } else {
                        Toast.makeText(SignupActivity.this, "注册失败：" + ReturnCode, Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
