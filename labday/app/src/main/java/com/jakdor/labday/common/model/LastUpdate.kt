package com.jakdor.labday.common.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * last-update response
 */
class LastUpdate {

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    /**
     * No args constructor for use in serialization
     */
    constructor() {}

    /**
     * @param updatedAt last update string
     */
    constructor(updatedAt: String) : super() {
        this.updatedAt = updatedAt
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastUpdate

        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        return updatedAt?.hashCode() ?: 0
    }

}