package ru.pwssv67.healthcounter.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.pwssv67.healthcounter.Extensions.DayStats
import ru.pwssv67.healthcounter.R

class HistoryAdapter(val days:List<DayStats>):RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        holder.date.text = days[position].day
        holder.drinkCounter.text = days[position].glasses.toString()
        holder.eatCounter.text = days[position].calories.toString()
        holder.trainingCounter.text = days[position].training.toString()
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