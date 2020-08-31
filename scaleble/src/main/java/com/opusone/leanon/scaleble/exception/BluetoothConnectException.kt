package com.opusone.leanon.scaleble.exception

import com.opusone.leanon.scaleble.extension.simpleTag
import java.lang.RuntimeException

open class BluetoothConnectException(msg : String) : RuntimeException("블루투스 연결 에러 [message : ${msg}]")

class BluetoothInitException : BluetoothConnectException("블루투스를 초기화중에 에러가 발생하였습니다.")
class BluetoothStartException : BluetoothConnectException("블루투스를 시작중에 에러가 발생하였습니다.")
class BluetoothDisConnectException : BluetoothConnectException("블루투스를 종료중에 에러가 발생하였습니다.")
class BluetoothAlreadyConnectedException : BluetoothConnectException("이미 블루투스 장비와 연결이 되어있습니다")
class BluetoothAlreadyTryConnectException : BluetoothConnectException("이미 블루투스에 연결을 시도하고 있습니다")