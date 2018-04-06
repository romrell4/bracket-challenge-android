package com.romrell4.bracketchallenge.model

import android.os.Parcel
import com.romrell4.bracketchallenge.support.BCParcelable
import com.romrell4.bracketchallenge.support.BCParcelable.Companion.parcelableCreator
import com.romrell4.bracketchallenge.support.readDate
import com.romrell4.bracketchallenge.support.writeDate
import java.util.*

/**
 * Created by romrell4 on 2/25/18
 */
data class Tournament(
		var tournamentId: Int,
		var name: String?,
		var masterBracketId: Int?,
		var drawsUrl: String?,
		var imageUrl: String?,
		var startDate: Date?,
		var endDate: Date?
): BCParcelable {
	private constructor(parcel: Parcel): this(
			parcel.readInt(),
			parcel.readString(),
			parcel.readValue(Int::class.java.classLoader) as? Int,
			parcel.readString(),
			parcel.readString(),
			parcel.readDate(),
			parcel.readDate()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(tournamentId)
		parcel.writeString(name)
		parcel.writeValue(masterBracketId)
		parcel.writeString(drawsUrl)
		parcel.writeString(imageUrl)
		parcel.writeDate(startDate)
		parcel.writeDate(endDate)
	}

	companion object {
		@JvmField
		val CREATOR = parcelableCreator(::Tournament)
	}
}