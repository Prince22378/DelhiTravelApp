// StationRepository.kt
package com.example.delhitravelapp.data

import kotlinx.coroutines.flow.Flow

class StationRepository(private val dao: StationDao) {
    suspend fun insert(list: List<StationEntity>) = dao.insertAll(list)
    suspend fun getById(id: String) = dao.getById(id)
    suspend fun getAllStations(): List<StationEntity> = dao.getAllStations()
    fun searchStations(query: String): Flow<List<StationEntity>> = dao.searchStations(query)
    suspend fun getStationsByLine(line: String): List<StationEntity> = dao.getStationsByLine(line)
    suspend fun deleteAll() = dao.deleteAll()
}
