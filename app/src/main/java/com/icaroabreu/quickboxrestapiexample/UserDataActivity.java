package com.icaroabreu.quickboxrestapiexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.icaroabreu.quickboxrestapiexample.util.Constant;
import com.icaroabreu.quickboxrestapiexample.util.Utils;
import com.icaroabreu.quickboxrestapiexample.webservice.Webservice;
import com.icaroabreu.quickboxrestapiexample.webservice.model.NewUser;
import com.icaroabreu.quickboxrestapiexample.webservice.model.NewUserAPI;
import com.icaroabreu.quickboxrestapiexample.webservice.model.User;
import com.icaroabreu.quickboxrestapiexample.webservice.model.UserAPI;
import com.icaroabreu.quickboxrestapiexample.webservice.model.UserAuthCredentials;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class UserDataActivity extends AppCompatActivity {

    @InjectView(R.id.login)
    EditText mLogin;
    @InjectView(R.id.login_holder)
    TextInputLayout mLoginHolder;
    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.password_holder)
    TextInputLayout mPasswordHolder;
    @InjectView(R.id.confirm_password)
    EditText mConfirmPassword;
    @InjectView(R.id.confirm_password_holder)
    TextInputLayout mConfirmPasswordHolder;
    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.email_holder)
    TextInputLayout mEmailHolder;
    @InjectView(R.id.user_name)
    EditText mUserName;
    @InjectView(R.id.website)
    EditText mWebsite;
    @InjectView(R.id.confirm_button)
    Button mConfirmButton;
    @InjectView(R.id.content_signup)
    ScrollView mContentSignup;
    @InjectView(R.id.loading)
    ProgressBar mLoading;
    @InjectView(R.id.tag_list)
    EditText mTagList;
    @InjectView(R.id.phone)
    EditText mPhone;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        ButterKnife.inject(this);

        mContentSignup.setVisibility(View.GONE);

        String userLogin = PreferenceManager.getDefaultSharedPreferences(this).getString(Constant.USER_LOGIN, null);
        if (userLogin != null) {
            UserAuthCredentials userCredentials = new UserAuthCredentials(userLogin,
                    PreferenceManager.getDefaultSharedPreferences(this).getString(Constant.USER_PASSWORD, ""));

            Webservice.getRestEndpoint(this).userSignIn(userCredentials).enqueue(new Callback<UserAPI>() {
                @Override
                public void onResponse(Response<UserAPI> response, Retrofit retrofit) {
                    runOnUiThread(() -> {
                        mContentSignup.setVisibility(View.VISIBLE);
                        mLoading.setVisibility(View.GONE);
                        mPasswordHolder.setVisibility(View.GONE);
                        mConfirmPasswordHolder.setVisibility(View.GONE);

                        User user = response.body().getUser();
                        mLogin.setText(user.getLogin());
                        mEmail.setText(user.getEmail());

                        if (user.getFull_name() != null)
                            mUserName.setText(user.getFull_name());

                        if (user.getCustom_data() != null)
                            mTagList.setText(user.getUser_tags());

                        if (user.getWebsite() != null)
                            mWebsite.setText(user.getWebsite());

                        if (user.getPhone() != null)
                            mPhone.setText(user.getPhone());

                        mConfirmButton.setText(R.string.action_update_user);
                        editMode = true;
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    Utils.displayAlert(UserDataActivity.this, R.string.warning_generic, R.string.warning_unknown, (dialog, which) -> {
                        onBackPressed();
                    });
                }
            });
        } else {
            mContentSignup.setVisibility(View.VISIBLE);
            mLoading.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.confirm_button)
    public void signUpUser() {
        boolean success = true;

        if (!editMode) {
            if (!Utils.isValidEmail(mEmail.getText())) {
                success = false;
                mEmailHolder.setErrorEnabled(true);
                mEmailHolder.setError(getString(R.string.warning_invalid_email));
            } else {
                mEmailHolder.setErrorEnabled(false);
            }

            if (mEmail.getText().toString().isEmpty()) {
                success = false;
                mEmailHolder.setErrorEnabled(true);
                mEmailHolder.setError(getString(R.string.warning_required));
            } else {
                mEmailHolder.setErrorEnabled(false);
            }

            if (mPassword.getText().toString().isEmpty()) {
                success = false;
                mPasswordHolder.setErrorEnabled(true);
                mPasswordHolder.setError(getString(R.string.warning_required));
            } else {
                mPasswordHolder.setErrorEnabled(false);
            }

            if (mConfirmPassword.getText().toString().isEmpty()) {
                success = false;
                mConfirmPasswordHolder.setErrorEnabled(true);
                mConfirmPasswordHolder.setError(getString(R.string.warning_required));
            } else {
                mConfirmPasswordHolder.setErrorEnabled(false);
            }

            if (!mConfirmPassword.getText().toString().equals(mPassword.getText().toString())) {
                success = false;
                mConfirmPasswordHolder.setErrorEnabled(true);
                mConfirmPasswordHolder.setError(getString(R.string.warning_diferent_passwords));
            } else {
                mConfirmPasswordHolder.setErrorEnabled(false);
            }

            if (success) {
                NewUserAPI userAPI = new NewUserAPI();
                NewUser user = new NewUser();
                user.setEmail(mEmail.getText().toString());
                user.setLogin(mLogin.getText().toString());
                user.setFull_name(mUserName.getText().toString());
                user.setPassword(mPassword.getText().toString());
                user.setWebsite(mWebsite.getText().toString());
                user.setTag_list(mTagList.getText().toString());
                user.setPhone(mPhone.getText().toString());

                userAPI.setUser(user);

                Webservice.getRestEndpoint(this).userSignUp(userAPI).enqueue(new Callback<UserAPI>() {
                    @Override
                    public void onResponse(Response<UserAPI> response, Retrofit retrofit) {

                        if(response.isSuccess()){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UserDataActivity.this).edit();

                            editor.putString(Constant.USER_ID, response.body().getUser().getId());
                            editor.putString(Constant.USER_LOGIN, user.getLogin());
                            editor.putString(Constant.USER_PASSWORD, user.getPassword());

                            editor.apply();

                            runOnUiThread(() -> {
                                mConfirmButton.setText(R.string.action_update_user);
                                editMode = true;
                            });
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Utils.displayAlert(UserDataActivity.this, R.string.warning_generic, R.string.warning_unknown);
                    }
                });
            }
        } else {

            UserAPI userAPI = new UserAPI();
            User user = new User();
            user.setEmail(mEmail.getText().toString());
            user.setLogin(mLogin.getText().toString());
            user.setFull_name(mUserName.getText().toString());
            user.setWebsite(mWebsite.getText().toString());
            user.setTag_list(mTagList.getText().toString());
            user.setPhone(mPhone.getText().toString());

            userAPI.setUser(user);

            Webservice.getRestEndpoint(this).updateUser(
                    PreferenceManager.getDefaultSharedPreferences(UserDataActivity.this).getInt(Constant.USER_ID, 0),
                    userAPI).enqueue(new Callback<UserAPI>() {
                @Override
                public void onResponse(Response<UserAPI> response, Retrofit retrofit) {
                    if (response.isSuccess())
                        Utils.displayAlert(UserDataActivity.this, getString(R.string.label_success), getString(R.string.label_user_updated));
                    else
                        Utils.displayAlert(UserDataActivity.this, R.string.warning_generic, R.string.warning_unknown);
                }

                @Override
                public void onFailure(Throwable t) {
                    Utils.displayAlert(UserDataActivity.this, R.string.warning_generic, R.string.warning_unknown);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserDataActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
