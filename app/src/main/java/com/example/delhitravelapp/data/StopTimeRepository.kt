// StopTimeRepository.kt
package com.example.delhitravelapp.data

class StopTimeRepository(private val dao: StopTimeDao) {
    suspend fun insert(list: List<StopTimeEntity>) = dao.insertAll(list)
    suspend fun getByTripAndStop(tripId: String, stopId: String) =
        dao.getByTripAndStop(tripId, stopId)
    suspend fun allOrdered() = dao.getAllStopTimesOrdered()
}
