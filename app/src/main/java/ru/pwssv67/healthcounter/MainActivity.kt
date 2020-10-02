package ru.pwssv67.healthcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.pwssv67.healthcounter.Dialogs.AddDialog
import ru.pwssv67.healthcounter.Extensions.DayStats
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
        if (day.training>=1) { day.training-- }
        trainingCounterView.text = "${day.training}"
        viewModel.saveDayStatsData(day)
    }

    private fun minusEat() {
        if (day.calories>=1) { day.calories-- }
        eatCounterView.text = "${day.calories}"
        viewModel.saveDayStatsData(day)
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
    }

    override fun onDialogAddClick(dialog: DialogFragment, counter: Int, isFood: Boolean) {
        dialog.dismiss()
        if (isFood){
            day.calories += counter
            eatCounterView.text = "${day.calories}"
        } else {
            day.training += counter
            trainingCounterView.text = "${day.training}"
        }
        viewModel.saveDayStatsData(day)
    }


}


