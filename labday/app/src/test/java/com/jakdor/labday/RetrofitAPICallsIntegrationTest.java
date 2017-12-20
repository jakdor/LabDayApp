package com.jakdor.labday;

import com.google.gson.Gson;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.RetrofitBuilder;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.reactivex.observers.TestObserver;

public class RetrofitAPICallsIntegrationTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    RetrofitBuilder retrofitBuilder;

    private final String appDataJsonPath = "app/src/test/assets/api/app_data.json";

    private final String dummyApiUrl = LabService.MOCK_API_URL;
    private final String dummyToken = "dummyToken";
    private final String dummyLogin = "user";
    private final String dummyPassword = "password";

    @Before
    public void setUp() throws Exception {
        retrofitBuilder = new RetrofitBuilder();
    }

    @Test
    public void appDataLoginTest() throws Exception {
        Gson gson = new Gson();
        AppData appData = gson.fromJson(readFile(appDataJsonPath), AppData.class);

        LabService labService = retrofitBuilder.createService(
                dummyApiUrl, LabService.class, dummyLogin, dummyPassword);
        TestObserver<AppData> testObserver = new CustomTestObserver<>(appData);
        labService.getAppData().subscribe(testObserver);

        testObserver.assertSubscribed();
        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.onComplete();
    }

    @Test
    public void appDataTokenTest() throws Exception {
        Gson gson = new Gson();
        AppData appData = gson.fromJson(readFile(appDataJsonPath), AppData.class);

        LabService labService = retrofitBuilder.createService(dummyApiUrl, LabService.class, dummyToken);
        TestObserver<AppData> testObserver = new CustomTestObserver<>(appData);
        labService.getAppData().subscribe(testObserver);

        testObserver.assertSubscribed();
        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.onComplete();
    }

    private String readFile(String filePath) throws Exception{
        Path path = Paths.get(filePath);
        return new String(Files.readAllBytes(path));
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