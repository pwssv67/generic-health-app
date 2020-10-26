package ru.pwssv67.healthcounter.UI.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import ru.pwssv67.healthcounter.Extensions.ChartBar
import ru.pwssv67.healthcounter.R
import java.security.KeyStore


class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?=null,
    defStyleAttr:Int=0
):View(context ,attrs ,defStyleAttr) {
    companion object {
        val defaultPoints = ArrayList<Int>(listOf(7,8,0,10,6,7).reversed())
    }


    private lateinit var gestureDetector: GestureDetector
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private val bar1 = ChartBar(RectF(), paint)
    private val bar2 = ChartBar(RectF(), paint)
    private val bar3 = ChartBar(RectF(), paint)
    private val bar4 = ChartBar(RectF(), paint)
    private val bar5 = ChartBar(RectF(), paint)
    private val barLeft = ChartBar(RectF(0f,0f,0f,0f), paint)
    private val barRight = ChartBar(RectF(0f,0f,0f,0f), paint)
    var points: ArrayList<Int> = defaultPoints
    private val bars = arrayOf(bar1, bar2, bar3, bar4, bar5)
    private val rounding = 3f
    var limit:Int = 8
    var maxValue:Int = limit+2
    private var animated = false
    private var isValueSet = false
    private var insetCustom = 10f
    private val zeroHeight = 5f
    private var factorVertical = 50f
    private var factorHorizontal = 15f
    private var barWidth = 5f
    private var barSpacing = 7f
    private var barsAmount = 3
    private var isRightBarActive = false
    private var isLeftBarActive = false
    private var scrollAccumulator = 0
    var succesColor = context.getColor(R.color.colorPrimaryDark)
    var defaultColor = context.getColor(R.color.colorPrimary)
    var accentColor = context.getColor(R.color.colorAccent)
    var textColor = context.getColor(R.color.primaryText)
    var highlightedBar:ChartBar? = null
    lateinit var text:String
    //var text = "Стаканов"

    init {
        if (attrs!= null && !this.isInEditMode) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ChartView)
            if (a != null) {
                text = a.getText(R.styleable.ChartView_text).toString()
            }
            a.recycle()
        } else {
            text = "Sample"
        }
       gestureDetector = GestureDetector(context, MyGestureListener())
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        factorHorizontal = measuredWidth/(barWidth*5 + barSpacing * 6)
        factorVertical = measuredHeight/(maxValue+10).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!isValueSet) setBars()
        drawLimitLine(canvas)
        drawBars(canvas)
        drawValue(canvas)
        drawText(canvas)
    }

    private fun drawText(canvas: Canvas?) {
        textPaint.color = textColor
        textPaint.textSize = 8*factorHorizontal
        if (!this.isInEditMode) {textPaint.typeface = Typeface.create(ResourcesCompat.getFont(context, R.font.roboto_light), Typeface.NORMAL)}
        else {textPaint.typeface = Typeface.SANS_SERIF}
        textPaint.textAlign = Paint.Align.CENTER
        canvas?.drawText(text, width/2f, 4*factorVertical, textPaint)
    }

    private fun drawValue(canvas: Canvas?) {
        if (highlightedBar != null) {
            val bar= highlightedBar as ChartBar
            textPaint.color = bar.color
            textPaint.textSize = 6*factorHorizontal
            if (!this.isInEditMode) {textPaint.typeface = Typeface.create(ResourcesCompat.getFont(context, R.font.roboto_light), Typeface.NORMAL)}
            else {textPaint.typeface = Typeface.SANS_SERIF}
            textPaint.textAlign = Paint.Align.CENTER
            canvas?.drawText(bar.value.toString(), width/2f, 8*factorVertical, textPaint)
        } else return
    }

    private fun drawBars(canvas: Canvas?) {
        for (i in bars.indices) {
            paint.color = bars[i].color
            canvas?.drawRoundRect(
                bars[i].rect,
                rounding * factorHorizontal / 1.5f,
                rounding * factorHorizontal / 1.5f,
                bars[i].paint
            )
        }
        if (isLeftBarActive) {
            paint.color = barLeft.color
            canvas?.drawRoundRect(
                barLeft.rect,
                rounding * factorHorizontal / 1.5f,
                rounding * factorHorizontal / 1.5f,
                barLeft.paint
            )
        }

        if (isRightBarActive) {
            paint.color = barRight.color
            canvas?.drawRoundRect(
                barRight.rect,
                rounding * factorHorizontal / 1.5f,
                rounding * factorHorizontal / 1.5f,
                barRight.paint
            )
        }
    }

    private fun drawLimitLine(canvas: Canvas?) {
        if (limit != 0) {
            paint.color = defaultColor
            paint.strokeWidth = 0.2f*factorVertical
            canvas?.drawLine(
                0f,
                height - limit * factorVertical,
                width.toFloat(),
                height - limit * factorVertical,
                paint
            )
        }
    }

    private fun setBars() {
        setValues()
        setRects()
        isValueSet = true
    }

    private fun setRects() {
        for (i in bars.indices) {
            Log.e("setrects", "$i")
            bars[i].rect.bottom = height - insetCustom
            if (bars[i].value <=0) {bars[i].rect.top = bars[i].rect.bottom - zeroHeight*factorHorizontal/1.3f}
            else {bars[i].rect.top = height - (bars[i].value)*factorVertical - rounding*factorHorizontal/3f}
            bars[i].rect.left = (i*barWidth + (i+1)*barSpacing )*factorHorizontal
            bars[i].rect.right = bars[i].rect.left + barWidth*factorHorizontal
        }
    }

    private fun setValues() {
        if (points.size >=5) {
            for (i in (points.size-1) downTo (points.size-5)) {
                val barNumber = bars.size - points.size + i
                Log.e("", "i $i in bar${barNumber}")
                bars[barNumber].value = points[i]
                bars[barNumber].i = i
                if (points[i] >= limit) {
                    bars[barNumber].color = succesColor
                } else {
                    bars[barNumber].color = defaultColor
                }
            }
        }
        else {
            for (i in points.indices) {
                bars[i].value = points.reversed()[i]
                bars[i].i = i
                if (points[i] >= limit) {
                    bars[i].color = succesColor
                } else {
                    bars[i].color = defaultColor
                }
            }
        }
        maxValue = points.max() ?: limit+2
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> onTouch(event.x, event.y)
            MotionEvent.ACTION_UP -> onUp()
        }
        gestureDetector.onTouchEvent(event)
        super.onTouchEvent(event)
        invalidate()
        return true

    }

    private fun onUp() {
        unhighlightBar()
        invalidate()
    }

    private fun unhighlightBar() {
        val i = bars.indexOf(highlightedBar)
        if (i >= 0) {
            bars[i].color = when {
                bars[i].value >= limit -> succesColor
                else -> defaultColor
            }
        }
        highlightedBar = null
    }

    private fun onTouch(x: Float, y:Float) {
        val bar = highlightedBar
        if (bar!=null) {
            if (RectF(
                    bar.rect.left - barSpacing * factorHorizontal / 2,
                    insetCustom * factorVertical,
                    bar.rect.right + barSpacing * factorHorizontal / 2,
                    bar.rect.bottom
                ).contains(x,y)) {
                    return
                }
        }

        for (bar in bars) {
            val rect = RectF(bar.rect.left-barSpacing*factorHorizontal/2, insetCustom*factorVertical, bar.rect.right+barSpacing*factorHorizontal/2, bar.rect.bottom)
            if (rect.contains(x,y)) {
                bar.color = accentColor
                highlightedBar = bar
                invalidate()
                return
            }
        }

        var rect = RectF(barLeft.rect.left-barSpacing*factorHorizontal/2, insetCustom*factorVertical, barLeft.rect.right+barSpacing*factorHorizontal/2, barLeft.rect.bottom)
        if (rect.contains(x,y)) {
            barLeft.color = accentColor
            highlightedBar = barLeft
            invalidate()
            return
        }

        rect = RectF(barRight.rect.left-barSpacing*factorHorizontal/2, insetCustom*factorVertical, barRight.rect.right+barSpacing*factorHorizontal/2, barRight.rect.bottom)
        if (rect.contains(x,y)) {
            barRight.color = accentColor
            highlightedBar = barRight
            invalidate()
            return
        }
    }

    private fun scroll(x:Int, y:Int) {
        unhighlightBar()
        if (x<0) { // swiping right, must show left
            Log.e("", "${bars[0].i}")
            if (points.size > barsAmount && bars[0].i != 0) {
                for (bar in bars) {
                    bar.rect.left -= x
                    bar.rect.right -= x
                }
                barRight.rect.left -= x
                barRight.rect.right -= x
                barLeft.rect.left -= x
                barLeft.rect.right -= x
                //scrollAccumulator -= x


                if (bars[0].i >= 1) {
                    if (bars[4].rect.left >= width  && isLeftBarActive) {
                        scrollAccumulator = 0
                        Log.e("", "sec")
                        for (i in bars.size - 1 downTo 1) {
                            bars[i].i = bars[i - 1].i
                            bars[i].color = bars[i - 1].color
                            bars[i].value = bars[i - 1].value
                            bars[i].rect = RectF(bars[i - 1].rect)
                        }
                        bars[0].i = barLeft.i
                        bars[0].color = barLeft.color
                        bars[0].value = barLeft.value
                        bars[0].rect = RectF(barLeft.rect)
                        isLeftBarActive = false

                        if (bars[0].i > 0) {
                            barLeft.i = bars[0].i - 1
                            val i = barLeft.i
                            barLeft.value = points[i]
                            barLeft.color = if (barLeft.value >= limit) {
                                succesColor
                            } else {
                                defaultColor
                            }
                            if (barLeft.value <= 0) {
                                barLeft.rect.top =
                                    barLeft.rect.bottom - zeroHeight * factorHorizontal / 1.3f
                            } else {
                                barLeft.rect.top =
                                    height - (barLeft.value) * factorVertical - rounding * factorHorizontal / 3f
                            }
                            barLeft.rect.bottom = height - insetCustom
                            barLeft.rect.right = bars[0].rect.left - barSpacing * factorHorizontal
                            barLeft.rect.left = barLeft.rect.right - barWidth * factorHorizontal
                            isLeftBarActive = true
                        }
                    }

                    if (!isLeftBarActive && bars[0].i > 0) {
                        Log.e("", "first")
                        isLeftBarActive = true
                        barLeft.i = bars[0].i - 1
                        val i = barLeft.i
                        barLeft.value = points[i]
                        barLeft.color = if (barLeft.value >= limit) {
                            succesColor
                        } else {
                            defaultColor
                        }
                        if (barLeft.value <= 0) {
                            barLeft.rect.top =
                                barLeft.rect.bottom - zeroHeight * factorHorizontal / 1.3f
                        } else {
                            barLeft.rect.top =
                                height - (barLeft.value) * factorVertical - rounding * factorHorizontal / 3f
                        }
                        barLeft.rect.bottom = height - insetCustom
                        barLeft.rect.right = bars[0].rect.left - barSpacing * factorHorizontal
                        barLeft.rect.left = barLeft.rect.right - barWidth * factorHorizontal
                    }
                }
            }
        }
        invalidate()
    }

    fun animateBars() {

    }

    fun notifyDataSetChanged() {
        invalidate()
    }

    private inner class MyGestureListener : SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            scroll(distanceX.toInt(), distanceY.toInt())
            return true
        }
    }


}