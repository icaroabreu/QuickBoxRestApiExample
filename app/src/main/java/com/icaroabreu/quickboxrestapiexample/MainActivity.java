package com.icaroabreu.quickboxrestapiexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
            getSession(user, false);
        } else {
            mLoading.setVisibility(View.GONE);
            mContentMain.setVisibility(View.VISIBLE);
        }
    }

    private void getSession(UserAuthCredentials user, boolean isRequestingResetPassword) {

        long timestamp = new Date().getTime() / 1000;
        int nonce = new Random().nextInt();

        String message;

        if(user != null)
            message = MessageFormat.format(
                        getString(R.string.auth_hmac_sha_body_with_user),
                        String.valueOf(BuildConfig.APPLICATIONID),
                        BuildConfig.AUTHORIZATIONKEY, String.valueOf(nonce),
                        String.valueOf(timestamp),
                        user.getLogin(), user.getPassword());
        else
            message = MessageFormat.format(
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
                        if(response.body().getSession().getUser_id() != 0)
                            editor.putInt(Constant.USER_ID, response.body().getSession().getUser_id());
                        editor.putString(Constant.USER_LOGIN, user.getLogin());
                        editor.putString(Constant.USER_PASSWORD, user.getPassword());
                    }

                    editor.apply();
                    if(isRequestingResetPassword)
                        executeRequest();
                    else {
                        Intent intent = new Intent(MainActivity.this, UserDataActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    runOnUiThread(() -> {
                        mLoginHolder.setErrorEnabled(true);

                        mLoading.setVisibility(View.GONE);
                        mContentMain.setVisibility(View.VISIBLE);
                    });
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
        if (mLogin.getText().toString().isEmpty()) {
            mLoginHolder.setErrorEnabled(true);
            mLoginHolder.setError(getString(R.string.warning_invalid_email));
        } else if (mPassword.getText().toString().isEmpty()) {
            mPasswordHolder.setErrorEnabled(true);
            mPasswordHolder.setError(getString(R.string.warning_required));
        }

        if(mLogin.getText().toString().isEmpty() || mPassword.getText().toString().isEmpty())
            return;
        else {
            mLoginHolder.setErrorEnabled(false);
            mPasswordHolder.setErrorEnabled(false);
            UserAuthCredentials user = new UserAuthCredentials(mLogin.getText().toString(), mPassword.getText().toString());
            getSession(user, false);
        }
    }

    @OnClick(R.id.signup_button)
    public void openSignUpForm() {
        getSession(null, false);
    }

    @OnClick(R.id.request_reset_password)
    public void requestResetPassword() {

        getSession(null, true);

    }

    private void executeRequest(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setMessage(R.string.label_reset_password);

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint(R.string.prompt_email);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.action_confirm,
                (dialog, which) -> {
                    String email = input.getText().toString();
                    if (Utils.isValidEmail(email)) {
                        Webservice.getRestEndpoint(MainActivity.this).requestResetPassword(email).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Response<Void> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    Utils.displayAlert(MainActivity.this, getString(R.string.label_success), getString(R.string.label_check_email));
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Utils.displayAlert(MainActivity.this, R.string.warning_generic, R.string.warning_unknown);
                            }
                        });
                    } else {
                        Utils.displayAlert(MainActivity.this, getString(R.string.warning_generic), getString(R.string.warning_invalid_email));
                        runOnUiThread(() -> {
                            mLoading.setVisibility(View.GONE);
                            mContentMain.setVisibility(View.VISIBLE);
                        });
                    }
                });

        alertDialog.setNegativeButton(R.string.action_cancel, null);

        alertDialog.show();
    }
}
