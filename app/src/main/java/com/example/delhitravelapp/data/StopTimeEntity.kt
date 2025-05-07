// StopTimeEntity.kt
package com.example.delhitravelapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stop_times")
data class StopTimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: String,
    val stopId: String,
    val stopSequence: Int,
    val arrivalTime: String? = null,
    val departureTime: String? = null,
    val stopHeadsign: String? = null,
    val pickupType: Int? = null,
    val dropOffType: Int? = null,
    val shapeDistTraveled: Double? = null,
    val timepoint: Int? = null,
    val continuousPickup: Int? = null,
    val continuousDropOff: Int? = null
)
