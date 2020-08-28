package com.opusone.leanon.scaleble.event

import com.yolanda.health.qnblesdk.listener.QNBleDeviceDiscoveryListener
import com.yolanda.health.qnblesdk.out.QNBleBroadcastDevice
import com.yolanda.health.qnblesdk.out.QNBleDevice
import com.yolanda.health.qnblesdk.out.QNBleKitchenDevice
import io.reactivex.subjects.BehaviorSubject

class DeviceDiscoverer : IDeviceDiscoverer{
    override val discoveryObserver : BehaviorSubject<DeviceDiscoveryType> = BehaviorSubject.createDefault(DeviceDiscoveryType.INIT)

    override fun onDeviceDiscover(device: QNBleDevice?) {
        discoveryObserver.onNext(DeviceDiscoveryType.ON_DEVICE_DISCERVER.apply { this.device = device })
    }

    override fun onStopScan() {
        discoveryObserver.onNext(DeviceDiscoveryType.ON_STOP_SCAN)
    }

    override fun onScanFail(p0: Int) {
        discoveryObserver.onNext(DeviceDiscoveryType.ON_SCAN_FAIL)
    }

    override fun onStartScan() {
        discoveryObserver.onNext(DeviceDiscoveryType.ON_START_SCAN)
    }

}

interface IDeviceDiscoverer : QNBleDeviceDiscoveryListener{
    val discoveryObserver : BehaviorSubject<DeviceDiscoveryType>
    override fun onBroadcastDeviceDiscover(p0: QNBleBroadcastDevice?) {}
    override fun onKitchenDeviceDiscover(p0: QNBleKitchenDevice?) {}
}

enum class DeviceDiscoveryType(
    var device : QNBleDevice? = null
){
    INIT,
    ON_DEVICE_DISCERVER,
    ON_STOP_SCAN,
    ON_START_SCAN,
    ON_SCAN_FAIL
}