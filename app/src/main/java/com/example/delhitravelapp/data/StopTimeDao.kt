// StopTimeDao.kt
package com.example.delhitravelapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StopTimeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(times: List<StopTimeEntity>)

    @Query("SELECT * FROM stop_times WHERE tripId = :tripId AND stopId = :stopId LIMIT 1")
    suspend fun getByTripAndStop(tripId: String, stopId: String): StopTimeEntity?

    @Query("SELECT * FROM stop_times ORDER BY tripId, stopSequence")
    suspend fun getAllStopTimesOrdered(): List<StopTimeEntity>

    @Query("DELETE FROM stop_times")
    suspend fun deleteAll()
}
