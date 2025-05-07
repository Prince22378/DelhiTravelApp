// RouteEntity.kt
package com.example.delhitravelapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val routeId: String,
    val agencyId: String? = null,
    val routeShortName: String? = null,
    val routeLongName: String,
    val routeDesc: String? = null,
    val routeType: Int? = null,
    val routeUrl: String? = null,
    val routeColor: String? = null,
    val routeTextColor: String? = null,
    val routeSortOrder: Int? = null,
    val continuousPickup: Int? = null,
    val continuousDropOff: Int? = null
)
