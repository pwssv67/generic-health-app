package ru.pwssv67.healthcounter.extensions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import ru.pwssv67.healthcounter.App

fun showKeyboard(v:View) {
    val imm = App.applicationContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(v,0)
}

fun hideKeyboard(v:View?) {
    val imm = App.applicationContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (v!=null) {imm.hideSoftInputFromWindow(v.windowToken,0)}
}

fun normalStringDateToShort(longDate:String): String {
    var shortDate = longDate.substring(5)
    shortDate = shortDate.substring(3..4) + '.' + shortDate.substring(0..1)
    return shortDate
}

fun getLocation(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        null
    } else {
        locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
    }
}