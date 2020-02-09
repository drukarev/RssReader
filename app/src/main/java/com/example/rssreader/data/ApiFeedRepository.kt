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
        SourceFeed("https://blog.us.playstation.com/feed/?paged="),
        SourceFeed("https://blog.mozilla.org/feed/?paged="),
        SourceFeed("https://blogs.windows.com/feed/?paged="),
        SourceFeed("https://blog.jetbrains.com/feed/?paged=")
    )

    private var currentPage = 1

    override suspend fun getPage(loadFromScratch: Boolean): Response<Page<FeedItem>> {
        if (!hasInternetConnection()) {
            Log.d(TAG, "No internet connection")
            return Response.Fail(Failure.NoConnection)
        }

        if (loadFromScratch) {
            currentPage = 1
            feeds.forEach { it.hasMorePages = true }
        }

        Log.d(TAG, "Loading page $currentPage")

        val deferredFeeds = feeds.filter { it.hasMorePages }
            .map { sourceFeed ->
                GlobalScope.async(Dispatchers.IO) {
                    try {
                        Log.d(TAG, "Loading feed ${sourceFeed.url}")
                        val response = httpClient.get<InputStream>("${sourceFeed.url}$currentPage")
                        when (val parsedResponse = parseAsRss(response)) {
                            is Response.Result -> parsedResponse.value
                            is Response.Fail -> {
                                Log.d(TAG, "Parsing failure. Removing ${sourceFeed.url} from list of feeds")
                                sourceFeed.hasMorePages = false
                                emptyList()
                            }
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "Request failure. Removing ${sourceFeed.url} from list of feeds", e)
                        sourceFeed.hasMorePages = false
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

    private data class SourceFeed(
        val url: String,
        var hasMorePages: Boolean = true
    )
}
