// TripRepository.kt
package com.example.delhitravelapp.data

class TripRepository(private val dao: TripDao) {
    suspend fun insert(list: List<TripEntity>) = dao.insertAll(list)
    suspend fun getById(id: String) = dao.getById(id)
    suspend fun getAllTrips(): List<TripEntity> = dao.getAllTrips()
    suspend fun deleteAll() = dao.deleteAll()
}
