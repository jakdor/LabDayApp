package com.jakdor.labday.common.network;

import com.jakdor.labday.common.model.AccessToken;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.maps.MapPath;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API endpoints
 */
public interface LabService {
    //mock api address
    String MOCK_API_URL = "https://1ec1fcc6-6966-422c-8a1e-409c926f4f3e.mock.pstmn.io/";
    String GOOGLE_API = "https://maps.googleapis.com/maps/api/directions/";

    @GET("api/app_data")
    Observable<AppData> getAppData();

    @GET("api/last_update")
    Observable<String> getLastUpdate();

    @GET("api/login")
    Observable<AccessToken> getAccessToken();

    @GET("json")
    Observable<MapPath> getMapPath(@Query("origin") String origin,
                                   @Query("destination") String destination,
                                   @Query("key") String apiKey);
}
