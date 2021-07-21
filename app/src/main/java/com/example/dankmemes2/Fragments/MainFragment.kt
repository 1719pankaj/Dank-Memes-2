package com.example.dankmemes2.Fragments

import android.content.Context
import android.graphics.Movie
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.dankmemes2.Adapters.MemeListAdapter
import com.example.dankmemes2.DataClasses.Meme
import com.example.dankmemes2.MySingleton
import com.example.dankmemes2.R
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import org.json.JSONArray


class MainFragment : Fragment() {
    lateinit var mAdapter: MemeListAdapter
    var downloadId = 0
    var isScrolling: Boolean = false
    lateinit var memeArray: ArrayList<Meme>

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim) }

    private var clicked: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        view.overflowFab.setOnClickListener {
            onAddClicked()
        }
        view.configFab.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_configFragment) }
        view.starFab.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_starFragment) }

        //IDHAR SE
        val manager = LinearLayoutManager(context)
        view.recyclerView.layoutManager = manager
        mAdapter = MemeListAdapter(requireActivity(), requireContext())
        view.recyclerView.adapter = mAdapter
        fetchData(view)
        view.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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
                    fetchData(view)
                    !isScrolling
                }
            }
        })
        view.swipeContainer.setOnRefreshListener {
            mAdapter.clear()
            fetchData(view)
            swipeContainer.setRefreshing(false)
        }
        //IDHAR TAK SAB RECYCLER

        return view
    }

    private fun onAddClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked) {
            configFab.visibility = View.VISIBLE
            starFab.visibility = View.VISIBLE
        } else {
            configFab.visibility = View.INVISIBLE
            starFab.visibility = View.INVISIBLE
        }
    }


    private fun setAnimation(clicked: Boolean) {
        if(!clicked) {
            overflowFab.startAnimation(rotateOpen)
            configFab.startAnimation(fromBottom)
            starFab.startAnimation(fromBottom)
        } else {
            overflowFab.startAnimation(rotateClose)
            configFab.startAnimation(toBottom)
            starFab.startAnimation(toBottom)
        }
    }


    fun fetchData(view: View): Boolean {
        Log.i("Tagger", "Progress bar storted")
        view.progressBar.visibility = View.VISIBLE
        if(getUserPreferences().isNullOrEmpty()) {
            val url = "https://meme-api.herokuapp.com/gimme/10"
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, {
                val memeJsonArray = it.getJSONArray("memes")
                memeArray = ArrayList<Meme>()
                getMemeMetadata(memeJsonArray, memeArray)

                mAdapter.appendMeme(memeArray)

                view.progressBar.visibility = View.GONE

            }, Response.ErrorListener {
                Toast.makeText(context, "Net off hai shayad", Toast.LENGTH_SHORT).show()
            }) {}
            context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
        }
        else if(getUserPreferences()!!.isNotEmpty()){
            val userSource = getUserPreferences()
            val size = userSource!!.size
            val setSize: Int = try { 6/size }
            catch (e: Exception) { e.printStackTrace(); 6 }
            for(i in userSource) {
                val i = if(i == "/Default") "" else i
                val url = "https://meme-api.herokuapp.com/gimme$i/$setSize"
                val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null, {
                    val memeJsonArray = it.getJSONArray("memes")
                    memeArray = ArrayList<Meme>()
                    getMemeMetadata(memeJsonArray, memeArray)
                    mAdapter.appendMeme(memeArray)
                    view.progressBar.visibility = View.GONE


                }, Response.ErrorListener {
                    Toast.makeText(context, "Net off hai shayad", Toast.LENGTH_SHORT).show()
                }) {}
                context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
            }

        }
        return true
    }

    fun getMemeMetadata(memeJsonArray: JSONArray, memeArray: ArrayList<Meme>): ArrayList<Meme> {
        for (i in 0 until memeJsonArray.length()) {
            val memeJsonObject = memeJsonArray.getJSONObject(i)
            val meme = Meme(
                memeJsonObject.getString("subreddit"),
                memeJsonObject.getString("title"),
                memeJsonObject.getString("url"),
                memeJsonObject.getString("preview"),
                memeJsonObject.getString("author"),
                memeJsonObject.getString("ups")
            )
            val format = meme.url.subSequence(meme.url.length-4, meme.url.length)
            if(format != ".gif") {
                memeArray.add(meme)
            }
        }
        return memeArray
    }

    private fun getUserPreferences(): MutableSet<String>? {
        val sharedPref = requireActivity().getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        return sharedPref.getStringSet("userSources", null)
    }

}