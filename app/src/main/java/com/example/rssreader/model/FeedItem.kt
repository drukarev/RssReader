package com.example.rssreader.model

import org.threeten.bp.OffsetDateTime

data class FeedItem(
    val id: String,
    val title: String?,
    val author: String?,
    val date: OffsetDateTime?, //TODO: check for date correctness
    val sourceFeedName: String?
)
