
package com.jakdor.labday.common.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Speaker implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("speaker_name")
    @Expose
    private String speakerName;
    @SerializedName("speaker_info")
    @Expose
    private String speakerInfo;
    @SerializedName("speaker_img")
    @Expose
    private String speakerImg;
    private final static long serialVersionUID = -3275378227390756649L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Speaker() {
    }

    /**
     * @param id speaker id
     * @param speakerInfo short info about info
     * @param speakerImg speaker image url
     * @param speakerName name & surname
     */
    public Speaker(Integer id, String speakerName, String speakerInfo, String speakerImg) {
        super();
        this.id = id;
        this.speakerName = speakerName;
        this.speakerInfo = speakerInfo;
        this.speakerImg = speakerImg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public String getSpeakerInfo() {
        return speakerInfo;
    }

    public void setSpeakerInfo(String speakerInfo) {
        this.speakerInfo = speakerInfo;
    }

    public String getSpeakerImg() {
        return speakerImg;
    }

    public void setSpeakerImg(String speakerImg) {
        this.speakerImg = speakerImg;
    }

}
