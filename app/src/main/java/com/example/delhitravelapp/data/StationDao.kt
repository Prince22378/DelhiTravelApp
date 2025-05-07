// StationDao.kt
package com.example.delhitravelapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(stations: List<StationEntity>)

    @Query("SELECT * FROM stations WHERE stopId = :id LIMIT 1")
    suspend fun getById(id: String): StationEntity?

//    @Query("SELECT * FROM stations WHERE stopName LIKE :query ORDER BY stopName")
//    fun searchStations(query: String): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations ORDER BY stopName")
    suspend fun getAllStations(): List<StationEntity>

    @Query("DELETE FROM stations")
    suspend fun deleteAll()

    // Use LIKE with % for partial matching anywhere in the name
    @Query("SELECT * FROM stations WHERE stopName LIKE '%' || :query || '%' ORDER BY stopName")
    fun searchStations(query: String): Flow<List<StationEntity>>

    // New query to fetch stations by line (useful for route calculation)
    @Query("SELECT * FROM stations WHERE line = :line ORDER BY `order`")
    suspend fun getStationsByLine(line: String): List<StationEntity>

}
