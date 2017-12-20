package com.jakdor.labday;

import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.RetrofitBuilder;
import com.jakdor.labday.common.repository.NetworkManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManagerTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private NetworkManager networkManager;

    @Mock
    private RetrofitBuilder retrofitBuilder;

    private LabService labService;

    Retrofit.Builder retrofit = new Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    private final String dummyApiUrl = LabService.MOCK_API_URL;
    private final String dummyToken = "dummyToken";
    private final String dummyLogin = "user";
    private final String dummyPassword = "password";

    @Before
    public void setUp() throws Exception {
        labService = retrofit.baseUrl(dummyApiUrl).build().create(LabService.class);
        Mockito.when(retrofitBuilder.createService(dummyApiUrl, LabService.class))
                .thenReturn(labService);

        Mockito.when(retrofitBuilder.createService(dummyApiUrl, LabService.class, dummyToken))
                .thenReturn(labService);

        Mockito.when(retrofitBuilder.createService(
                dummyApiUrl, LabService.class, dummyLogin, dummyPassword)).thenReturn(labService);
    }

    @Test
    public void configAuthNoneTest() throws Exception {
        networkManager.configAuth(dummyApiUrl);

        Assert.assertEquals(labService.hashCode(), networkManager.getLabService().hashCode());
        Assert.assertNull(networkManager.getLoginLabService());
    }

    @Test
    public void configAuthTokenTest() throws Exception {
        networkManager.configAuth(dummyApiUrl, dummyToken);

        Assert.assertEquals(labService.hashCode(), networkManager.getLabService().hashCode());
        Assert.assertNull(networkManager.getLoginLabService());
    }

    @Test
    public void configAuthLoginTest() throws Exception {
        networkManager.configAuth(dummyApiUrl, dummyLogin, dummyPassword);

        Assert.assertEquals(labService.hashCode(), networkManager.getLoginLabService().hashCode());
        Assert.assertNull(networkManager.getLabService());
    }
}