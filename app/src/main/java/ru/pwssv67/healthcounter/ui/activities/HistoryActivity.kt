package ru.pwssv67.healthcounter.ui.activities


import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_history.*
import ru.pwssv67.healthcounter.extensions.DayStats
import ru.pwssv67.healthcounter.extensions.Profile
import ru.pwssv67.healthcounter.R
import ru.pwssv67.healthcounter.extensions.normalStringDateToShort
import ru.pwssv67.healthcounter.ui.views.ChartView
import ru.pwssv67.healthcounter.viewModels.HistoryViewModel
import java.util.*
import kotlin.collections.ArrayList


class HistoryActivity : AppCompatActivity(){

    private lateinit var chartGlasses:ChartView
    private lateinit var chartCalories:ChartView
    private lateinit var chartTraining:ChartView
    private lateinit var profile: Profile
    private val arrayGlasses = ArrayList<Pair<Int, String>>()
    private val arrayCalories = ArrayList<Pair<Int, String>>()
    private val arrayTraining = ArrayList<Pair<Int, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val historyViewModel =
            HistoryViewModel(application)

        chartGlasses = glasses_chart_custom
        chartCalories = calories_chart_custom
        chartTraining = training_chart_custom

        val observer = Observer<List<DayStats>> {
            if (!it.isNullOrEmpty()) {
                arrayGlasses.clear()
                for (day in it) {
                    arrayGlasses.add(day.glasses to normalStringDateToShort(day.day))
                    arrayCalories.add(day.calories to normalStringDateToShort(day.day))
                    arrayTraining.add(day.training to normalStringDateToShort(day.day))
                }
                setDataOnCharts()
            }
        }

        profile = historyViewModel.getProfile()
        historyViewModel.getDayStats().observe(this, observer)
    }

    override fun onResume() {
        super.onResume()
        chartGlasses.notifyDataSetChanged()
    }

    private fun setDataOnCharts() {
        Log.e("sdf", "seteddata")
        if( (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            return
        }

        chartGlasses.limit = profile.drink_goal
        chartCalories.limit = profile.eat_goal_second
        chartTraining.limit = profile.training_goal
        chartGlasses.points = arrayGlasses
        chartGlasses.notifyDataSetChanged()
        chartCalories.points = arrayCalories
        chartCalories.notifyDataSetChanged()
        chartTraining.points = arrayTraining
        chartTraining.notifyDataSetChanged()

    }

}
