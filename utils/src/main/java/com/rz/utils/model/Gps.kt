package com.rz.utils.model

/**
 * @author Vondear
 * @date 2017/6/19
 */

class Gps {

    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()

    constructor()

    constructor(longitude: Double, mLatitude: Double) {
        latitude = mLatitude
        this.longitude = longitude
    }

    override fun toString(): String {
        return longitude.toString() + "," + latitude
    }
}
