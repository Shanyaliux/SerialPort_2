package com.shanya.serialport

import android.util.Log
import androidx.lifecycle.*
import com.ejlchina.okhttps.HTTP
import com.google.gson.Gson
import com.shanya.serialport.update.VersionInfo
import com.shanya.serialportutil.SerialPortUtil
import kotlinx.coroutines.launch

class MyViewModel: ViewModel() {

    private val tag = "OkHttps E: -->   "

    private val http: HTTP = HTTP.builder()
        .baseUrl("https://shanya-01.coding.net/p/SerialPort/d/SerialPort/git/raw/master")
        .build()

    private var versionInfo = VersionInfo(0,"","","","")

    private val _hasUpdateLiveData = MutableLiveData<Boolean>()
    val hasUpdateLiveData:LiveData<Boolean>
    get() = _hasUpdateLiveData

    private val _downloadProcessLiveData = MutableLiveData<Double>()
    val downloadProcessLiveData:LiveData<Double>
    get() = _downloadProcessLiveData

    private val _infoLiveData = MutableLiveData<MutableList<Info>>()
    val infoLiveData: LiveData<MutableList<Info>>
    get() = _infoLiveData

    val infoList: MutableList<Info> = emptyList<Info>().toMutableList()

    val scanStatusLiveData = MutableLiveData<Boolean>()

    init {
        scanStatusLiveData.value = false
        _hasUpdateLiveData.value = false
        _downloadProcessLiveData.value = 0.0
        _infoLiveData.value = infoList
        checkForUpdate()
    }

    fun updateInfoList(){
        _infoLiveData.value = infoList
    }

    fun checkForUpdate(){
        http.async("/update.json")
            .setOnResponse {
                versionInfo = Gson().fromJson(it.body.toString(),VersionInfo::class.java)

                viewModelScope.launch {
                    _hasUpdateLiveData.value = BuildConfig.VERSION_CODE < versionInfo.versionCode
                }
            }
            .setOnException {
                Log.e(tag,it.toString())
            }
            .setOnComplete {
                Log.d(tag,it.toString())
            }
            .get()
    }

    fun download(path:String){
        http.async(versionInfo.downloadUrl)
            .setOnResponse {
                it.body
                    .stepRate(0.01)
                    .setOnProcess {process ->
                        viewModelScope.launch {
                            _downloadProcessLiveData.value = process.rate
                        }
                    }
                    .toFile(path + versionInfo.fileName)
                    .start()
            }
            .setOnException {  }
            .setOnComplete {  }
            .get()
    }

    fun getVersionInfo() = versionInfo
}

