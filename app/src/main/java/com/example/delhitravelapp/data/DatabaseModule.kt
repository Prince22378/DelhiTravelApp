// DatabaseModule.kt
package com.example.delhitravelapp.data

import android.content.Context

object DatabaseModule {
    private var db: AppDatabase? = null
    fun provideDatabase(ctx: Context): AppDatabase =
        db ?: AppDatabase.getInstance(ctx).also { db = it }

    fun provideStationRepo(ctx: Context) =
        StationRepository(provideDatabase(ctx).stationDao())

    fun provideRouteRepo(ctx: Context) =
        RouteRepository(provideDatabase(ctx).routeDao())

    fun provideTripRepo(ctx: Context) =
        TripRepository(provideDatabase(ctx).tripDao())

    fun provideStopTimeRepo(ctx: Context) =
        StopTimeRepository(provideDatabase(ctx).stopTimeDao())
}
