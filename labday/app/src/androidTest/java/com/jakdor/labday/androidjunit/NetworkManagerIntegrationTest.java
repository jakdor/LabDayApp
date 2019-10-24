package com.jakdor.labday.androidjunit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.gson.Gson;
import com.jakdor.labday.common.model.AccessToken;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.LastUpdate;
import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.RetrofitBuilder;
import com.jakdor.labday.common.repository.NetworkManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.reactivex.observers.TestObserver;

import static com.jakdor.labday.androidjunit.TestUtils.readAssetFile;

/**
 * {@link NetworkManager} integration tests on local REST API mock
 */
public class NetworkManagerIntegrationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Context testContext;

    private NetworkManager networkManager;

    private final String dummyApiUrl = LabService.MOCK_API_URL; //RESTMockServer.getUrl()
    private final String dummyToken = "c6d74cec06f72f91b41666c9e289fc872a896e44";
    private final String dummyLogin = "test";
    private final String dummyPassword = "1234asdf";

    /**
     * Setup local Mock REST API server for instrumentation tests
     */
    @Before
    public void setUp() throws Exception {
        testContext = InstrumentationRegistry.getInstrumentation().getContext();
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
        networkManager.configAuth(dummyApiUrl);

        Gson gson = new Gson();
        AccessToken expectedAccessToken
                = gson.fromJson(readAssetFile(testContext, "api/login.json"), AccessToken.class);

        TestObserver<AccessToken> testObserver
                = networkManager.getAccessToken(dummyLogin, dummyPassword).test();
        testObserver.awaitCount(1);

        testObserver.assertSubscribed();
        testObserver.assertNoErrors();
        testObserver.assertValue(accessToken -> {
            Assert.assertEquals(expectedAccessToken, accessToken);
            Assert.assertEquals(expectedAccessToken.hashCode(), accessToken.hashCode());
            return true;
        });

        testObserver.onComplete();
    }

    /**
     * {@link NetworkManager} integration test: get last_update id from local REST API mock server
     */
    @Test
    public void getLastUpdate() throws Exception {
        networkManager.configAuth(dummyApiUrl);

        Gson gson = new Gson();
        LastUpdate expectedLastUpdate
                = gson.fromJson(readAssetFile(testContext, "api/last_update.json"), LastUpdate.class);

        TestObserver<LastUpdate> testObserver = networkManager.getLastUpdate().test();
        testObserver.awaitCount(1);

        testObserver.assertSubscribed();
        testObserver.assertNoErrors();
        testObserver.assertValue(s -> {
            Assert.assertEquals(expectedLastUpdate, s);
            return true;
        });

        testObserver.onComplete();
    }

    /**
     * {@link NetworkManager} integration test: get AppData from local REST API mock server,
     * compere parsed response to AppData parsed directly from .json file
     */
    @Test
    public void getAppDataTest() throws Exception {
        networkManager.configAuth(dummyApiUrl, dummyToken);

        Gson gson = new Gson();
        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        TestObserver<AppData> testObserver = networkManager.getAppData().test();
        testObserver.awaitCount(1);

        testObserver.assertSubscribed();
        testObserver.assertNoErrors();
        testObserver.assertValue(appData1 -> {
            Assert.assertEquals(appData, appData1);
            Assert.assertEquals(appData.hashCode(), appData1.hashCode());
            return true;
        });

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

        TestObserver<AppData> testObserver = networkManager.getAppData().test();
        testObserver.awaitCount(1);

        testObserver.assertSubscribed();
        testObserver.assertNoErrors();
        testObserver.assertValue(appData1 -> {
            Assert.assertEquals(appData, appData1);
            Assert.assertEquals(appData.hashCode(), appData1.hashCode());
            return true;
        });

        testObserver.onComplete();
    }
}
