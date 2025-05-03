// RouteEntity.kt
package com.example.delhitravelapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val routeId: String,
    val agencyId: String,
    val routeShortName: String?,
    val routeLongName: String,
    val routeType: Int
)
