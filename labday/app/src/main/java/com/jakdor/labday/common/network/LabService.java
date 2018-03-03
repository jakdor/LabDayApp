package com.jakdor.labday.common.network;

import com.jakdor.labday.common.model.AccessToken;
import com.jakdor.labday.common.model.AppData;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Retrofit API endpoints
 */
public interface LabService {
    //mock api address
    String MOCK_API_URL = "https://1ec1fcc6-6966-422c-8a1e-409c926f4f3e.mock.pstmn.io/";

    @GET("api/app_data")
    Observable<AppData> getAppData();

    @GET("api/last_update")
    Observable<String> getLastUpdate();

    @GET("api/login")
    Observable<AccessToken> getAccessToken();
}
