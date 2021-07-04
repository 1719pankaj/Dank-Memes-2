package com.example.dankmemes2.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dankmemes2.Adapters.SubredditListAdapterval
import com.example.dankmemes2.DataClasses.Subreddit
import com.example.dankmemes2.R
import kotlinx.android.synthetic.main.fragment_config.view.*

class ConfigFragment : Fragment() {

    lateinit var mAdapter: SubredditListAdapterval

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view =  inflater.inflate(R.layout.fragment_config, container, false)

            view.doneBT.setOnClickListener { findNavController().navigate(R.id.action_configFragment_to_mainFragment) }
            mAdapter = SubredditListAdapterval(requireActivity(), requireContext())
            val manager = LinearLayoutManager(context)
            view.subRecycler.layoutManager = manager
            val items = fetchData()


        return view
    }

    private fun fetchData() {
        val subArray = ArrayList<Subreddit>()

        for (i in 0..10) {
            val subreddit = Subreddit("meme")
            subArray.add(subreddit)
            Log.i("TAGG","For loop")
        }
        Log.i("TAGG", "${subArray}")
        mAdapter.updateMeme(subArray)
    }


}