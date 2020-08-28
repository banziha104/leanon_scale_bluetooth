package com.opusone.leanon.scaleble.domain

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class ScaleUser : Parcelable {
    var userId: String? = null
    var height = 0
    var gender: String? = null
    var birthDay: Date? = null
    var athleteType = 0
    var choseShape = 0
    var choseGoal = 0
    var clothesWeight = 0.0

    constructor() {}

    override fun toString(): String {
        return "ScaleUser{" +
                "userId='" + userId + '\'' +
                ", height=" + height +
                ", gender='" + gender + '\'' +
                ", birthDay=" + birthDay +
                ", athleteType=" + athleteType +
                ", choseShape=" + choseShape +
                ", choseGoal=" + choseGoal +
                ", clothesWeight=" + clothesWeight +
                '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(userId)
        dest.writeInt(height)
        dest.writeString(gender)
        dest.writeLong(if (birthDay != null) birthDay!!.time else -1)
        dest.writeInt(athleteType)
        dest.writeInt(choseShape)
        dest.writeInt(choseGoal)
        dest.writeDouble(clothesWeight)
    }

    private constructor(`in`: Parcel) {
        userId = `in`.readString()
        height = `in`.readInt()
        gender = `in`.readString()
        val tmpBirthDay = `in`.readLong()
        birthDay = if (tmpBirthDay == -1L) null else Date(tmpBirthDay)
        athleteType = `in`.readInt()
        choseShape = `in`.readInt()
        choseGoal = `in`.readInt()
        clothesWeight = `in`.readDouble()
    }

    companion object {
        val CREATOR: Parcelable.Creator<ScaleUser?> = object : Parcelable.Creator<ScaleUser?> {
            override fun createFromParcel(source: Parcel): ScaleUser? {
                return ScaleUser(source)
            }

            override fun newArray(size: Int): Array<ScaleUser?> {
                return arrayOfNulls(size)
            }
        }
    }
}
