package com.jakdor.labday;

import com.google.gson.Gson;
import com.jakdor.labday.common.model.AccessToken;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.LastUpdate;
import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.RetrofitBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.reactivex.observers.TestObserver;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RetrofitAPICallsIntegrationTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    RetrofitBuilder retrofitBuilder;

    private final String appDataJsonPath = "src/test/assets/api/app_data.json";
    private final String lastUpdateJsonPath = "src/test/assets/api/last_update.json";
    private final String loginAccessTokenPath = "src/test/assets/api/login.json";

    private final String dummyApiUrl = LabService.MOCK_API_URL;
    private final String dummyToken = "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6Ijk3IiwiaWF0IjoxNTUxMjA0MDc4LCJ1c2VybmFtZSI6InVzZXIyIn0.i_Ihq879bPQFeBFHM_FcqZZS-LQXn3ZRqjwN4RzQhLHGvs5diKSma2jGOu50pO2CdUeMfFmpJngBC91C1CEneQ";
    private final String dummyLogin = "user2";
    private final String dummyPassword = "user";

    @Before
    public void setUp() throws Exception {
        retrofitBuilder = new RetrofitBuilder();
    }

    /**
     * Tests login API call
     */
    @Test
    public void loginTest() throws Exception {
        LabService labService = retrofitBuilder.createService(
                dummyApiUrl, LabService.class);

        TestObserver<AccessToken> testObserver
                = labService.getAccessToken(dummyLogin, dummyPassword).test();
        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(accessToken1 -> {
            assertNotNull(accessToken1);
            assertNotNull(accessToken1.getAccessToken());
            return true;
        });

        testObserver.onComplete();
    }

    /**
     * Tests API response to last_update call
     */
    @Test
    public void lastUpdateTest() throws Exception {
        LabService labService = retrofitBuilder.createService(dummyApiUrl, LabService.class);

        TestObserver<LastUpdate> testObserver = labService.getLastUpdate().test();
        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(s -> {
            assertNotNull(s);
            assertNotNull(s.getUpdatedAt());
            assertFalse(s.getUpdatedAt().isEmpty());
            return true;
        });

        testObserver.onComplete();
    }

    /**
     * Tests response from AppData API call with token authorization
     */
    @Test
    public void appDataTokenTest() throws Exception {
        Gson gson = new Gson();
        AppData appData = gson.fromJson(readFile(appDataJsonPath), AppData.class);

        LabService labService = retrofitBuilder.createService(dummyApiUrl, LabService.class, dummyToken);

        TestObserver<AppData> testObserver = labService.getAppData().test();
        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(appData1 -> {
            assertEquals(appData, appData1);
            assertEquals(appData.hashCode(), appData1.hashCode());
            return true;
        });

        testObserver.onComplete();
    }

    /**
     * Reads file to string
     * @return String
     */
    private String readFile(String filePath) throws Exception{
        Path path = Paths.get(filePath);
        return new String(Files.readAllBytes(path));
    }
}