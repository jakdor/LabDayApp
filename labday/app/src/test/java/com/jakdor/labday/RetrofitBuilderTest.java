package com.jakdor.labday;

import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.network.AuthenticationInterceptor;
import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.RetrofitBuilder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.observers.TestObserver;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class RetrofitBuilderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    AuthenticationInterceptor authenticationInterceptor;

    @InjectMocks
    RetrofitBuilder retrofitBuilder;

    private final String dummyApiUrl = LabService.MOCK_API_URL;
    private final String dummyToken = "givbhsjdokfj";
    private final String message = "duno lol";

    /**
     * test RetrofitBuilder in isolation from AuthenticationInterceptor
     */
    @Test
    public void createServiceWithInterceptor() throws Exception {
        Request request = new Request.Builder()
                .header("Authorization", dummyToken)
                .get()
                .url(dummyApiUrl)
                .build();

        ResponseBody dummyResponse = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 0;
            }

            @Override
            public BufferedSource source() {
                return null;
            }
        };

        when(authenticationInterceptor.intercept(any())).thenReturn(
                new Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_2)
                        .code(200)
                        .body(dummyResponse)
                        .message(message)
                        .build());

        LabService labService = retrofitBuilder.createService(
                dummyApiUrl, LabService.class, dummyToken);

        TestObserver<AppData> testObserver = labService.getAppData().test();
        testObserver.assertSubscribed();
        testObserver.onComplete();

        InOrder order = inOrder(authenticationInterceptor);
        order.verify(authenticationInterceptor, calls(1))
                .setAuthToken(dummyToken);
        order.verify(authenticationInterceptor, calls(1))
                .intercept(any());
    }
}
