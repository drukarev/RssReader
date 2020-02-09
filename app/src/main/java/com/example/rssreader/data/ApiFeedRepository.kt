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
import kotlinx.coroutines.*
import java.io.InputStream
import java.lang.Exception

private const val TAG = "Page Repository"

class ApiFeedRepository(
    private val httpClient: HttpClient,
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

        val deferredFeeds = feeds.map { url ->
            GlobalScope.async(Dispatchers.IO) {
                try {
                    val response = httpClient.get<InputStream>("$url$currentPage")
                    when (val parsedResponse = parseAsRss(response)) {
                        is Response.Result -> return@async parsedResponse.value
                        is Response.Fail -> {
                            Log.d(TAG, "Parsing failure")
                            emptyList<FeedItem>()
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Request failure", e)
                    emptyList<FeedItem>()
                }
            }
        }
        val items = deferredFeeds.awaitAll().flatten()

        currentPage++

        Log.d(TAG, "Found ${items.size} new items")

        return Response.Result(
            Page(items, items.isNotEmpty())
        )
    }
}
