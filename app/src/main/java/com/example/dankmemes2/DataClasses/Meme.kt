package com.example.dankmemes2.DataClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Meme(
    var subreddit: String,
    var title: String,
    var url: String,
    val preview: String,
    var author: String,
    var ups: String
)  : Parcelable