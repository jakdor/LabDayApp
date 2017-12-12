
package com.jakdor.labday.common.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Filtered timetable by users path
 */
public class Timetable implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("path_id")
    @Expose
    private Integer pathId;
    @SerializedName("event_id")
    @Expose
    private Integer eventId;
    private final static long serialVersionUID = 6175087089283085794L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Timetable() {
    }

    /**
     * @param id timetable id
     * @param eventId {@link Event} id
     * @param pathId {@link Path} id
     */
    public Timetable(Integer id, Integer pathId, Integer eventId) {
        super();
        this.id = id;
        this.pathId = pathId;
        this.eventId = eventId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPathId() {
        return pathId;
    }

    public void setPathId(Integer pathId) {
        this.pathId = pathId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

}
