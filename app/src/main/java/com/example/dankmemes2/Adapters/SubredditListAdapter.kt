package com.example.dankmemes2.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.dankmemes2.DataClasses.Meme
import com.example.dankmemes2.DataClasses.Subreddit
import com.example.dankmemes2.Fragments.ConfigFragment
import com.example.dankmemes2.R

class SubredditListAdapterval (val activity: Activity, val context: Context) : RecyclerView.Adapter<SubredditListAdapterval.SubredditViewHolder>() {

    private val item: ArrayList<Subreddit> = ArrayList()
    val configFragment = ConfigFragment()
    lateinit var final_list: ArrayList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subreddit, parent, false)
        val viewHolder = SubredditViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: SubredditViewHolder, position: Int) {
        val currentSubreddit = item[position]

        holder.checkBox.text = currentSubreddit.name
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                final_list.add(currentSubreddit.name)
            }
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    fun updateMeme(updatedSubs: ArrayList<Subreddit>) {
        item.addAll(updatedSubs)
        notifyDataSetChanged()
    }

    inner class SubredditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    }
}