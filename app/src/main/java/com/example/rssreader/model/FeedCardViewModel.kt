package com.example.rssreader.model

/**
 * View representation of the feed card
 */
data class FeedCardViewModel(
    val id: String,
    val title: CharSequence,
    val author: CharSequence,
    val date: CharSequence,
    val sourceFeedName: CharSequence
)
