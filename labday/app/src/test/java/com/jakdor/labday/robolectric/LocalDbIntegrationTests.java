package com.jakdor.labday.robolectric;

import com.google.gson.Gson;
import com.jakdor.labday.TestApp;
import com.jakdor.labday.common.localdb.LocalDbHandler;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxStatus;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.reactivex.observers.TestObserver;

/**
 * local db integration tests
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class LocalDbIntegrationTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final String appDataJsonPath = "app/src/test/assets/api/app_data.json";

    private LocalDbHandler localDbHandler;

    @Before
    public void setUp() throws Exception{
        localDbHandler = new LocalDbHandler(RuntimeEnvironment.application);
    }

    @After
    public void tearDown() throws Exception{
        localDbHandler.dropDb();
    }

    /**
     * Save and load {@link com.jakdor.labday.common.model.AppData} to local db
     */
    @Test
    public void integrationTestSaveAndLoad() throws Exception{
        Gson gson = new Gson();
        AppData appData = gson.fromJson(readFile(appDataJsonPath), AppData.class);

        localDbHandler.pushAppDataToDb(appData);

        TestObserver<RxResponse<AppData>> testObserver =
                localDbHandler.getAppDataFromDb().test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();
        testObserver.assertValue(appDataRxResponse -> {

            Assert.assertEquals(RxStatus.SUCCESS_DB, appDataRxResponse.status);
            Assert.assertNotNull(appDataRxResponse.data);
            Assert.assertNull(appDataRxResponse.error);
            Assert.assertEquals(appData, appDataRxResponse.data);
            Assert.assertEquals(appData.hashCode(), appDataRxResponse.data.hashCode());

            Assert.assertNotNull(appDataRxResponse.data.getEvents());
            Assert.assertNotNull(appDataRxResponse.data.getMapOthers());
            Assert.assertNotNull(appDataRxResponse.data.getPaths());
            Assert.assertNotNull(appDataRxResponse.data.getSpeakers());
            Assert.assertNotNull(appDataRxResponse.data.getTimetables());

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
