package com.opusone.leanon.scaleble

import com.opusone.leanon.scaleble.domain.ScaleData
import com.yolanda.health.qnblesdk.out.QNBleDevice
import com.yolanda.health.qnblesdk.out.QNScaleData
import com.yolanda.health.qnblesdk.out.QNScaleItemData
import com.yolanda.health.qnblesdk.out.QNScaleStoreData
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface IScaleBleController : IBleHandler {
    fun init(): Single<Unit>
    fun startScan(): Single<QNBleDevice>
    fun stopScan(): Completable
    fun connectDevice(device: QNBleDevice): Single<Unit>
    fun disconnectDevice(device: QNBleDevice): Completable
    fun getStoreData(): Single<List<QNScaleData>>
    fun getDataObserver(): Observable<QNDataType>
    fun deinit(): Completable
}

enum class QNDataType(
    var data: ScaleData? = null
) {
    MEASUREMENT, // 측정중
    COMPLETE; // 측정 완료;
}


