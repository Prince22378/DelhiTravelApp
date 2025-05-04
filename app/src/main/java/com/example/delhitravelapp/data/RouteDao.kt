// RouteDao.kt
package com.example.delhitravelapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(routes: List<RouteEntity>)

    @Query("SELECT * FROM routes WHERE routeId = :id LIMIT 1")
    suspend fun getById(id: String): RouteEntity?

    // example: fetch all routes for display
    @Query("SELECT * FROM routes ORDER BY routeShortName")
    fun allRoutes(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE routeId IN(:ids)")
    suspend fun getRoutesByIds(ids: List<String>): List<RouteEntity>

    @Query("DELETE FROM routes")
    suspend fun deleteAll()
}
