
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
    @SerializedName("speaker_id")
    @Expose
    private Integer speakerId;
    @SerializedName("dor1_img")
    @Expose
    private String dor1Img;
    @SerializedName("dor2_img")
    @Expose
    private String dor2Img;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    private final static long serialVersionUID = -9219322149753535630L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Event() {
    }

    /**
     * @param topic event topic
     * @param img main event image url
     * @param dor1Img front dor image url
     * @param dor2Img additional dor image url
     * @param info event info
     * @param id event id
     * @param speakerId {@link Speaker} id
     * @param address human readable address
     * @param name event name
     * @param longitude event address longitude
     * @param latitude event address latitude
     * @param room event room number
     */
    public Event(Integer id, String name, String img, String address, String room,
                 String info, String topic, Integer speakerId, String dor1Img,
                 String dor2Img, String latitude, String longitude) {
        super();
        this.id = id;
        this.name = name;
        this.img = img;
        this.address = address;
        this.room = room;
        this.info = info;
        this.topic = topic;
        this.speakerId = speakerId;
        this.dor1Img = dor1Img;
        this.dor2Img = dor2Img;
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

    public Integer getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(Integer speakerId) {
        this.speakerId = speakerId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (id != null ? !id.equals(event.id) : event.id != null) return false;
        if (name != null ? !name.equals(event.name) : event.name != null) return false;
        if (img != null ? !img.equals(event.img) : event.img != null) return false;
        if (address != null ? !address.equals(event.address) : event.address != null) return false;
        if (room != null ? !room.equals(event.room) : event.room != null) return false;
        if (info != null ? !info.equals(event.info) : event.info != null) return false;
        if (topic != null ? !topic.equals(event.topic) : event.topic != null) return false;
        if (speakerId != null ? !speakerId.equals(event.speakerId) : event.speakerId != null)
            return false;
        if (dor1Img != null ? !dor1Img.equals(event.dor1Img) : event.dor1Img != null) return false;
        if (dor2Img != null ? !dor2Img.equals(event.dor2Img) : event.dor2Img != null) return false;
        if (latitude != null ? !latitude.equals(event.latitude) : event.latitude != null)
            return false;
        return longitude != null ? longitude.equals(event.longitude) : event.longitude == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (img != null ? img.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (room != null ? room.hashCode() : 0);
        result = 31 * result + (info != null ? info.hashCode() : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (speakerId != null ? speakerId.hashCode() : 0);
        result = 31 * result + (dor1Img != null ? dor1Img.hashCode() : 0);
        result = 31 * result + (dor2Img != null ? dor2Img.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        return result;
    }
}
