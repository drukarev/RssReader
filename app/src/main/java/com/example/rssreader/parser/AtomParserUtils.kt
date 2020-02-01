package com.example.rssreader.parser

import android.util.Log
import com.example.rssreader.model.FeedItem
import org.xmlpull.v1.XmlPullParser

//TODO: check which fields are optional in atom
fun XmlPullParser.parseAtom(): List<FeedItem> {
    val items = mutableListOf<FeedItem>()

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

    while (true ) {
        val a = next()
        if (a == XmlPullParser.END_TAG) {
            break
        }
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }

        if (name == "entry") {
            items.add(readAtomItem(sourceFeedName))
        } else {
            skip()
        }
    }

    return items
}

fun XmlPullParser.readAtomItem(sourceFeedName: String): FeedItem {
    var id: String = "" //TODO: throw exception if empty
    var title: String? = null
    var author: String? = null
    var date: String? = null

    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (name) {
            "id" -> id = readText()
            "title" -> title = readText()
            "author" -> author = readAtomAuthor()
            "published" -> date = readText()
            else -> skip()
        }
    }

    return FeedItem(
        uid = id,
        title = title,
        author = author,
        date = date,
        sourceFeedName = sourceFeedName
    )
}

fun XmlPullParser.readAtomAuthor(): String? {
    var authorName: String? = null

    while (next() != XmlPullParser.END_TAG) {
        if (eventType != XmlPullParser.START_TAG) {
            continue
        }
        when (name) {
            "name" -> authorName = readText()
            else -> skip()
        }
    }

    return authorName
}
