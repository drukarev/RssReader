package com.example.rssreader.model

sealed class ListViewModel<T : Any> {
    data class Error<T : Any>(
        val title: CharSequence,
        val subtitle: CharSequence
    ) : ListViewModel<T>()

    data class Data<T : Any>(
        val items: List<ListItemViewModel<T>>,
        val hasMoreItems: Boolean
    ) : ListViewModel<T>()

    class Progress<T : Any>: ListViewModel<T>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}
