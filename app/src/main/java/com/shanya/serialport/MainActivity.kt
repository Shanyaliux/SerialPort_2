package com.shanya.serialport

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import kotlinx.android.synthetic.main.search_devices.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var serialPortUtil: SerialPortUtil
    private lateinit var myViewModel: MyViewModel

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
                    TODO("Not yet implemented")
                }

            })
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

                Toast.makeText(this,myViewModel.getHasUpdate().toString(),Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
