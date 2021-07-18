package com.example.dankmemes2.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dankmemes2.Adapters.StarMemeListAdapter
import com.example.dankmemes2.DataClasses.Meme
import com.example.dankmemes2.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_star.view.*


class StarFragment : Fragment() {

    lateinit var mAdapter: StarMemeListAdapter
    var isScrolling: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_star, container, false)

        view.fabBack.setOnClickListener { findNavController().navigate(R.id.action_starFragment_to_mainFragment) }

        //IDHAR SE
        val manager = LinearLayoutManager(context)
        view.starRecyclerView.layoutManager = manager
        mAdapter = StarMemeListAdapter(requireActivity(), requireContext())
        view.starRecyclerView.adapter = mAdapter
        val items = fetchStars()
        view.starRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentItems = manager.childCount
                val totalItems = manager.itemCount
                val scrollOutItems = manager.findFirstVisibleItemPosition()

                if(isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    getFromFirestore()
                    !isScrolling
                }
            }
        })
        view.starSwipeContainer.setOnRefreshListener {
            fetchStars()
            view.starSwipeContainer.setRefreshing(false)
        }
        //IDHAR TAK SAB RECYCLER

        return view
    }

    fun fetchStars() {
        mAdapter.clear()
        getFromFirestore()
    }

    private fun getFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("starrMemes")
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    for(document in it.result!!) {
                        val meme = Meme (
                            document.data.getValue("subreddit").toString(),
                            document.data.getValue("title").toString(),
                            document.data.getValue("url").toString(),
                            "placeholder MainFragment getFromFirestore()",
                            document.data.getValue("author").toString(),
                            document.data.getValue("ups").toString()
                        )
                        Log.i("tagg", meme.toString())
                        mAdapter.appendSingleMeme(meme)
                    }
                }

            }
    }

}