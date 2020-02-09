package com.example.rssreader.data

import com.example.rssreader.model.ListViewModel

/**
 * Repository for handling the pagination from the data source.
 */
interface PaginationRepository<T: Any> {

    /**
     * Removes all stored data and returns first page.
     */
    suspend fun loadFromScratch(): ListViewModel<T>
    /**
     * Returns all previous items + next page.
     */
    suspend fun autoLoad(): ListViewModel<T>
}
