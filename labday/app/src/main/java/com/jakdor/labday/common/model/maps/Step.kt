package com.jakdor.labday.common.model.maps

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Step {

    @SerializedName("end_location")
    @Expose
    var endLocation: EndLocation? = null
    @SerializedName("start_location")
    @Expose
    var startLocation: StartLocation? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param endLocation
     * @param startLocation
     */
    constructor(endLocation: EndLocation, startLocation: StartLocation) : super() {
        this.endLocation = endLocation
        this.startLocation = startLocation
    }

}
