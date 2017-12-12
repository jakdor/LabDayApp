
package com.jakdor.labday.common.model;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Bundled API response
 */
public class AppData implements Serializable
{

    @SerializedName("events")
    @Expose
    private List<Event> events = null;
    @SerializedName("map_others")
    @Expose
    private List<MapOther> mapOthers = null;
    @SerializedName("paths")
    @Expose
    private List<Path> paths = null;
    @SerializedName("timetables")
    @Expose
    private List<Timetable> timetables = null;
    private final static long serialVersionUID = 8816216271836209839L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public AppData() {
    }

    /**
     * @param paths List of {@link Path}
     * @param events  List of {@link Event}
     * @param mapOthers List of {@link MapOther}
     * @param timetables List of {@link Timetable}
     */
    public AppData(List<Event> events, List<MapOther> mapOthers, List<Path> paths, List<Timetable> timetables) {
        super();
        this.events = events;
        this.mapOthers = mapOthers;
        this.paths = paths;
        this.timetables = timetables;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<MapOther> getMapOthers() {
        return mapOthers;
    }

    public void setMapOthers(List<MapOther> mapOthers) {
        this.mapOthers = mapOthers;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public List<Timetable> getTimetables() {
        return timetables;
    }

    public void setTimetables(List<Timetable> timetables) {
        this.timetables = timetables;
    }

}
