package com.jakdor.labday;

import androidx.annotation.NonNull;

import com.jakdor.labday.common.network.AuthenticationInterceptor;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptorTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    private AuthenticationInterceptor authenticationInterceptor;

    private final String dummyToken = "testToken";
    private final String dummyUrl = "http://dummyurl.com";
    private final String message = "duno lol";

    @Before
    public void setUp() throws Exception {
        authenticationInterceptor = new AuthenticationInterceptor();
        authenticationInterceptor.setAuthToken(dummyToken);
    }

    /**
     * Test authentication header setup
     */
    @Test
    public void interceptTest() throws Exception {
        Interceptor.Chain chain = new Interceptor.Chain() {
            @Override
            public Request request() {
                return new Request.Builder().get().url(dummyUrl).build();
            }

            @Override
            public Response proceed(@NonNull Request request) throws IOException {
                return new Response.Builder().request(request).protocol(Protocol.HTTP_2).code(200)
                        .headers(request.headers()).message(message).build();
            }

            @Nullable
            @Override
            public Connection connection() {
                return null;
            }

            @Override
            public Call call() {
                return null;
            }

            @Override
            public int connectTimeoutMillis() {
                return 0;
            }

            @Override
            public Interceptor.Chain withConnectTimeout(int timeout, @NonNull TimeUnit unit) {
                return null;
            }

            @Override
            public int readTimeoutMillis() {
                return 0;
            }

            @Override
            public Interceptor.Chain withReadTimeout(int timeout, @NonNull TimeUnit unit) {
                return null;
            }

            @Override
            public int writeTimeoutMillis() {
                return 0;
            }

            @Override
            public Interceptor.Chain withWriteTimeout(int timeout, @NotNull TimeUnit unit) {
                return null;
            }
        };

        Response testResponse = authenticationInterceptor.intercept(chain);

        Assert.assertEquals("token " + dummyToken, testResponse.header("Authorization"));
        Assert.assertEquals(message, testResponse.message());
        Assert.assertEquals(dummyUrl + "/", testResponse.request().url().toString());
    }

}