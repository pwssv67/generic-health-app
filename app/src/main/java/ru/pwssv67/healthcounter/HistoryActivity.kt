package ru.pwssv67.healthcounter


import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.activity_history_charts.*
import ru.pwssv67.healthcounter.Adapters.HistoryAdapter
import ru.pwssv67.healthcounter.Extensions.DayStats


class HistoryActivity : AppCompatActivity() {

    lateinit var chart:LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_charts)
        //val recyclerView: RecyclerView = findViewById(R.id.rv_history)
        val historyViewModel = HistoryViewModel(application)
        val adapter:HistoryAdapter
        chart = glasses_chart
       // recyclerView.adapter = HistoryAdapter()
        //adapter = recyclerView.adapter as HistoryAdapter
        val observer = Observer<List<DayStats>> {
            if (it!=null) {
                val map = it.associateBy({it.day.drop(5)}, {it.glasses.toFloat()}).toMutableMap()
                val lmap = LinkedHashMap(map)
                //chart.animate(lmap)
                //adapter.updateData(it)
                //days.reverse()
            }
        }
        setData()
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

    fun setData() {
        val values = ArrayList<Entry>()
        for (i in 0 until 10) {
            val ran = (Math.random() * 50).toFloat()
            values.add(
                Entry (i.toFloat(), ran)
            )
        }
        val set1 = LineDataSet(values, "Dataset1")
        set1.setDrawIcons(false)

        set1.lineWidth = 5f

        set1.color = getColor(R.color.colorPrimaryDark)

        set1.mode = LineDataSet.Mode.CUBIC_BEZIER
        set1.cubicIntensity = 0.2f
        set1.setCircleColor(Color.BLACK)
        set1.circleRadius = 0f

        set1.highLightColor = getColor(R.color.colorAccent)
        set1.setDrawValues(false)

        set1.setDrawHorizontalHighlightIndicator(false)

        set1.setDrawFilled(true)
        set1.fillFormatter =
            IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }

        set1.fillColor = getColor(R.color.colorPrimaryDark)
        set1.fillAlpha = 100

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1)

        val data = LineData(dataSets)
        data.setDrawValues(false)

        chart.data = data
        chart.setDrawGridBackground(false)
        chart.setDrawMarkers(false)
        chart.setViewPortOffsets(0f,0f,0f,0f)
        chart.legend.isEnabled = false
        chart.xAxis.isEnabled = false
        chart.axisLeft.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.maxHighlightDistance = 300f



        chart.animateXY(500, 500)
    }
}
