package ru.pwssv67.healthcounter.UI.View

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import ru.pwssv67.healthcounter.Extensions.ChartBar
import ru.pwssv67.healthcounter.R
import kotlin.collections.ArrayList

class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?=null,
    defStyleAttr:Int=0
):View(context ,attrs ,defStyleAttr) {
    companion object {
        val defaultPoints = ArrayList<Int>(listOf(7,8,0,10,6).reversed())
    }

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private val bar1 = ChartBar(RectF(), paint)
    private val bar2 = ChartBar(RectF(), paint)
    private val bar3 = ChartBar(RectF(), paint)
    private val bar4 = ChartBar(RectF(), paint)
    private val bar5 = ChartBar(RectF(), paint)
    private val barLeft = ChartBar(RectF(), paint)
    private val barRight = ChartBar(RectF(), paint)
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
        if (!this.isInEditMode) {textPaint.typeface = Typeface.create(ResourcesCompat.getFont(context, R.font.roboto_regular), Typeface.NORMAL)}
        else {textPaint.typeface = Typeface.SANS_SERIF}
        textPaint.textAlign = Paint.Align.CENTER
        canvas?.drawText(text, width/2f, 4*factorVertical, textPaint)
    }

    private fun drawValue(canvas: Canvas?) {
        if (highlightedBar != null) {
            val bar= highlightedBar as ChartBar
            textPaint.color = bar.color
            textPaint.textSize = 6*factorHorizontal
            if (!this.isInEditMode) {textPaint.typeface = Typeface.create(ResourcesCompat.getFont(context, R.font.roboto_regular), Typeface.NORMAL)}
            else {textPaint.typeface = Typeface.SANS_SERIF}
            textPaint.textAlign = Paint.Align.CENTER
            canvas?.drawText(bar.value.toString(), width/2f, 8*factorVertical, textPaint)
        } else return
    }

    private fun drawBars(canvas: Canvas?) {
        for (i in 0 until barsAmount) {
            paint.color = bars[i].color
            canvas?.drawRoundRect(
                bars[i].rect,
                rounding * factorHorizontal / 1.5f,
                rounding * factorHorizontal / 1.5f,
                bars[i].paint
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
            bars[i].rect.bottom = height - insetCustom
            if (bars[i].value <=0) {bars[i].rect.top = bars[i].rect.bottom - rounding*factorHorizontal/1.3f}
            else {bars[i].rect.top = height - (bars[i].value)*factorVertical - rounding*factorHorizontal/3f}
            bars[i].rect.left = (i*barWidth + (i+1)*barSpacing )*factorHorizontal
            bars[i].rect.right = bars[i].rect.left + barWidth*factorHorizontal
        }
    }

    private fun setValues() {
        for (i in 0 until barsAmount) {
            bars[i].value = points.reversed()[i]
            if (points[i]>=limit) {
                bars[i].color = succesColor
            }
            else {
                bars[i].color = defaultColor
            }
        }
        maxValue = points.max() ?: limit+2
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> onTouch(event.x, event.y)
            MotionEvent.ACTION_UP -> onUp()
        }
        super.onTouchEvent(event)
        return true

    }

    private fun onUp() {
        val i = bars.indexOf(highlightedBar)
        if (i>=0) {
            bars[i].color = when {
                bars[i].value >= limit -> succesColor
                else -> defaultColor
            }
        }
        highlightedBar = null
        invalidate()
    }

    private fun onTouch(x: Float, y:Float) {
        for (bar in bars) {
            val rect = RectF(bar.rect.left-barSpacing*factorHorizontal/2, insetCustom*factorVertical, bar.rect.right+barSpacing*factorHorizontal/2, bar.rect.bottom)
            if (rect.contains(x,y)) {
                Log.e("yes", "yes")
                bar.color = accentColor
                highlightedBar = bar
                invalidate()
                return
            }
        }
    }

    fun animateBars() {

    }

    fun notifyDataSetChanged() {
        invalidate()
    }



}