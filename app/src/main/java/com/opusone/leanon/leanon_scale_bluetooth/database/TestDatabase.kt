package com.opusone.leanon.test.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.opusone.leanon.test.database.dao.DeviceDao
import com.opusone.leanon.test.database.entity.DeviceEntity

@Database(
    entities = [DeviceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DeviceDatabase  : RoomDatabase(){
    abstract fun deviceDao() : DeviceDao
}