package ru.pwssv67.healthcounter.Extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import ru.pwssv67.healthcounter.App

fun showKeyboard(v:View) {
    val imm = App.applicationContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(v,0)
}

fun hideKeyboard(v:View?) {
    val imm = App.applicationContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (v!=null) {imm.hideSoftInputFromWindow(v.windowToken,0)}
}
