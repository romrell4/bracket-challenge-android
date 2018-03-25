package com.romrell4.bracketchallenge.support

import android.app.Activity
import android.app.AlertDialog
import android.support.annotation.StringRes
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

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
