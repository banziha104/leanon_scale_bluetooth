package com.opusone.leanon.scaleble.event

import android.os.Parcel
import com.opusone.leanon.scaleble.QNDataType
import com.opusone.leanon.scaleble.domain.ScaleData
import com.yolanda.health.qnblesdk.listener.QNScaleDataListener
import com.yolanda.health.qnblesdk.out.QNBleDevice
import com.yolanda.health.qnblesdk.out.QNScaleData
import com.yolanda.health.qnblesdk.out.QNScaleItemData
import com.yolanda.health.qnblesdk.out.QNScaleStoreData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject

class DataReceiver() : IDataReceiver {
    override val storeDataObserver: BehaviorSubject<List<QNScaleData>> = BehaviorSubject.create()
    override val dataObserver: PublishSubject<QNDataType> = PublishSubject.create()

    override fun onGetUnsteadyWeight(p0: QNBleDevice?, p1: Double) {
        dataObserver.onNext(QNDataType.MEASUREMENT.apply { this.data = ScaleData.fromWeight(p1) })
    }

    override fun onGetScaleData(p0: QNBleDevice?, data: QNScaleData?) {
        if (data != null){
            dataObserver.onNext(QNDataType.COMPLETE.apply { this.data = ScaleData.from(data.allItem) })
        }
    }
    override fun onGetStoredScale(p0: QNBleDevice?, list: MutableList<QNScaleStoreData>?) {
//        TODO("Not yet implemented")
    }
}

interface IDataReceiver : QNScaleDataListener{
    // 저장된 데이터
    val storeDataObserver : BehaviorSubject<List<QNScaleData>>

    // 측정중 데이터
    val dataObserver: PublishSubject<QNDataType>

    override fun onScaleStateChange(p0: QNBleDevice?, p1: Int) {}
    override fun onGetElectric(p0: QNBleDevice?, p1: Int) {}
}