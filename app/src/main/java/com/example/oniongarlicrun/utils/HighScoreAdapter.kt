package com.example.oniongarlicrun.utils
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oniongarlicrun.HighScore
import com.example.oniongarlicrun.R
import androidx.core.graphics.toColorInt

class HighScoreAdapter(
    private val scores: List<HighScore>,
    private val onMapClick: (lat: Double, lon: Double) -> Unit
) : RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    class HighScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.item_name)
        val score: TextView = itemView.findViewById(R.id.item_score)
        val btnMap: Button = itemView.findViewById(R.id.item_btn_map)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_high_score, parent, false)
        return HighScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: HighScoreViewHolder, position: Int) {
        val score = scores[position]
        holder.name.text = score.name
        holder.score.text = score.score.toString()

        // Highlight selected
        holder.itemView.setBackgroundColor(
            if (position == selectedPosition) "#D0F0C0".toColorInt()
            else Color.TRANSPARENT
        )

        holder.btnMap.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onMapClick(score.lat, score.lon)
        }
    }

    override fun getItemCount(): Int = scores.size
}
