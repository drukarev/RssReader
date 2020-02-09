package com.example.rssreader.model

import java.util.Queue

data class Page<T : Any>(
    val data: Queue<T>,
    val hasMoreItems: Boolean
)
