package ru.pwssv67.healthcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_history_charts.*
import ru.pwssv67.healthcounter.Adapters.HistoryAdapter
import ru.pwssv67.healthcounter.Extensions.DayStats

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_charts)
        //val recyclerView: RecyclerView = findViewById(R.id.rv_history)
        val historyViewModel = HistoryViewModel(application)
        val adapter:HistoryAdapter
        val chart = chart_glasses
       // recyclerView.adapter = HistoryAdapter()
        //adapter = recyclerView.adapter as HistoryAdapter
        val observer = Observer<List<DayStats>> {
            if (it!=null) {
                val map = it.associateBy({it.day.drop(5)}, {it.glasses.toFloat()}).toMutableMap()
                val lmap = LinkedHashMap(map)
                chart.animate(lmap)
                //adapter.updateData(it)
                //days.reverse()
            }
        }
        historyViewModel.getDayStats().observe(this, observer)
    }

    fun generateShit():List<DayStats> {
        val l = mutableListOf<DayStats>()
        for (i in 0..10) {
            l.add(DayStats(0,0,0,"$i"))
        }
        Log.e("eef", "effe")
        return l
    }
}
