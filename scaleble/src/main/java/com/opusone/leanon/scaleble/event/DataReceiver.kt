package com.opusone.leanon.scaleble.event

import android.util.Log
import com.opusone.leanon.scaleble.QNDataType
import com.opusone.leanon.scaleble.domain.ScaleData
import com.opusone.leanon.scaleble.extension.bleTag
import com.yolanda.health.qnblesdk.listener.QNScaleDataListener
import com.yolanda.health.qnblesdk.out.QNBleDevice
import com.yolanda.health.qnblesdk.out.QNScaleData
import com.yolanda.health.qnblesdk.out.QNScaleStoreData
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.ArrayList

class DataReceiver(
    val compositeDisposable: CompositeDisposable
) : IDataReceiver {
    override val storeDataObserver: BehaviorSubject<List<QNScaleStoreData>> = BehaviorSubject.create()
    override val dataObserver: PublishSubject<QNDataType> = PublishSubject.create()
    override val storedList : ArrayList<QNScaleStoreData> = arrayListOf()
    override fun onGetUnsteadyWeight(p0: QNBleDevice?, p1: Double) {
        dataObserver.onNext(QNDataType.MEASUREMENT.apply { this.data = ScaleData.fromWeight(p1) })
    }

    override fun onGetScaleData(p0: QNBleDevice?, data: QNScaleData?) {
        if (data != null){
            dataObserver.onNext(QNDataType.COMPLETE.apply { this.data = ScaleData.from(data.allItem) })
        }
    }
    override fun onGetStoredScale(p0: QNBleDevice?, list: MutableList<QNScaleStoreData>?) {
        list?.forEach { Log.d(bleTag,"저장된 데이터 ${it.measureTime} ${it.weight}") }
        compositeDisposable += Observable
            .just(list)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe ({ list ->
                if (list != null && list.isNotEmpty()){
                    list.forEach { Log.d(bleTag,"observableListCheck : ${it.measureTime} / ${it.weight} / ${list.size}") }
                    storedList.addAll(list)
                    storeDataObserver.onNext(storedList)
                }
            },{
                it.printStackTrace()
            })
    }
}

interface IDataReceiver : QNScaleDataListener{
    // 저장된 데이터
    val storeDataObserver : BehaviorSubject<List<QNScaleStoreData>>

    // 측정중 데이터
    val dataObserver: PublishSubject<QNDataType>

    val storedList : ArrayList<QNScaleStoreData>

    override fun onScaleStateChange(p0: QNBleDevice?, p1: Int) {}
    override fun onGetElectric(p0: QNBleDevice?, p1: Int) {}
}