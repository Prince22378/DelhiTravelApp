package com.example.delhitravelapp.data

import android.util.Log
import java.util.PriorityQueue

data class Edge(val fromStopId: String, val toStopId: String, val line: String)

class RouteCalculation(
    private val stationDao: StationDao,
    private val stopTimeDao: StopTimeDao,
    private val tripRepo: TripRepository,
    private val routeRepo: RouteRepository
) {
    data class Path(val stations: List<String>, val lines: List<String>, val arrivalTimes: List<String>)

    private suspend fun buildGraph(): Pair<List<Edge>, List<StationEntity>> {
        val allStations = stationDao.getAllStations()
        Log.d("RouteCalculation", "Total stations: ${allStations.size}")
        if (allStations.isEmpty()) {
            Log.e("RouteCalculation", "No stations found in database. Cannot build graph.")
        }

        val allTrips = tripRepo.getAllTrips()
        Log.d("RouteCalculation", "Total trips: ${allTrips.size}")
        if (allTrips.isEmpty()) {
            Log.e("RouteCalculation", "No trips found in database. Cannot build graph.")
        }
        val tripToRoute = allTrips.associate { it.tripId to it.routeId }

        val allRoutes = routeRepo.getAllRoutes()
        Log.d("RouteCalculation", "Total routes: ${allRoutes.size}")
        if (allRoutes.isEmpty()) {
            Log.e("RouteCalculation", "No routes found in database. Cannot build graph.")
        }
        val routeToLine = allRoutes.associate { route ->
            val lineName = if (route.routeLongName.contains("_")) {
                route.routeLongName.split("_")[0].lowercase().replaceFirstChar { it.uppercase() }
            } else {
                route.routeLongName.lowercase().replaceFirstChar { it.uppercase() }
            }
            Log.d("RouteCalculation", "Route ${route.routeId} mapped to line: $lineName")
            route.routeId to lineName
        }

        val allStopTimes = stopTimeDao.getAllStopTimesOrdered()
        Log.d("RouteCalculation", "Total stop times: ${allStopTimes.size}")
        if (allStopTimes.isEmpty()) {
            Log.e("RouteCalculation", "No stop times found in database. Graph will lack edges between consecutive stops.")
        }

        val edges = mutableListOf<Edge>()
        for ((tripId, stops) in allStopTimes.groupBy { it.tripId }) {
            val routeId = tripToRoute[tripId] ?: continue
            val routeLine = routeToLine[routeId] ?: continue
            Log.d("RouteCalculation", "Processing trip $tripId with route $routeId on line $routeLine")
            stops.sortedBy { it.stopSequence }.zipWithNext { a, b ->
                edges += Edge(a.stopId, b.stopId, routeLine)
                edges += Edge(b.stopId, a.stopId, routeLine)
                Log.d("RouteCalculation", "Added edge: ${a.stopId} -> ${b.stopId} on $routeLine")
            }
        }

        val interchangeStations = allStations.filter { it.interchangeAvailable == 1 }
        Log.d("RouteCalculation", "Total interchange stations: ${interchangeStations.size}")
        interchangeStations.forEach { station ->
            Log.d("RouteCalculation", "Processing interchange for station: ${station.stopId}, interchangeAvailable: ${station.interchangeAvailable}, interchangingStationId: ${station.interchangingStationId}")
            val interchangeStationIds = station.interchangingStationId.split("-").map { it.trim() }
            interchangeStationIds.forEach { otherId ->
                if (otherId.isNotEmpty() && otherId != station.stopId) {
                    val otherStation = allStations.firstOrNull { it.stopId == otherId }
                    if (otherStation != null) {
                        edges += Edge(station.stopId, otherId, station.line)
                        edges += Edge(otherId, station.stopId, otherStation.line)
                        Log.d("RouteCalculation", "Added interchange edge: ${station.stopId} -> $otherId (From ${station.line} to ${otherStation.line})")
                    } else {
                        Log.w("RouteCalculation", "Interchange station $otherId not found for ${station.stopId}")
                    }
                } else {
                    Log.d("RouteCalculation", "Skipping invalid interchange ID: $otherId for station ${station.stopId}")
                }
            }
        }

        Log.d("RouteCalculation", "Total edges in graph: ${edges.size}")
        Log.d("RouteCalculation", "Graph edges: $edges")
        if (edges.isEmpty()) {
            Log.e("RouteCalculation", "Graph has no edges. Route calculation will fail.")
        }
        return edges to allStations
    }

    private suspend fun getArrivalTimes(pathStations: List<String>, pathLines: List<String>): List<String> {
        val allStopTimes = stopTimeDao.getAllStopTimesOrdered()
        val stopTimesByTripId = allStopTimes.groupBy { it.tripId }
        val allTrips = tripRepo.getAllTrips()
        val tripToRoute = allTrips.associate { it.tripId to it.routeId }
        val allRoutes = routeRepo.getAllRoutes()
        val routeToLine = allRoutes.associate { route ->
            val lineName = if (route.routeLongName.contains("_")) {
                route.routeLongName.split("_")[0].lowercase().replaceFirstChar { it.uppercase() }
            } else {
                route.routeLongName.lowercase().replaceFirstChar { it.uppercase() }
            }
            route.routeId to lineName
        }

        val arrivalTimes = mutableListOf<String>()
        var currentTripId: String? = null
        var currentLineIndex = 0

        for (i in pathStations.indices) {
            val stopId = pathStations[i]
            val line = if (i == 0) pathLines.getOrNull(0) ?: "" else pathLines[i - 1]

            if (i == 0 || (i > 0 && pathLines[i - 1] != pathLines.getOrElse(i - 2) { "" })) {
                currentTripId = null
                currentLineIndex = i
            }

            if (currentTripId == null) {
                for ((tripId, stopTimes) in stopTimesByTripId) {
                    val routeId = tripToRoute[tripId] ?: continue
                    val routeLine = routeToLine[routeId] ?: continue
                    if (routeLine == line && stopTimes.any { it.stopId == stopId }) {
                        currentTripId = tripId
                        break
                    }
                }
            }

            val stopTimes = stopTimesByTripId[currentTripId] ?: emptyList()
            val matchingStopTime = stopTimes.firstOrNull { it.stopId == stopId }
            arrivalTimes.add(matchingStopTime?.arrivalTime ?: "N/A")
        }

        Log.d("RouteCalculation", "Arrival times for path $pathStations: $arrivalTimes")
        return arrivalTimes
    }

    suspend fun findShortestRoute(startId: String, endId: String): Path {
        val (edges, _) = buildGraph()
        val graph = edges.groupBy { it.fromStopId }

        val distances = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
        val previous = mutableMapOf<String, String?>()
        val previousLine = mutableMapOf<String, String>()
        val queue = PriorityQueue<Pair<String, Int>>(compareBy { it.second })
        val visited = mutableSetOf<String>()

        distances[startId] = 0
        previous[startId] = null
        queue.add(startId to 0)

        while (queue.isNotEmpty()) {
            val (current, dist) = queue.remove()
            if (current == endId) break
            if (current in visited) continue
            visited.add(current)

            val neighbors = graph[current] ?: continue
            for (edge in neighbors) {
                if (edge.toStopId in visited) continue
                val newDist = distances.getValue(current) + 1
                if (newDist < distances.getValue(edge.toStopId)) {
                    distances[edge.toStopId] = newDist
                    previous[edge.toStopId] = current
                    previousLine[edge.toStopId] = edge.line
                    queue.add(edge.toStopId to newDist)
                }
            }
        }

        Log.d("RouteCalculation", "Distances (Shortest): $distances")
        Log.d("RouteCalculation", "Previous (Shortest): $previous")

        if (previous[endId] == null && startId != endId) {
            Log.w("RouteCalculation", "No path found from $startId to $endId (Shortest)")
            return Path(listOf(endId), emptyList(), emptyList())
        }

        val pathStations = mutableListOf<String>()
        val pathLines = mutableListOf<String>()
        var current: String? = endId
        while (current != null) {
            pathStations.add(0, current)
            val prev = previous[current]
            if (prev != null) {
                pathLines.add(0, previousLine[current] ?: "")
            }
            Log.d("RouteCalculation", "Current (Shortest): $current, Previous: $prev")
            current = prev
        }

        Log.d("RouteCalculation", "Final path stations (Shortest): $pathStations")
        Log.d("RouteCalculation", "Final path lines (Shortest): $pathLines")

        val arrivalTimes = getArrivalTimes(pathStations, pathLines)
        return Path(pathStations, pathLines, arrivalTimes)
    }

    suspend fun findMinimumInterchangeRoute(startId: String, endId: String): Path {
        val (edges, _) = buildGraph()
        val graph = edges.groupBy { it.fromStopId }

        data class Node(val stopId: String, val cost: Int, val prevLine: String?)

        val distances = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
        val previous = mutableMapOf<String, String?>()
        val previousLine = mutableMapOf<String, String>()
        val queue = PriorityQueue<Node>(compareBy { it.cost })
        val visited = mutableSetOf<String>()

        distances[startId] = 0
        previous[startId] = null
        queue.add(Node(startId, 0, null))

        while (queue.isNotEmpty()) {
            val (current, currentCost, currentLine) = queue.remove()
            if (current == endId) break
            if (current in visited) continue
            visited.add(current)

            val neighbors = graph[current] ?: continue
            for (edge in neighbors) {
                if (edge.toStopId in visited) continue
                val isInterchange = currentLine != null && edge.line != currentLine
                val edgeCost = if (isInterchange) 100 else 1
                val newDist = distances.getValue(current) + edgeCost

                Log.d("RouteCalculation", "Edge: $current -> ${edge.toStopId}, Prev Line: $currentLine, Current Line: ${edge.line}, Is Interchange: $isInterchange, Edge Cost: $edgeCost, New Dist: $newDist")

                if (newDist < distances.getValue(edge.toStopId)) {
                    distances[edge.toStopId] = newDist
                    previous[edge.toStopId] = current
                    previousLine[edge.toStopId] = edge.line
                    queue.add(Node(edge.toStopId, newDist, edge.line))
                }
            }
        }

        Log.d("RouteCalculation", "Distances (Min Interchange): $distances")
        Log.d("RouteCalculation", "Previous (Min Interchange): $previous")

        if (previous[endId] == null && startId != endId) {
            Log.w("RouteCalculation", "No path found from $startId to $endId (Min Interchange)")
            return Path(listOf(endId), emptyList(), emptyList())
        }

        val pathStations = mutableListOf<String>()
        val pathLines = mutableListOf<String>()
        var current: String? = endId
        while (current != null) {
            pathStations.add(0, current)
            val prev = previous[current]
            if (prev != null) {
                pathLines.add(0, previousLine[current] ?: "")
            }
            Log.d("RouteCalculation", "Current (Min Interchange): $current, Previous: $prev")
            current = prev
        }

        Log.d("RouteCalculation", "Final path stations (Min Interchange): $pathStations")
        Log.d("RouteCalculation", "Final path lines (Min Interchange): $pathLines")

        val arrivalTimes = getArrivalTimes(pathStations, pathLines)
        return Path(pathStations, pathLines, arrivalTimes)
    }
}