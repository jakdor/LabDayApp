package com.jakdor.labday.common.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private String authToken;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Authorization", "Bearer " + authToken);

        Request request = builder.build();
        return chain.proceed(request);
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
