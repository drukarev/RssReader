package com.example.rssreader.parser

import android.util.Log
import android.util.Xml
import com.example.rssreader.model.Failure.InvalidRssXml
import com.example.rssreader.model.FeedItem
import com.example.rssreader.model.Response
import com.example.rssreader.model.Response.Fail
import com.example.rssreader.model.Response.Result
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

fun parseAsRss(inputStream: InputStream): Response<List<FeedItem>> {
    val parser: XmlPullParser = Xml.newPullParser()
    parser.setInput(inputStream, null)
    return try {
        Result(parser.parseRss())
    } catch (e: XmlPullParserException) {
        logParsingFailure(e)
        Fail(InvalidRssXml)
    } catch (e: IOException) {
        logParsingFailure(e)
        Fail(InvalidRssXml)
    }
}

private fun logParsingFailure(e: Exception) {
    Log.d("LogParsingFailure", "Failed to parse rss", e)
}
