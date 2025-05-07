//package com.example.delhitravelapp.data
//
//import android.content.Context
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import com.opencsv.CSVReader
//import kotlinx.coroutines.withContext
//import java.io.InputStreamReader
//
//class GtfsParser(
//    private val ctx: Context,
//    private val stationRepo: StationRepository,
//    private val routeRepo: RouteRepository,
//    private val tripRepo: TripRepository,
//    private val stopTimeRepo: StopTimeRepository
//) {
//    suspend fun parseAll() = withContext(Dispatchers.IO) {
//        // (Optional) clear existing tables:
//        // val db = DatabaseModule.provideDatabase(ctx)
//        // db.clearAllData()
//
//        parseStops()
//        parseRoutes()
//        parseTrips()
//        parseStopTimes()
//    }
//
//    suspend fun parseStops() {
//        ctx.assets.open("gfts/stops.txt").bufferedReader().useLines { lines ->
//            lines.drop(1).forEach { ln ->
//                val cols = ln.split(",")
//                if (cols.size >= 6) {
//                    val ent = StationEntity(
//                        stopId   = cols[0],
//                        stopCode = cols[1].ifBlank { null },
//                        stopName = cols[2],
//                        stopLat  = cols[4].toDoubleOrNull() ?: 0.0,
//                        stopLon  = cols[5].toDoubleOrNull() ?: 0.0,
//                        zoneId   = ""
//                    )
//                    stationRepo.insert(listOf(ent))
//                }
//            }
//        }
//    }
//
//    suspend fun parseRoutes() {
//        ctx.assets.open("gfts/routes.txt").bufferedReader().useLines { lines ->
//            lines.drop(1).forEach { ln ->
//                val cols = ln.split(",")
//                if (cols.size >= 6) {
//                    val ent = RouteEntity(
//                        routeId        = cols[0],
//                        agencyId       = cols[1].ifBlank { "DMRC" },
//                        routeShortName = cols[2].ifBlank { null },
//                        routeLongName  = cols[3],
//                        routeType      = cols[5].toIntOrNull() ?: 1
//                    )
//                    routeRepo.insert(listOf(ent))
//                }
//            }
//        }
//    }
//
//    suspend fun parseTrips() {
//        ctx.assets.open("gfts/trips.txt").bufferedReader().useLines { lines ->
//            lines.drop(1).forEach { ln ->
//                val cols = ln.split(",")
//                if (cols.size >= 8) {
//                    val ent = TripEntity(
//                        tripId    = cols[2],
//                        routeId   = cols[0],
//                        serviceId = cols[1],
//                        shapeId   = cols[7].ifBlank { null }
//                    )
//                    tripRepo.insert(listOf(ent))
//                }
//            }
//        }
//    }
//
//    suspend fun parseStopTimes() {
//        ctx.assets.open("gfts/stop_times.txt").bufferedReader().useLines { lines ->
//            lines.drop(1).forEach { ln ->
//                val cols = ln.split(",")
//                if (cols.size >= 5) {
//                    val ent = StopTimeEntity(
//                        tripId       = cols[0],
//                        stopId       = cols[3],
//                        stopSequence = cols[4].toIntOrNull() ?: 0,
//                        arrivalTime  = cols[1].ifBlank { null },
//                        departureTime= cols[2].ifBlank { null }
//                    )
//                    stopTimeRepo.insert(listOf(ent))
//                }
//            }
//        }
//    }
//}

///////////////////////////
package com.example.delhitravelapp.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GtfsParser(
    private val ctx: Context,
    private val stationRepo: StationRepository,
    private val routeRepo: RouteRepository,
    private val tripRepo: TripRepository,
    private val stopTimeRepo: StopTimeRepository
) {
    suspend fun parseAll() = withContext(Dispatchers.IO) {
        // Clear existing data
        stationRepo.deleteAll()
        routeRepo.deleteAll()
        tripRepo.deleteAll()
        stopTimeRepo.deleteAll()

        parseStops()
        parseStationsAdditional()
        parseRoutes()
        parseTrips()
        parseStopTimes()

        Log.d("GtfsParser", "Parsing completed. Stations: ${stationRepo.getAllStations().size}, Routes: ${routeRepo.getAllRoutes().size}, Trips: ${tripRepo.getAllTrips().size}, StopTimes: ${stopTimeRepo.getAllStopTimes().size}")
    }

    suspend fun parseStops() {
        ctx.assets.open("gfts/stops.txt").bufferedReader().useLines { lines ->
            val stations = lines.drop(1).mapNotNull { ln ->
                val cols = ln.split(",")
                if (cols.size >= 6) {
                    val ent = StationEntity(
                        stopId = cols[0].trim(),
                        stopName = cols[2].trim(),
                        lat = cols[4].trim().toDoubleOrNull() ?: return@mapNotNull null,
                        lon = cols[5].trim().toDoubleOrNull() ?: return@mapNotNull null,
                        stopCode = cols[1].ifBlank { null },
                        stopDesc = if (cols.size > 3) cols[3].ifBlank { null } else null,
                        zoneId = null,
                        line = "", // Will be updated by f.txt
                        interchangeAvailable = 0,
                        interchangeId = 0,
                        interchangeDesc = "",
                        stationLayout = "",
                        order = 0,
                        interchangingStationId = "",
                        segmentId = "",
                        split = 0
                    )
                    Log.d("GtfsParser", "Parsed stop: ${ent.stopId} - ${ent.stopName}")
                    ent
                } else {
                    Log.w("GtfsParser", "Invalid stop format: $ln")
                    null
                }
            }.toList()
            stationRepo.insert(stations)
        }
    }

    suspend fun parseStationsAdditional() {
        val existingStations = stationRepo.getAllStations().associateBy { it.stopId }.toMutableMap()
        ctx.assets.open("gfts/f.txt").bufferedReader().useLines { lines ->
            val updatedStations = lines.drop(1).mapNotNull { ln ->
                val cols = ln.split(",")
                if (cols.size >= 11) {
                    val stopId = cols[0].trim()
                    val existing = existingStations[stopId]
                    if (existing != null) {
                        val updated = existing.copy(
                            line = cols[2].trim().lowercase().replaceFirstChar { it.uppercase() }, // Normalize line name
                            interchangeAvailable = cols[3].trim().toIntOrNull() ?: 0,
                            interchangeId = cols[4].trim().toIntOrNull() ?: 0,
                            interchangeDesc = cols[5].trim(),
                            stationLayout = cols[6].trim(),
                            order = cols[7].trim().toIntOrNull() ?: 0,
                            interchangingStationId = cols[8].trim(),
                            segmentId = cols[9].trim(),
                            split = cols[10].trim().toIntOrNull() ?: 0
                        )
                        Log.d("GtfsParser", "Updated station $stopId: ${updated.line}, Interchange: ${updated.interchangeAvailable}")
                        updated
                    } else {
                        Log.w("GtfsParser", "Station not found in stops.txt for stopId: $stopId")
                        null
                    }
                } else {
                    Log.w("GtfsParser", "Invalid f.txt format: $ln")
                    null
                }
            }.toList()
            stationRepo.insert(updatedStations)
        }
    }

    suspend fun parseRoutes() {
        ctx.assets.open("gfts/routes.txt").bufferedReader().useLines { lines ->
            val routes = lines.drop(1).mapNotNull { ln ->
                val cols = ln.split(",")
                if (cols.size >= 12) {
                    val ent = RouteEntity(
                        routeId = cols[0].trim(),
                        agencyId = cols[1].ifBlank { null },
                        routeShortName = cols[2].ifBlank { null },
                        routeLongName = cols[3].trim(),
                        routeDesc = cols[4].ifBlank { null },
                        routeType = cols[5].trim().toIntOrNull(),
                        routeUrl = cols[6].ifBlank { null },
                        routeColor = cols[7].ifBlank { null },
                        routeTextColor = cols[8].ifBlank { null },
                        routeSortOrder = cols[9].trim().toIntOrNull(),
                        continuousPickup = cols[10].trim().toIntOrNull(),
                        continuousDropOff = cols[11].trim().toIntOrNull()
                    )
                    Log.d("GtfsParser", "Parsed route: ${ent.routeId} - ${ent.routeLongName}")
                    ent
                } else {
                    Log.w("GtfsParser", "Invalid route format: $ln")
                    null
                }
            }.toList()
            routeRepo.insert(routes)
        }
    }

    suspend fun parseTrips() {
        ctx.assets.open("gfts/trips.txt").bufferedReader().useLines { lines ->
            val trips = lines.drop(1).mapNotNull { ln ->
                val cols = ln.split(",")
                if (cols.size >= 10) {
                    val ent = TripEntity(
                        tripId = cols[2].trim(),
                        routeId = cols[0].trim(),
                        serviceId = cols[1].ifBlank { null },
                        tripHeadsign = cols[3].ifBlank { null },
                        tripShortName = cols[4].ifBlank { null },
                        directionId = cols[5].trim().toIntOrNull(),
                        blockId = cols[6].ifBlank { null },
                        shapeId = cols[7].ifBlank { null },
                        wheelchairAccessible = cols[8].trim().toIntOrNull(),
                        bikesAllowed = cols[9].trim().toIntOrNull()
                    )
                    Log.d("GtfsParser", "Parsed trip: ${ent.tripId} for route ${ent.routeId}")
                    ent
                } else {
                    Log.w("GtfsParser", "Invalid trip format: $ln")
                    null
                }
            }.toList()
            tripRepo.insert(trips)
        }
    }

    suspend fun parseStopTimes() {
        ctx.assets.open("gfts/stop_times.txt").bufferedReader().useLines { lines ->
            val stopTimes = lines.drop(1).mapNotNull { ln ->
                val cols = ln.split(",")
                if (cols.size >= 12) {
                    val ent = StopTimeEntity(
                        tripId = cols[0].trim(),
                        stopId = cols[3].trim(),
                        stopSequence = cols[4].trim().toIntOrNull() ?: return@mapNotNull null,
                        arrivalTime = cols[1].ifBlank { null },
                        departureTime = cols[2].ifBlank { null },
                        stopHeadsign = cols[5].ifBlank { null },
                        pickupType = cols[6].trim().toIntOrNull(),
                        dropOffType = cols[7].trim().toIntOrNull(),
                        shapeDistTraveled = cols[8].trim().toDoubleOrNull(),
                        timepoint = cols[9].trim().toIntOrNull(),
                        continuousPickup = cols[10].trim().toIntOrNull(),
                        continuousDropOff = cols[11].trim().toIntOrNull()
                    )
                    Log.d("GtfsParser", "Parsed stop time: ${ent.tripId} - ${ent.stopId} - ${ent.stopSequence}")
                    ent
                } else {
                    Log.w("GtfsParser", "Invalid stop time format: $ln")
                    null
                }
            }.toList()
            stopTimeRepo.insert(stopTimes)
        }
    }
}