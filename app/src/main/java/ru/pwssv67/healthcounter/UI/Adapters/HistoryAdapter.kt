package ru.pwssv67.healthcounter.UI.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.pwssv67.healthcounter.Extensions.DayStats
import ru.pwssv67.healthcounter.R

class HistoryAdapter:RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    var days: List<DayStats> = listOf()

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        val pos = days.size - 1 - position
        holder.date.text = days[pos].day
        holder.drinkCounter.text = days[pos].glasses.toString()
        holder.eatCounter.text = days[pos].calories.toString()
        holder.trainingCounter.text = days[pos].training.toString()
    }

    public fun updateData(data:List<DayStats>) {
        days = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = days.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_fragment, parent, false)
        return ViewHolder(itemView)
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val drinkCounter:TextView
        val eatCounter:TextView
        val trainingCounter:TextView
        val date: TextView
        init {
            drinkCounter = itemView.findViewById(R.id.tv_drink_counter)
            eatCounter = itemView.findViewById(R.id.tv_eat_counter)
            trainingCounter = itemView.findViewById(R.id.tv_training_counter)
            date = itemView.findViewById(R.id.tv_date)
        }
    }
}