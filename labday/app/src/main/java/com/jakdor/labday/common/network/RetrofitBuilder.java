package com.jakdor.labday.common.network;

import com.jakdor.labday.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Configures Retrofit
 */
public class RetrofitBuilder {

    private Retrofit retrofit;

    private HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    private AuthenticationInterceptor authInterceptor = new AuthenticationInterceptor();

    private Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    private OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
            .readTimeout(7, TimeUnit.SECONDS)
            .writeTimeout(7, TimeUnit.SECONDS)
            .connectTimeout(7, TimeUnit.SECONDS);

    /**
     * No authorization header
     * @param apiUrl base API url
     * @param serviceClass retrofit config interface
     * @param <S> serviceClass type
     * @return retrofit instance
     */
    public <S> S createService(String apiUrl, Class<S> serviceClass) {
        addLogger();
        retrofitBuilder.baseUrl(apiUrl).client(okHttpClient.build());
        retrofit = retrofitBuilder.build();
        return retrofit.create(serviceClass);
    }

    /**
     * Token authorization header - token provided after successful login with
     * one-time login & password
     * @param apiUrl base API url
     * @param serviceClass retrofit config interface
     * @param authToken authorization token
     * @param <S> serviceClass type
     * @return retrofit instance
     */
    public <S> S createService(String apiUrl, Class<S> serviceClass, final String authToken) {
        if (authToken != null) {
            if(!authToken.isEmpty()) {
                authInterceptor.setAuthToken(authToken);

                if (!okHttpClient.interceptors().contains(authInterceptor)) {
                    okHttpClient.addInterceptor(authInterceptor);
                    addLogger();
                    retrofitBuilder.baseUrl(apiUrl).client(okHttpClient.build());
                    retrofit = retrofitBuilder.build();
                }
            }
        }

        return retrofit.create(serviceClass);
    }

    /**
     * Adds logger interceptor to okHttp config
     */
    private void addLogger(){
        if(BuildConfig.DEBUG) {
            if (!okHttpClient.interceptors().contains(httpLoggingInterceptor)) {
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                okHttpClient.addInterceptor(httpLoggingInterceptor);
            }
        }
    }
}
