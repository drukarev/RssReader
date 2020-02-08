package com.example.rssreader.model

data class FeedCardViewModel(
    val id: String,
    val title: CharSequence,
    val author: CharSequence,
    val date: CharSequence,
    val sourceFeedName: CharSequence
)
