package com.jakdor.labday.androidjunt;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;
import com.jakdor.labday.R;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.network.RetrofitBuilder;
import com.jakdor.labday.common.repository.NetworkManager;
import com.jakdor.labday.common.repository.ProjectRepository;
import com.jakdor.labday.rx.RxSchedulersFacade;
import com.jakdor.labday.rx.RxStatus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.appflate.restmock.RESTMockServer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.jakdor.labday.androidjunt.TestUtils.readAssetFile;

/**
 * {@link ProjectRepository} integration tests on local REST API mock
 */
public class ProjectRepositoryIntegrationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Context testContext;
    private Context targetContext;

    private ProjectRepository projectRepository;

    private final String dummyApiUrl = RESTMockServer.getUrl();
    private final String dummyApiBadUrl = "http://www.dummy.com/";
    private final String dummyLogin = "user";
    private final String dummyPassword = "password";

    @Before
    public void setUp() throws Exception {
        testContext = InstrumentationRegistry.getContext();
        targetContext = InstrumentationRegistry.getTargetContext();

        projectRepository = new ProjectRepository(
                new NetworkManager(new RetrofitBuilder()), new RxSchedulersFacade());
    }

    @Test
    public void checkInitRepoStateTest() throws Exception {
        Assert.assertEquals(projectRepository.getRepositoryState(),
                ProjectRepository.repositoryStates.INIT);
    }

    @Test
    public void sharedPreferencesTest() throws Exception {
        projectRepository.saveApiLastUpdateId("1234", targetContext);
        Assert.assertTrue(projectRepository.isLocalDataCurrent("1234", targetContext));

        SharedPreferences sharedPreferences = targetContext.getSharedPreferences(
                targetContext.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        String test = sharedPreferences.getString(
                targetContext.getString(R.string.pref_api_last_update_id), "0");

        Assert.assertEquals("1234", test);
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

        Gson gson = new Gson();
        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        CompositeDisposable disposable = new CompositeDisposable();

        disposable.add(projectRepository.getAppData(dummyApiUrl)
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Assert.fail())
                .subscribe(appDataRxResponse -> {

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

                    disposable.dispose();
                }));
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

        CompositeDisposable disposable = new CompositeDisposable();

        disposable.add(projectRepository.getAppData(dummyApiBadUrl)
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Assert.fail())
                .subscribe(appDataRxResponse -> {

                    Assert.assertNotNull(appDataRxResponse);
                    Assert.assertNull(appDataRxResponse.data);
                    Assert.assertNotNull(appDataRxResponse.error);
                    Assert.assertEquals(RxStatus.ERROR, appDataRxResponse.status);

                    Assert.assertEquals(projectRepository.getRepositoryState(),
                            ProjectRepository.repositoryStates.ERROR);

                    disposable.dispose();
                }));
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

        Gson gson = new Gson();
        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        CompositeDisposable disposable = new CompositeDisposable();

        disposable.add(projectRepository.getUpdate(dummyApiUrl, targetContext)
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Assert.fail())
                .subscribe(appDataRxResponse -> {

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

                    Assert.assertEquals(readAssetFile(testContext, "api/last_update.json"),
                            sharedPreferences.getString(targetContext.getString(
                                    R.string.pref_api_last_update_id), null));

                    disposable.dispose();

                }));
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

        sharedPreferences.edit().putString(
                targetContext.getString(R.string.pref_api_last_update_id),
                readAssetFile(testContext, "api/last_update.json")).commit();

        Assert.assertEquals(projectRepository.getRepositoryState(),
                ProjectRepository.repositoryStates.INIT);
        Assert.assertNull(projectRepository.getData());

        Gson gson = new Gson();
        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        CompositeDisposable disposable = new CompositeDisposable();

        disposable.add(projectRepository.getUpdate(dummyApiUrl, targetContext)
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Assert.fail())
                .subscribe(appDataRxResponse -> {

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

                    Assert.assertEquals(readAssetFile(testContext, "api/last_update.json"),
                            sharedPreferences.getString(targetContext.getString(
                                    R.string.pref_api_last_update_id), null));

                    disposable.dispose();

                }));
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

        Gson gson = new Gson();
        AppData appData = gson.fromJson(
                readAssetFile(testContext, "api/app_data.json"), AppData.class);

        CompositeDisposable disposable = new CompositeDisposable();

        disposable.add(projectRepository.getUpdate(dummyApiBadUrl, targetContext)
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Assert.fail())
                .subscribe(appDataRxResponse -> {

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

                    disposable.dispose();

                }));
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

        CompositeDisposable disposable = new CompositeDisposable();

        disposable.add(projectRepository.login(dummyApiUrl, targetContext, dummyLogin, dummyPassword)
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Assert.fail())
                .subscribe(appDataRxResponse -> {

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

                    Assert.assertEquals(readAssetFile(testContext, "api/last_update.json"),
                            sharedPreferences.getString(targetContext.getString(
                                    R.string.pref_api_last_update_id), null));

                    Assert.assertTrue(projectRepository.isLoggedIn());

                    disposable.dispose();

                }));
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

        CompositeDisposable disposable = new CompositeDisposable();

        disposable.add(projectRepository.login(dummyApiBadUrl, targetContext, dummyLogin, dummyPassword)
                .subscribeOn(Schedulers.io())
                .doOnError(throwable -> Assert.fail())
                .subscribe(appDataRxResponse -> {

                        Assert.assertNotNull(appDataRxResponse);
                        Assert.assertNull(appDataRxResponse.data);
                        Assert.assertEquals(RxStatus.NO_INTERNET, appDataRxResponse.status);

                        disposable.dispose();
                }));
    }
}
