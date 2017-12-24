package com.jakdor.labday.androidjunt;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;
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

    private ProjectRepository projectRepository;

    private final String dummyApiUrl = RESTMockServer.getUrl();
    private final String dummyApiBadUrl = "http://www.dummy.com/";

    @Before
    public void setUp() throws Exception {
        testContext = InstrumentationRegistry.getContext();

        projectRepository = new ProjectRepository(
                new NetworkManager(new RetrofitBuilder()), new RxSchedulersFacade());
    }

    @Test
    public void checkInitRepoStateTest() throws Exception {
        Assert.assertEquals(projectRepository.getRepositoryState(),
                ProjectRepository.repositoryStates.INIT);
    }

    /**
     * {@link ProjectRepository} getAppData() / getData() integration test scenario 1
     * - check init ProjectRepository state
     * - get appData API response (successful)
     * - check ProjectRepository after successful call
     */
    @Test
    public void integrationTestScenario1() throws Exception {
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
    public void integrationTestScenario2() throws Exception {
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
}
