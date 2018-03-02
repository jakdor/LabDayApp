package com.jakdor.labday.common.model.maps

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Duration {

    @SerializedName("text")
    @Expose
    var text: String? = null
    @SerializedName("value")
    @Expose
    var value: Int? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param text
     * @param value
     */
    constructor(text: String, value: Int?) : super() {
        this.text = text
        this.value = value
    }

}
