package com.example.rssreader.parser

import com.example.rssreader.model.FeedItem
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.xmlpull.v1.XmlPullParser

//TODO: check which fields are optional in rss
fun XmlPullParser.parseRss(): List<FeedItem> {
    val items = mutableListOf<FeedItem>()

    nextTag()
    var sourceFeedName: String? = null

    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }

        if (name == "title") {
            sourceFeedName = readText()
        }
    }

    checkNotNull(sourceFeedName)

    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }

        if (name == "item") {
            items.add(readRssItem(sourceFeedName))
        } else {
            skip()
        }
    }

    return items
}

fun XmlPullParser.readRssItem(sourceFeedName: String): FeedItem {
    var id: String = "" //TODO: throw exception if empty
    var title: String? = null //TODO: check description if title is empty
    var author: String? = null ////TODO: check dc:creator and author
    var date: String? = null

    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }

        when (name) {
            "guid" -> id = readText()
            "title" -> title = readText()
            "creator" -> author = readText()
            "pubDate" -> date = readText()
            else -> skip()
        }
    }

    return FeedItem(
        uid = id,
        title = title,
        author = author,
        date = OffsetDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME),
        sourceFeedName = sourceFeedName
    )
}
