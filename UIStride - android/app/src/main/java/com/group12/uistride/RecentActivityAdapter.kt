package com.group12.uistride

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class RecentActivity(
    val date: String,
    val distance: String,
    val time: String,
    val speed: String
)

class RecentActivityAdapter(private val activityList: List<RecentActivity>) :
    RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val distanceTextView: TextView = itemView.findViewById(R.id.distanceTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val speedTextView: TextView = itemView.findViewById(R.id.speedTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_recent_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activityList[position]
        holder.dateTextView.text = activity.date
        holder.distanceTextView.text = activity.distance
        holder.timeTextView.text = activity.time
        holder.speedTextView.text = activity.speed
    }

    override fun getItemCount() = activityList.size
}
