package ru.pwssv67.healthcounter.extensions

import android.graphics.Paint
import android.graphics.RectF

class ChartBar(var rect: RectF, var paint: Paint) {
    var value:Int = 0
    var color:Int = 0
    var i:Int = -1
    var xCoord = "0"

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
            xCoord = chartBar.xCoord
        }
    }
}