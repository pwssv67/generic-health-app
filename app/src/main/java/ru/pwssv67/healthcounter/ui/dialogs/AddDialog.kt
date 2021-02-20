package ru.pwssv67.healthcounter.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import ru.pwssv67.healthcounter.R

class AddDialog(private val isFood:Boolean = true, private val isAdd:Boolean = true):DialogFragment() {

    private lateinit var addButton: TextView
    private lateinit var input: EditText
    private lateinit var caption: TextView
    private lateinit var headerCaption: TextView

    private lateinit var mListener: AddDialogListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.add_dialog, container, false)
        if(dialog!=null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setBackgroundDrawableResource(R.drawable.add_dialog_rounded_bg)
        }

        return view
    }

    interface AddDialogListener{
        fun onDialogAddClick(dialog:DialogFragment, counter:Int, isFood: Boolean, isAdd: Boolean)

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val generalView = inflater?.inflate(R.layout.add_dialog, null)
        input = generalView?.findViewById(R.id.et_calories_counter) as EditText
        builder.setView(generalView)
        addButton = generalView.findViewById(R.id.tv_add_food) as TextView
        addButton.setOnClickListener {
            mListener.onDialogAddClick(this, if (input.text.isNullOrBlank() || input.text.length > 4) {0}  else {input.text.toString().toInt()}, isFood, isAdd)
        }
        caption = generalView.findViewById(R.id.tv_add_food_calories) as TextView
        if (!isFood){
            caption.text = getText(R.string.minutes)
        }
        headerCaption = generalView.findViewById(R.id.header_caption) as TextView
        if (!isAdd) {
            headerCaption.text = getText(R.string.how_much_subtract)
            addButton.text = getText(R.string.remove)
        }
        input.setOnKeyListener{ _: View, i: Int, keyEvent: KeyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                mListener.onDialogAddClick(this, if (input.text.isNullOrBlank() || input.text.length > 4) {0}  else {input.text.toString().toInt()}, isFood, isAdd)
                true
            } else {
                false
            }
        }
        input.showSoftInputOnFocus = true
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