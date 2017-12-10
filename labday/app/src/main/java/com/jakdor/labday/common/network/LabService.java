package com.jakdor.labday.common.network;

import com.jakdor.labday.common.model.Project;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit API endpoints
 */
public interface LabService {
    //architecture test with GitHub API
    String API_URL = "https://api.github.com/";

    @GET("users/{user}/repos")
    Call<List<Project>> getProjectList(@Path("user") String user);
}
