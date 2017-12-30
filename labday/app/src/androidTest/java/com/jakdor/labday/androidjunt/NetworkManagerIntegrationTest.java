package com.jakdor.labday.androidjunt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;
import com.jakdor.labday.common.model.AccessToken;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.network.RetrofitBuilder;
import com.jakdor.labday.common.repository.NetworkManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.appflate.restmock.RESTMockServer;
import io.reactivex.observers.TestObserver;

import static com.jakdor.labday.androidjunt.TestUtils.readAssetFile;

/**
 * {@link NetworkManager} integration tests on local REST API mock
 */
public class NetworkManagerIntegrationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Context testContext;

    private NetworkManager networkManager;

    private final String dummyApiUrl = RESTMockServer.getUrl();
    private final String dummyToken = "dummyToken";
    private final String dummyLogin = "user";
    private final String dummyPassword = "password";

    /**
     * Setup local Mock REST API server for instrumentation tests
     */
    @Before
    public void setUp() throws Exception {
        testContext = InstrumentationRegistry.getContext();
        networkManager = new NetworkManager(new RetrofitBuilder());
    }

    /**
     * {@link NetworkManager} unit test: check network status
     */
    @Test
    public void checkNetworkStatusTest() throws Exception {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) testContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo == null){
            Assert.assertFalse(networkManager.checkNetworkStatus(testContext));
        }
        else {
            Assert.assertTrue(networkManager.checkNetworkStatus(testContext));
        }
    }

    /**
     * {@link NetworkManager} integration test: login request to, local REST API server mock
     */
    @Test
    public void loginTest() throws Exception{
        networkManager.configAuth(dummyApiUrl, dummyLogin, dummyPassword);

        Gson gson = new Gson();
        AccessToken expectedAccessToken
                = gson.fromJson(readAssetFile(testContext, "api/login.json"), AccessToken.class);

        TestObserver<AccessToken> testObserver = new CustomTestObserver<>(expectedAccessToken);
        networkManager.getAccessToken().subscribe(testObserver);

        testObserver.assertSubscribed();
        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.onComplete();
    }

    /**
     * {@link NetworkManager} integration test: get last_update id from local REST API mock server
     */
    @Test
    public void getLastUpdate() throws Exception {
        networkManager.configAuth(dummyApiUrl);

        String expectedLastUpdate
                = new String(readAssetFile(testContext, "api/last_update.json").getBytes());

        TestObserver<String> testObserver = new CustomTestObserver<>(expectedLastUpdate);
        networkManager.getLastUpdate().subscribe(testObserver);

        testObserver.assertSubscribed();
        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.onComplete();
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
        testObserver.assertNoErrors();
        testObserver.onComplete();
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
        testObserver.assertNoErrors();
        testObserver.onComplete();
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
            Assert.assertEquals(data.hashCode(), t.hashCode());
        }
    }
}
