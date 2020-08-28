package com.opusone.leanon.leanon_scale_bluetooth

import android.app.Application
import androidx.room.Room
import com.opusone.leanon.test.database.DeviceDatabase
import com.opusone.leanon.test.database.LocalDataBase

class BleApp : Application(){
    override fun onCreate() {
        super.onCreate()
        initLocalDB()
    }

    private fun initLocalDB(){
        LocalDataBase.deviceDatabase = Room
            .databaseBuilder(this,DeviceDatabase::class.java,"scale_test.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}