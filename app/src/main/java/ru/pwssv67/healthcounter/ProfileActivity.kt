package ru.pwssv67.healthcounter

import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.opengl.ETC1Util
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.renderscript.Sampler
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_profile.*
import ru.pwssv67.healthcounter.Extensions.Profile

class ProfileActivity : AppCompatActivity() {
    lateinit var saveButton: TextView
    lateinit var drinkGoal:EditText
    lateinit var caloriesGoal:EditText
    lateinit var trainingGoal:EditText
    lateinit var viewModel:DayViewModel
    lateinit var profile: Profile
    var isEditMode = false

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = DayViewModel(application)
        profile = viewModel.getProfile()
        drinkGoal.setText(profile.drink_goal.toString())
        caloriesGoal.setText(profile.eat_goal_second.toString())
        trainingGoal.setText(profile.training_goal.toString())
    }

    private fun initViews() {
        saveButton = tv_save
        drinkGoal = et_drink_goal_picker
        caloriesGoal = et_calories_goal_picker
        trainingGoal = et_training_goal_picker

        saveButton.setOnClickListener {
            changeState()
        }
    }

    private fun changeState() {
        if (isEditMode) {
            if (et_drink_goal_picker.text == null) {
                et_drink_goal_picker.setText(0)
            }
            if (et_calories_goal_picker.text == null) {
                et_calories_goal_picker.setText(0)
            }
            if (et_training_goal_picker.text == null) {
                et_training_goal_picker.setText(0)
            }
            profile.training_goal = et_training_goal_picker.text.toString().toInt()
            if (profile.training_goal<=0) {profile.training_goal = 30}
            profile.drink_goal = et_drink_goal_picker.text.toString().toInt()
            if (profile.drink_goal <= 0 ) {profile.drink_goal = 8}
            profile.eat_goal_second = et_calories_goal_picker.text.toString().toInt()
            profile.eat_goal_first = if (profile.eat_goal_second>1000) {profile.eat_goal_second-1000} else {500}
            if (profile.eat_goal_second<=0) {
                profile.eat_goal_second = 2500
                profile.eat_goal_first = 1500
            }
            viewModel.saveProfile(profile)

            drinkGoal.isFocusable = false
            drinkGoal.isFocusableInTouchMode = false
            drinkGoal.isEnabled = false

            caloriesGoal.isFocusable = false
            caloriesGoal.isFocusableInTouchMode = false
            caloriesGoal.isEnabled = false

            trainingGoal.isFocusable = false
            trainingGoal.isFocusableInTouchMode = false
            trainingGoal.isEnabled = false

            saveButtonAnimation()

            isEditMode = false
        }
        else {
            drinkGoal.isFocusable = true
            drinkGoal.isFocusableInTouchMode = true
            drinkGoal.isEnabled = true

            caloriesGoal.isFocusable = true
            caloriesGoal.isFocusableInTouchMode = true
            caloriesGoal.isEnabled = true

            trainingGoal.isFocusable = true
            trainingGoal.isFocusableInTouchMode = true
            trainingGoal.isEnabled = true

            saveButtonAnimation()

            isEditMode = true
        }
    }

    private fun saveButtonAnimation() {
        val colorFirst = getColor(R.color.primaryText)
        val colorSecond = getColor(R.color.backgroundColor)
        if (isEditMode) {
            saveButton.text = getText(R.string.edit)


            val backgroundColorAnimation = ValueAnimator.ofArgb(colorSecond, colorFirst)
            backgroundColorAnimation.duration = 175
            backgroundColorAnimation.addUpdateListener { animation ->
                saveButton.background.setTint(animation.animatedValue as Int)
            }

            val textColorAnimation = ValueAnimator.ofArgb(colorFirst, colorSecond)
            textColorAnimation.duration = 175
            textColorAnimation.addUpdateListener { animation ->
                saveButton.setTextColor(animation.animatedValue as Int)
            }

            backgroundColorAnimation.start()
            textColorAnimation.start()
        }
        else {
            saveButton.text = getText(R.string.save)

            val backgroundColorAnimation = ValueAnimator.ofArgb(colorFirst, colorSecond)
            backgroundColorAnimation.duration = 175
            backgroundColorAnimation.addUpdateListener { animation ->
                saveButton.background.setTint(animation.animatedValue as Int)
            }

            val textColorAnimation = ValueAnimator.ofArgb(colorSecond, colorFirst)
            textColorAnimation.duration = 175
            textColorAnimation.addUpdateListener { animation ->
                saveButton.setTextColor(animation.animatedValue as Int)
            }

            backgroundColorAnimation.start()
            textColorAnimation.start()
        }
    }
}
