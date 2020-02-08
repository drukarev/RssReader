package com.example.rssreader.data

import com.example.rssreader.model.Page
import com.example.rssreader.model.Response

interface ListRepository<T : Any> {
     suspend fun getPage(loadFromScratch: Boolean): Response<Page<T>>
}
