package com.example.dankmemes2.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.dankmemes2.R
import kotlinx.android.synthetic.main.fragment_config.view.*

class ConfigFragment : Fragment() {
    var final_list: ArrayList<String> = arrayListOf()

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view =  inflater.inflate(R.layout.fragment_config, container, false)

            view.doneBT.setOnClickListener {
                saveUserPreferences()
                Toast.makeText(context, "Preferences Updated", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_configFragment_to_mainFragment)
            }

            if (getCheckState("/Default")) {
                view.defaultCB.isChecked = true
                final_list.add("/Default")
            }

            if (getCheckState("/IndianDankMemes")) {
                view.indianCB.isChecked = true
                final_list.add("/IndianDankMemes")
            }
            if (getCheckState("/BanglaMemes")) {
                view.bengaliCB.isChecked = true
                final_list.add("/BanglaMemes")
            }

            view.defaultCB.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    final_list.add("/Default")
                    saveCheckState("/Default", true)
                }
                else{
                    final_list.remove("/Default")
                    saveCheckState("/Default", false)
                }
            }

            view.indianCB.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    final_list.add("/IndianDankMemes")
                    saveCheckState("/IndianDankMemes", true)
                }
                else{
                    final_list.remove("/IndianDankMemes")
                    saveCheckState("/IndianDankMemes", false)
                }
            }

            view.bengaliCB.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    final_list.add("/BanglaMemes")
                    saveCheckState("/BanglaMemes", true)
                }
                else{
                    final_list.remove("/BanglaMemes")
                    saveCheckState("/BanglaMemes", false)

                }
            }


        return view
    }

    override fun onStop() {
        super.onStop()
        saveUserPreferences()
        Toast.makeText(context, "Preferences Updated", Toast.LENGTH_SHORT).show()
        Log.i("tagg", "onStop Called")
    }

    private fun saveCheckState(checkBoxName: String, state: Boolean) {
        val sharedPref = requireActivity().getSharedPreferences("chkState", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(checkBoxName, state)
        editor.apply()
    }

    private fun getCheckState(checkBoxName: String): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("chkState", Context.MODE_PRIVATE)
        return sharedPref.getBoolean(checkBoxName, false)
    }


    private fun saveUserPreferences() {
        val sharedPref = requireActivity().getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putStringSet("userSources", final_list.toSet())
        editor.apply()
    }
}