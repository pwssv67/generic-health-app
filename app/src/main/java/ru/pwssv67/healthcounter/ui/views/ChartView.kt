package ru.pwssv67.healthcounter.ui.views

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.*
import ru.pwssv67.healthcounter.extensions.ChartBar
import ru.pwssv67.healthcounter.R
import kotlin.math.abs
import kotlin.math.sqrt


class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?=null,
    defStyleAttr:Int=0
):View(context ,attrs ,defStyleAttr) {
    companion object {
        val defaultPoints = ArrayList<Pair<Int, String>>(listOf(4 to "0",7 to "1",3 to "2",4 to "3",2 to "4",6 to "5",7 to "6",8 to "7",9 to "8",10 to "9",11 to "10").reversed())
    }

    private var gestureDetector: GestureDetector
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private var textPaintSecondary = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private var textPaintAccent = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private val additionalBar = ChartBar(RectF(0f,0f,0f,0f), paint)
    var points: ArrayList<Pair<Int, String>> = defaultPoints
    private val bars = ArrayList<ChartBar>()
    private val rounding = 3f
    var limit:Int = 5
    private var maxValue:Int = limit+2
    private var animated = false
    private var isValueSet = false
    private var paddingBottom = 70f
    private var paddingTop = 3f
    private val zeroHeight = 2f
    private var factorVertical = 50f
    private var factorHorizontal = 15f
    private var barWidth = 3f
    private var barSpacing = 7f
    private var barsAmount = 6
    private var isAdditionalBarActive = false
    private var scrollAccumulator = 0
    private var isScrolled = false
    private var animationCounter = 1f
    private val defaultAnimationDuration = 250L
    private var isVerticalFactorAnimated = false
    private val tempRectF = RectF()
    var successColor = context.getColor(R.color.colorPrimaryDark)
    var defaultColor = context.getColor(R.color.colorPrimary)
    var accentColor = context.getColor(R.color.colorAccent)
    var textColor = context.getColor(R.color.primaryText)
    var highlightedBar:ChartBar? = null
    var showLimit = false
    lateinit var text:String
    var textSize = 80
    var labelTextSize = 40
    var scope = MainScope()
    //var text = "Стаканов"
    // TODO refactor the hell all. 2 addBars -> 1


    init {
        if (attrs!= null
            //&& !this.isInEditMode
        ) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ChartView)
            if (a != null) {
                text = (a.getText(R.styleable.ChartView_text) ?: "Sample").toString()
                labelTextSize = a.getDimensionPixelSize(R.styleable.ChartView_labelTextSize, 50)
                textSize = a.getDimensionPixelSize(R.styleable.ChartView_android_textSize, 80)
                barsAmount = a.getInt(R.styleable.ChartView_barsAmount, 7)
            }
            a.recycle()
        } else {
            text = "Sample"
        }

       gestureDetector = GestureDetector(context, MyGestureListener())
        for (i in 0 until barsAmount) {
            bars.add(ChartBar(RectF(), paint))
        }

        setTextPaints(context)
    }

    private fun setTextPaints(context: Context) {
        textPaint.color = textColor
        textPaint.textSize = textSize.toFloat()
        if (!this.isInEditMode) {
            textPaint.typeface = Typeface.create(
                ResourcesCompat.getFont(context, R.font.roboto_light),
                Typeface.NORMAL
            )
        } else {
            textPaint.typeface = Typeface.SANS_SERIF
        }
        textPaint.textAlign = Paint.Align.CENTER

        textPaintSecondary.color = textColor
        textPaintSecondary.textSize = labelTextSize.toFloat()
        if (!this.isInEditMode) {
            textPaintSecondary.typeface = Typeface.create(
                ResourcesCompat.getFont(context, R.font.roboto_light),
                Typeface.NORMAL
            )
        } else {
            textPaintSecondary.typeface = Typeface.SANS_SERIF
        }
        textPaintSecondary.textAlign = Paint.Align.CENTER

        textPaintAccent.color = accentColor
        textPaintAccent.textSize = textSize * 0.7f
        if (!this.isInEditMode) {
            textPaintAccent.typeface = Typeface.create(
                ResourcesCompat.getFont(context, R.font.roboto_light),
                Typeface.NORMAL
            )
        } else {
            textPaintAccent.typeface = Typeface.SANS_SERIF
        }
        textPaintAccent.textAlign = Paint.Align.CENTER
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
        if (isAdditionalBarActive && additionalBar.value>max) {max = additionalBar.value}

        if (max <= 0) max = limit
        factorVertical = h / (max*2).toFloat()
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
        canvas?.drawText(text, width/2f, textPaint.textSize*1.2f, textPaint)
    }

    private fun drawValue(canvas: Canvas?) {
        if (highlightedBar != null) {
            val bar= highlightedBar as ChartBar
            canvas?.drawText(bar.value.toString(), width/2f, textPaintAccent.textSize*3f, textPaintAccent)
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

            if (bars[i].color != 0 && bars[i].xCoord != "") {
                canvas?.drawText(
                    bars[i].xCoord,
                    (bars[i].rect.left + bars[i].rect.right) / 2,
                    bars[i].rect.bottom + paddingBottom / 1.5F,
                    textPaintSecondary
                )
            }
        }

        if (isAdditionalBarActive) {
            paint.color = additionalBar.color
            var top = height - (additionalBar.value)*factorVertical*animationCounter - rounding*factorHorizontal/3f
            if (additionalBar.rect.bottom-top < zeroHeight*factorHorizontal/1.3f) top =  additionalBar.rect.bottom - zeroHeight*factorHorizontal/1.3f*animationCounter
            additionalBar.rect.top = top
            canvas?.drawRoundRect(
                additionalBar.rect,
                rounding * factorHorizontal / 1.5f,
                rounding * factorHorizontal / 1.5f,
                additionalBar.paint
            )

            canvas?.drawText(additionalBar.xCoord, (additionalBar.rect.left + additionalBar.rect.right ) / 2 , additionalBar.rect.bottom + paddingBottom/1.5F, textPaintSecondary)
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
                bars[barNumber].value = points[i].first
                bars[barNumber].xCoord = points[i].second
                bars[barNumber].i = i
                if (bars[barNumber].value >= limit) {
                    bars[barNumber].color = successColor
                } else {
                    bars[barNumber].color = defaultColor
                }
            }
        }

        else {
            for (i in points.size-1 downTo 0) {
                bars[i].value = points[i].first
                bars[i].xCoord = points[i].second
                bars[i].i = i
                if (bars[i].value >= limit) {
                    bars[i].color = successColor
                } else {
                    bars[i].color = defaultColor
                }
            }
        }

        maxValue = points.maxByOrNull { it.first }?.first ?: (limit*1.2).toInt()

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> onTouch(event.x, event.y)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                onUp()
                //scope.launch {normalizeBarsPosition() }

            }
        }
        gestureDetector.onTouchEvent(event)
        super.onTouchEvent(event)
        invalidate()
        return true

    }

    private fun onUp() {
        unHighlightBar()
        invalidate()
    }

    private fun unHighlightBar() {

        for (bar in bars) {
            if (bar.color == accentColor) {
                bar.color = when {
                    bar.value >= limit -> successColor
                    else -> defaultColor
                }
            }
        }

        if (additionalBar.color == accentColor) {
            additionalBar.color = when {
                additionalBar.value >= limit -> successColor
                else -> defaultColor
            }
        }

        highlightedBar = null
    }

    private fun onTouch(x: Float, y:Float) {

        if (highlightedBar != null) {
            with(tempRectF) {
                left = highlightedBar!!.rect.left - barSpacing * factorHorizontal / 2
                top = paddingTop
                right = highlightedBar!!.rect.right + barSpacing * factorHorizontal / 2
                bottom = bars[0].rect.bottom
            }

            if (tempRectF.contains(x,y)) {
                    return
                }

        }

        unHighlightBar()
        for (bar in bars) {
            with (tempRectF) {
                left = bar.rect.left-barSpacing*factorHorizontal/2
                top = paddingTop
                right = bar.rect.right+barSpacing*factorHorizontal/2
                bottom = height - paddingBottom
            }

            if (tempRectF.contains(x,y) && bar.color != 0) {
                bar.color = accentColor
                highlightedBar = bar
                invalidate()
                return
            }
        }

        with (tempRectF) {
            left = additionalBar.rect.left-barSpacing*factorHorizontal/2
            top = paddingTop
            right = additionalBar.rect.right+barSpacing*factorHorizontal/2
            bottom = bars[0].rect.bottom
        }

        if (tempRectF.contains(x,y)) {
            additionalBar.color = accentColor
            highlightedBar = additionalBar
            invalidate()
            return
        }

    }

    private fun scroll(x:Int, y:Int) {
        if (points.size <= bars.size)
        unHighlightBar()
        if (isScrolled) return
        isScrolled = true
        val tempFactor = factorVertical
        moveBars(x, tempFactor)
        setFactors()
        animateFactorChange(tempFactor, factorVertical)
        invalidate()
        isScrolled = false
        scope.launch {
            normalizeBarsPosition()
        }
    }

    private fun moveBars(x: Int, tempFactor: Float) {
        if (x < 0) { // swiping right, must show left
            moveBarsRight(x, tempFactor)
        } else {
            moveBarsLeft(x, tempFactor)
        }
    }

   private suspend fun normalizeBarsPosition():Boolean {
        delay(250)
        if (isScrolled || bars[0].i != 0 && bars.last().i != points.size-1) return false
        var center = 0f
        if (barsAmount%2 == 1) {
            center = bars[barsAmount/2].rect.centerX()
            Log.e("center", "${barsAmount/2}")
            Log.e("delta = ", "${center - width/2f}")
        } else {
            center = (bars[barsAmount/2].rect.centerX() + bars[barsAmount/2+1].rect.centerX())/2
        }

       if (center != width/2f) {
           val isNeg = center-width/2f < 0
           val x = abs(((center-width/2f)/5.2).toInt())
           val animator = ValueAnimator.ofInt(0, x)
           animator.duration = 200
           animator.interpolator = LinearInterpolator()
           animator.addUpdateListener {
               if (it.animatedValue == x) isScrolled = false
               moveBars(if (isNeg) -(it.animatedValue as Int) else it.animatedValue as Int, factorVertical)
               invalidate()
           }
           isScrolled = true
           animator.start()
           return true
       }
        return false
    }

    private fun moveBarsLeft(x: Int, tempFactor: Float) {

        if (bars.last().i == points.size-1 && bars.last().rect.right <= width -  barSpacing * factorHorizontal || isAdditionalBarActive && additionalBar.i == points.size-1 && additionalBar.rect.right <= width -  barSpacing * factorHorizontal) {
            return
        }

        for (bar in bars) {
            bar.rect.left -= x
            bar.rect.right -= x
        }
        if (isAdditionalBarActive) {
            additionalBar.rect.left -= x
            additionalBar.rect.right -= x
        }

        if (bars.last().rect.right < width -  barSpacing*factorHorizontal && (!isAdditionalBarActive || additionalBar.rect.right < width -  barSpacing*factorHorizontal)) {
            var nextIndex = 0
            if (additionalBar.rect.left > bars.last().rect.left) {
                nextIndex = additionalBar.i+1
                for (i in 0 until bars.size-1) {
                    Log.d("M_bar", "$i was on ${bars[i].rect.centerX()} now on ${bars[i+1].rect.centerX()}")
                    bars[i].copy(bars[i+1])
                }
                bars.last().copy(additionalBar)
            }
            else {
                if (isAdditionalBarActive && additionalBar.rect.right > 0) {
                    for (i in bars.size-1 downTo 1) {
                        Log.d("M_bar", "barbar $i was on ${bars[i].rect.centerX()} now on ${bars[i-1].rect.centerX()}")
                        bars[i].copy(bars[i-1])
                    }
                    nextIndex = bars.last().i+1
                    bars[0].copy(additionalBar)
                } else {
                    nextIndex = bars.last().i+1
                }
            }
            if (nextIndex>=points.size) {return}
            additionalBar.i = nextIndex
            additionalBar.value = points[nextIndex].first
            additionalBar.xCoord = points[nextIndex].second
            additionalBar.color = if(additionalBar.value >= limit) {successColor} else {defaultColor}
            additionalBar.color = accentColor
            with (additionalBar.rect) {
                bottom = height - paddingBottom
                top = if (additionalBar.value <= maxValue/10)
                {bottom - zeroHeight*factorHorizontal/1.3f}
                else {height - additionalBar.value*factorVertical - rounding*factorHorizontal/1.3f}
                left = bars.last().rect.right + barSpacing * factorHorizontal
                right = left + barWidth * factorHorizontal
            }
            isAdditionalBarActive = true
        }
    }

    private fun moveBarsRight(x: Int, tempFactor: Float) {

        if (bars[0].i == 0 && bars[0].rect.left >= barSpacing * factorHorizontal || isAdditionalBarActive && additionalBar.i == 0 && additionalBar.rect.left >= barSpacing * factorHorizontal) {
            return
        }

        for (bar in bars) {
            bar.rect.left -= x
            bar.rect.right -=x
        }
        if (isAdditionalBarActive) {
            additionalBar.rect.left-=x
            additionalBar.rect.right-=x
        }

        if (bars[0].rect.left > barSpacing*factorHorizontal && (!isAdditionalBarActive || additionalBar.rect.left > barSpacing*factorHorizontal)) {
            var nextIndex = 0
            if (additionalBar.rect.left > bars.last().rect.left) {
                nextIndex = bars[0].i-1
                for (i in 0 until bars.size-1) {
                    bars[i].copy(bars[i+1])
                }
                if (isAdditionalBarActive) {bars.last().copy(additionalBar)}
            }
            else {
                if (isAdditionalBarActive && additionalBar.rect.right > 0) {
                    for (i in bars.size-1 downTo 1) {
                        bars[i].copy(bars[i-1])
                    }
                        nextIndex = additionalBar.i-1
                        bars[0].copy(additionalBar)
                } else {
                    nextIndex = bars[0].i-1
                }

            }
            if (nextIndex<0) {return}
            additionalBar.i = nextIndex
            additionalBar.value = points[nextIndex].first
            additionalBar.xCoord = points[nextIndex].second
            additionalBar.color = if(additionalBar.value >= limit) {successColor} else {defaultColor}
            with (additionalBar.rect) {
                bottom = height - paddingBottom
                top = if (additionalBar.value <= maxValue/10)
                    {bottom - zeroHeight*factorHorizontal/1.3f}
                else {height - additionalBar.value*factorVertical - rounding*factorHorizontal/1.3f}
                right = bars[0].rect.left - barSpacing * factorHorizontal
                left = right - barWidth * factorHorizontal
            }
            isAdditionalBarActive = true
        }
    }

    private fun animateBars(length:Long = 400) {
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

    private fun animateFactorChange(from:Float, to:Float, duration: Long = defaultAnimationDuration) {
        if (isVerticalFactorAnimated) return
        val animator = ValueAnimator.ofFloat(from, to)
        animator.duration = duration
        animator.interpolator = DecelerateInterpolator(3f)
        animator.addUpdateListener {
            factorVertical = it.animatedValue as Float
            invalidate()
            if (it.animatedValue as Float >= to*0.99) {
                isVerticalFactorAnimated = false
            }
        }
        isVerticalFactorAnimated = true
        animator.start()
    }

    fun notifyDataSetChanged() {
        //maxValue = points.max() ?: limit+2
        isValueSet = false
        invalidate()
    }

    private inner class MyGestureListener : SimpleOnGestureListener() {
        public var isScrolled = false

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            isScrolled = true
            scroll(distanceX.toInt(), distanceY.toInt())
            isScrolled = false
            return true
        }
    }


}