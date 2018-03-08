package com.jakdor.labday.common.network;

import com.jakdor.labday.common.model.AccessToken;
import com.jakdor.labday.common.model.AppData;
import com.jakdor.labday.common.model.LastUpdate;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Retrofit API endpoints
 */
public interface LabService {
    //mock api address
    String MOCK_API_URL = "https://1ec1fcc6-6966-422c-8a1e-409c926f4f3e.mock.pstmn.io/";
    String API_URL = "http://156.17.225.123:8000/";

    @GET("api/app-data")
    Observable<AppData> getAppData();

    @GET("api/last-update")
    Observable<LastUpdate> getLastUpdate();

    @FormUrlEncoded
    @POST("api/login")
    Observable<AccessToken> getAccessToken(@Field("username") String user, @Field("password") String pass);
}
