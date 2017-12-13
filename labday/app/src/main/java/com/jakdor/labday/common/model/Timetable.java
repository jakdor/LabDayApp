
package com.jakdor.labday.common.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Timetable with {@link Event} time and ids, {@link Path} ids
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
    @SerializedName("time_start")
    @Expose
    private Integer timeStart;
    @SerializedName("time_end")
    @Expose
    private Integer timeEnd;
    private final static long serialVersionUID = -2119482767850416706L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Timetable() {
    }

    /**
     * @param id timetable id
     * @param timeStart {@link Event} start time in sec from unix epoch
     * @param timeEnd {@link Event} end time in sec from unix epoch
     * @param eventId {@link Event} id
     * @param pathId {@link Path} id
     */
    public Timetable(Integer id, Integer pathId, Integer eventId, Integer timeStart, Integer timeEnd) {
        super();
        this.id = id;
        this.pathId = pathId;
        this.eventId = eventId;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
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

}
