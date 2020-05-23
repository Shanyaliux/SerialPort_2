package com.shanya.serialport

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ejlchina.okhttps.HTTP
import com.google.gson.Gson
import com.shanya.serialport.update.VersionInfo


class MyViewModel: ViewModel() {

    private val tag = "OkHttps E: -->   "

    private val http: HTTP = HTTP.builder()
        .baseUrl("https://shanya-01.coding.net/p/SerialPort/d/SerialPort/git/raw/master")
        .build()

    private var versionInfo = VersionInfo(0,"","","","")

    private var hasUpdate = false

    val scanStatusLiveData = MutableLiveData<Boolean>()


    init {
        scanStatusLiveData.value = false
        checkForUpdate()
    }

    fun checkForUpdate(){
        http.async("/update.json")
            .setOnResponse {
                versionInfo = Gson().fromJson(it.body.toString(),VersionInfo::class.java)

                hasUpdate = BuildConfig.VERSION_CODE < versionInfo.versionCode
            }
            .setOnException {
                Log.e(tag,it.toString())
            }
            .setOnComplete {
                Log.d(tag,it.toString())
            }
            .get()
    }

    fun getVersionInfo() = versionInfo
    fun getHasUpdate() = hasUpdate

}