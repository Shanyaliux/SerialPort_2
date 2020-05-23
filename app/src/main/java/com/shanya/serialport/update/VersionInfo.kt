package com.shanya.serialport.update

data class VersionInfo (
    val versionCode:Int,
    val versionName:String,
    val fileName:String,
    val updateContent:String,
    val downloadUrl:String
)