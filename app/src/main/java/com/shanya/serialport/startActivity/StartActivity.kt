package com.shanya.serialport.startActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import com.shanya.serialport.MainActivity
import com.shanya.serialport.R
import com.shanya.serialport.databinding.ActivityStartBinding

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

        Handler().postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        },1500)
    }
}
