
package com.jakdor.labday.common.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Stores information about single LabDay event
 */
public class Event implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("img")
    @Expose
    private String img;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("room")
    @Expose
    private String room;
    @SerializedName("info")
    @Expose
    private String info;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("speaker_name")
    @Expose
    private String speakerName;
    @SerializedName("speaker_info")
    @Expose
    private String speakerInfo;
    @SerializedName("speaker_img")
    @Expose
    private String speakerImg;
    @SerializedName("dor1_img")
    @Expose
    private String dor1Img;
    @SerializedName("dor2_img")
    @Expose
    private String dor2Img;
    @SerializedName("time_start")
    @Expose
    private Integer timeStart;
    @SerializedName("time_end")
    @Expose
    private Integer timeEnd;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    private final static long serialVersionUID = -7797136571328555190L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Event() {
    }

    /**
     * @param topic event topic
     * @param speakerImg speaker image
     * @param img main event image url
     * @param dor1Img front dor image url
     * @param dor2Img additional dor image url
     * @param info event info
     * @param id event id
     * @param timeEnd event end time in sec form unix epoch
     * @param timeStart event start time in sec from unix epoch
     * @param speakerInfo speaker info
     * @param address human readable address
     * @param name event name
     * @param longitude event address longitude
     * @param latitude event address latitude
     * @param speakerName speaker name & surname
     * @param room event room number
     */
    public Event(Integer id, String name, String img, String address, String room, String info, String topic, String speakerName, String speakerInfo, String speakerImg, String dor1Img, String dor2Img, Integer timeStart, Integer timeEnd, String latitude, String longitude) {
        super();
        this.id = id;
        this.name = name;
        this.img = img;
        this.address = address;
        this.room = room;
        this.info = info;
        this.topic = topic;
        this.speakerName = speakerName;
        this.speakerInfo = speakerInfo;
        this.speakerImg = speakerImg;
        this.dor1Img = dor1Img;
        this.dor2Img = dor2Img;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public String getDor1Img() {
        return dor1Img;
    }

    public void setDor1Img(String dor1Img) {
        this.dor1Img = dor1Img;
    }

    public String getDor2Img() {
        return dor2Img;
    }

    public void setDor2Img(String dor2Img) {
        this.dor2Img = dor2Img;
    }

    public Integer getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Integer timeStart) {
        this.timeStart = timeStart;
    }

    public Integer getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Integer timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
