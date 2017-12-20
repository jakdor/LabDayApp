
package com.jakdor.labday.common.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Other events location and info
 */
public class MapOther implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("info")
    @Expose
    private String info;
    @SerializedName("img")
    @Expose
    private String img;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    private final static long serialVersionUID = -1106803934400507868L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MapOther() {
    }

    /**
     * @param id MapEvent id
     * @param name MapEvent name
     * @param img place image
     * @param longitude place longitude
     * @param latitude place latitude
     * @param type MapEvent place type
     * @param info place information
     */
    public MapOther(Integer id, Integer type, String name, String info, String img, String latitude, String longitude) {
        super();
        this.id = id;
        this.type = type;
        this.name = name;
        this.info = info;
        this.img = img;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

        MapOther mapOther = (MapOther) o;

        if (id != null ? !id.equals(mapOther.id) : mapOther.id != null) return false;
        if (type != null ? !type.equals(mapOther.type) : mapOther.type != null) return false;
        if (name != null ? !name.equals(mapOther.name) : mapOther.name != null) return false;
        if (info != null ? !info.equals(mapOther.info) : mapOther.info != null) return false;
        if (img != null ? !img.equals(mapOther.img) : mapOther.img != null) return false;
        if (latitude != null ? !latitude.equals(mapOther.latitude) : mapOther.latitude != null)
            return false;
        return longitude != null ? longitude.equals(mapOther.longitude) : mapOther.longitude == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (info != null ? info.hashCode() : 0);
        result = 31 * result + (img != null ? img.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        return result;
    }
}
