package ru.pwssv67.healthcounter.ui.activities


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


    /*private fun setDataGlasses(values: List<DayStats>) {

        val array = ArrayList<Entry>()

        if (chart.data != null && chart.data.dataSetCount >=0) {
            val data = chart.data.getDataSetByIndex(0) as LineDataSet
            for (i in 0..values.size) {
                array.add(
                    Entry(i.toFloat(), values[i].glasses.toFloat())
                )
            }
            data.values = array
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        }
        else {

            for (i in values.indices) {
                array.add(
                    Entry(i.toFloat(), values[i].glasses.toFloat())
                )
            }

            val set1 = LineDataSet(array, "Dataset1")
            set1.setDrawIcons(false)

            set1.lineWidth = 0f


            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            set1.cubicIntensity = 0.2f
            set1.setCircleColor(Color.TRANSPARENT)
            set1.circleRadius = 0f
            set1.setDrawCircleHole(false)

            set1.highLightColor = getColor(R.color.colorAccent)
            set1.setDrawValues(false)

            set1.setDrawHorizontalHighlightIndicator(false)

            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }

            set1.valueFormatter = MyValueFormatter()




            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(this,
                    R.drawable.chart_background_gradient
                )
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.BLACK
            }

            val line = LimitLine(profile.drink_goal.toFloat())

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1)

            val data = LineData(dataSets)
            data.setDrawValues(true)
            data.isHighlightEnabled = true


            val mTf = ResourcesCompat.getFont(applicationContext,
                R.font.roboto_medium
            )
            data.setValueTypeface(mTf)
            data.setValueTextColor(getColor(R.color.primaryText))
            data.setValueTextSize(16f)

            chart.data = data
            chart.setDrawGridBackground(false)
            chart.setDrawMarkers(true)
            chart.setViewPortOffsets(5f, 0f, 5f, 0f)
            chart.legend.isEnabled = false
            chart.xAxis.isEnabled = false
            chart.axisLeft.isEnabled = false
            chart.axisRight.isEnabled = false
            chart.description.isEnabled = false
            chart.maxHighlightDistance = 300f



            chart.animateXY(500, 500)

        }


    }
     */

}
