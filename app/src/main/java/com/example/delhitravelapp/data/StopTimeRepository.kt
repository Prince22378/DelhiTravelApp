// StopTimeRepository.kt
package com.example.delhitravelapp.data

class StopTimeRepository(private val dao: StopTimeDao) {
    suspend fun insert(list: List<StopTimeEntity>) = dao.insertAll(list)
    suspend fun getByTripAndStop(tripId: String, stopId: String) =
        dao.getByTripAndStop(tripId, stopId)
    suspend fun getRouteIdsForStop(stopId: String) =
        dao.getRouteIdsForStop(stopId)
    suspend fun allOrdered() = dao.getAllStopTimesOrdered() /// can remove
    suspend fun getAllStopTimesOrdered(): List<StopTimeEntity> = dao.getAllStopTimesOrdered()
    suspend fun getAllStopTimes(): List<StopTimeEntity> = dao.getAllStopTimes()
    suspend fun deleteAll() = dao.deleteAll()
}
