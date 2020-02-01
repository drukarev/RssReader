package com.example.rssreader.parser

import android.util.Xml
import com.example.rssreader.model.FeedItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

fun parse(inputStream: InputStream): List<FeedItem> {
    val parser: XmlPullParser = Xml.newPullParser()
    parser.setInput(inputStream, null)
    parser.nextTag()

    return when {
        parser.name == "rss" -> parser.parseRss()
        parser.name == "feed" -> parser.parseAtom()
        else -> emptyList()
    }
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
