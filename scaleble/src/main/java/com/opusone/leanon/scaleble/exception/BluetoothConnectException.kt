package com.opusone.leanon.scaleble.exception

import java.lang.RuntimeException

class BluetoothInitException : RuntimeException("블루투스를 초기화중에 에러가 발생하였습니다.")
class BluetoothStartException : RuntimeException("블루투스를 시작중에 에러가 발생하였습니다.")
class BluetoothDisConnectException : RuntimeException("블루투스를 종료중에 에러가 발생하였습니다.")