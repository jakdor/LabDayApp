package com.jakdor.labday.common.model.maps

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Leg {

    @SerializedName("distance")
    @Expose
    var distance: Distance? = null
    @SerializedName("duration")
    @Expose
    var duration: Duration? = null
    @SerializedName("end_location")
    @Expose
    var endLocation: EndLocation? = null
    @SerializedName("start_location")
    @Expose
    var startLocation: StartLocation? = null
    @SerializedName("steps")
    @Expose
    var steps: List<Step>? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param duration
     * @param distance
     * @param endLocation
     * @param startLocation
     * @param steps
     */
    constructor(distance: Distance, duration: Duration, endLocation: EndLocation, startLocation: StartLocation, steps: List<Step>) : super() {
        this.distance = distance
        this.duration = duration
        this.endLocation = endLocation
        this.startLocation = startLocation
        this.steps = steps
    }

}
