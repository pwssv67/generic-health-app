package ru.pwssv67.healthcounter

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_history.*
import ru.pwssv67.healthcounter.Adapters.HistoryAdapter
import ru.pwssv67.healthcounter.Extensions.DayStats

class HistoryActivity:AppCompatActivity(R.layout.activity_history) {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_history)
        //Snackbar.make(rv_history, "   ", Snackbar.LENGTH_SHORT)
        //Log.d("ewf", "efewf")
        //var rvStarted = false
        //val recyclerView:RecyclerView = findViewById(R.id.rv_history)
        /*
        val historyViewModel = HistoryViewModel(application)
        Log.e("ewf", "efewf")
        var days = mutableListOf<DayStats>()

        val observer = Observer<List<DayStats>> {
            if (it!=null) {
                Snackbar.make(rv_history, it[0].day, Snackbar.LENGTH_SHORT)
                days.clear()
                days.addAll(0, it)
                if (!rvStarted) {
                    recyclerView.refreshDrawableState()
                    rvStarted = true
                }
            }
        }

        days.add(DayStats(0,0,0))
        historyViewModel.getDayStats().observe(this, observer)
        Log.e("ewf", "efewf")
         */
        //recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerView.adapter = HistoryAdapter(generateShit())
        Log.d("k", "jnjn")

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