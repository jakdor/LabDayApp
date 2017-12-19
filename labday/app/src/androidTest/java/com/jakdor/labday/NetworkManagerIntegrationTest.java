package com.jakdor.labday;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.network.RetrofitBuilder;
import com.jakdor.labday.common.repository.NetworkManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.appflate.restmock.RESTMockServer;
import io.reactivex.observers.TestObserver;

import static io.appflate.restmock.utils.RequestMatchers.pathContains;

/**
 * {@link NetworkManager} integration tests on local REST API mock
 */
public class NetworkManagerIntegrationTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    private Context testContext;

    private NetworkManager networkManager;

    private final String dummyApiUrl = RESTMockServer.getUrl();
    private final String dummyToken = "dummyToken";
    private final String dummyLogin = "user";
    private final String dummyPassword = "password";

    @BeforeClass
    public static void beforeClass() throws Exception {
        RESTMockServer.whenGET(pathContains("api/app_data"))
                .thenReturnFile(200, "api/app_data.json");
    }

    /**
     * Setup local Mock REST API server for instrumentation tests
     */
    @Before
    public void setUp() throws Exception {
        testContext = InstrumentationRegistry.getContext();
        networkManager = new NetworkManager(new RetrofitBuilder());
    }

    /**
     * {@link NetworkManager} integration test: get AppData from local REST API mock server,
     * compere parsed response to AppData parsed directly from .json file
     */
    @Test
    public void getAppDataTest() throws Exception {
        networkManager.configAuth(dummyApiUrl);

        Gson gson = new Gson();
        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        TestObserver<AppData> testObserver = new CustomTestObserver<>(appData);
        networkManager.getAppData().subscribe(testObserver);

        testObserver.assertSubscribed();
        testObserver.assertValueCount(1);
    }

    /**
     * {@link NetworkManager} integration test: same as getAppDataTest() but with token Authorization
     */
    @Test
    public void getAppDataTokenTest() throws Exception {
        networkManager.configAuth(dummyApiUrl, dummyToken);

        Gson gson = new Gson();
        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        TestObserver<AppData> testObserver = new CustomTestObserver<>(appData);
        networkManager.getAppData().subscribe(testObserver);

        testObserver.assertSubscribed();
        testObserver.assertValueCount(1);
    }

    /**
     * Read asset file to String
     * @param context required to access assets in apk
     * @param path file path
     * @return file loaded to String
     * @throws Exception file parsing / assets access Exceptions
     */
    private String readAssetFile(Context context, String path) throws Exception{
        StringBuilder stringBuilder = new StringBuilder();
        InputStream json = context.getAssets().open(path);
        BufferedReader bufferedReader
                = new BufferedReader(new InputStreamReader(json, "UTF-8"));

        String str;
        while ((str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str);
        }

        bufferedReader.close();

        return new String(stringBuilder);
    }

    /**
     * Custom RXJava TestObserver, after getting data from API call comperes is to Object
     * provided in constructor
     * @param <T>
     */
    private class CustomTestObserver<T> extends TestObserver<T>{

        private T data;

        CustomTestObserver(T t){
            this.data = t;
        }

        @Override
        public void onNext(T t) {
            super.onNext(t);
            Assert.assertEquals(data, t);
        }
    }
}
