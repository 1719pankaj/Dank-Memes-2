package com.example.dankmemes2.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dankmemes2.Adapters.SubredditListAdapterval
import com.example.dankmemes2.DataClasses.Subreddit
import com.example.dankmemes2.R
import kotlinx.android.synthetic.main.fragment_config.view.*

class ConfigFragment : Fragment(), SubredditListAdapterval.SubredditItemChecked{

    var final_list: ArrayList<String> = arrayListOf()
    lateinit var mAdapter: SubredditListAdapterval

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view =  inflater.inflate(R.layout.fragment_config, container, false)

            view.doneBT.setOnClickListener {
                saveUserPreferences()
                Toast.makeText(context, "Preferences Updated", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_configFragment_to_mainFragment)
            }
            val manager = LinearLayoutManager(context)
            view.subRecycler.layoutManager = manager
            val items = fetchData()
            mAdapter = SubredditListAdapterval(items, requireActivity(), requireContext(), this)
            view.subRecycler.adapter = mAdapter



        return view
    }

    private fun fetchData(): ArrayList<Subreddit> {
        val subArray = ArrayList<Subreddit>()
        subArray.add(Subreddit("/Default"))
        subArray.add(Subreddit("/IndianDankMemes"))
        subArray.add(Subreddit("/BanglaMemes"))
//        subArray.add(Subreddit("/pakistanimemes"))

        return subArray
    }

    private fun saveUserPreferences() {
        val sharedPref = requireActivity().getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putStringSet("userSources", final_list.toSet())
        editor.apply()
    }



    override fun onItemChecked(item: Subreddit) {
        final_list.add(item.name)
    }

    override fun onItemUnchecked(item: Subreddit) {
        final_list.remove(item.name)
    }
}