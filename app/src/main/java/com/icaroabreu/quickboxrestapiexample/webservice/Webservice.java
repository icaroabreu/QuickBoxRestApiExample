package com.icaroabreu.quickboxrestapiexample.webservice;

import android.content.Context;
import android.preference.PreferenceManager;

import com.icaroabreu.quickboxrestapiexample.BuildConfig;
import com.icaroabreu.quickboxrestapiexample.util.Constant;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by icaroabreu on 16/01/2016.
 * QuickBoxRestApiExample
 */
public class Webservice {

    private static Webservice mInstance;
    private final RestEndpoint restEndpoint;

    public static synchronized Webservice getInstance(Context context) {
        if(mInstance == null)
            mInstance = new Webservice(context);
        return mInstance;
    }

    public Webservice(Context context) {

        OkHttpClient client = new OkHttpClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(interceptor);
        client.interceptors().add(chain -> {
            Request original = chain.request();

            String token = PreferenceManager.getDefaultSharedPreferences(context).getString(Constant.SESSION_TOKEN, null);
            Request.Builder request = original.newBuilder();

            if (token != null) {
                request.addHeader(Constant.HEADER_PREFIX, token);
            }

            request.method(original.method(), original.body());

            return chain.proceed(request.build());
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.REST_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        restEndpoint = retrofit.create(RestEndpoint.class);
    }

    public static RestEndpoint getRestEndpoint(Context context) {
        return getInstance(context).restEndpoint;
    }

}
