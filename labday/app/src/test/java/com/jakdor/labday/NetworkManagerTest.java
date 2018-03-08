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

    private Retrofit.Builder retrofit = new Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    private final String dummyApiUrl = LabService.API_URL;
    private final String dummyToken = "c6d74cec06f72f91b41666c9e289fc872a896e44";
    private final String dummyLogin = "test";
    private final String dummyPassword = "1234asdf";

    @Before
    public void setUp() throws Exception {
        labService = retrofit.baseUrl(dummyApiUrl).build().create(LabService.class);
        Mockito.when(retrofitBuilder.createService(dummyApiUrl, LabService.class))
                .thenReturn(labService);

        Mockito.when(retrofitBuilder.createService(dummyApiUrl, LabService.class, dummyToken))
                .thenReturn(labService);
    }

    @Test
    public void configAuthNoneTest() throws Exception {
        networkManager.configAuth(dummyApiUrl);

        Assert.assertEquals(labService.hashCode(), networkManager.getLoginLabService().hashCode());
        Assert.assertNull(networkManager.getLabService());
    }

    @Test
    public void configAuthTokenTest() throws Exception {
        networkManager.configAuth(dummyApiUrl, dummyToken);

        Assert.assertEquals(labService.hashCode(), networkManager.getLabService().hashCode());
        Assert.assertNull(networkManager.getLoginLabService());
    }
}