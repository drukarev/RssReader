package com.example.rssreader.data

import android.content.Context
import com.example.rssreader.model.AppDatabase
import com.example.rssreader.model.FeedItem
import com.example.rssreader.parser.parse
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentLinkedQueue

class ApiFeedRepository {

    private val feeds = listOf(
        "https://habr.com/ru/rss/all/all/?fl=ru", //TODO: remove
        "https://blog.jetbrains.com/feed/",
        "https://www.objc.io/feed.xml",
        "https://bitbucket.org/blog/feed",
        "https://www.blog.google/products/android/rss",
        "https://github.blog/engineering.atom",
        "https://about.gitlab.com/atom.xml"
    )

    suspend fun updateFeedItems(applicationContext: Context) {
        return withContext(Dispatchers.IO) {
            val client = HttpClient(Android)

            val data = ConcurrentLinkedQueue<FeedItem>()
            runBlocking {
                feeds.forEach {
                    launch {
                        data.addAll(parse(client.get(it)))
                    }
                }
            }

            AppDatabase.get(applicationContext).feedDao().insertAll(data.toList())
        }
    }
}
