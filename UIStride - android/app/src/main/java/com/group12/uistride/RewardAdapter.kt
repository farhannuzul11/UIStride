package com.group12.uistride

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.group12.uistride.model.Reward

class RewardAdapter(
    private val rewardsList: List<Reward>,
    private val onRewardClick: (Reward) -> Unit,
    private val totalPoints: Int
) : RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reward_item, parent, false)
        return RewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val reward = rewardsList[position]
        holder.bind(reward)

        holder.itemView.setOnClickListener {
            // Logika untuk redeem reward
            if (reward.pointsRequired <= totalPoints) {
                onRewardClick(reward)
            } else {
                Toast.makeText(holder.itemView.context, "Not enough points to redeem this reward.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = rewardsList.size

    inner class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rewardName: TextView = itemView.findViewById(R.id.rewardNameTextView)
        private val rewardPoints: TextView = itemView.findViewById(R.id.rewardPointsTextView)
        private val rewardDescription: TextView = itemView.findViewById(R.id.rewardDescriptionTextView)
        private val redeemButton: Button = itemView.findViewById(R.id.redeemButton) // Tambahkan referensi ke tombol

        fun bind(reward: Reward) {
            rewardName.text = reward.name
            rewardPoints.text = "${reward.pointsRequired} points required"
            rewardDescription.text = reward.description

            // Update status apakah pengguna memiliki cukup poin
            if (reward.pointsRequired > totalPoints) {
                rewardPoints.setTextColor(Color.RED)
                redeemButton.isEnabled = false // Nonaktifkan tombol jika poin tidak cukup
            } else {
                rewardPoints.setTextColor(Color.GREEN)
                redeemButton.isEnabled = true // Aktifkan tombol jika poin cukup
            }

            // Tangani klik tombol redeem
            redeemButton.setOnClickListener {
                if (reward.pointsRequired <= totalPoints) {
                    onRewardClick(reward) // Panggil callback saat tombol ditekan
                } else {
                    Toast.makeText(itemView.context, "Not enough points to redeem this reward.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}


