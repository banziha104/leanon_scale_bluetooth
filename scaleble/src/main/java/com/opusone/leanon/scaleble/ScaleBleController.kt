package com.opusone.leanon.scaleble

import android.content.Context
import android.util.Log
import com.opusone.leanon.scaleble.event.*
import com.opusone.leanon.scaleble.exception.BluetoothAlreadyConnectedException
import com.opusone.leanon.scaleble.exception.BluetoothAlreadyTryConnectException
import com.opusone.leanon.scaleble.exception.BluetoothInitException
import com.opusone.leanon.scaleble.extension.bleTag
import com.opusone.leanon.scaleble.extension.io
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
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import java.io.Closeable
import java.lang.Exception
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class ScaleBle(
    private val context: Context,
    private val viewModel: ScaleBleHandler = ScaleBleHandler(context)
) : IScaleBleController, IBleHandler by viewModel, Closeable {

    private val CONNECT_TIMEOUT = 3 * 1000
    private val compositeDisposable : CompositeDisposable = CompositeDisposable()

    private val pullingObserver : Observable<Long> by lazy {
        Observable
            .interval(0,3, TimeUnit.SECONDS, Schedulers.io())
            .takeWhile{ isPull }
    }

    private val connectionInterval : Observable<Long> by lazy {
        Observable
            .interval(0,7,TimeUnit.SECONDS,Schedulers.io())
            .takeWhile{ !isConnected && !isCallDisconnected }
    }

    private val scaleApi : QNBleApi by lazy {
        QNBleApi.getInstance(context)
    }


    private var isPull = false
    private var isConnected = false
    private var isCallDisconnected = false
    private var tryConnecting = false

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

    override fun startScan() : Single<QNBleDevice> {
        return Single.create<Unit> { emit ->
            isPull = true
            compositeDisposable += pullingObserver.subscribe {
                scaleApi.startBleDeviceDiscovery { code, msg ->
                    Log.d(bleTag, "startDiscovery code:$code;msg:$msg")
                    if (code == CheckStatus.OK.code) {
                        emit.onSuccess(Unit)
                    } else if (code == CheckStatus.ERROR_BLUETOOTH_CLOSED.code) {
                        emit.onError(RuntimeException())
                    }
                }
            }
        }.flatMap {
            discoveryObserver
                .io()
                .filter{ it == DeviceDiscoveryType.ON_DEVICE_DISCERVER && it.device != null }
                .map { it.device!! }
                .firstOrError()
                .doOnSuccess {
                    viewModel.currentDevice = it
                    Log.d(bleTag,"여기 뭐가 자꾸 오나")
                    connectDevice(it)
                        .subscribe({
                            Log.d(bleTag,"컨넥션 체크")
                        },{
                            it.printStackTrace()
                        })
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


    override fun connectDevice(device: QNBleDevice): Single<Unit> = Single.create { emit ->
        try {
            isConnected = false
            isCallDisconnected = false

            compositeDisposable += connectObserver.io().subscribe({
                if (it == BLEConnectType.ON_CONNECTED || it == BLEConnectType.ON_SERVICE_SEARCH_COMPLETE ){
                    isConnected = true
                    tryConnecting = false
                    Log.d(bleTag,"이미 연결됨")
                    return@subscribe
                }else{
                    Log.d(bleTag,"연결시도")
                    if (!tryConnecting){
                        tryConnecting = true
                        compositeDisposable += connectionInterval
                            .subscribe({
                                scaleApi.connectDevice(
                                    device,
                                    viewModel.createQNUser(context, scaleApi)
                                ) { code, msg ->
                                    Log.d(bleTag, "connect code: $code, msg: $msg")
                                    if (code == 0) {
                                        emit.onSuccess(Unit)
                                    } else {
                                        emit.onError(BluetoothAlreadyTryConnectException())
                                    }
                                }
                        }, {
                            emit.onError(it)
                            it.printStackTrace()
                        })
                    }
                }
            },{
                it.printStackTrace()
            })
        } catch (e: Exception) {
            emit.onError(e)
            e.printStackTrace()
        }
    }

    override fun disconnectDevice(device: QNBleDevice): Completable = Completable.create { emit ->
        isCallDisconnected = true
        scaleApi.disconnectDevice(device){ code, msg ->
            Log.d(bleTag, "disconnect code: $code, msg: $msg")
            emit.onComplete()
        }
    }

    override fun close() {
        compositeDisposable.clear()
    }


    // 받아온 데이터를 저장함
    override fun getStoreData(): Single<List<QNScaleData>> = Single.create { emit ->
        try {
            emit.onSuccess(
                storeDataObserver
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .map { list ->
                        list.map {
                            Log.d(bleTag,"이쪽인가")
                            it.setUser(viewModel.createQNUser(context, scaleApi))
                            val scaleData = it.generateScaleData()
                            scaleData.measureTime = it.measureTime
                            scaleData
                        }
                    }
                    .debounce(500L, TimeUnit.MILLISECONDS)
                    .blockingFirst()
                    .distinctBy { it.measureTime }
                    .sortedBy { it.measureTime }
            )
        }catch (e : Exception){
            e.printStackTrace()
            emit.onError(e)
        }
    }


    override fun getDataObserver(): Observable<QNDataType> = dataObserver

    override fun deinit(): Completable = Completable.create{
        compositeDisposable.clear()
        it.onComplete()
    }
}

interface IBleHandler : IBleConnector, IDeviceDiscoverer, IDataReceiver