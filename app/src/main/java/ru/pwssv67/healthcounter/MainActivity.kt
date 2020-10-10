package ru.pwssv67.healthcounter

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.util.rangeTo
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import ru.pwssv67.healthcounter.Dialogs.AddDialog
import ru.pwssv67.healthcounter.Extensions.DayStats
import ru.pwssv67.healthcounter.Extensions.Goal
import ru.pwssv67.healthcounter.Extensions.showKeyboard

class MainActivity : AppCompatActivity(), AddDialog.AddDialogListener {

    private lateinit var viewModel: DayViewModel
    private lateinit var day : DayStats
    lateinit var drinkCounterView: TextView
    lateinit var eatCounterView: TextView
    lateinit var trainingCounterView: TextView
    lateinit var drinkAdd: ImageView
    lateinit var eatAdd: ImageView
    lateinit var trainingAdd: ImageView
    lateinit var drinkMinus: ImageView
    lateinit var eatMinus: ImageView
    lateinit var trainingMinus: ImageView
    lateinit var drinkLayout:LinearLayout
    lateinit var drinkCaption:TextView
    lateinit var trainingLayout: LinearLayout
    lateinit var trainingCaption: TextView
    lateinit var imageDrink: ImageView
    lateinit var imageTraining: ImageView
    lateinit var caloriesLayout: LinearLayout
    lateinit var caloriesCaption: TextView
    lateinit var eatImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()
        bindViews()
        loadData()
    }

    private fun initViewModel() {
        viewModel = DayViewModel()
            //ViewModelProvider(this, )
        viewModel.getDayStatsData().observe(this, Observer {day = it})
    }

    private fun loadData() {
        //Load data from saved
        day = viewModel.getDayStatsData().value ?: DayStats(
            0,
            0,
            0
        )
        if (day.glasses >= 8) {goalReachedGlasses()}
        if (day.calories >= 1500) {
            goalReachedCalories()

            val buttonColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), getColor(R.color.secondaryText), getColor(R.color.backgroundColor))
            buttonColorAnimation.duration = 1500
            buttonColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
                eatAdd.drawable.setTint(animation?.animatedValue as Int)
                eatMinus.drawable.setTint(animation.animatedValue as Int)
            })
            buttonColorAnimation.start()
        }
        if (day.training >= 30) {goalReachedTraining()}
        drinkCounterView.text = "${day.glasses}"
        eatCounterView.text = "${day.calories}"
        trainingCounterView.text = "${day.training}"
    }

    private fun bindViews() {
        drinkCounterView = tv_drink_counter
        eatCounterView = tv_eat_counter
        trainingCounterView = tv_training_counter
        drinkAdd = iv_drink_plus
        eatAdd = iv_eat_plus
        trainingAdd = iv_training_plus
        drinkMinus = iv_drink_minus
        eatMinus = iv_eat_minus
        trainingMinus = iv_training_minus
        drinkLayout = layout_drink
        drinkCaption = tv_glasses
        trainingLayout = layout_training
        trainingCaption = tv_training
        imageDrink = iv_drink_image
        imageTraining = iv_training_image
        caloriesLayout = layout_eat
        caloriesCaption = tv_calories
        eatImage = iv_eat_image



        //Click Listeners

        //Add
        drinkAdd.setOnClickListener{
            addDrink()
        }

        eatAdd.setOnClickListener{
            addEat()
        }

        trainingAdd.setOnClickListener {
            addTraining()
        }

        //Minus
        drinkMinus.setOnClickListener {
            minusDrink()
        }

        eatMinus.setOnClickListener {
            minusEat()
        }

        trainingMinus.setOnClickListener {
            minusTraining()
        }

        // Text Views
        drinkCounterView.setOnClickListener {
            viewDrinks()
        }

        eatCounterView.setOnClickListener {
            viewEat()
        }

        trainingCounterView.setOnClickListener {
            viewTraining()
        }

    }

    private fun viewTraining() {
        Snackbar.make(layout_main, "Скоро будет вкусно, а пока ремонт", Snackbar.LENGTH_LONG).show()
    }

    private fun viewEat() {
        Snackbar.make(layout_main, "Скоро будет вкусно, а пока ремонт", Snackbar.LENGTH_LONG).show()
    }

    private fun viewDrinks() {
        Snackbar.make(layout_main, "Скоро будет вкусно, а пока ремонт", Snackbar.LENGTH_LONG).show()
    }

    private fun minusTraining() {
        val newFragment = AddDialog(false, false)
        newFragment.show(supportFragmentManager, "")
    }

    private fun minusEat() {
        val newFragment = AddDialog(true, false)
        newFragment.show(supportFragmentManager, "")
    }

    private fun minusDrink() {
        if (day.glasses>=1) { day.glasses-- }
        drinkCounterView.text = "${day.glasses}"
        viewModel.saveDayStatsData(day)
        showKeyboard(iv_drink_minus)
    }

    private fun addTraining() {
        //trainingCounter++
        //trainingCounterView.text = "$trainingCounter"
        val newFragment = AddDialog(false)
        newFragment.show(supportFragmentManager, "")
    }

    private fun addEat() {
        //eatCounter++
        //eatCounterView.text = "$eatCounter"
        val newFragment = AddDialog()
        newFragment.show(supportFragmentManager, "")

    }

    private fun addDrink() {
        day.glasses++
        drinkCounterView.text = "${day.glasses}"
        viewModel.saveDayStatsData(day)
        if (day.glasses>=8) {
            goalReached(Goal.GLASSES.goalType)
        }
    }

    override fun onDialogAddClick(dialog: DialogFragment, counter: Int, isFood: Boolean, isAdd: Boolean) {
        dialog.dismiss()
        if (isFood){
            if (isAdd) {
                day.calories += counter
            }
            else {
                if (day.calories > counter) {
                    day.calories -= counter
                }
                else {
                    day.calories = 0
                }
            }
            eatCounterView.text = "${day.calories}"
            if (day.calories in 1500..2500 || day.calories>=2500 && day.calories-counter<=2500 && isAdd || day.calories < 1500 && !isAdd && day.calories + counter>1500 ){
                goalReached(Goal.CALORIES.goalType)
            }
        } else {
            if (isAdd) {
                day.training += counter
            }
            else {
                if (day.training > counter) {
                    day.training -= counter
                }
                else {
                    day.training = 0
                }
            }
            trainingCounterView.text = "${day.training}"
            if (day.training >= 30) {
                goalReached(Goal.TRAINING.goalType)
            }
        }
        viewModel.saveDayStatsData(day)
    }

    fun goalReached(goalType: String) {
        when (goalType){
            Goal.GLASSES.goalType -> goalReachedGlasses()
            Goal.CALORIES.goalType -> goalReachedCalories()
            Goal.TRAINING.goalType -> goalReachedTraining()
            else -> Error("wrong goal to goalReached")
        }
    }

    private fun goalReachedGlasses() {
        val colorFrom = getColor(R.color.backgroundColor)
        val colorTo = getColor(R.color.successColor)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        val background = drinkLayout.background as Drawable
        colorAnimation.duration = 1000
        colorAnimation.interpolator = OvershootInterpolator()
        colorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            background.setTint(animation?.animatedValue as Int)
        })

        //val alphaAnimation = ValueAnimator.ofInt(0xFF, 0x00)
        val image = imageDrink.drawable
        //alphaAnimation.duration = 500
        //alphaAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
        //    drinkAdd.imageAlpha = alphaAnimation.animatedValue as Int
        //    drinkMinus.imageAlpha = alphaAnimation.animatedValue as Int
        //})

        val textColorFrom = getColor(R.color.secondaryText)
        val textColorTo = getColor(R.color.backgroundColor)
        val addImage = drinkAdd.drawable
        val minusImage = drinkMinus.drawable
        val textColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
        textColorAnimation.duration = 500
        textColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            drinkCounterView.setTextColor(animation?.animatedValue as Int)
            drinkCaption.setTextColor(animation.animatedValue as Int)
            image.setTint(animation.animatedValue as Int)
            addImage.setTint(animation.animatedValue as Int)
            minusImage.setTint(animation.animatedValue as Int)
        })

        val scale = imageDrink.scaleX
        val scaleAnimation = ValueAnimator.ofFloat(scale, (scale*1.4).toFloat(), scale)
        scaleAnimation.duration = 500
        scaleAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageDrink.scaleX = animation?.animatedValue as Float
            imageDrink.scaleY = animation.animatedValue as Float
        })

        val rotateAnimation = ValueAnimator.ofInt(0, 720)
        rotateAnimation.duration = 1000
        rotateAnimation.interpolator = OvershootInterpolator()
        rotateAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageDrink.rotation = (animation?.animatedValue as Int).toFloat()
            if (animation.animatedValue as Int >= 360) {
                imageDrink.setImageResource(R.drawable.ic_check_circle_black_24dp)
            }
        })

        //alphaAnimation.start()
        colorAnimation.start()
        textColorAnimation.start()
        rotateAnimation.start()
        scaleAnimation.start()
        //drinkAdd.isClickable = false
        //drinkMinus.isClickable = false
    }

    private fun goalReachedCalories() {
        var colorFrom = getColor(R.color.backgroundColor)
        var colorTo = getColor(R.color.backgroundColor)
        when (day.calories) {
                in 1500..2500 -> {
                    colorFrom = getColor(R.color.backgroundColor)
                    colorTo = getColor(R.color.warningColor)
                }
                in 0..1500 -> {
                    return
                }
                else -> {
                    colorFrom = getColor(R.color.warningColor)
                    colorTo = getColor(R.color.dangerColor)

                }
        }
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        val background = caloriesLayout.background as Drawable
        colorAnimation.duration = 1000
        colorAnimation.interpolator = OvershootInterpolator()
        colorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            background.setTint(animation?.animatedValue as Int)
        })

        val textColorFrom = getColor(R.color.secondaryText)
        val textColorTo = getColor(R.color.backgroundColor)
        val addImage = eatAdd.drawable
        val minusImage = eatMinus.drawable
        val textColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
        textColorAnimation.duration = 1000
        textColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            eatCounterView.setTextColor(animation?.animatedValue as Int)
            caloriesCaption.setTextColor(animation.animatedValue as Int)
            eatImage.drawable.setTint(animation.animatedValue as Int)
        })

        val buttonColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
        buttonColorAnimation.duration = 1000
        buttonColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            addImage.setTint(animation?.animatedValue as Int)
            minusImage.setTint(animation.animatedValue as Int)
        })

        textColorAnimation.start()
        buttonColorAnimation.start()
        colorAnimation.start()
    }

    private fun goalReachedTraining() {
        val colorFrom = getColor(R.color.backgroundColor)
        val colorTo = getColor(R.color.successColor)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        val background = trainingLayout.background as Drawable

        colorAnimation.duration = 1000
        colorAnimation.interpolator = OvershootInterpolator()
        colorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            background.setTint(animation?.animatedValue as Int)
        })

        //val alphaAnimation = ValueAnimator.ofInt(0xFF, 0x00)
        //alphaAnimation.duration = 500
        //alphaAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
        //    trainingAdd.imageAlpha = alphaAnimation.animatedValue as Int
        //    trainingMinus.imageAlpha = alphaAnimation.animatedValue as Int
        //})

        val textColorFrom = getColor(R.color.secondaryText)
        val textColorTo = getColor(R.color.backgroundColor)
        val textColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
        textColorAnimation.duration = 1000
        textColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            trainingAdd.drawable.setTint(animation?.animatedValue as Int)
            trainingMinus.drawable.setTint(animation.animatedValue as Int)
            trainingCounterView.setTextColor(animation.animatedValue as Int)
            trainingCaption.setTextColor(animation.animatedValue as Int)
        })


        val scale = imageTraining.scaleX
        val scaleAnimation = ValueAnimator.ofFloat(scale, (scale*1.4).toFloat(), scale)
        scaleAnimation.duration = 500
        scaleAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageTraining.scaleX = animation?.animatedValue as Float
            imageTraining.scaleY = animation.animatedValue as Float
        })

        val rotateAnimation = ValueAnimator.ofInt(0, 720)
        rotateAnimation.duration = 1000
        rotateAnimation.interpolator = OvershootInterpolator()
        rotateAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageTraining.rotation = (animation?.animatedValue as Int).toFloat()
            if (animation.animatedValue as Int >= 360) {
                imageTraining.setImageResource(R.drawable.ic_check_circle_black_24dp)
            }
        })

        //alphaAnimation.start()
        colorAnimation.start()
        textColorAnimation.start()
        scaleAnimation.start()
        rotateAnimation.start()
        //trainingAdd.isClickable = false
        //trainingMinus.isClickable = false
    }

}


