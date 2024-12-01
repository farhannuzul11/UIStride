package com.group12.uistride

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group12.uistride.model.Activity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ActivityAdapter(private val activities: List<Activity>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        holder.bind(activity)
    }

    override fun getItemCount(): Int = activities.size

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val startTimeTextView: TextView = itemView.findViewById(R.id.startTimeTextView)
        private val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTextView)
        private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        private val distanceTextView: TextView = itemView.findViewById(R.id.distanceTextView)
        private val stepsTextView: TextView = itemView.findViewById(R.id.stepsTextView)

        fun bind(activity: Activity) {
            try {
                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormatter = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a", Locale.getDefault())

                val startTime = LocalDateTime.parse(activity.startTime, inputFormatter)
                val endTime = LocalDateTime.parse(activity.endTime, inputFormatter)

                startTimeTextView.text = "Start: ${startTime.format(outputFormatter)}"
                endTimeTextView.text = "End: ${endTime.format(outputFormatter)}"
            } catch (e: Exception) {
                startTimeTextView.text = "Start: Invalid date"
                endTimeTextView.text = "End: Invalid date"
            }

            durationTextView.text = "Duration: ${activity.duration}"
            distanceTextView.text = "Distance: %.2f km".format(activity.distance)
            stepsTextView.text = "Steps: ${activity.steps}"
        }
    }
}
