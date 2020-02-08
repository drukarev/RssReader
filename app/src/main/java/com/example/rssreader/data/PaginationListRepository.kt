package com.example.rssreader.data

import com.example.rssreader.model.ListViewModel

interface PaginationListRepository<T: Any> {

    suspend fun loadFromScratch(): ListViewModel<T>
    suspend fun autoLoad(): ListViewModel<T>
}
