package com.example.rssreader.model

sealed class ListViewModel<T : Any>

//TODO: rename

data class ListErrorViewModel<T : Any>(
    val title: CharSequence,
    val subtitle: CharSequence
) : ListViewModel<T>()

data class ListDataViewModel<T : Any>(
    val items: List<ListItemViewModel<T>>,
    val hasMoreItems: Boolean
) : ListViewModel<T>()

class ListProgressViewModel<T : Any>: ListViewModel<T>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
