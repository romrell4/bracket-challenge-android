package com.romrell4.bracketchallenge.support

import android.app.AlertDialog
import android.content.Context
import android.os.Parcel
import androidx.annotation.StringRes
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import java.util.*

/**
 * Created by romrell4 on 3/25/18
 */
fun Context.showLoadingDialog(): AlertDialog {
	return AlertDialog.Builder(this)
			.setTitle("Loading...")
			.setMessage("Loading data from the server. Please wait a moment...")
			.show()
}

fun Context.showToast(message: String, length: Int = LENGTH_SHORT) {
	Toast.makeText(this, message, length).show()
}

fun Context.showToast(@StringRes resId: Int, length: Int = LENGTH_SHORT) {
	Toast.makeText(this, resId, length).show()
}

fun Fragment.showLoadingDialog() = requireContext().showLoadingDialog()

fun Fragment.showToast(message: String, length: Int = LENGTH_SHORT) {
	requireContext().showToast(message, length)
}

fun Fragment.showToast(@StringRes resId: Int, length: Int = LENGTH_SHORT) {
	requireContext().showToast(resId, length)
}

fun Parcel.readDate(): Date? {
	val dateLong = readValue(Long::class.java.classLoader) as? Long
	return if (dateLong != null) Date(dateLong) else null
}

fun Parcel.writeDate(date: Date?) {
	writeValue(date?.time)
}

var View.visible: Boolean
	get() = visibility == View.VISIBLE
	set(value) {
		visibility = if (value) View.VISIBLE else View.GONE
	}