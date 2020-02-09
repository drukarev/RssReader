package com.example.rssreader.view

import android.content.Context
import com.example.rssreader.R
import com.example.rssreader.model.*
import com.example.rssreader.model.Failure.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

private const val DATE_PATTERN = "YYYY.MM.dd, HH:mm"

fun <T : Any> formatFeedItem(context: Context, item: FeedItem): ListItemViewModel.Data<T> {
    return ListItemViewModel.Data(
        FeedCardViewModel(
            id = item.id,
            title = item.title ?: context.getString(R.string.feed_item_no_title),
            author = item.author ?: context.getString(R.string.feed_item_no_author),
            date = item.date.formatDateTime(),
            sourceFeedName = item.sourceFeedName
        )
    )
}

fun <T : Any> formatFailureItem(context: Context, failure: Failure): ListItemViewModel.Error<T> {
    return ListItemViewModel.Error(failure.getText(context))
}

fun <T : Any> formatFailureFullScreen(context: Context, failure: Failure): ListViewModel.Error<T> {
    return ListViewModel.Error(failure.getText(context), context.getString(R.string.error_subtitle_retry))
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

fun OffsetDateTime.formatDateTime(): CharSequence {
    return LocalDateTime.ofInstant(toInstant(), ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(DATE_PATTERN))
}
