package ru.pwssv67.healthcounter.UI.View

import android.animation.ValueAnimator
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
import kotlin.math.absoluteValue


class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?=null,
    defStyleAttr:Int=0
):View(context ,attrs ,defStyleAttr) {
    companion object {
        val defaultPoints = ArrayList<Int>(listOf(1,2,3,4,5,6,7,8,9,10,11).reversed())
    }

    private var gestureDetector: GestureDetector
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private val barLeft = ChartBar(RectF(0f,0f,0f,0f), paint)
    private val barRight = ChartBar(RectF(0f,0f,0f,0f), paint)
    var points: ArrayList<Int> = defaultPoints
    private val bars = ArrayList<ChartBar>()
    private val rounding = 3f
    var limit:Int = 8
    private var maxValue:Int = limit+2
    private var animated = false
    private var isValueSet = false
    private var paddingBottom = 10f
    private var paddingTop = 3f
    private val zeroHeight = 2f
    private var factorVertical = 50f
    private var factorHorizontal = 15f
    private var barWidth = 3f
    private var barSpacing = 7f
    private var barsAmount = 6
    private var isRightBarActive = false
    private var isLeftBarActive = false
    private var scrollAccumulator = 0
    private var isScrolled = false
    private var animationCounter = 1f
    var successColor = context.getColor(R.color.colorPrimaryDark)
    var defaultColor = context.getColor(R.color.colorPrimary)
    var accentColor = context.getColor(R.color.colorAccent)
    var textColor = context.getColor(R.color.primaryText)
    var highlightedBar:ChartBar? = null
    var showLimit = false
    lateinit var text:String
    var textSizeInSp = 36
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
        for (i in 0 until barsAmount) {
            bars.add(ChartBar(RectF(), paint))
        }
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setFactors()
    }

    private fun setFactors() {
        var w = width
        var h = height
        if (measuredHeight > height) h = measuredHeight
        if (measuredWidth > width) w = measuredWidth
        factorHorizontal = w / (barWidth * bars.size + barSpacing * (bars.size + 1))

        var max = bars[0].value
        for (i in 1 until bars.size) {
            if (bars[i].value>max) {
                max = bars[i].value
            }
        }
        if (isLeftBarActive) {
            if (barLeft.value>max) max = barLeft.value
        }
        if (isRightBarActive) {
            if (barRight.value>max) max = barRight.value
        }

        if (max <= 0) max = limit
        factorVertical = h / (max*2).toFloat()
        Log.e("fac max", "$factorVertical $max")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!isValueSet) {
            setBars()
            if(!this.isInEditMode) animateBars()
        }
        drawLimitLine(canvas)
        drawBars(canvas)
        drawValue(canvas)
        drawText(canvas)
    }

    private fun drawText(canvas: Canvas?) {
        textPaint.color = textColor
        textPaint.textSize = textSizeInSp*resources.displayMetrics.scaledDensity
        if (!this.isInEditMode) {textPaint.typeface = Typeface.create(ResourcesCompat.getFont(context, R.font.roboto_light), Typeface.NORMAL)}
        else {textPaint.typeface = Typeface.SANS_SERIF}
        textPaint.textAlign = Paint.Align.CENTER
        canvas?.drawText(text, width/2f, textPaint.textSize*1.2f, textPaint)
    }

    private fun drawValue(canvas: Canvas?) {
        if (highlightedBar != null) {
            val bar= highlightedBar as ChartBar
            textPaint.color = successColor
            textPaint.textSize = textSizeInSp*resources.displayMetrics.scaledDensity*0.8f
            if (!this.isInEditMode) {textPaint.typeface = Typeface.create(ResourcesCompat.getFont(context, R.font.roboto_light), Typeface.NORMAL)}
            else {textPaint.typeface = Typeface.SANS_SERIF}
            textPaint.textAlign = Paint.Align.CENTER
            canvas?.drawText(bar.value.toString(), width/2f, textPaint.textSize*3f, textPaint)
        } else return
    }

    private fun drawBars(canvas: Canvas?) {
        for (i in bars.indices) {
            paint.color = bars[i].color
            var top = height - (bars[i].value)*factorVertical*animationCounter - rounding*factorHorizontal/3f
            if (bars[i].rect.bottom-top < zeroHeight*factorHorizontal/1.3f) top =  bars[i].rect.bottom - zeroHeight*factorHorizontal/1.3f*animationCounter
            bars[i].rect.top = top
            canvas?.drawRoundRect(
                bars[i].rect,
                rounding * factorHorizontal / 1.5f,
                rounding * factorHorizontal / 1.5f,
                bars[i].paint
            )
        }

        if (isLeftBarActive) {
            paint.color = barLeft.color
            var top = height - (barLeft.value)*factorVertical*animationCounter - rounding*factorHorizontal/3f
            if (barLeft.rect.bottom-top < zeroHeight*factorHorizontal/1.3f) top =  barLeft.rect.bottom - zeroHeight*factorHorizontal/1.3f*animationCounter
            barLeft.rect.top = top
            canvas?.drawRoundRect(
                barLeft.rect,
                rounding * factorHorizontal / 1.5f,
                rounding * factorHorizontal / 1.5f,
                barLeft.paint
            )
        }

        if (isRightBarActive) {
            paint.color = barRight.color
            var top = height - (barRight.value)*factorVertical*animationCounter - rounding*factorHorizontal/3f
            if (barRight.rect.bottom-top < zeroHeight*factorHorizontal/1.3f) top =  barRight.rect.bottom - zeroHeight*factorHorizontal/1.3f*animationCounter
            barRight.rect.top = top
            canvas?.drawRoundRect(
                barRight.rect,
                rounding * factorHorizontal / 1.5f,
                rounding * factorHorizontal / 1.5f,
                barRight.paint
            )
        }
    }

    private fun drawLimitLine(canvas: Canvas?) {
        if (limit != 0 && showLimit) {
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
        setFactors()
        setRects()
        isValueSet = true
    }

    private fun setRects() {
        for (i in bars.indices) {
            bars[i].rect.bottom = height - paddingBottom
            if (bars[i].value <= maxValue/10) {bars[i].rect.top = bars[i].rect.bottom - zeroHeight*factorHorizontal/1.3f}
            else {bars[i].rect.top = height - (bars[i].value)*factorVertical - rounding*factorHorizontal/3f}
            bars[i].rect.left = (i*barWidth + (i+1)*barSpacing )*factorHorizontal
            bars[i].rect.right = bars[i].rect.left + barWidth*factorHorizontal
        }
    }

    private fun setValues() {
        if (points.size >= bars.size) {
            for (i in (points.size-1) downTo (points.size-bars.size)) {
                val barNumber = bars.size - points.size + i
                bars[barNumber].value = points[i]
                bars[barNumber].i = i
                if (points[i] >= limit) {
                    bars[barNumber].color = successColor
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
                    bars[i].color = successColor
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
                bars[i].value >= limit -> successColor
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
                    paddingBottom * factorVertical,
                    bar.rect.right + barSpacing * factorHorizontal / 2,
                    bar.rect.bottom
                ).contains(x,y)) {
                    return
                }
        }

        for (bar in bars) {
            val rect = RectF(bar.rect.left-barSpacing*factorHorizontal/2, paddingBottom*factorVertical, bar.rect.right+barSpacing*factorHorizontal/2, bar.rect.bottom)
            if (rect.contains(x,y)) {
                bar.color = accentColor
                highlightedBar = bar
                invalidate()
                return
            }
        }

        var rect = RectF(barLeft.rect.left-barSpacing*factorHorizontal/2, paddingBottom*factorVertical, barLeft.rect.right+barSpacing*factorHorizontal/2, barLeft.rect.bottom)
        if (rect.contains(x,y)) {
            barLeft.color = accentColor
            highlightedBar = barLeft
            invalidate()
            return
        }

        rect = RectF(barRight.rect.left-barSpacing*factorHorizontal/2, paddingBottom*factorVertical, barRight.rect.right+barSpacing*factorHorizontal/2, barRight.rect.bottom)
        if (rect.contains(x,y)) {
            barRight.color = accentColor
            highlightedBar = barRight
            invalidate()
            return
        }
    }

    private fun scroll(x:Int, y:Int) {
        unhighlightBar()
        if (isScrolled) return
        isScrolled = true
        if (x<0) { // swiping right, must show left
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
                    if ((bars[bars.size - 1].rect.left >= width) && isLeftBarActive ) {
                        scrollAccumulator = 0

                        if (isRightBarActive) {
                            bars.last().copy(barRight)
                            isRightBarActive = false
                        }

                        for (i in bars.size - 1 downTo 1) {
                            bars[i].copy(bars[i-1])
                        }

                        bars[0].copy(barLeft)
                        isLeftBarActive = false

                        if (bars[0].i > 0) {
                            barLeft.i = bars[0].i - 1
                            val i = barLeft.i
                            barLeft.value = points[i]
                            barLeft.color = if (barLeft.value >= limit) { successColor } else { defaultColor }

                            barLeft.rect.bottom = height - paddingBottom

                            if (barLeft.value <= maxValue/10) {
                                barLeft.rect.top =
                                    barLeft.rect.bottom - zeroHeight * factorHorizontal / 1.3f
                            } else {
                                barLeft.rect.top =
                                    height - (barLeft.value) * factorVertical - rounding * factorHorizontal / 3f
                            }
                            barLeft.rect.right = bars[0].rect.left - barSpacing * factorHorizontal
                            barLeft.rect.left = barLeft.rect.right - barWidth * factorHorizontal
                            isLeftBarActive = true
                        }
                    }

                    if (!isLeftBarActive && bars[0].i > 0) {
                        barLeft.i = bars[0].i - 1
                        val i = barLeft.i
                        barLeft.value = points[i]
                        barLeft.color = if (barLeft.value >= limit) { successColor } else { defaultColor }
                        barLeft.rect.bottom = height - paddingBottom

                        if (barLeft.value <= maxValue/10) {
                            barLeft.rect.top =
                                barLeft.rect.bottom - zeroHeight * factorHorizontal / 1.3f
                        } else {
                            barLeft.rect.top =
                                height - (barLeft.value) * factorVertical - rounding * factorHorizontal / 3f
                        }
                        barLeft.rect.right = bars[0].rect.left - barSpacing * factorHorizontal
                        barLeft.rect.left = barLeft.rect.right - barWidth * factorHorizontal
                        isLeftBarActive = true
                    }
                }
            }
        } else {

            if (points.size > barsAmount && bars[bars.size-1].i != points.size-1) {
                for (bar in bars) {
                    bar.rect.left -= x
                    bar.rect.right -= x
                }
                barRight.rect.left -= x
                barRight.rect.right -= x
                barLeft.rect.left -= x
                barLeft.rect.right -= x
                //scrollAccumulator -= x


                if (bars[bars.size - 1].i <= points.size-2) {
                    if ((bars[0].rect.right <= 0) && isRightBarActive ) {
                        scrollAccumulator = 0

                        if (isLeftBarActive) {
                            bars.first().copy(barLeft)
                            isLeftBarActive = false
                        }

                        for (i in 0 until bars.size-1) {
                            bars[i].copy(bars[i+1])
                        }

                        bars[bars.size - 1].copy(barRight)
                        isRightBarActive = false

                        if (bars[bars.size - 1].i < bars.size-1) {
                            barRight.i = bars[bars.size - 1].i + 1
                            val i = barRight.i
                            barRight.value = points[i]
                            barRight.color = if (barRight.value >= limit) {
                                successColor
                            } else {
                                defaultColor
                            }
                            barRight.rect.bottom = height - paddingBottom
                            if (barRight.value <= maxValue / 10) {
                                barRight.rect.top =
                                    barRight.rect.bottom - zeroHeight * factorHorizontal / 1.3f
                            } else {
                                barRight.rect.top =
                                    height - (barRight.value) * factorVertical - rounding * factorHorizontal / 3f
                            }
                            barRight.rect.left = bars[bars.size - 1].rect.right + barSpacing * factorHorizontal
                            barRight.rect.right = barRight.rect.left + barWidth * factorHorizontal
                            isRightBarActive = true
                        }
                    }

                    if (!isRightBarActive && bars[bars.size - 1].i < points.size-1) {
                        barRight.i = bars[bars.size - 1].i + 1
                        val i = barRight.i
                        barRight.value = points[i]
                        barRight.color = if (barRight.value >= limit) {
                            successColor
                        } else {
                            defaultColor
                        }
                        barRight.rect.bottom = height - paddingBottom
                        if (barRight.value <= maxValue/ 10) {
                            barRight.rect.top =
                                barRight.rect.bottom - zeroHeight * factorHorizontal / 1.3f
                        } else {
                            barRight.rect.top =
                                height - (barRight.value) * factorVertical - rounding * factorHorizontal / 3f
                        }
                        barRight.rect.left = bars[bars.size - 1].rect.right + barSpacing * factorHorizontal
                        barRight.rect.right = barRight.rect.left + barWidth * factorHorizontal
                        isRightBarActive = true
                    }
                }
            }
        }
        setFactors()
        invalidate()
        //invalidate()
        isScrolled = false
    }

    fun animateBars(length:Long = 500) {
        val animator = ValueAnimator.ofFloat(0f,1f)
        animator.duration = length
        animator.addUpdateListener {
            animationCounter = it.animatedValue as Float
            if (it.animatedValue as Float > 0.9f) {
                isScrolled = false
            }
            invalidate()
        }
        isScrolled = true
        animator.start()
    }

    fun notifyDataSetChanged() {
        //maxValue = points.max() ?: limit+2
        isValueSet = false
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