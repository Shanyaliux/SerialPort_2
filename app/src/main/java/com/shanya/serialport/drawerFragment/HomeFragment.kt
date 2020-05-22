package com.shanya.serialport.drawerFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.shanya.serialport.R
import com.shanya.serialport.viewpagerfragment.CarFragment
import com.shanya.serialport.viewpagerfragment.CommunicationFragment
import com.shanya.serialport.viewpagerfragment.ControlFragment
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewPager2.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount() = 3

            override fun createFragment(position: Int) =
                when(position){
                    0 -> CommunicationFragment()
                    1 -> ControlFragment()
                    else -> CarFragment()
                }
        }

        TabLayoutMediator(tabLayout,viewPager2){tab, position ->
            when(position){
                0 -> tab.text = ""
                1 -> tab.text = ""
                else -> tab.text = ""
            }
        }.attach()
    }

}
