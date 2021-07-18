package com.example.dankmemes2.DataClasses

data class Meme(
    var subreddit: String,
    var title: String,
    var url: String,
    val preview: String,
    var author: String,
    var ups: String
) {
}