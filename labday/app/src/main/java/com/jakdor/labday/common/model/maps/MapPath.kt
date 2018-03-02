package com.jakdor.labday.common.model.maps

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MapPath {

    @SerializedName("routes")
    @Expose
    var routes: List<Route>? = null
    @SerializedName("status")
    @Expose
    var status: String? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param status
     * @param routes
     */
    constructor(routes: List<Route>, status: String) : super() {
        this.routes = routes
        this.status = status
    }

}
