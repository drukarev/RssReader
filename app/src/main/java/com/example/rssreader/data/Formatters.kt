package com.example.rssreader.data

import com.example.rssreader.model.*
import com.example.rssreader.model.Failure.*
import org.threeten.bp.format.DateTimeFormatter

fun formatFeedItem(item: FeedItem): ListItemViewModel.Data {
    return ListItemViewModel.Data(
        FeedCardViewModel(
            id = item.id,
            title = item.title ?: "No title",
            author = item.author ?: "No author",
            date = item.date?.format(DateTimeFormatter.ofPattern("dd MMMM, HH:mm")) ?: "No date",
            sourceFeedName = item.sourceFeedName ?: "SourceFeedName"
        )
    )
}

fun formatFailureItem(failure: Failure): ListItemViewModel.Error {
    return ListItemViewModel.Error(failure.getText())
}

fun <T : Any> formatFailureFullScreen(failure: Failure): ListErrorViewModel<T> {
    return ListErrorViewModel(failure.getText(), "Try to refresh")
}

private fun Failure.getText(): String {
    return when (this) {
        Unknown -> "Unknown error"
        NoItems -> "No items in feed"
        InvalidRssXml -> "Invalid xml"
        NoConnection -> "No connection"
    }
}
