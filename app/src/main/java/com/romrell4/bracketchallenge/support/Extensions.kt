package com.romrell4.bracketchallenge.support

import android.app.Activity
import android.app.AlertDialog
import android.os.Parcel
import android.support.annotation.StringRes
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import java.util.*

/**
 * Created by romrell4 on 3/25/18
 */
fun Activity.showLoadingDialog(): AlertDialog {
    return AlertDialog.Builder(this)
            .setTitle("Loading...")
            .setMessage("Loading data from the server. Please wait a moment...")
            .show()
}

fun Activity.showToast(message: String, length: Int = LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Activity.showToast(@StringRes resId: Int, length: Int = LENGTH_SHORT) {
    Toast.makeText(this, resId, length).show()
}

fun Parcel.readDate(): Date? {
    val dateLong = readValue(Long::class.java.classLoader) as? Long
    return if (dateLong != null) Date(dateLong) else null
}

fun Parcel.writeDate(date: Date?) {
    writeValue(date?.time)
}
