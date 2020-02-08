package com.example.rssreader.model

sealed class ListViewModel<T : Any>

data class ListErrorViewModel<T : Any>(
    val title: CharSequence,
    val subtitle: CharSequence
) : ListViewModel<T>()

data class ListDataViewModel<T : Any>(
    val items: List<ListItemViewModel>,
    val hasMoreItems: Boolean
) : ListViewModel<T>()
