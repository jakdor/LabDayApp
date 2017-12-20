
package com.jakdor.labday.common.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Path implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("info")
    @Expose
    private String info;
    @SerializedName("active")
    @Expose
    private Integer active;
    private final static long serialVersionUID = 1552605528728563098L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Path() {
    }

    /**
     * 
     * @param id
     * @param name
     * @param active
     * @param info
     */
    public Path(Integer id, String name, String info, Integer active) {
        super();
        this.id = id;
        this.name = name;
        this.info = info;
        this.active = active;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        if (id != null ? !id.equals(path.id) : path.id != null) return false;
        if (name != null ? !name.equals(path.name) : path.name != null) return false;
        if (info != null ? !info.equals(path.info) : path.info != null) return false;
        return active != null ? active.equals(path.active) : path.active == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (info != null ? info.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        return result;
    }
}
