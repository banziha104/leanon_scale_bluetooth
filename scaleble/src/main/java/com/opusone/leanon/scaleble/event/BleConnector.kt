package com.opusone.leanon.scaleble.event

import com.yolanda.health.qnblesdk.listener.QNBleConnectionChangeListener
import com.yolanda.health.qnblesdk.out.QNBleDevice
import io.reactivex.subjects.BehaviorSubject

class BleConnector : IBleConnector{
    override val connectObserver : BehaviorSubject<BLEConnectType> = BehaviorSubject.createDefault(BLEConnectType.INIT)

    override fun onConnecting(p0: QNBleDevice?) = connectObserver.onNext(BLEConnectType.ON_CONNECT)
    override fun onConnectError(p0: QNBleDevice?, p1: Int) = connectObserver.onNext(BLEConnectType.ON_CONNECT_ERROR)
    override fun onConnected(p0: QNBleDevice?) = connectObserver.onNext(BLEConnectType.ON_CONNECTED)
    override fun onServiceSearchComplete(p0: QNBleDevice?) = connectObserver.onNext(BLEConnectType.ON_SERVICE_SEARCH_COMPLETE)
    override fun onDisconnected(p0: QNBleDevice?) = connectObserver.onNext(BLEConnectType.ON_DISCONNECTED)
    override fun onDisconnecting(p0: QNBleDevice?) = connectObserver.onNext(BLEConnectType.ON_DISCONNECTING)
}

interface IBleConnector : QNBleConnectionChangeListener{
    val connectObserver : BehaviorSubject<BLEConnectType>
}

enum class BLEConnectType{
    INIT,
    ON_CONNECT,
    ON_CONNECTED,
    ON_SERVICE_SEARCH_COMPLETE,
    ON_DISCONNECTED,
    ON_DISCONNECTING,
    ON_CONNECT_ERROR
}