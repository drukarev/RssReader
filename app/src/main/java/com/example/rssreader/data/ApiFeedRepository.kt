package com.example.rssreader.data

import com.example.rssreader.model.FeedItem
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue

class ApiFeedRepository() {

    private val feeds = listOf(
        "https://blog.jetbrains.com/feed/",
        "https://www.objc.io/feed.xml",
        "https://bitbucket.org/blog/feed",
        "https://www.blog.google/products/android/rss"
//        "https://github.blog/engineering.atom",
//        "https://about.gitlab.com/atom.xml",
    )

    suspend fun getFeedItems(): List<FeedItem> {
        val client = HttpClient(Android)

        val data = ConcurrentLinkedQueue<FeedItem>()
        runBlocking {
            feeds.forEach {
                launch(Dispatchers.IO) {
                    data.addAll(parse(client.get(it)))
                }
            }
        }
        return data.toList()
    }
}
