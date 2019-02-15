package com.example.geotag

import android.location.Location
import java.io.FileDescriptor
import java.io.Serializable
import java.lang.StringBuilder

class Tag constructor(location : Location, building : String, floor : Int, room : String, description: String) {
    val location = location
    val building = building
    val floor = floor
    val room = room
    val description = description


    override fun toString():String{
        val builder = StringBuilder()
        builder
            .append(description)
            .append(",")
            .append(location.latitude)
            .append(",")
            .append(location.longitude)
            .append(",")
            .append(location.altitude)
            .append(",")
            .append(building)
            .append(",")
            .append(floor)
            .append(",")
            .append(room)

        return builder.toString()
    }
}