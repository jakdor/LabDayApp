package com.jakdor.labday.androidjunit;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import com.facebook.soloader.SoLoader;
import com.google.gson.Gson;
import com.jakdor.labday.R;
import com.jakdor.labday.TestApp;
import com.jakdor.labday.common.localdb.LocalDbHandler;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.LastUpdate;
import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.RetrofitBuilder;
import com.jakdor.labday.common.repository.NetworkManager;
import com.jakdor.labday.common.repository.ProjectRepository;
import com.jakdor.labday.rx.RxResponse;
import com.jakdor.labday.rx.RxSchedulersFacade;
import com.jakdor.labday.rx.RxStatus;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileOutputStream;
import java.nio.charset.Charset;

import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static com.jakdor.labday.androidjunit.TestUtils.readAssetFile;

/**
 * {@link ProjectRepository} integration tests on local REST API mock
 */
public class ProjectRepositoryIntegrationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Context testContext;
    private Context targetContext;

    private ProjectRepository projectRepository;
    private LocalDbHandler localDbHandler;

    private final String dummyApiUrl = LabService.MOCK_API_URL; //RESTMockServer.getUrl()
    private final String dummyApiBadUrl = "http://www.dummy.com/";
    private final String dummyLogin = "test";
    private final String dummyPassword = "1234asdf";
    private final String dummyToken = "c6d74cec06f72f91b41666c9e289fc872a896e44";

    @Before
    public void setUp() throws Exception {
        targetContext = ApplicationProvider.getApplicationContext(); //todo replace with mock, this is a real app context!
        testContext = InstrumentationRegistry.getInstrumentation().getContext();

        SoLoader.init(targetContext, false);

        localDbHandler = new LocalDbHandler(
                Instrumentation.newApplication(TestApp.class, targetContext));

        projectRepository = new ProjectRepository(
                new NetworkManager(new RetrofitBuilder()),
                localDbHandler,
                new RxSchedulersFacade());
    }

    @After
    public void tearDown() throws Exception{
        localDbHandler.dropDb();
        projectRepository.deleteToken(targetContext);
        projectRepository.saveApiLastUpdateId("0", targetContext);
    }

    @Test
    public void checkInitRepoStateTest() throws Exception {
        Assert.assertEquals(projectRepository.getRepositoryState(),
                ProjectRepository.repositoryStates.INIT);
    }

    /**
     * {@link ProjectRepository} Save lastUpdate id, check if saved id matches expected
     */
    @Test
    public void sharedPreferencesLastUpdateTest() throws Exception {
        projectRepository.saveApiLastUpdateId("1234", targetContext);
        Assert.assertTrue(projectRepository.isLocalDataCurrent("1234", targetContext));

        SharedPreferences sharedPreferences = targetContext.getSharedPreferences(
                targetContext.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        String test = sharedPreferences.getString(
                targetContext.getString(R.string.pref_api_last_update_id), "0");

        Assert.assertEquals("1234", test);
    }

    /**
     * {@link ProjectRepository} saveAccessToken, loadAccessToken methods test
     * - test saving and loading of access token
     */
    @Test
    public void integrationAccessTokenSaveLoadTest() throws Exception {
        Assert.assertNull(projectRepository.getAccessToken());
        projectRepository.saveAccessToken(dummyToken, targetContext);
        Assert.assertEquals(dummyToken, projectRepository.getAccessToken());
        projectRepository.setAccessToken(null);

        projectRepository.loadAccessToken(targetContext);
        Assert.assertEquals(dummyToken, projectRepository.getAccessToken());
    }

    /**
     * {@link ProjectRepository} readFile() unit test
     */
    @Test
    public void readFileTest() throws Exception {
        String testStr = "Test123";

        FileOutputStream outputStream = targetContext.openFileOutput("testFile", Context.MODE_PRIVATE);
        outputStream.write(testStr.getBytes(Charset.forName("UTF-8")));
        outputStream.close();

        byte[] savedStr = projectRepository.readFile(targetContext, "testFile");

        Assert.assertEquals(testStr, new String(savedStr));
    }

    /**
     * {@link ProjectRepository} getAppData() / getData() integration test scenario 1
     * - check init ProjectRepository state
     * - get appData API response (successful)
     * - check ProjectRepository after successful call
     */
    @Test
    public void integrationDataTestScenario1() throws Exception {
        Assert.assertEquals(projectRepository.getRepositoryState(),
                ProjectRepository.repositoryStates.INIT);
        Assert.assertNull(projectRepository.getData());

        projectRepository.setAccessToken(dummyToken);

        Gson gson = new Gson();
        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        TestObserver<RxResponse<AppData>> testObserver =
                projectRepository.getAppData(dummyApiUrl, targetContext)
                        .subscribeOn(Schedulers.io())
                        .doOnError(throwable -> Assert.fail())
                        .test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(appDataRxResponse -> {
            Assert.assertNotNull(appDataRxResponse);
            Assert.assertNotNull(appDataRxResponse.data);
            Assert.assertNull(appDataRxResponse.error);
            Assert.assertEquals(RxStatus.SUCCESS, appDataRxResponse.status);
            Assert.assertEquals(appData, appDataRxResponse.data);
            Assert.assertEquals(appData.hashCode(), appDataRxResponse.data.hashCode());

            Assert.assertEquals(projectRepository.getRepositoryState(),
                    ProjectRepository.repositoryStates.READY);

            Assert.assertNotNull(projectRepository.getData());
            Assert.assertEquals(projectRepository.getData().data, appData);
            return true;
        });

        testObserver.onComplete();
    }

    /**
     * {@link ProjectRepository} getAppData() / getData() integration test scenario 2
     * - check init ProjectRepository state
     * - get appData API response (failed)
     */
    @Test
    public void integrationDataTestScenario2() throws Exception {
        Assert.assertEquals(projectRepository.getRepositoryState(),
                ProjectRepository.repositoryStates.INIT);
        Assert.assertNull(projectRepository.getData());

        projectRepository.setAccessToken(dummyToken);

        TestObserver<RxResponse<AppData>> testObserver =
                projectRepository.getAppData(dummyApiBadUrl, targetContext)
                        .subscribeOn(Schedulers.io())
                        .doOnError(throwable -> Assert.fail())
                        .test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(appDataRxResponse -> {
            Assert.assertNotNull(appDataRxResponse);
            Assert.assertNull(appDataRxResponse.data);
            Assert.assertNotNull(appDataRxResponse.error);
            Assert.assertEquals(RxStatus.ERROR, appDataRxResponse.status);

            Assert.assertEquals(ProjectRepository.repositoryStates.ERROR,
                    projectRepository.getRepositoryState());
            return true;
        });

        testObserver.onComplete();
    }

    /**
     * {@link ProjectRepository} getUpdate() integration test scenario 1
     * - local last update id doesn't match API id
     * - get API last update id
     * - get appData API response (successful)
     * - check ProjectRepository after successful call
     */
    @Test
    public void integrationUpdateTestScenario1() throws Exception {
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences(
                targetContext.getString(R.string.pref_file_name), Context.MODE_PRIVATE);

        sharedPreferences.edit().putString(
                targetContext.getString(R.string.pref_api_last_update_id), "0").commit();

        Assert.assertEquals(projectRepository.getRepositoryState(),
                ProjectRepository.repositoryStates.INIT);
        Assert.assertNull(projectRepository.getData());

        projectRepository.setAccessToken(dummyToken);

        Gson gson = new Gson();
        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        TestObserver<RxResponse<AppData>> testObserver =
                projectRepository.getUpdate(dummyApiUrl, targetContext)
                        .subscribeOn(Schedulers.io())
                        .doOnError(throwable -> Assert.fail())
                        .test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(appDataRxResponse -> {
            Assert.assertNotNull(appDataRxResponse);
            Assert.assertNotNull(appDataRxResponse.data);
            Assert.assertNull(appDataRxResponse.error);
            Assert.assertEquals(RxStatus.SUCCESS, appDataRxResponse.status);
            Assert.assertEquals(appData, appDataRxResponse.data);
            Assert.assertEquals(appData.hashCode(), appDataRxResponse.data.hashCode());

            Assert.assertEquals(projectRepository.getRepositoryState(),
                    ProjectRepository.repositoryStates.READY);

            Assert.assertNotNull(projectRepository.getData());
            Assert.assertEquals(projectRepository.getData().data, appData);

            LastUpdate expectedLastUpdate = gson.fromJson(
                    readAssetFile(testContext, "api/last_update.json"), LastUpdate.class);
            Assert.assertNotNull(expectedLastUpdate.getUpdatedAt());
            Assert.assertEquals(new String(expectedLastUpdate.getUpdatedAt().toCharArray()),
                    sharedPreferences.getString(targetContext.getString(
                            R.string.pref_api_last_update_id), null));

            return true;
        });

        testObserver.onComplete();
    }

    /**
     * {@link ProjectRepository} getUpdate() integration test scenario 2
     * - local last update id matches API id
     * - get API last update id
     * - load AppData from local db
     * - check ProjectRepository after successful call/load
     */
    @Test
    public void integrationUpdateTestScenario2() throws Exception {
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences(
                targetContext.getString(R.string.pref_file_name), Context.MODE_PRIVATE);

        Gson gson = new Gson();
        LastUpdate expectedLastUpdate = gson.fromJson(
                readAssetFile(testContext, "api/last_update.json"), LastUpdate.class);
        sharedPreferences.edit().putString(
                targetContext.getString(R.string.pref_api_last_update_id),
                expectedLastUpdate.getUpdatedAt()).commit();

        Assert.assertEquals(projectRepository.getRepositoryState(),
                ProjectRepository.repositoryStates.INIT);
        Assert.assertNull(projectRepository.getData());

        projectRepository.setAccessToken(dummyToken);

        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        localDbHandler.pushAppDataToDb(appData);

        TestObserver<RxResponse<AppData>> testObserver =
                projectRepository.getUpdate(dummyApiUrl, targetContext)
                        .subscribeOn(Schedulers.io())
                        .doOnError(throwable -> Assert.fail())
                        .test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(appDataRxResponse -> {
            Assert.assertNotNull(appDataRxResponse);
            Assert.assertNotNull(appDataRxResponse.data);
            Assert.assertNull(appDataRxResponse.error);
            Assert.assertEquals(RxStatus.SUCCESS_DB, appDataRxResponse.status);
            Assert.assertEquals(appData, appDataRxResponse.data);
            Assert.assertEquals(appData.hashCode(), appDataRxResponse.data.hashCode());

            Assert.assertEquals(ProjectRepository.repositoryStates.READY,
                    projectRepository.getRepositoryState());

            Assert.assertNotNull(projectRepository.getData());
            Assert.assertEquals(appData, projectRepository.getData().data);

            Assert.assertNotNull(expectedLastUpdate.getUpdatedAt());
            Assert.assertEquals(new String(expectedLastUpdate.getUpdatedAt().toCharArray()),
                    sharedPreferences.getString(targetContext.getString(
                            R.string.pref_api_last_update_id), null));

            return true;
        });

        testObserver.onComplete();
    }

    /**
     * {@link ProjectRepository} getUpdate() integration test scenario 3
     * - get API last update id (failed)
     * - load AppData from local db
     * - check ProjectRepository after successful load
     */
    @Test
    public void integrationUpdateTestScenario3() throws Exception {
        Assert.assertEquals(projectRepository.getRepositoryState(),
                ProjectRepository.repositoryStates.INIT);
        Assert.assertNull(projectRepository.getData());

        projectRepository.setAccessToken(dummyToken);

        Gson gson = new Gson();
        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        localDbHandler.pushAppDataToDb(appData);

        TestObserver<RxResponse<AppData>> testObserver =
                projectRepository.getUpdate(dummyApiBadUrl, targetContext)
                        .subscribeOn(Schedulers.io())
                        .doOnError(throwable -> Assert.fail())
                        .test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(appDataRxResponse -> {
            Assert.assertNotNull(appDataRxResponse);
            Assert.assertNotNull(appDataRxResponse.data);
            Assert.assertNull(appDataRxResponse.error);
            Assert.assertEquals(RxStatus.SUCCESS_DB, appDataRxResponse.status);
            Assert.assertEquals(appData, appDataRxResponse.data);
            Assert.assertEquals(appData.hashCode(), appDataRxResponse.data.hashCode());

            Assert.assertEquals(ProjectRepository.repositoryStates.READY,
                    projectRepository.getRepositoryState());

            Assert.assertNotNull(projectRepository.getData());
            Assert.assertEquals(appData, projectRepository.getData().data);
            return true;
        });

        testObserver.onComplete();
    }

    /**
     * {@link ProjectRepository} login() api call
     * - get accessToken after successful login
     * - get AppData
     * - check ProjectRepository after successful login/load
     */
    @Test
    public void integrationLoginTestScenario1() throws Exception {
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences(
                targetContext.getString(R.string.pref_file_name), Context.MODE_PRIVATE);

        sharedPreferences.edit().putString(
                targetContext.getString(R.string.pref_api_last_update_id), "null").commit();

        Assert.assertEquals(projectRepository.getRepositoryState(),
                ProjectRepository.repositoryStates.INIT);
        Assert.assertNull(projectRepository.getData());

        Gson gson = new Gson();
        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        TestObserver<RxResponse<AppData>> testObserver =
                projectRepository.login(dummyApiUrl, targetContext, dummyLogin, dummyPassword)
                        .subscribeOn(Schedulers.io())
                        .doOnError(throwable -> Assert.fail())
                        .test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(appDataRxResponse -> {
            Assert.assertNotNull(appDataRxResponse);
            Assert.assertNotNull(appDataRxResponse.data);
            Assert.assertNull(appDataRxResponse.error);
            Assert.assertEquals(RxStatus.SUCCESS, appDataRxResponse.status);
            Assert.assertEquals(appData, appDataRxResponse.data);
            Assert.assertEquals(appData.hashCode(), appDataRxResponse.data.hashCode());

            Assert.assertEquals(projectRepository.getRepositoryState(),
                    ProjectRepository.repositoryStates.READY);

            Assert.assertNotNull(projectRepository.getData());
            Assert.assertEquals(projectRepository.getData().data, appData);

            LastUpdate expectedLastUpdate = gson.fromJson(
                    readAssetFile(testContext, "api/last_update.json"), LastUpdate.class);
            Assert.assertNotNull(expectedLastUpdate.getUpdatedAt());
            Assert.assertEquals(new String(expectedLastUpdate.getUpdatedAt().toCharArray()),
                    sharedPreferences.getString(targetContext.getString(
                            R.string.pref_api_last_update_id), null));

            Assert.assertTrue(projectRepository.isLoggedIn(targetContext));
            return true;
        });

        testObserver.onComplete();
    }

    /**
     * {@link ProjectRepository} login() api call
     * - get accessToken after successful login (failed)
     */
    @Test
    public void integrationLoginTestScenario2() throws Exception {
        Assert.assertEquals(projectRepository.getRepositoryState(),
                ProjectRepository.repositoryStates.INIT);
        Assert.assertNull(projectRepository.getData());

        TestObserver<RxResponse<AppData>> testObserver =
                projectRepository.login(dummyApiBadUrl, targetContext, dummyLogin, dummyPassword)
                        .subscribeOn(Schedulers.io())
                        .doOnError(throwable -> Assert.fail())
                        .test();

        testObserver.assertSubscribed();
        testObserver.awaitCount(1);
        testObserver.assertNoErrors();

        testObserver.assertValue(appDataRxResponse -> {
            Assert.assertNotNull(appDataRxResponse);
            Assert.assertNull(appDataRxResponse.data);
            Assert.assertEquals(RxStatus.LOGIN_ERROR, appDataRxResponse.status);
            return true;
        });

        testObserver.onComplete();
    }
}
