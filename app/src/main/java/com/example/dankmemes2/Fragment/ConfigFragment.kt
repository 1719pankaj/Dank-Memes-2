package com.example.dankmemes2.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.dankmemes2.R
import kotlinx.android.synthetic.main.fragment_config.view.*

class ConfigFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_config, container, false)

        view.doneBT.setOnClickListener { findNavController().navigate(R.id.action_configFragment_to_mainFragment) }

        return view
    }


}