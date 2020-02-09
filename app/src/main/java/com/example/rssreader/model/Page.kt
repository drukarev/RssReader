package com.example.rssreader.model

import java.util.Queue

/**
 * One page, loaded by the [PageRepository].
 * Doesn't guarantee  sorting of the elements in [data]
 */
data class Page<T : Any>(
    val data: Queue<T>,
    val hasMoreItems: Boolean
)
