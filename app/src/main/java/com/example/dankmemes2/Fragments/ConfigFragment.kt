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

            if (!onBoardingFinishedChk()) {
                view.onboardingCfgMsg.visibility = View.VISIBLE
                onBoardingFinished()
            }

            view.doneBT.setOnClickListener {
                saveUserPreferences()
                Toast.makeText(context, "Preferences Updated", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_configFragment_to_mainFragment)
            }

            //Ok I know this is a shitty system but what can I say? the only other way is a fucking recyclerView
            //So here down you make a getCheckState method if block
            //Actually just copy my homework and change names ( For every single new block )
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

            //For every single new checkbox make a setOnCheckedChangeListener and copy my homework ( Change the names FFS )
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
            //Look I know this is not elegant but it works and I don't feel like implementing MVVM rn.


        return view
    }

    private fun onBoardingFinishedChk(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
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