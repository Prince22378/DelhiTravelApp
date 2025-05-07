// TripDao.kt
package com.example.delhitravelapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(trips: List<TripEntity>)

    @Query("SELECT * FROM trips WHERE tripId = :id LIMIT 1")
    suspend fun getById(id: String): TripEntity?

    @Query("DELETE FROM trips")
    suspend fun deleteAll()
    // Add this new query
    @Query("SELECT * FROM trips")
    suspend fun getAllTrips(): List<TripEntity>
}
