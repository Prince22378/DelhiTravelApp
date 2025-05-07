// TripEntity.kt
package com.example.delhitravelapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val tripId: String,
    val routeId: String,
    val serviceId: String? = null,
    val tripHeadsign: String? = null,
    val tripShortName: String? = null,
    val directionId: Int? = null,
    val blockId: String? = null,
    val shapeId: String? = null,
    val wheelchairAccessible: Int? = null,
    val bikesAllowed: Int? = null
)
