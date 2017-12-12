package com.jakdor.labday.common.network;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Configures Retrofit
 */
public class RetrofitBuilder {

    private Retrofit retrofit;

    private Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    private OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS);

    /**
     * No authorization header
     * @param apiUrl base API url
     * @param serviceClass retrofit config interface
     * @param <S> serviceClass type
     * @return retrofit instance
     */
    public <S> S createService(String apiUrl, Class<S> serviceClass) {
        retrofitBuilder.baseUrl(apiUrl).client(okHttpClient.build());
        retrofit = retrofitBuilder.build();
        return retrofit.create(serviceClass);
    }

    /**
     * Basic login & password encoded in base64 authorization header
     * @param apiUrl base API url
     * @param serviceClass retrofit config interface
     * @param username one-time login
     * @param password one-time password
     * @param <S> serviceClass type
     * @return retrofit instance
     */
    public <S> S createService(String apiUrl, Class<S> serviceClass, String username, String password) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            String authToken = Credentials.basic(username, password);
            return createService(apiUrl, serviceClass, authToken);
        }

        return createService(apiUrl, serviceClass);
    }

    /**
     * Token encoded in base64 authorization header - token provided after successful login with
     * one-time login & password
     * @param apiUrl base API url
     * @param serviceClass retrofit config interface
     * @param authToken authorization token
     * @param <S> serviceClass type
     * @return retrofit instance
     */
    public <S> S createService(String apiUrl, Class<S> serviceClass, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

            if (!okHttpClient.interceptors().contains(interceptor)) {
                okHttpClient.addInterceptor(interceptor);
                retrofitBuilder.baseUrl(apiUrl).client(okHttpClient.build());
                retrofit = retrofitBuilder.build();
            }
        }

        return retrofit.create(serviceClass);
    }
}
