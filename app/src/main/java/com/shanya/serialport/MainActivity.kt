package com.shanya.serialport

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.shanya.serialport.databinding.ActivityMainBinding
import com.shanya.serialportutil.SerialPortUtil
import com.shanya.serialportutil.SerialPortUtil.*
import kotlinx.android.synthetic.main.download_process.view.*
import kotlinx.android.synthetic.main.search_devices.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.StringReader

const val REQUEST_INSTALL_PERMISSION = 0x664
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var serialPortUtil: SerialPortUtil
    private lateinit var myViewModel: MyViewModel
    private var downloadProgressBar:ProgressBar ?= null
    private var downloadDialog:AlertDialog ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home,R.id.nav_support, R.id.nav_about), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        serialPortUtil = getInstance(this, object : OnSerialPortListener{
                override fun onScanStatus(status: Boolean) {
                    myViewModel.scanStatusLiveData.value = status
                }

                override fun onReceivedData(data: String?) {
                    MainScope().launch {
                        myViewModel.infoList.add(Info(MSG_RECE_TYPE, data.toString()))
                        myViewModel.updateInfoList()
                    }
                }
            })

        myViewModel.hasUpdateLiveData.observe(this,hasUpdateObserver)
    }

    private val hasUpdateObserver = Observer<Boolean> {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.new_version_found))
                .setMessage(myViewModel.getVersionInfo().updateContent)
                .setPositiveButton(this.getString(R.string.download_immediately)) { _, _ ->

                    myViewModel.hasUpdateLiveData.removeObservers(this)

                    myViewModel.download(this.externalCacheDir.toString() + File.separator)

                    val layoutInflater = LayoutInflater.from(this)
                    val downloadView = layoutInflater.inflate(R.layout.download_process,null)
                    val downloadBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                        .setTitle(this.getString(R.string.downloading))
                        .setView(downloadView)
                    downloadDialog = downloadBuilder.create()
                    downloadDialog?.setCancelable(false)
                    downloadDialog?.show()
                    downloadProgressBar = downloadView.progressBarDownload
                    myViewModel.downloadProcessLiveData.observe(this, downloadProcessObserver)
                }
                .setNegativeButton(this.getString(R.string.next_time)) { _, _ ->
                    myViewModel.hasUpdateLiveData.removeObservers(this)
                }
        if (myViewModel.hasUpdateLiveData.value!!){
            builder.show()
        }


    }

    private val downloadProcessObserver = Observer<Double> {
        downloadProgressBar?.progressBarDownload?.progress = (it * 100).toInt()
        if (it >= 1) {
            downloadDialog?.cancel()
            myViewModel.downloadProcessLiveData.removeObservers(this)
            installDialog(this.externalCacheDir.toString() + File.separator + myViewModel.getVersionInfo().fileName)
        }

    }

    private fun installDialog(path: String){
        val builder: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.tips))
                .setMessage(this.getString(R.string.install_dialog_message) + " “$path”")
                .setPositiveButton(this.getString(R.string.install_now)){_,_->
                    checkInstallPermission()
                    installApk(path)
                }
                .setNegativeButton(this.getString(R.string.install_after)){_,_->

                }
        builder.show()
    }

    private fun installApk(path: String){
        val apk = File(path)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", apk)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        }else{
            intent.setDataAndType(Uri.fromFile(apk),"application/vnd.android.package-archive")
        }
        startActivity(intent)
    }

    private fun checkInstallPermission(){
        val intent = Intent()
        val packageUri = Uri.parse("package:" + this.packageName)
        intent.data = packageUri
        if (Build.VERSION.SDK_INT >= 26){
            if (!packageManager.canRequestPackageInstalls()){
                intent.action = Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
                startActivityForResult(intent, REQUEST_INSTALL_PERMISSION)
                Toast.makeText(this,this.getString(R.string.unknown_source_permissions),Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_INSTALL_PERMISSION){
            installDialog(this.externalCacheDir.toString() + File.separator + myViewModel.getVersionInfo().fileName)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuConnect -> {
                val layoutInflater = LayoutInflater.from(this)
                val dialogView = layoutInflater.inflate(R.layout.search_devices,null)

                val builder :AlertDialog.Builder = AlertDialog.Builder(this)
                    .setView(dialogView)

                dialogView.listViewPairedDevices.onItemClickListener = serialPortUtil.devicesClickListener
                dialogView.listViewUnpairedDevices.onItemClickListener = serialPortUtil.devicesClickListener
                dialogView.listViewPairedDevices.adapter = serialPortUtil.pairedDevicesArrayAdapter
                dialogView.listViewUnpairedDevices.adapter = serialPortUtil.unPairedDevicesArrayAdapter

                dialogView.buttonScan.setOnClickListener {
                    serialPortUtil.doDiscovery()
                    myViewModel.scanStatusLiveData.observe(this, Observer {
                        if (it){
                            dialogView.progressBarScan.visibility = View.VISIBLE
                        }else{
                            dialogView.progressBarScan.visibility = View.GONE
                        }
                    })
                }
                builder.show()
            }
            R.id.menuCheckUpdate -> {
                myViewModel.checkForUpdate()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

