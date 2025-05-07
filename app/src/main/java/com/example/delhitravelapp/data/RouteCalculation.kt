//// RouteCalculation.kt
//package com.example.delhitravelapp.data
//
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import java.util.PriorityQueue
//
//data class Edge(
//    val fromStopId: String,
//    val toStopId:   String,
//    val routeId:    String
//)
//
//class RouteCalculation(private val stopTimeDao: StopTimeDao) {
//
//    suspend fun findPath(
//        startId: String,
//        endId:   String
//    ): List<Pair<String, String?>> = withContext(Dispatchers.IO) {
//        val all = stopTimeDao.getAllStopTimesOrdered()
//        val edges = mutableListOf<Edge>()
//
//        all.groupBy { it.tripId }.forEach { (_, stops) ->
//            stops.sortedBy { it.stopSequence }
//                .zipWithNext { a, b ->
//                    edges += Edge(a.stopId, b.stopId, a.tripId)
//                    edges += Edge(b.stopId, a.stopId, a.tripId)
//                }
//        }
//
//        val parent = mutableMapOf<String, Pair<String, Edge?>>()
//        val queue  = ArrayDeque<String>()
//        queue += startId
//        parent[startId] = startId to null
//
//        while (queue.isNotEmpty()) {
//            val cur = queue.removeFirst()
//            if (cur == endId) break
//            edges.filter { it.fromStopId == cur }.forEach { e ->
//                if (!parent.containsKey(e.toStopId)) {
//                    parent[e.toStopId] = cur to e
//                    queue += e.toStopId
//                }
//            }
//        }
//
//        if (!parent.containsKey(endId)) return@withContext emptyList()
//
//        val path = mutableListOf<Pair<String, String?>>()
//        var cur = endId
//        while (true) {
//            val (p, edge) = parent[cur]!!
//            path += cur to edge?.routeId
//            if (p == cur) break
//            cur = p
//        }
//        path.asReversed()
//    }
//}

////////////////////////////////
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
    data class Path(val stations: List<String>, val lines: List<String>)

    // Helper function to build the graph (shared between both methods)
    private suspend fun buildGraph(): Pair<List<Edge>, List<StationEntity>> {
        val allStations = stationDao.getAllStations()
        Log.d("RouteCalculation", "Total stations: ${allStations.size}")

        val allTrips = tripRepo.getAllTrips()
        Log.d("RouteCalculation", "Total trips: ${allTrips.size}")
        val tripToRoute = allTrips.associate { it.tripId to it.routeId }

        val allRoutes = routeRepo.getAllRoutes()
        Log.d("RouteCalculation", "Total routes: ${allRoutes.size}")
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

        allStations.filter { it.interchangeAvailable == 1 }.forEach { station ->
            val interchangeStationIds = station.interchangingStationId.split("-").map { it.trim() }
            interchangeStationIds.forEach { otherId ->
                if (otherId.isNotEmpty() && otherId != station.stopId) {
                    val otherStation = allStations.firstOrNull { it.stopId == otherId }
                    if (otherStation != null) {
                        edges += Edge(station.stopId, otherId, station.line)
                        edges += Edge(otherId, station.stopId, otherStation.line)
                        Log.d("RouteCalculation", "Added interchange edge: ${station.stopId} -> $otherId")
                    } else {
                        Log.w("RouteCalculation", "Interchange station $otherId not found for ${station.stopId}")
                    }
                }
            }
        }

        Log.d("RouteCalculation", "Total edges in graph: ${edges.size}")
        Log.d("RouteCalculation", "Graph edges: $edges")
        return edges to allStations
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
                val newDist = distances.getValue(current) + 1 // Cost is 1 per station
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
            return Path(listOf(endId), emptyList())
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

        return Path(pathStations, pathLines)
    }

    suspend fun findMinimumInterchangeRoute(startId: String, endId: String): Path {
        val (edges, _) = buildGraph()
        val graph = edges.groupBy { it.fromStopId }

        // Use a custom data class to track both the total cost and the previous line
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
                // Calculate cost: 1 for same-line travel, 100 for an interchange
                val isInterchange = currentLine != null && edge.line != currentLine
                val edgeCost = if (isInterchange) 100 else 1
                val newDist = distances.getValue(current) + edgeCost

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
            return Path(listOf(endId), emptyList())
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

        return Path(pathStations, pathLines)
    }
}