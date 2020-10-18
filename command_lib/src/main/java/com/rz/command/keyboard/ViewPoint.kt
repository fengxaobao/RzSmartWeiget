package com.rz.command.keyboard

/**
 * @author Valence
 * @version 1.0
 * @since 2017/09/13
 */
class ViewPoint {
    var coo_x: Int
    var coo_y: Int
    var coo_time: Long

    constructor() {
        coo_x = 0
        coo_y = 0
        coo_time = 0
    }

    constructor(coo_x: Int, coo_y: Int, coo_time: Long) {
        this.coo_x = coo_x
        this.coo_y = coo_y
        this.coo_time = coo_time
    }

    fun clearPoint() {
        coo_x = 0
        coo_y = 0
        coo_time = 0
    }
}