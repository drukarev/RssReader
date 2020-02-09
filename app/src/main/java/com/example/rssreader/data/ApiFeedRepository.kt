package com.example.rssreader.data

import android.util.Log
import com.example.rssreader.model.Failure
import com.example.rssreader.model.FeedItem
import com.example.rssreader.model.Page
import com.example.rssreader.model.Response
import com.example.rssreader.parser.parseAsRss
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.lang.Exception
import java.util.concurrent.ConcurrentLinkedQueue

private const val TAG = "Page Repository"

class ApiFeedRepository(
    private val hasInternetConnection: () -> Boolean
) : PageRepository<FeedItem> {

    private val feeds = listOf(
        "https://blog.us.playstation.com/feed/?paged=",
        "https://blog.mozilla.org/feed/?paged=",
        "https://blogs.windows.com/feed/?paged=",
        "https://blog.jetbrains.com/feed/?paged="
    )

    private var currentPage = 1

    override suspend fun getPage(loadFromScratch: Boolean): Response<Page<FeedItem>> {
        if (!hasInternetConnection()) {
            Log.d(TAG, "No internet connection")
            return Response.Fail(Failure.NoConnection)
        }

        if (loadFromScratch) {
            currentPage = 1
        }

        Log.d(TAG, "Loading page $currentPage")

        val client = HttpClient(Android)

        val data = ConcurrentLinkedQueue<FeedItem>()
        runBlocking {
            feeds.forEach { url ->
                launch {
                    try {
                        val response = client.get<InputStream>("$url$currentPage")
                        when (val parsedResponse = parseAsRss(response)) {
                            is Response.Result -> data.addAll(parsedResponse.value)
                            is Response.Fail -> Log.e(TAG, "Parsing failure")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Request failure", e)
                    }
                }
            }
        }
        currentPage++

        Log.d(TAG, "Found ${data.size} new items")

        return Response.Result(
            Page(data, data.isNotEmpty())
        )
    }
}
