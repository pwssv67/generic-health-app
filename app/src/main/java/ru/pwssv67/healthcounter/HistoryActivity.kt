package ru.pwssv67.healthcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import ru.pwssv67.healthcounter.Adapters.HistoryAdapter
import ru.pwssv67.healthcounter.Extensions.DayStats

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val recyclerView: RecyclerView = findViewById(R.id.rv_history)
        val historyViewModel = HistoryViewModel(application)
        val adapter:HistoryAdapter
        recyclerView.adapter = HistoryAdapter()
        adapter = recyclerView.adapter as HistoryAdapter
        val observer = Observer<List<DayStats>> {
            if (it!=null) {
                adapter.updateData(it)
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
