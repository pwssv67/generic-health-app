package ru.pwssv67.healthcounter.UI.Activity

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.pwssv67.healthcounter.UI.Dialogs.AddDialog
import ru.pwssv67.healthcounter.Extensions.DayStats
import ru.pwssv67.healthcounter.Extensions.Goal
import ru.pwssv67.healthcounter.Extensions.Profile
import ru.pwssv67.healthcounter.R
import ru.pwssv67.healthcounter.ViewModels.DayViewModel

class MainActivity : AppCompatActivity(), AddDialog.AddDialogListener {

    private lateinit var viewModel: DayViewModel
    private lateinit var day : DayStats
    private lateinit var drinkCounterView: TextView
    private lateinit var eatCounterView: TextView
    private lateinit var trainingCounterView: TextView
    private lateinit var drinkAdd: ImageView
    private lateinit var eatAdd: ImageView
    private lateinit var trainingAdd: ImageView
    private lateinit var drinkMinus: ImageView
    private lateinit var eatMinus: ImageView
    private lateinit var trainingMinus: ImageView
    private lateinit var drinkBackground: View
    private lateinit var trainingBackground: View
    private lateinit var imageDrink: ImageView
    private lateinit var imageTraining: ImageView
    private lateinit var caloriesBackground: View
    private lateinit var eatImage: ImageView
    private lateinit var showHistory: ImageView
    private lateinit var settingsButton:ImageView
    private lateinit var helpButton: ImageView
    lateinit var mAdView:AdView
    var isGoalReachedDrink= false
    var isGoalReachedTraining = false
    var isGoalReachedCalories = false
    lateinit var profile: Profile
    private val ANIMATION_LONG:Long = 1000
    private val ANIMATION_SHORT:Long = 375


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        initViewModel()
        loadData()

        MobileAds.initialize(this) {}

        mAdView = av_ad_view
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()
        profile = viewModel.getProfile()
        updateUIColors()
    }

    private fun initViewModel() {
        viewModel = DayViewModel(application)
            //ViewModelProvider(this, )
        viewModel.getDayStatsData().observe(this, Observer {
            if (it == null) {day = DayStats(0,0,0)}
            else {
                day = it
                drinkCounterView.text = "${day.glasses}"
                eatCounterView.text = "${day.calories}"
                trainingCounterView.text = "${day.training}"
                if (!isGoalReachedDrink && !isGoalReachedTraining && !isGoalReachedCalories) updateUIColors()
            }
        })
        profile = viewModel.getProfile()
    }

    private fun loadData() {
        //Load data from saved
        day = viewModel.getDayStatsData().value ?: DayStats(
            0,
            0,
            0
        )
        //updateUIColors()
        drinkCounterView.text = "${day.glasses}"
        eatCounterView.text = "${day.calories}"
        trainingCounterView.text = "${day.training}"
    }

    private fun updateUIColors() {

        if (isGoalReachedDrink && day.glasses<profile.drink_goal) {
            goalUnreachedGlasses()
            isGoalReachedDrink = false
        }

        if (day.glasses >= profile.drink_goal && !isGoalReachedDrink) {
            goalReachedGlasses()
            isGoalReachedDrink = true
        }

        if (day.calories < profile.eat_goal_second && isGoalReachedCalories) {
            goalUnreachedCalories()
            isGoalReachedCalories = false
        }
        if (day.calories>=profile.eat_goal_second && !isGoalReachedCalories) {
            goalReachedCalories()
            isGoalReachedCalories = true

        }

        if (day.training<profile.training_goal && isGoalReachedTraining) {
            goalUnreachedTraining()
            isGoalReachedTraining = false
        }
        if (day.training >= profile.training_goal && !isGoalReachedTraining) {
            goalReachedTraining()
            isGoalReachedTraining = true
        }
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
        drinkBackground = v_drink_background
        trainingBackground = v_training_background
        imageDrink = iv_drink_image
        imageTraining = iv_training_image
        caloriesBackground = v_eat_background
        eatImage = iv_eat_image
        showHistory = iv_show_history
        settingsButton = iv_settings
        helpButton = iv_help




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

        showHistory.setOnClickListener {
            val intent = Intent(applicationContext, HistoryActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intent)
        }

        helpButton.setOnClickListener {
            viewHelp()
        }

    }

    private fun viewHelp() {
        Snackbar.make(layout_main, "Скоро будет помощь, а пока ремонт", Snackbar.LENGTH_LONG).show()
    }

    private fun viewTraining() {
        Snackbar.make(layout_main, "Скоро будет тренировка, а пока ремонт", Snackbar.LENGTH_LONG).show()
    }

    private fun viewEat() {
        Snackbar.make(layout_main, "Скоро будет вкусно, а пока ремонт", Snackbar.LENGTH_LONG).show()
    }

    private fun viewDrinks() {
        Snackbar.make(layout_main, "Скоро будут напитки, а пока ремонт", Snackbar.LENGTH_LONG).show()
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
        if (day.glasses>=1) { day.glasses--}
        drinkCounterView.text = "${day.glasses}"
        viewModel.saveDayStatsData(day)
        if (day.glasses < profile.drink_goal && isGoalReachedDrink) {
            goalUnreachedGlasses()
            isGoalReachedDrink = false
        }
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
        if (day.glasses>=profile.drink_goal && !isGoalReachedDrink) {
            isGoalReachedDrink = true
            goalReached(Goal.GLASSES.goalType)
        }
    }

    override fun onDialogAddClick(dialog: DialogFragment, counter: Int, isFood: Boolean, isAdd: Boolean) {
        dialog.dismiss()
        if (isFood){
            if (isAdd) {
                day.calories += counter
                if (day.calories>=profile.eat_goal_second && !isGoalReachedCalories) {
                    goalReachedCalories()
                    isGoalReachedCalories = true
                }
            }
            else {
                if (day.calories > counter) {
                    day.calories -= counter
                }
                else {
                    day.calories = 0
                }

                if (day.calories < profile.eat_goal_second && isGoalReachedCalories) {
                    goalUnreachedCalories()
                    isGoalReachedCalories = false
                }
            }
            eatCounterView.text = "${day.calories}"
        } else {
            if (isAdd) {
                day.training += counter
                if (day.training >= profile.training_goal && !isGoalReachedTraining) {
                    isGoalReachedTraining = true
                    goalReached(Goal.TRAINING.goalType)
                }
            }
            else {
                if (day.training > counter) {
                    day.training -= counter
                }
                else {
                    day.training = 0
                }
                if (day.training<profile.training_goal && isGoalReachedTraining) {
                    goalUnreachedTraining()
                    isGoalReachedTraining = false
                }
            }
            trainingCounterView.text = "${day.training}"
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
        val colorFrom = getColor(R.color.colorPrimary)
        val colorTo = getColor(R.color.colorPrimaryDark)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        val background = drinkBackground.background as Drawable
        colorAnimation.duration = ANIMATION_LONG
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

        val textColorFrom = getColor(R.color.primaryText)
        val textColorTo = getColor(R.color.backgroundColor)
        val addImage = drinkAdd.drawable
        val minusImage = drinkMinus.drawable
        val textColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
        textColorAnimation.duration = ANIMATION_SHORT
        textColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            drinkCounterView.setTextColor(animation?.animatedValue as Int)
            //image.setTint(animation.animatedValue as Int)
            addImage.setTint(animation.animatedValue as Int)
            minusImage.setTint(animation.animatedValue as Int)
        })

        val scale = 1.0.toFloat()
        Log.e("", "$scale")
        val scaleAnimation = ValueAnimator.ofFloat(scale, (scale*1.4).toFloat(), scale)
        scaleAnimation.duration = ANIMATION_SHORT
        scaleAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageDrink.scaleX = animation?.animatedValue as Float
            imageDrink.scaleY = animation.animatedValue as Float
        })

        val rotateAnimation = ValueAnimator.ofInt(0, 720)
        rotateAnimation.duration = ANIMATION_LONG
        rotateAnimation.interpolator = OvershootInterpolator()
        rotateAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageDrink.rotation = (animation?.animatedValue as Int).toFloat()
            //if (animation.animatedValue as Int >= 360) {
            //    imageDrink.setImageResource(R.drawable.ic_check_circle_black_24dp)
            //}
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
        val colorFrom = getColor(R.color.colorPrimary)
        val colorTo = getColor(R.color.colorPrimaryDark)

        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        val background = caloriesBackground.background as Drawable
        colorAnimation.duration = ANIMATION_LONG
        colorAnimation.interpolator = OvershootInterpolator()
        colorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            background.setTint(animation?.animatedValue as Int)
        })

        val textColorFrom = getColor(R.color.primaryText)
        val textColorTo = getColor(R.color.backgroundColor)
        val addImage = eatAdd.drawable
        val minusImage = eatMinus.drawable
        val textColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
        textColorAnimation.duration = ANIMATION_SHORT
        textColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            eatCounterView.setTextColor(animation?.animatedValue as Int)
        })

        val buttonColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
        buttonColorAnimation.duration = ANIMATION_SHORT
        buttonColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            addImage.setTint(animation?.animatedValue as Int)
            minusImage.setTint(animation.animatedValue as Int)
        })

        textColorAnimation.start()
        buttonColorAnimation.start()
        colorAnimation.start()
    }

    private fun goalReachedTraining() {
        val colorFrom = getColor(R.color.colorPrimary)
        val colorTo = getColor(R.color.colorPrimaryDark)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        val background = trainingBackground.background as Drawable

        colorAnimation.duration = ANIMATION_LONG
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

        val textColorFrom = getColor(R.color.primaryText)
        val textColorTo = getColor(R.color.backgroundColor)
        val textColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
        textColorAnimation.duration = ANIMATION_SHORT
        textColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            trainingAdd.drawable.setTint(animation?.animatedValue as Int)
            trainingMinus.drawable.setTint(animation.animatedValue as Int)
            trainingCounterView.setTextColor(animation.animatedValue as Int)
        })


        val scale = imageTraining.scaleX
        val scaleAnimation = ValueAnimator.ofFloat(scale, (scale*1.4).toFloat(), scale)
        scaleAnimation.duration = ANIMATION_SHORT
        scaleAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageTraining.scaleX = animation?.animatedValue as Float
            imageTraining.scaleY = animation.animatedValue as Float
        })

        val rotateAnimation = ValueAnimator.ofInt(0, 720)
        rotateAnimation.duration = ANIMATION_LONG
        rotateAnimation.interpolator = OvershootInterpolator()
        rotateAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageTraining.rotation = (animation?.animatedValue as Int).toFloat()
            //if (animation.animatedValue as Int >= 360) {
            //    imageTraining.setImageResource(R.drawable.ic_check_circle_black_24dp)
            //}
        })

        textColorAnimation.start()
        //alphaAnimation.start()
        colorAnimation.start()
        scaleAnimation.start()
        rotateAnimation.start()
        //trainingAdd.isClickable = false
        //trainingMinus.isClickable = false
    }

    private fun goalUnreachedGlasses() {
        val colorFrom = getColor(R.color.colorPrimaryDark)
        val colorTo = getColor(R.color.colorPrimary)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        val background = drinkBackground.background as Drawable
        colorAnimation.duration = ANIMATION_LONG
        colorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            background.setTint(animation?.animatedValue as Int)
        })

        val image = getDrawable(R.drawable.ic_glass_of_water_64dp)

        val textColorFrom = getColor(R.color.backgroundColor)
        val textColorTo = getColor(R.color.primaryText)
        val addImage = drinkAdd.drawable
        val minusImage = drinkMinus.drawable
        val textColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
        textColorAnimation.duration = ANIMATION_SHORT
        textColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            drinkCounterView.setTextColor(animation?.animatedValue as Int)
            minusImage.setTint(animation.animatedValue as Int)
        })

        val addColorTo = getColor(R.color.colorPrimaryDark)
        val addColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom,addColorTo)
        addColorAnimation.duration = ANIMATION_SHORT
        addColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            addImage.setTint(animation?.animatedValue as Int)
        })

        val scale = 1.0.toFloat()
        val scaleAnimation = ValueAnimator.ofFloat(scale, (scale*1.4).toFloat(), scale)
        scaleAnimation.duration = ANIMATION_SHORT
        scaleAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageDrink.scaleX = animation?.animatedValue as Float
            imageDrink.scaleY = animation.animatedValue as Float
        })

        val rotateAnimation = ValueAnimator.ofInt(0, -720)
        rotateAnimation.duration = ANIMATION_LONG
        rotateAnimation.interpolator = OvershootInterpolator()
        rotateAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageDrink.rotation = (animation?.animatedValue as Int).toFloat()
            //if (animation.animatedValue as Int <= -360) {
            //    imageDrink.setImageResource(R.drawable.ic_glass_of_water_64dp)
            //}
        })

        colorAnimation.start()
        textColorAnimation.start()
        addColorAnimation.start()
        rotateAnimation.start()
        scaleAnimation.start()
        //drinkAdd.isClickable = false
        //drinkMinus.isClickable = false
    }

    private fun goalUnreachedTraining() {
        val colorFrom = getColor(R.color.colorPrimaryDark)
        val colorTo = getColor(R.color.colorPrimary)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        val background = trainingBackground.background as Drawable

        colorAnimation.duration = ANIMATION_LONG
        colorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            background.setTint(animation?.animatedValue as Int)
        })

        val textColorFrom = getColor(R.color.backgroundColor)
        val textColorTo = getColor(R.color.primaryText)
        val textColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
        textColorAnimation.duration = ANIMATION_SHORT
        textColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            //trainingAdd.drawable.setTint(animation?.animatedValue as Int)
            trainingMinus.drawable.setTint(animation?.animatedValue as Int)
            trainingCounterView.setTextColor(animation.animatedValue as Int)
        })

        val addColorTo = getColor(R.color.colorPrimaryDark)
        val addColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, addColorTo)
        addColorAnimation.duration = ANIMATION_SHORT
        addColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            trainingAdd.drawable.setTint(animation?.animatedValue as Int)
        })

        val scale = imageTraining.scaleX
        val scaleAnimation = ValueAnimator.ofFloat(scale, (scale*1.4).toFloat(), scale)
        scaleAnimation.duration = ANIMATION_SHORT
        scaleAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageTraining.scaleX = animation?.animatedValue as Float
            imageTraining.scaleY = animation.animatedValue as Float
        })

        val rotateAnimation = ValueAnimator.ofInt(0, -720)
        rotateAnimation.duration = ANIMATION_LONG
        rotateAnimation.interpolator = OvershootInterpolator()
        rotateAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            imageTraining.rotation = (animation?.animatedValue as Int).toFloat()
            //if (animation.animatedValue as Int <= -360) {
            //    imageTraining.setImageResource(R.drawable.ic_fitness_64dp)
            //}
        })

        colorAnimation.start()
        textColorAnimation.start()
        addColorAnimation.start()
        scaleAnimation.start()
        rotateAnimation.start()
    }

    private fun goalUnreachedCalories() {
        val colorFrom = getColor(R.color.colorPrimaryDark)
        val colorTo = getColor(R.color.colorPrimary)

        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        val background = caloriesBackground.background as Drawable
        colorAnimation.duration = ANIMATION_LONG
        colorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
            background.setTint(animation?.animatedValue as Int)
        })


            val textColorFrom = getColor(R.color.backgroundColor)
            val textColorTo = getColor(R.color.primaryText)
            val addImage = eatAdd.drawable
            val minusImage = eatMinus.drawable
            val textColorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
            textColorAnimation.duration = ANIMATION_SHORT

            textColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
                eatCounterView.setTextColor(animation?.animatedValue as Int)
            })

            val buttonColorAnimation =
                ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, textColorTo)
            buttonColorAnimation.duration = ANIMATION_SHORT
            buttonColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
                //addImage.setTint(animation?.animatedValue as Int)
                minusImage.setTint(animation?.animatedValue as Int)
            })

            val addColorTo = getColor(R.color.colorPrimaryDark)
            val addButtonColorAnimation =
                ValueAnimator.ofObject(ArgbEvaluator(), textColorFrom, addColorTo)
            addButtonColorAnimation.duration = ANIMATION_SHORT
            addButtonColorAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
                addImage.setTint(animation?.animatedValue as Int)
            })

            textColorAnimation.start()
            buttonColorAnimation.start()
            addButtonColorAnimation.start()
        colorAnimation.start()
    }
}


