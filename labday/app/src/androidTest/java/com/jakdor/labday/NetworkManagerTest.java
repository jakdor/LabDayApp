package com.jakdor.labday;


import android.support.test.runner.AndroidJUnit4;

import com.jakdor.labday.common.network.LabService;
import com.jakdor.labday.common.network.RetrofitBuilder;
import com.jakdor.labday.common.repository.NetworkManager;
import com.jakdor.labday.di.AppComponent;
import com.jakdor.labday.di.RepositoryModule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(AndroidJUnit4.class)
public class NetworkManagerTest {

    @Rule
    public DaggerMockRule<AppComponent> mockitoRule =
            new DaggerMockRule<>(AppComponent.class, new RepositoryModule())
                    .set(component -> networkManager = component.networkManager());

    @Mock
    RetrofitBuilder retrofitBuilder;

    NetworkManager networkManager;

    Retrofit.Builder retrofit = new Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    final String dummyApiUrl = LabService.MOCK_API_URL; //todo replace with local API mock framework

    @Test
    public void configAuthNoneTest() throws Exception {

        LabService labService = retrofit.baseUrl(dummyApiUrl).build().create(LabService.class);

        Mockito.when(retrofitBuilder.createService(dummyApiUrl, LabService.class))
                .thenReturn(labService);

        networkManager.configAuth(dummyApiUrl);

        Assert.assertEquals(labService, networkManager.getLabService());
    }
}