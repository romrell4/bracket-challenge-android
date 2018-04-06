package com.romrell4.bracketchallenge.support

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by romrell4 on 3/25/18
 */
interface BCParcelable: Parcelable {
	companion object {
		inline fun <reified T> parcelableCreator(crossinline create: (Parcel) -> T) = object: Parcelable.Creator<T> {
			override fun createFromParcel(source: Parcel) = create(source)
			override fun newArray(size: Int) = arrayOfNulls<T>(size)
		}
	}

	override fun describeContents() = 0
}
