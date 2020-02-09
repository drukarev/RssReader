package com.example.rssreader.model

import org.threeten.bp.OffsetDateTime

/**
 * One element of the rss feed
 */
data class FeedItem(
    val id: String,
    val date: OffsetDateTime,
    val sourceFeedName: String,
    val title: String?,
    val author: String?
)
