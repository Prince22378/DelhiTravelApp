// RouteCalculation.kt
package com.example.delhitravelapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Edge(
    val fromStopId: String,
    val toStopId:   String,
    val routeId:    String
)

class RouteCalculation(private val stopTimeDao: StopTimeDao) {

    suspend fun findPath(
        startId: String,
        endId:   String
    ): List<Pair<String, String?>> = withContext(Dispatchers.IO) {
        val all = stopTimeDao.getAllStopTimesOrdered()
        val edges = mutableListOf<Edge>()

        all.groupBy { it.tripId }.forEach { (_, stops) ->
            stops.sortedBy { it.stopSequence }
                .zipWithNext { a, b ->
                    edges += Edge(a.stopId, b.stopId, a.tripId)
                    edges += Edge(b.stopId, a.stopId, a.tripId)
                }
        }

        val parent = mutableMapOf<String, Pair<String, Edge?>>()
        val queue  = ArrayDeque<String>()
        queue += startId
        parent[startId] = startId to null

        while (queue.isNotEmpty()) {
            val cur = queue.removeFirst()
            if (cur == endId) break
            edges.filter { it.fromStopId == cur }.forEach { e ->
                if (!parent.containsKey(e.toStopId)) {
                    parent[e.toStopId] = cur to e
                    queue += e.toStopId
                }
            }
        }

        if (!parent.containsKey(endId)) return@withContext emptyList()

        val path = mutableListOf<Pair<String, String?>>()
        var cur = endId
        while (true) {
            val (p, edge) = parent[cur]!!
            path += cur to edge?.routeId
            if (p == cur) break
            cur = p
        }
        path.asReversed()
    }
}
