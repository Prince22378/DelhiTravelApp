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
    val arrivalTime: String?,
    val departureTime: String?
)
