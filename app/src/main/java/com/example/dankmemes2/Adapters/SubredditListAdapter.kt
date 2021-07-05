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
import kotlinx.android.synthetic.main.item_subreddit.view.*

class SubredditListAdapterval (val item: ArrayList<Subreddit>, val activity: Activity, val context: Context, private val listener: SubredditItemChecked) : RecyclerView.Adapter<SubredditListAdapterval.SubredditViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubredditViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subreddit, parent, false)
        val viewHolder = SubredditViewHolder(view)

        view.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                listener.onItemChecked(item[viewHolder.adapterPosition])
            }
            else{
                listener.onItemUnchecked(item[viewHolder.adapterPosition])
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SubredditViewHolder, position: Int) {
        val currentSubreddit = item[position]

        holder.checkBox.text = currentSubreddit.name
    }

    override fun getItemCount(): Int {
        return item.size
    }

    fun updateMeme(updatedSubs: ArrayList<Subreddit>) {
        item.addAll(updatedSubs.shuffled())
        notifyDataSetChanged()
    }

    inner class SubredditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    }

    interface SubredditItemChecked{
        fun onItemChecked(item: Subreddit)
        fun onItemUnchecked(item: Subreddit)
    }
}