package com.shanya.serialport.viewpagerfragment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.shanya.serialport.Info
import com.shanya.serialport.MSG_SEND_TYPE
import com.shanya.serialport.MyViewModel

import com.shanya.serialport.R
import com.shanya.serialportutil.SerialPortUtil
import kotlinx.android.synthetic.main.button_info_dialog.view.*
import kotlinx.android.synthetic.main.fragment_communication.*
import kotlinx.android.synthetic.main.fragment_control.*

/**
 * A simple [Fragment] subclass.
 */
const val CONTROL_BUTTON_DATA = "control_button_data"
const val CONTROL_BUTTON_NAME = "control_button_name"
class ControlFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control, container, false)
    }

    private lateinit var serialPortUtil: SerialPortUtil
    private lateinit var sharedPreferencesDataSend: SharedPreferences
    private lateinit var myViewModel: MyViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val buttons = arrayListOf<Button>(button1,button2,button3,button4,button5,
            button6,button7,button8,button9,button10,
            button11,button12,button13,button14,button15)
        myViewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)
        serialPortUtil = SerialPortUtil.getInstance(requireActivity(),object :SerialPortUtil.OnSerialPortListener{
            override fun onScanStatus(status: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onReceivedData(data: String?) {
                TODO("Not yet implemented")
            }
        })

        val buttonListener = ButtonListener()
        sharedPreferencesDataSend = requireActivity().getSharedPreferences(CONTROL_BUTTON_DATA,
            Context.MODE_PRIVATE)
        val sharedPreferences = requireActivity().getSharedPreferences(CONTROL_BUTTON_NAME,Context.MODE_PRIVATE)

        for (button in buttons){
            button.text = sharedPreferences.getString(button.id.toString(),"")
            button.setOnClickListener(buttonListener)
        }
    }

    private fun createDialog(button:Button){
        val layoutInflater = LayoutInflater.from(requireActivity())
        val dialogView = layoutInflater.inflate(R.layout.button_info_dialog,null)
        val builder = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .setPositiveButton("Yes"){_,_ ->
                button.text = dialogView.editTextButtonName.text
                saveButtonInfo(button,dialogView.editTextButtonName.text.toString(),
                    dialogView.editTextButtonData.text.toString())
            }
            .setNegativeButton("No"){_,_ ->

            }
        builder.show()
    }

    private fun saveButtonInfo(button:Button,name:String,data:String){
        val sharedPreferencesName = requireActivity().getSharedPreferences(CONTROL_BUTTON_NAME,Context.MODE_PRIVATE)
        val editorName = sharedPreferencesName.edit()
        val sharedPreferencesData = requireActivity().getSharedPreferences(CONTROL_BUTTON_DATA,Context.MODE_PRIVATE)
        val editorData = sharedPreferencesData.edit()

        editorName.putString(button.id.toString(),name)
        editorName.apply()
        editorData.putString(button.id.toString(),data)
        editorData.apply()
    }

    inner class ButtonListener: View.OnClickListener,View.OnTouchListener {

        override fun onClick(v: View?) {
            if (switchControl.isChecked){
                createDialog(v as Button)
            }else{
                val data = sharedPreferencesDataSend.getString(v?.id.toString(),"")
                serialPortUtil.sendData(data)
                myViewModel.infoList.add(Info(MSG_SEND_TYPE,data.toString()))
                myViewModel.updateInfoList()
            }
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            TODO("Not yet implemented")
        }
    }
}
