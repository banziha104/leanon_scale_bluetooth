package com.opusone.leanon.scaleble

import android.content.Context
import android.os.Parcel
import android.util.Log
import com.opusone.leanon.scaleble.domain.ScaleUser
import com.opusone.leanon.scaleble.event.*
import com.opusone.leanon.scaleble.extension.bleTag
import com.opusone.leanon.scaleble.extension.io
import com.opusone.leanon.scaleble.extension.plusAssign
import com.yolanda.health.qnblesdk.constant.UserGoal
import com.yolanda.health.qnblesdk.constant.UserShape
import com.yolanda.health.qnblesdk.out.QNBleApi
import com.yolanda.health.qnblesdk.out.QNBleDevice
import com.yolanda.health.qnblesdk.out.QNUser
import io.reactivex.disposables.CompositeDisposable
import java.io.Closeable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ScaleBleViewModel (
    private val context : Context,
    private val bleConnector: BleConnector = BleConnector(),
    private val deviceDiscoverer: DeviceDiscoverer = DeviceDiscoverer(),
    private val dataReceiver: DataReceiver = DataReceiver()
) : Closeable,
    IBleHandler,
    IBleConnector by bleConnector,
    IDeviceDiscoverer by deviceDiscoverer,
    IDataReceiver by dataReceiver{

    private val compositeDisposable = CompositeDisposable()
    internal var currentDevice : QNBleDevice? = null

    fun observe(scaleApi : QNBleApi){
        observeDeviceFinding(scaleApi)
    }

    fun observeDeviceFinding(scaleApi : QNBleApi){
//        compositeDisposable +=
//            discoveryObserver
//                .io()
//                .subscribe({
//                    if (it == DeviceDiscoveryType.ON_DEVICE_DISCERVER && it.device != null){
//                        currentDevice = it.device
//                        scaleApi.connectDevice(it.device!!,createQNUser(context,scaleApi)){ code,msg ->
//                            Log.d(bleTag,"$code $msg")
//                        }
//                    }
//                },{
//                    it.printStackTrace()
//                })

        compositeDisposable += dataObserver.io().subscribe {
            if (it.data?.weight != null){
                Log.d(bleTag, "${it.name} : ${it.data?.weight}")
            }
        }
    }

    fun disconnect(scaleApi: QNBleApi){
        if (currentDevice != null){
            scaleApi.disconnectDevice(currentDevice!!){ code, msg ->
                Log.d(bleTag, "디바이스 해제 완료 code:$code, msg:$msg")
            }
        }else {
            Log.d(bleTag,"연결된 디바이스가 없습니다")
        }

    }

    override fun close() {
        compositeDisposable.clear()
    }

    @Synchronized
    fun createQNUser(context: Context, scaleApi: QNBleApi): QNUser {
        val scaleUser = ScaleUser().apply {
            choseShape = 0
            choseGoal = 0
            clothesWeight = 0.0
            gender = "male"
            userId = "12321378"
            height = 170
            athleteType = 0
            birthDay = birthdateToDate("19920101")
        }

        val userShape: UserShape = when (scaleUser.choseShape) {
            0 -> UserShape.SHAPE_NONE
            1 -> UserShape.SHAPE_SLIM
            2 -> UserShape.SHAPE_NORMAL
            3 -> UserShape.SHAPE_STRONG
            4 -> UserShape.SHAPE_PLIM
            else -> UserShape.SHAPE_NONE
        }

        val userGoal: UserGoal = when (scaleUser.choseGoal) {
            0 -> UserGoal.GOAL_NONE
            1 -> UserGoal.GOAL_LOSE_FAT
            2 -> UserGoal.GOAL_STAY_HEALTH
            3 -> UserGoal.GOAL_GAIN_MUSCLE
            4 -> UserGoal.POWER_OFTEN_EXERCISE
            5 -> UserGoal.POWER_LITTLE_EXERCISE
            6 -> UserGoal.POWER_OFTEN_RUN
            else -> UserGoal.GOAL_NONE
        }


        Log.d(bleTag,"스케일유저 : ${scaleUser.userId}, ${scaleUser.height}, ${scaleUser.gender}, ${scaleUser.birthDay}, ${scaleUser.athleteType}, ${userShape}, ${userGoal}, ${scaleUser.clothesWeight}" )

        val a = scaleApi.buildUser(
            scaleUser.userId ?: "",
            scaleUser.height,
            scaleUser.gender ?: "male",
            scaleUser.birthDay ?: birthdateToDate("19920101"),
            scaleUser.athleteType,
            userShape,
            userGoal,
            scaleUser.clothesWeight
        ) { code, msg ->
            Log.d(bleTag, "buildUser code:$code, 사용자 정보:$msg")
        }
        return a ?: QNUser.CREATOR.createFromParcel(Parcel.obtain())
    }

    private fun birthdateToDate(birthdate: String): Date {
        if (birthdate.isEmpty() || birthdate.length < 8) {
            return Date()
        }

        try {
            val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            return simpleDateFormat.parse(birthdate)

        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return Date()
    }
}