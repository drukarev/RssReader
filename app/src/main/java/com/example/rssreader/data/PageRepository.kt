package com.example.rssreader.data

import com.example.rssreader.model.Page
import com.example.rssreader.model.Response

/**
 * Repository, that for each call of [getPage] returns either next page or [Response.Fail].
 */
interface PageRepository<T : Any> {
     suspend fun getPage(loadFromScratch: Boolean): Response<Page<T>>
}
