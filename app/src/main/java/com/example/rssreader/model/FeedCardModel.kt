package com.example.rssreader.model

data class FeedCardModel(
    val id: Int,
    val title: CharSequence,
    val author: CharSequence,
    val date: CharSequence,
    val sourceFeedName: CharSequence
)
