package com.opusone.leanon.test.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qn_device")
data class DeviceEntity(
    val mac: String? = null,
    val name : String = "Scale",
    val modeId : String = "0000",
    val bluetoothName: String? = null,
    val rssi : Int = 0,
    val isScreenOn : Boolean = false,
    val isSupportWifi : Boolean = false,
    val isOneToOne : Boolean = false,
    val supportChangeUnit : Boolean= false,
    val method : Int = 4,
    val deviceType : Int = 100,
    @PrimaryKey(autoGenerate = false) var id : Long = 1
) {
    override fun toString(): String {
        val field = this.javaClass.declaredFields
        return field.map {
            it.isAccessible
            it
        }.fold("",{ acc, field -> acc + "/ ${field.name} + ${field.get(this)}"})
    }
}

class DeviceEntityParcel(
    val mac: String? = null,
    val name : String = "Scale",
    val modeId : String = "0000",
    val bluetoothName: String? = null,
    val rssi : Int = 0,
    val isScreenOn : Boolean = false,
    val isSupportWifi : Boolean = false,
    val isOneToOne : Boolean = false,
    val supportChangeUnit : Boolean= false,
    val method : Int = 4,
    val deviceType : Int = 100,
    @PrimaryKey(autoGenerate = false) var id : Long = 1
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString() ?: "Scale",
        parcel.readString() ?: "0000",
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong()
    )

    override fun toString(): String {
        val field = this.javaClass.declaredFields
        return field.map {
            it.isAccessible
            it
        }.fold("",{ acc, field -> acc + "/ ${field.name} + ${field.get(this)}"})
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mac)
        parcel.writeString(name)
        parcel.writeString(modeId)
        parcel.writeString(bluetoothName)
        parcel.writeInt(rssi)
        parcel.writeByte(if (isScreenOn) 1 else 0)
        parcel.writeByte(if (isSupportWifi) 1 else 0)
        parcel.writeByte(if (isOneToOne) 1 else 0)
        parcel.writeByte(if (supportChangeUnit) 1 else 0)
        parcel.writeInt(method)
        parcel.writeInt(deviceType)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DeviceEntityParcel> {
        fun from(deviceEntity: DeviceEntity) : DeviceEntityParcel = deviceEntity.run { DeviceEntityParcel(mac, name, modeId, bluetoothName, rssi, isScreenOn, isSupportWifi, isOneToOne, supportChangeUnit, method, deviceType) }
        override fun createFromParcel(parcel: Parcel): DeviceEntityParcel {
            return DeviceEntityParcel(parcel)
        }

        override fun newArray(size: Int): Array<DeviceEntityParcel?> {
            return arrayOfNulls(size)
        }
    }
}