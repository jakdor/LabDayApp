package com.jakdor.labday.common.network;

import com.jakdor.labday.common.model.AppData;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit API endpoints
 */
public interface LabService {
    //architecture test with GitHub API
    String API_URL = "https://api.github.com/";

    @GET("users/{user}/repos")
    Observable<List<AppData>> getProjectList(@Path("user") String user);
}
