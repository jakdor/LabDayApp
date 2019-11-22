package com.jakdor.labday.common.network;

import com.jakdor.labday.common.model.AccessToken;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.LastUpdate;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit API endpoints
 */
public interface LabService {
    //mock api address
    String MOCK_API_URL = "https://d27f1691-a7d0-44fe-b396-7d0e0af88757.mock.pstmn.io";
    String API_URL = "http://tramwaj.asi.wroclaw.pl:5000/";

    @GET("api/app_data")
    Observable<AppData> getAppData();

    @GET("api/last_update")
    Observable<LastUpdate> getLastUpdate();

    @POST("api/login")
    Observable<AccessToken> getAccessToken(@Query("username") String user, @Query("password") String pass);
}
