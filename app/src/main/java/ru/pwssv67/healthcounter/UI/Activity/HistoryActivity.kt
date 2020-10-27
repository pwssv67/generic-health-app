package ru.pwssv67.healthcounter.UI.Activity


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_history.*
import ru.pwssv67.healthcounter.UI.Adapters.HistoryAdapter
import ru.pwssv67.healthcounter.Extensions.DayStats
import ru.pwssv67.healthcounter.Extensions.Profile
import ru.pwssv67.healthcounter.R
import ru.pwssv67.healthcounter.UI.View.ChartView
import ru.pwssv67.healthcounter.ViewModels.HistoryViewModel


class HistoryActivity : AppCompatActivity(){

    lateinit var chart:ChartView
    lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        //val recyclerView: RecyclerView = findViewById(R.id.rv_history)
        val historyViewModel =
            HistoryViewModel(application)
        val adapter:HistoryAdapter
        chart = glasses_chart_custom
       // recyclerView.adapter = HistoryAdapter()
        //adapter = recyclerView.adapter as HistoryAdapter
        var arrayGlasses = ArrayList<Int>()
        val observer = Observer<List<DayStats>> {
            if (it!=null && !it.isNullOrEmpty()) {
                arrayGlasses.clear()
                for (day in it) {
                    arrayGlasses.add(day.glasses)
                }
                chart.points = arrayGlasses
                chart.notifyDataSetChanged()
                Log.e("obs", "${it.size}")
                Log.e("obs", "${arrayGlasses.size}")
            }
        }

        profile = historyViewModel.getProfile()
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
