package com.example.rssreader.parser

import com.example.rssreader.model.FeedItem
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

@Throws(XmlPullParserException::class, IOException::class)
fun XmlPullParser.parseRss(): List<FeedItem> {
    nextTag()
    require(XmlPullParser.START_TAG, null, "rss")
    val items = mutableListOf<FeedItem>()
    nextTag()
    require(XmlPullParser.START_TAG, null, "channel")
    var sourceFeedName: String? = null

    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }

        if (name == "title") {
            sourceFeedName = readText()
        }
    }

    if (sourceFeedName == null) {
        throw XmlPullParserException("Didn't find required field _title_")
    }

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

@Throws(IOException::class, XmlPullParserException::class)
fun XmlPullParser.readRssItem(sourceFeedName: String): FeedItem {
    var id = ""
    var title: String? = null
    var author: String? = null
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
        id = id,
        title = title,
        author = author,
        date = date?.run { OffsetDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME) } ?: OffsetDateTime.now(),
        sourceFeedName = sourceFeedName
    )
}

@Throws(IOException::class, XmlPullParserException::class)
fun XmlPullParser.readText(): String {
    var result = ""
    val event = next()
    if (event == XmlPullParser.TEXT || event == XmlPullParser.CDSECT) {
        result = text
        nextTag()
    }
    return result
}

@Throws(XmlPullParserException::class, IOException::class)
fun XmlPullParser.skip() {
    check(eventType == XmlPullParser.START_TAG)
    var depth = 1
    while (depth != 0) {
        when (next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}
