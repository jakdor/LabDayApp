package com.jakdor.labday.common.network;

import com.jakdor.labday.common.model.maps.MapPath;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit configuration interface for Google API calls
 */
public interface MapService {

    String GOOGLE_API = "https://maps.googleapis.com/maps/api/directions/";

    @GET("json")
    Observable<MapPath> getMapPath(@Query("origin") String origin,
                                   @Query("destination") String destination,
                                   @Query("key") String apiKey,
                                   @Query("mode") String mode);
}
