package ru.pwssv67.healthcounter.Extensions

import android.graphics.Paint
import android.graphics.RectF

class ChartBar(rect: RectF, paint: Paint) {
    var rect: RectF
    var value:Int = 0
    var paint: Paint
    var color:Int = 0
    var i:Int = -1
    var xCoord = "0"

    init {
        this.rect = rect
        this.paint = paint
    }

    fun copy(chartBar: ChartBar) {
         with (this.rect) {
             left = chartBar.rect.left
             top = chartBar.rect.top
             right = chartBar.rect.right
             bottom = chartBar.rect.bottom
         }

        with(this) {
            value = chartBar.value
            i = chartBar.i
            color = chartBar.color
        }
    }
}