package com.opusone.leanon.leanon_scale_bluetooth

import android.Manifest
import android.os.Bundle
import android.os.Parcel
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import com.opusone.leanon.scaleble.ScaleBle
import com.opusone.leanon.scaleble.event.BleConnector
import com.opusone.leanon.scaleble.event.DeviceDiscoveryType
import com.opusone.leanon.scaleble.extension.io
import com.opusone.leanon.scaleble.extension.plusAssign
import com.opusone.leanon.test.database.LocalDataBase
import com.opusone.leanon.test.database.entity.DeviceEntity
import com.yolanda.health.qnblesdk.out.QNBleDevice
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


fun bleLog(str : String) = Log.d("BLE LOG",str)

class MainActivity : AppCompatActivity(), PermissionController.CallBack {
    val compositeDisposable = CompositeDisposable()
    lateinit var currentDevice : QNBleDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionController(this, arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE
        )).checkVersion()
    }

    override fun init() {
        val ble = ScaleBle(this)
        initButton(ble)
        initDevice()


        compositeDisposable += ble.connectObserver.subscribe({
            bleLog("connector : ${it.name}")
        },{
            bleLog("connector Observer Error")
            it.printStackTrace()
        })

        compositeDisposable += ble.discoveryObserver
            .subscribe({
                if (it == DeviceDiscoveryType.ON_DEVICE_DISCERVER && it.device != null){
                    currentDevice = it.device!!
                    LocalDataBase
                        .deviceDatabase
                        .deviceDao()
                        .insertOrUpdate(it.device!!.run {
                        DeviceEntity(mac, name, modeId, bluetoothName, rssi, isScreenOn, isSupportWifi, false, false, 4, deviceType)
                    }).io().subscribe({

                    },{
                        it.printStackTrace()
                    })
                }
                bleLog("discoverer : ${it.name}")
            },{
                bleLog("discoverer Observer Error")
                it.printStackTrace()
            })

        compositeDisposable += ble.init().subscribe({
            bleLog("INIT")
        },{
            bleLog("INIT ERROR")
            it.printStackTrace()
        })

        compositeDisposable += ble.getStoreData().io().subscribe({
            it.forEach { bleLog("저장된 데이터 : ${it.measureTime} ${it.allItem}") }
        },{
            bleLog("에러")
            it.printStackTrace()
        })
    }

    private fun initButton(ble : ScaleBle){

        compositeDisposable += start.clicks()
            .subscribe({
                bleLog("BLE START")
                ble.startScan().subscribe({
                    bleLog("성공함 ${it.mac}")
                },{
                    bleLog("실패함")
                    it.printStackTrace()
                })
            },{
                it.printStackTrace()
            },{
                bleLog("start complete")
            })

        compositeDisposable += stop.clicks()
            .subscribe({
                bleLog("BLE START")
                ble.stopScan().subscribe {  }
//                scanDisposable.clear()
            },{
                it.printStackTrace()
            },{
                bleLog("start complete")
            })

        compositeDisposable += connect.clicks()
            .subscribe({
                bleLog("Connect")
                ble.connectDevice(currentDevice).subscribe({},{it.printStackTrace()})
            },{
                it.printStackTrace()
            },{
                bleLog("start complete")
            })

        compositeDisposable += disconnect.clicks()
            .subscribe({
                bleLog("DISCONNECT")
                ble.disconnectDevice(currentDevice).subscribe{}
            },{
                it.printStackTrace()
            },{
                bleLog("start complete")
            })
    }

    private fun initDevice(){
        compositeDisposable += LocalDataBase
            .deviceDatabase
            .deviceDao()
            .findAll()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe({
                val data = it.firstOrNull{ it.id == 1L}
                bleLog(it.firstOrNull{ it.id == 1L}?.toString() ?: "데이터 없슴")
                if (data != null){
                    currentDevice = QNBleDevice.CREATOR.createFromParcel(Parcel.obtain()).getBleDevice(data.modeId,data.mac)
                }
            },{
                it.printStackTrace()
            })
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}