package com.icaroabreu.quickboxrestapiexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.icaroabreu.quickboxrestapiexample.util.Constant;
import com.icaroabreu.quickboxrestapiexample.util.Utils;
import com.icaroabreu.quickboxrestapiexample.webservice.Webservice;
import com.icaroabreu.quickboxrestapiexample.webservice.model.RequestSessionBody;
import com.icaroabreu.quickboxrestapiexample.webservice.model.ResponseSession;
import com.icaroabreu.quickboxrestapiexample.webservice.model.UserAuthCredentials;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.login)
    EditText mLogin;
    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.content_main)
    ScrollView mContentMain;
    @InjectView(R.id.loading)
    ProgressBar mLoading;
    @InjectView(R.id.login_holder)
    TextInputLayout mLoginHolder;
    @InjectView(R.id.password_holder)
    TextInputLayout mPasswordHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mContentMain.setVisibility(View.GONE);

        String userLogin = PreferenceManager.getDefaultSharedPreferences(this).getString(Constant.USER_LOGIN, null);
        if (userLogin != null) {
            UserAuthCredentials user = new UserAuthCredentials(userLogin,
                    PreferenceManager.getDefaultSharedPreferences(this).getString(Constant.USER_PASSWORD, ""));
            getSession(user);
        } else {
            mLoading.setVisibility(View.GONE);
            mContentMain.setVisibility(View.VISIBLE);
        }
    }

    private void getSession(UserAuthCredentials user) {

        long timestamp = new Date().getTime() / 1000;
        int nonce = new Random().nextInt();

        String message = MessageFormat.format(
                getString(R.string.auth_hmac_sha_body),
                String.valueOf(BuildConfig.APPLICATIONID),
                BuildConfig.AUTHORIZATIONKEY, String.valueOf(nonce), String.valueOf(timestamp));
        System.out.println(message);

        String encryptedSignature = Utils.hash(message,
                BuildConfig.AUTHORIZATIONSECRET);

        RequestSessionBody requestBody = new RequestSessionBody(encryptedSignature, BuildConfig.AUTHORIZATIONKEY, BuildConfig.APPLICATIONID, nonce, timestamp, user);
        System.out.println(requestBody);
        mLoading.setVisibility(View.VISIBLE);
        mContentMain.setVisibility(View.GONE);
        Webservice.getRestEndpoint(MainActivity.this).authSession(requestBody).enqueue(new Callback<ResponseSession>() {
            @Override
            public void onResponse(Response<ResponseSession> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                    editor.putString(Constant.SESSION_ID, response.body().getSession().get_id());
                    editor.putString(Constant.SESSION_TOKEN, response.body().getSession().getToken());

                    if (user != null) {
                        editor.putString(Constant.USER_LOGIN, user.getLogin());
                        editor.putString(Constant.USER_PASSWORD, user.getPassword());
                    }

                    editor.apply();
                    startActivity(new Intent(MainActivity.this, UserDataActivity.class));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    mLoading.setVisibility(View.GONE);
                    mContentMain.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    @OnClick(R.id.signin_button)
    public void executeLogin() {
        if (!Utils.isValidEmail(mLogin.getText())) {
            mLoginHolder.setErrorEnabled(true);
            mLoginHolder.setError("Email inválido");
        } else if (!mPassword.getText().toString().isEmpty()) {
            mPasswordHolder.setErrorEnabled(true);
            mPasswordHolder.setError("Obrigatório");
        }

        if(!Utils.isValidEmail(mLogin.getText()) || !mPassword.getText().toString().isEmpty())
            return;
        else {
            mLoginHolder.setErrorEnabled(false);
            mPasswordHolder.setErrorEnabled(false);
            UserAuthCredentials user = new UserAuthCredentials(mLogin.getText().toString(), mPassword.getText().toString());
            getSession(user);
        }
    }

    @OnClick(R.id.signup_button)
    public void openSignUpForm() {
        getSession(null);
    }
}
