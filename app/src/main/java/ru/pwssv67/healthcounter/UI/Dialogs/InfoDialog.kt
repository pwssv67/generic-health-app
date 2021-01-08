package ru.pwssv67.healthcounter.UI.Dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.service.autofill.TextValueSanitizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import ru.pwssv67.healthcounter.Extensions.Goal
import ru.pwssv67.healthcounter.Extensions.InfoPurpose
import ru.pwssv67.healthcounter.R

class InfoDialog(val purpose:InfoPurpose):DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.info_dialog, container, false)
        if (dialog!=null && dialog?.window != null) {
            getDialog()?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            getDialog()?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            getDialog()?.window?.setBackgroundDrawableResource(R.drawable.add_dialog_rounded_bg)
        }
        return view
        //return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.info_dialog, null)
        builder.setView(view)

        val okButton = view?.findViewById(R.id.tv_ok) as TextView
        val textField = view.findViewById(R.id.tv_info_text) as TextView

        textField.text = when (purpose) {
            InfoPurpose.GLASSES -> getText(R.string.glasses_info)
            InfoPurpose.CALORIES -> getText(R.string.calories_info)
            InfoPurpose.TRAINING -> getText(R.string.training_info)
            InfoPurpose.GENERAL -> getText(R.string.lorem_ipsum)
            else -> "IDK why r yu here. purpose is wrong"
        }

        okButton.setOnClickListener {
            this.dismiss()
        }

        return builder.create()
        //return super.onCreateDialog(savedInstanceState)
    }
}