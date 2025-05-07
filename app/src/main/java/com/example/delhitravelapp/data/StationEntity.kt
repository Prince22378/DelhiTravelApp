package com.example.delhitravelapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey val stopId: String,
    val stopName: String,
    val lat: Double, // Changed from stopLat to lat
    val lon: Double, // Changed from stopLon to lon
    val stopCode: String? = null,
    val stopDesc: String? = null,
    val zoneId: String? = null,
    val line: String,
    val interchangeAvailable: Int,
    val interchangeId: Int,
    val interchangeDesc: String,
    val stationLayout: String,
    val order: Int,
    val interchangingStationId: String,
    val segmentId: String,
    val split: Int
)
