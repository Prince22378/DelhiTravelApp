// StationRepository.kt
package com.example.delhitravelapp.data

import kotlinx.coroutines.flow.Flow

class StationRepository(private val dao: StationDao) {
    suspend fun insert(list: List<StationEntity>) = dao.insertAll(list)
    suspend fun getById(id: String) = dao.getById(id)
    fun searchStations(query: String): Flow<List<StationEntity>> =
        dao.searchStations(query)
}
