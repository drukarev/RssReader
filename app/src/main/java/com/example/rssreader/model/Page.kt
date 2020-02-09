package com.example.rssreader.model

data class Page<T : Any>(
    val data: List<T>,
    val hasMoreItems: Boolean
)
