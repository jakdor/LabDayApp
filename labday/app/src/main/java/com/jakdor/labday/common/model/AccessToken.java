package com.jakdor.labday.common.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Json access token
 */
public class AccessToken {

    @SerializedName("token")
    @Expose
    private String accessToken;

    /**
     * No args constructor for use in serialization
     */
    public AccessToken() {
    }

    /**
     * @param accessToken returned from API after successful login
     */
    public AccessToken(String accessToken) {
        super();
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessToken that = (AccessToken) o;

        return accessToken != null ? accessToken.equals(that.accessToken) : that.accessToken == null;
    }

    @Override
    public int hashCode() {
        return accessToken != null ? accessToken.hashCode() : 0;
    }
}
