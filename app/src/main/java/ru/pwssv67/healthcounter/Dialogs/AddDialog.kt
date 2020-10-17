package ru.pwssv67.healthcounter.Dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.w3c.dom.Text
import ru.pwssv67.healthcounter.App
import ru.pwssv67.healthcounter.Extensions.hideKeyboard
import ru.pwssv67.healthcounter.Extensions.showKeyboard
import ru.pwssv67.healthcounter.R

class AddDialog(val isFood:Boolean = true, val isAdd:Boolean = true):DialogFragment() {

    lateinit var addButton: TextView
    lateinit var input: EditText
    lateinit var caption: TextView
    lateinit var headerCaption: TextView

    lateinit var mListener: AddDialogListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.add_dialog, container, false)
        if(dialog!=null && getDialog()?.window != null) {
            getDialog()?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            getDialog()?.window?.requestFeature(Window.FEATURE_NO_TITLE);
            getDialog()?.window?.setBackgroundDrawableResource(R.drawable.add_dialog_rounded_bg)
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val view = layoutInflater.inflate(R.layout.add_dialog, null)
        //inputCalories = et_calories_counter
    }
    public interface AddDialogListener{
        public fun onDialogAddClick(dialog:DialogFragment, counter:Int, isFood: Boolean, isAdd: Boolean)

    }


    public override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.add_dialog, null)
        input = view?.findViewById(R.id.et_calories_counter) as EditText
        builder.setView(view)
        addButton = view.findViewById(R.id.tv_add_food) as TextView
        addButton.setOnClickListener {
            mListener.onDialogAddClick(this, if (input.text.isNullOrBlank() || input.text.length > 4) {0}  else {input.text.toString().toInt()}, isFood, isAdd)
        }
        caption = view.findViewById(R.id.tv_add_food_calories) as TextView
        if (!isFood){
            caption.text = getText(R.string.minutes)
        }
        headerCaption = view.findViewById(R.id.header_caption) as TextView
        if (!isAdd) {
            headerCaption.text = getText(R.string.how_much_subtract)
        }
        input.setOnKeyListener{ view: View, i: Int, keyEvent: KeyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                mListener.onDialogAddClick(this, if (input.text.isNullOrBlank() || input.text.length > 4) {0}  else {input.text.toString().toInt()}, isFood, isAdd)
                true
            } else {
                false
            }
        }
        input.showSoftInputOnFocus = true
        /*
        input.setOnFocusChangeListener { v, hasFocus ->
            val imm = App.applicationContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if(hasFocus) {
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
            } else {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        */
        input.requestFocus()
        return builder.create()
    }

    override fun onAttach(activity: Activity) {

        try {
            mListener = activity as AddDialogListener
        } catch (e: ClassCastException) {
            throw java.lang.ClassCastException(activity.toString() + "must implement AddDialogListener")
        }
        super.onAttach(activity)

    }

}