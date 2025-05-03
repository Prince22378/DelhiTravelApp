// RouteRepository.kt
package com.example.delhitravelapp.data

import kotlinx.coroutines.flow.Flow

class RouteRepository(private val dao: RouteDao) {
    suspend fun insert(list: List<RouteEntity>) = dao.insertAll(list)
    suspend fun getById(id: String) = dao.getById(id)
    fun allRoutes(): Flow<List<RouteEntity>> = dao.allRoutes()
}
