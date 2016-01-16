package com.icaroabreu.quickboxrestapiexample.webservice;

import com.icaroabreu.quickboxrestapiexample.webservice.model.NewUserAPI;
import com.icaroabreu.quickboxrestapiexample.webservice.model.RequestSessionBody;
import com.icaroabreu.quickboxrestapiexample.webservice.model.ResponseSession;
import com.icaroabreu.quickboxrestapiexample.webservice.model.UserAPI;
import com.icaroabreu.quickboxrestapiexample.webservice.model.UserAuthCredentials;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by icaroabreu on 16/01/2016.
 * QuickBoxRestApiExample
 */
public interface RestEndpoint {

    @POST("auth.json")
    Call<ResponseSession> authSession (@Body RequestSessionBody requestBody);

    @POST("users.json")
    Call<UserAPI> userSignUp (@Body NewUserAPI user);

    @POST("login.json")
    Call<UserAPI> userSignIn (@Body UserAuthCredentials authCredentials);

    @PUT("users/{user}.json")
    Call<UserAPI> updateUser (@Path("user") int userId, @Body UserAPI user);

    @GET("users/password/reset.json")
    Call<Void> requestResetPassword ( @Query("email") String email);
}
