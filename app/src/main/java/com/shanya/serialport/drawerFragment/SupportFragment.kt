package com.shanya.serialport.drawerFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.shanya.serialport.R
import kotlinx.android.synthetic.main.fragment_support.*

/**
 * A simple [Fragment] subclass.
 */
class SupportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_support, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        buttonWechat.setOnClickListener {
            imageViewSupport.setImageResource(R.drawable.wechat)
        }

        buttonAlipay.setOnClickListener {
            imageViewSupport.setImageResource(R.drawable.alipay)
        }
    }

}
