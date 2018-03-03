package com.jakdor.labday.common.model.maps

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Bounds {

    @SerializedName("northeast")
    @Expose
    var northeast: Northeast? = null
    @SerializedName("southwest")
    @Expose
    var southwest: Southwest? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param southwest
     * @param northeast
     */
    constructor(northeast: Northeast, southwest: Southwest) : super() {
        this.northeast = northeast
        this.southwest = southwest
    }

}
