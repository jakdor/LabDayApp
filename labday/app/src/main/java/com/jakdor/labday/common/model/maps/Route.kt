package com.jakdor.labday.common.model.maps

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Route {

    @SerializedName("bounds")
    @Expose
    var bounds: Bounds? = null
    @SerializedName("legs")
    @Expose
    var legs: List<Leg>? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param bounds
     * @param legs
     */
    constructor(bounds: Bounds, legs: List<Leg>) : super() {
        this.bounds = bounds
        this.legs = legs
    }

}
