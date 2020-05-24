package com.shanya.serialport.viewpagerfragment

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import com.shanya.serialport.R
import kotlinx.android.synthetic.main.fragment_car.*

/**
 * A simple [Fragment] subclass.
 */
class CarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_car, container, false)
    }

    override fun onResume() {
        super.onResume()
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(requireActivity().getString(R.string.tips))
            .setMessage(requireActivity().getString(R.string.please_use_horizontally))
            .setCancelable(false)
        val dialog = builder.create()
        if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            dialog.show()
        }else{
            dialog.cancel()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        buttonLt.setOnClickListener {
            println("c")
        }

        buttonLt.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_UP -> {
                    println("up")
                    return@setOnTouchListener false
                }

                MotionEvent.ACTION_DOWN -> {
                    println("down")
                    return@setOnTouchListener false
                }

                MotionEvent.ACTION_CANCEL -> {
                    println("cancel")
                    return@setOnTouchListener false
                }
            }
            return@setOnTouchListener false

        }

    }

}
