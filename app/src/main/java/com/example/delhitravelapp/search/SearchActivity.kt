package com.example.delhitravelapp.search
import androidx.core.widget.addTextChangedListener

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delhitravelapp.HomeActivity
import com.example.delhitravelapp.R
import com.example.delhitravelapp.data.DatabaseModule
import com.example.delhitravelapp.data.GtfsParser
import com.example.delhitravelapp.data.RouteRepository
import com.example.delhitravelapp.data.StationRepository
import com.example.delhitravelapp.data.StopTimeRepository
import com.example.delhitravelapp.data.TripRepository
import com.example.delhitravelapp.data.StationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchActivity : AppCompatActivity() {
    private lateinit var acStart: AutoCompleteTextView
    private lateinit var acEnd: AutoCompleteTextView
    private lateinit var btnSearchRoutes: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // view refs
        acStart = findViewById(R.id.acStartStation)
        acEnd = findViewById(R.id.acEndStation)
        btnSearchRoutes = findViewById(R.id.btnShowRoutes)

        // init database and repos
        val db = DatabaseModule.provideDatabase(this)
        val stationRepo = StationRepository(db.stationDao())
        val routeRepo = RouteRepository(db.routeDao())
        val stopTimeRepo = StopTimeRepository(db.stopTimeDao())
        val parser = GtfsParser(
            this,
            stationRepo,
            routeRepo,
            TripRepository(db.tripDao()),
            stopTimeRepo
        )

        // parse stops and load station names for suggestions
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                parser.parseStops()
            }
            val stations = stationRepo.searchStations("").first()
            val names = stations.map { it.stopName }
            val adapter = ArrayAdapter(
                this@SearchActivity,
                android.R.layout.simple_dropdown_item_1line,
                names
            )
            acStart.setAdapter(adapter)
            acEnd.setAdapter(adapter)
        }

        // handle Search Routes click
        btnSearchRoutes.setOnClickListener {
            val startName = acStart.text.toString()
            val endName = acEnd.text.toString()
            lifecycleScope.launch {
                val stationsAll = stationRepo.searchStations("").first()
                val start = stationsAll.find { it.stopName == startName }
                val end = stationsAll.find { it.stopName == endName }
                if (start == null || end == null) {
                    Toast.makeText(
                        this@SearchActivity,
                        "Please select valid stations",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                // fetch routes for each
                val startRoutes = withContext(Dispatchers.IO) {
                    stopTimeRepo.getRouteIdsForStop(start.stopId)
                }
                val endRoutes = withContext(Dispatchers.IO) {
                    stopTimeRepo.getRouteIdsForStop(end.stopId)
                }
                val common = startRoutes.intersect(endRoutes.toSet())
                val display = if (common.isNotEmpty()) common.toTypedArray()
                else arrayOf("No direct routes found")

                AlertDialog.Builder(this@SearchActivity)
                    .setTitle("Routes between ${start.stopName} and ${end.stopName}")
                    .setItems(display, null)
                    .show()
            }
        }
    }
}