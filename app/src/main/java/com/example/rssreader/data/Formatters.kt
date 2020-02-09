package com.example.rssreader.data

import android.content.Context
import com.example.rssreader.R
import com.example.rssreader.model.*
import com.example.rssreader.model.Failure.*
import org.threeten.bp.format.DateTimeFormatter

private const val DATE_PATTERN = "dd MMMM, HH:mm"

fun formatFeedItem(context: Context, item: FeedItem): ListItemViewModel.Data {
    return ListItemViewModel.Data(
        FeedCardViewModel(
            id = item.id,
            title = item.title ?: context.getString(R.string.feed_item_no_title),
            author = item.author ?: context.getString(R.string.feed_item_no_author),
            date = item.date?.format(DateTimeFormatter.ofPattern(DATE_PATTERN))
                ?: context.getString(R.string.feed_item_no_date),
            sourceFeedName = item.sourceFeedName
        )
    )
}

fun formatFailureItem(context: Context, failure: Failure): ListItemViewModel.Error {
    return ListItemViewModel.Error(failure.getText(context))
}

fun <T : Any> formatFailureFullScreen(context: Context, failure: Failure): ListErrorViewModel<T> {
    return ListErrorViewModel(failure.getText(context), context.getString(R.string.error_subtitle_retry))
}

private fun Failure.getText(context: Context): String {
    return context.getString(
        when (this) {
            Unknown -> R.string.error_unknown
            NoItems -> R.string.error_no_items
            InvalidRssXml -> R.string.error_invalid_rss_xml
            NoConnection -> R.string.error_no_connection
        }
    )
}
