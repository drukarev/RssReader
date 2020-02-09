package com.example.rssreader.model

/**
 * One page, loaded by the [PageRepository].
 * Doesn't guarantee  sorting of the elements in [data]
 */
data class Page<T : Any>(
    val data: List<T>,
    val hasMoreItems: Boolean
)
