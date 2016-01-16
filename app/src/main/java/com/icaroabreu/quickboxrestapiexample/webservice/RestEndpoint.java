package com.icaroabreu.quickboxrestapiexample.webservice;

import com.icaroabreu.quickboxrestapiexample.webservice.model.RequestSessionBody;
import com.icaroabreu.quickboxrestapiexample.webservice.model.ResponseSession;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by icaroabreu on 16/01/2016.
 * QuickBoxRestApiExample
 */
public interface RestEndpoint {

    @POST("auth.json")
    Call<ResponseSession> authSession (@Body RequestSessionBody requestBody);

}
