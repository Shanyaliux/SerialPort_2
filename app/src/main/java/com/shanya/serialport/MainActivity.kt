package com.shanya.serialport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.shanya.serialport.databinding.ActivityMainBinding
import com.shanya.serialportutil.SerialPortUtil
import kotlinx.android.synthetic.main.search_devices.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var serialPortUtil: SerialPortUtil
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

        serialPortUtil = SerialPortUtil.getInstance(this) { data -> println(data) }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

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
                builder.show()
            }
            R.id.menuCheckUpdate -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}
