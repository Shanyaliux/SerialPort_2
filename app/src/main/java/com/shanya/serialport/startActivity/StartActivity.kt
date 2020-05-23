package com.shanya.serialport.startActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shanya.serialport.MainActivity
import com.shanya.serialport.R
import com.shanya.serialport.databinding.ActivityStartBinding
import java.util.ArrayList

const val REQUEST_PERMISSION = 0x658
class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewVersion.text = this.packageManager.getPackageInfo(this.packageName,0).versionName

        val animationLabelLeft = AnimationUtils.loadAnimation(this, R.anim.slide_from_left)
        animationLabelLeft.duration = 800
        val animationLabelRight = AnimationUtils.loadAnimation(this, R.anim.slide_from_right)
        animationLabelRight.duration = 800
        binding.textViewLabelLeft.animation = animationLabelLeft
        binding.textViewLabelRight.animation = animationLabelRight
        binding.textViewLabelLeft.visibility = View.VISIBLE
        binding.textViewLabelRight.visibility = View.VISIBLE

        val animationBottomInfo = AnimationUtils.loadAnimation(this,R.anim.slide_from_bottom)
        animationBottomInfo.duration = 800
        binding.textViewVersionLabel.animation = animationBottomInfo
        binding.textViewVersion.animation = animationBottomInfo
        binding.textViewPoweredLabel.animation = animationBottomInfo
        binding.textViewVersionLabel.visibility = View.VISIBLE
        binding.textViewVersion.visibility = View.VISIBLE
        binding.textViewPoweredLabel.visibility = View.VISIBLE

        //权限申请
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.INTERNET)
        val mPermissionList = ArrayList<String>()
        for (p in permissions){
            if (ContextCompat.checkSelfPermission(this,p) != PackageManager.PERMISSION_GRANTED){
                mPermissionList.add(p)
            }
        }
        if (mPermissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions,
                REQUEST_PERMISSION
            )
        }else {//全部权限已申请
            Toast.makeText(this,this.getString(R.string.required_permissions), Toast.LENGTH_SHORT).show()

            Handler().postDelayed({
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            },1000)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
                for (element in grantResults) {
                    if (element != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, this.getString(R.string.unauthorized_access), Toast.LENGTH_SHORT).show()
                    } else {
                        Handler().postDelayed({
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }, 1000)
                    }
                }
            }
        }
    }
}
