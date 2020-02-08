package com.example.rssreader.data

import com.example.rssreader.model.FeedItem
import com.example.rssreader.model.Page
import com.example.rssreader.model.Response
import com.example.rssreader.parser.parse
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentLinkedQueue

class ApiFeedRepository : ListRepository<FeedItem> {

    private val feeds = listOf(
        "https://blog.us.playstation.com/feed/?paged=",
        "https://blog.mozilla.org/feed/?paged=",
        "https://blogs.windows.com/feed/?paged=",
        "https://blog.jetbrains.com/feed/?paged="
    )

    private var currentPage = 1

    override suspend fun getPage(loadFromScratch: Boolean): Response<Page<FeedItem>> {
        if (loadFromScratch) {
            currentPage = 1
        }
        val client = HttpClient(Android)

        val data = ConcurrentLinkedQueue<FeedItem>()
        runBlocking {
            feeds.forEach {
                launch {
                    data.addAll(parse(client.get("$it$currentPage")))
                }
            }
        }
        currentPage++

        val list = data.toList()
        return Response.Result(
            Page(list, list.isNotEmpty())
        )
    }
}
