package com.jakdor.labday;

import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.RetrofitBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.reactivex.observers.TestObserver;

public class RetrofitAPICallsIntegrationTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    RetrofitBuilder retrofitBuilder;

    private final String dummyApiUrl = LabService.MOCK_API_URL; //todo replace with local API mock framework
    private final String dummyToken = "dummyToken";
    private final String dummyLogin = "user";
    private final String dummyPassword = "password";

    @Before
    public void setUp() throws Exception {
        retrofitBuilder = new RetrofitBuilder();
    }

    @Test
    public void appDataLoginTest() throws Exception {
        LabService labService = retrofitBuilder.createService(dummyApiUrl, LabService.class, dummyLogin, dummyPassword);
        TestObserver<AppData> testObserver = new TestObserver<>();
        labService.getAppData().subscribe(testObserver);

        testObserver.assertSubscribed();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1); //todo compere response to expected json
        testObserver.onComplete();
    }

    @Test
    public void appDataTokenTest() throws Exception {
        LabService labService = retrofitBuilder.createService(dummyApiUrl, LabService.class, dummyToken);
        TestObserver<AppData> testObserver = new TestObserver<>();
        labService.getAppData().subscribe(testObserver);

        testObserver.assertSubscribed();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);
        testObserver.onComplete();
    }
}