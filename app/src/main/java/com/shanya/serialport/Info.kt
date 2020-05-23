package com.shanya.serialport

const val MSG_RECE_TYPE = 0x566
const val MSG_SEND_TYPE = 0x567

data class Info(
    val type:Int,
    val content:String
)