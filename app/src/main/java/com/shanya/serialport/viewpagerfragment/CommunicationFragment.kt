package com.shanya.serialport.viewpagerfragment

import android.content.Context
import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shanya.serialport.*
import com.shanya.serialportutil.SerialPortUtil
import kotlinx.android.synthetic.main.fragment_communication.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class CommunicationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_communication, container, false)
    }

    private lateinit var serialPortUtil: SerialPortUtil

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val myViewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)
        serialPortUtil = SerialPortUtil.getInstance(requireActivity(),object :SerialPortUtil.OnSerialPortListener{
            override fun onScanStatus(status: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onReceivedData(data: String?) {
                TODO("Not yet implemented")
            }
        })
        val infoAdapter = InfoAdapter(requireActivity())
        infoRecyclerView.apply {
            adapter = infoAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }

        myViewModel.infoLiveData.observe(requireActivity(), Observer {
            infoAdapter.setInfo(it)
            infoRecyclerView.scrollToPosition(it.size - 1)
        })

        buttonSend.setOnClickListener {
            serialPortUtil.sendData(editTextSend.text.toString())
            myViewModel.infoList.add(Info(MSG_SEND_TYPE,editTextSend.text.toString()))
            myViewModel.updateInfoList()
        }

    }

}

class InfoAdapter internal constructor(context: Context): RecyclerView.Adapter<InfoAdapter.InfoViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var infos = emptyList<Info>()

    inner class InfoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val receiveLayout = itemView.findViewById(R.id.receiveLayout) as LinearLayout
        val sendLayout = itemView.findViewById(R.id.sendLayout) as LinearLayout

        val receiveType = itemView.findViewById(R.id.textViewReceiveType) as TextView
        val receiveTextView = itemView.findViewById(R.id.textViewReceice) as TextView
        val sendType = itemView.findViewById(R.id.textViewSendType) as TextView
        val sendTextView = itemView.findViewById(R.id.textViewSend) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val itemView = inflater.inflate(R.layout.info_cell,parent,false)
        return InfoViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return infos.size
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val current = infos[position]
        if (current.type == MSG_RECE_TYPE){
            holder.receiveLayout.visibility = View.VISIBLE
            holder.sendLayout.visibility = View.GONE
            holder.receiveTextView.text = current.content
        }else if(current.type == MSG_SEND_TYPE){
            holder.receiveLayout.visibility = View.GONE
            holder.sendLayout.visibility = View.VISIBLE
            holder.sendTextView.text = current.content
        }
    }

    internal fun setInfo(infos: List<Info>){
        this.infos = infos
        notifyDataSetChanged()
    }
}
