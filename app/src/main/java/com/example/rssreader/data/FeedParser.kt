package com.example.rssreader.data

import android.util.Xml
import com.example.rssreader.model.FeedItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

fun parse(inputStream: InputStream): List<FeedItem> {
    val items = mutableListOf<FeedItem>()
    val parser: XmlPullParser = Xml.newPullParser()
    parser.setInput(inputStream, null)
    parser.nextTag()

    parser.require(XmlPullParser.START_TAG, null, "rss") //TODO add atom feed
    parser.nextTag()
    parser.require(XmlPullParser.START_TAG, null, "channel") //TODO atom feed

    var sourceFeedName: String? = null

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }

        if (parser.name == "title") {
            sourceFeedName = readText(parser)
        }
    }

    checkNotNull(sourceFeedName)

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }

        if (parser.name == "item") {
            items.add(readItem(parser, sourceFeedName))
        } else {
            skip(parser)
        }
    }

    return items
}

fun readItem(parser: XmlPullParser, sourceFeedName: String): FeedItem {
    var title: String? = null //TODO: check description if title is empty
    var author: String? = null ////TODO: check dc:creator and author
    var date: String? = null

    while (parser.next() != XmlPullParser.END_TAG) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (parser.name) {
            "title" -> title = readText(parser)
            "author" -> author = readText(parser)
            "pubDate" -> date = readText(parser)
            else -> skip(parser)
        }
    }

    return FeedItem(
        title = title,
        author = author,
        date = date,
        sourceFeedName = sourceFeedName
    )
}

@Throws(IOException::class, XmlPullParserException::class)
private fun readText(parser: XmlPullParser): String {
    var result = ""
    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }
    return result
}

@Throws(XmlPullParserException::class, IOException::class)
private fun skip(parser: XmlPullParser) {
    check(parser.eventType == XmlPullParser.START_TAG)
    var depth = 1
    while (depth != 0) {
        when (parser.next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}
