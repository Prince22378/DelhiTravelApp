package com.example.delhitravelapp.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.RecyclerView
import com.example.delhitravelapp.R
import com.example.delhitravelapp.data.StationEntity

class StopAdapter(
    private var stops: List<StationEntity>,
    private val onClick: (StationEntity) -> Unit
) : RecyclerView.Adapter<StopAdapter.VH>() {
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.tvStopName)
        fun bind(entity: StationEntity) {
            name.text = entity.stopName
            itemView.setOnClickListener { onClick(entity) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stop, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(stops[position])

    override fun getItemCount() = stops.size

    fun updateList(newStops: List<StationEntity>) {
        stops = newStops
        notifyDataSetChanged()
    }
}