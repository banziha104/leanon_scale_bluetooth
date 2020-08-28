package com.opusone.leanon.scaleble

import android.content.Context
import android.util.Log
import com.opusone.leanon.scaleble.event.*
import com.opusone.leanon.scaleble.exception.BluetoothInitException
import com.opusone.leanon.scaleble.extension.bleTag
import com.opusone.leanon.scaleble.extension.plusAssign
import com.qingniu.health.qnscalesdk.BuildConfig
import com.qingniu.qnble.utils.QNLogUtils
import com.yolanda.health.qnblesdk.constant.CheckStatus
import com.yolanda.health.qnblesdk.out.QNBleApi
import com.yolanda.health.qnblesdk.out.QNBleDevice
import com.yolanda.health.qnblesdk.out.QNScaleData
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.Closeable
import java.lang.Exception
import java.util.concurrent.TimeUnit

class ScaleBle(
    private val context: Context,
    private val viewModel: ScaleBleViewModel = ScaleBleViewModel(context)
) : IScaleBle, IBleHandler by viewModel, Closeable {

    private val CONNECT_TIMEOUT = 3 * 1000
    private val compositeDisposable : CompositeDisposable = CompositeDisposable()
    private val pullingObserver : Observable<Long> by lazy {
        Observable
            .interval(3, TimeUnit.SECONDS, Schedulers.io())
            .takeWhile{ isPull }
    }

    private val scaleApi : QNBleApi by lazy {
        QNBleApi.getInstance(context)
    }

    private var isPull = false

    override fun init(): Single<Unit> = Single.create {
        try {
            val encryptPath = "file:///android_asset/123456789.qn"
            QNLogUtils.setLogEnable(BuildConfig.DEBUG)

            scaleApi.initSdk("123456789", encryptPath) { code, msg ->
                Log.d(bleTag, "initSdk code: $code, msg: $msg")
                viewModel.observe(scaleApi)
            }

            val mQnConfig = scaleApi.config

            mQnConfig?.apply {
                isEnhanceBleBroadcast = true
                isAllowDuplicates = true
                connectOutTime = CONNECT_TIMEOUT.toLong()
                duration = CONNECT_TIMEOUT
                unit = 0
                isOnlyScreenOn = false
                isNotCheckGPS = false
                save { _, s ->
                    Log.d(bleTag, "saved Data :$s")
                }
            }

            scaleApi.setBleConnectionChangeListener(this)
            scaleApi.setBleDeviceDiscoveryListener(this)
            scaleApi.setDataListener(this)
            it.onSuccess(Unit)
        } catch (e: Exception) {
            Log.d(bleTag, "init SDK FAIL")
            it.onError(BluetoothInitException())
        }
    }

    override fun startScan() : Completable = Completable.create { emit ->
            isPull = true
            compositeDisposable += pullingObserver.subscribe {
                scaleApi.startBleDeviceDiscovery { code, msg ->
                    Log.d(bleTag, "startDiscovery code:$code;msg:$msg")
                    if (code == CheckStatus.OK.code) {
                        Log.d(bleTag, "Start : OK")
                    } else if (code == CheckStatus.ERROR_BLUETOOTH_CLOSED.code) {
                        Log.d(bleTag, "Start : FAIL")
                    }
                    emit.onComplete()
                }
            }
        }

    override fun stopScan() : Completable = Completable.create{ emit ->
        isPull = false
        scaleApi.stopBleDeviceDiscovery { code, msg ->
            Log.d(bleTag, "stopBleDeviceDiscovery code: $code, msg: $msg")
            emit.onComplete()
        }
    }

    override fun connectDevice(device: QNBleDevice): Completable = Completable.create { emit ->
        scaleApi.connectDevice(device,viewModel.createQNUser(context,scaleApi)){ code, msg ->
            Log.d(bleTag, "connect code: $code, msg: $msg")
        }
    }

    override fun disconnectDevice(device: QNBleDevice): Completable = Completable.create { emit ->
        scaleApi.disconnectDevice(device){ code, msg ->
            Log.d(bleTag, "disconnect code: $code, msg: $msg")
        }
    }

    override fun close() {
        compositeDisposable.clear()
    }

    override fun getStoreData(): Single<List<QNScaleData>> {
        TODO("Not yet implemented")
    }

    override fun getDataObserver(): Observable<QNDataType> = dataObserver

    override fun deinit(): Completable = Completable.create{
        compositeDisposable.clear()
        it.onComplete()
    }

}

interface IBleHandler : IBleConnector, IDeviceDiscoverer, IDataReceiver