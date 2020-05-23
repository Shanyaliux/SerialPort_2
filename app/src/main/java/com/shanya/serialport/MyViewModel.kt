package com.shanya.serialport

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import com.ejlchina.okhttps.HTTP
import com.shanya.serialport.update.VersionInfo


class MyViewModel: ViewModel() {

    private val tag = "OkHttps E: -->   "

    private var http: HTTP = HTTP.builder()
        .baseUrl("https://shanya-01.coding.net/p/SerialPort/d/SerialPort/git/raw/master")
        .build()

    private lateinit var versionInfo: VersionInfo
    private var hasUpdate = false

    fun checkForUpdate(){
        http.async("/update.json")
            .setOnResponse {
                versionInfo = it.body.toBean(VersionInfo::class.java)
            }
            .setOnException {
                Log.e(tag,it.toString())
            }
            .setOnComplete {
                hasUpdate = BuildConfig.VERSION_CODE < versionInfo.versionCode
            }
            .get()
    }
}