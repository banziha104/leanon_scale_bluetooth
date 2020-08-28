package com.opusone.leanon.scaleble.extension


val Any.simpleTag : String
    get() = this.javaClass.simpleName

val Any.bleTag : String
    get() = "${this.javaClass.simpleName} BLE"
val Any.lyjTag : String
    get() = "${this.javaClass.simpleName} LYJ"