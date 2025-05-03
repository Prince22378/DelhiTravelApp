package com.example.delhitravelapp.data

import android.content.Context
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
        // (Optional) clear existing tables:
        // val db = DatabaseModule.provideDatabase(ctx)
        // db.clearAllData()

        parseStops()
        parseRoutes()
        parseTrips()
        parseStopTimes()
    }

    private suspend fun parseStops() {
        ctx.assets.open("gfts/stops.txt").bufferedReader().useLines { lines ->
            lines.drop(1).forEach { ln ->
                val cols = ln.split(",")
                if (cols.size >= 6) {
                    val ent = StationEntity(
                        stopId   = cols[0],
                        stopCode = cols[1].ifBlank { null },
                        stopName = cols[2],
                        stopLat  = cols[4].toDoubleOrNull() ?: 0.0,
                        stopLon  = cols[5].toDoubleOrNull() ?: 0.0,
                        zoneId   = ""
                    )
                    stationRepo.insert(listOf(ent))
                }
            }
        }
    }

    private suspend fun parseRoutes() {
        ctx.assets.open("gfts/routes.txt").bufferedReader().useLines { lines ->
            lines.drop(1).forEach { ln ->
                val cols = ln.split(",")
                if (cols.size >= 6) {
                    val ent = RouteEntity(
                        routeId        = cols[0],
                        agencyId       = cols[1].ifBlank { "DMRC" },
                        routeShortName = cols[2].ifBlank { null },
                        routeLongName  = cols[3],
                        routeType      = cols[5].toIntOrNull() ?: 1
                    )
                    routeRepo.insert(listOf(ent))
                }
            }
        }
    }

    private suspend fun parseTrips() {
        ctx.assets.open("gfts/trips.txt").bufferedReader().useLines { lines ->
            lines.drop(1).forEach { ln ->
                val cols = ln.split(",")
                if (cols.size >= 8) {
                    val ent = TripEntity(
                        tripId    = cols[2],
                        routeId   = cols[0],
                        serviceId = cols[1],
                        shapeId   = cols[7].ifBlank { null }
                    )
                    tripRepo.insert(listOf(ent))
                }
            }
        }
    }

    private suspend fun parseStopTimes() {
        ctx.assets.open("gfts/stop_times.txt").bufferedReader().useLines { lines ->
            lines.drop(1).forEach { ln ->
                val cols = ln.split(",")
                if (cols.size >= 5) {
                    val ent = StopTimeEntity(
                        tripId       = cols[0],
                        stopId       = cols[3],
                        stopSequence = cols[4].toIntOrNull() ?: 0,
                        arrivalTime  = cols[1].ifBlank { null },
                        departureTime= cols[2].ifBlank { null }
                    )
                    stopTimeRepo.insert(listOf(ent))
                }
            }
        }
    }
}
