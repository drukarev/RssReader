package com.example.rssreader.model

/**
 * View representation of the screen with pagination list.
 * Full screen progress and error can be shown when list is loading from the scratch.
 * If there are items in the list and we are loading next page, [Data] will be shown,
 * and last of the [Data.items] can be an error or progress item.
 */
sealed class ScreenViewModel<T : Any> {
    data class Error<T : Any>(
        val title: CharSequence,
        val subtitle: CharSequence
    ) : ScreenViewModel<T>()

    data class Data<T : Any>(
        val items: List<PaginationItemViewModel<T>>,
        val hasMoreItems: Boolean
    ) : ScreenViewModel<T>()

    class Progress<T : Any> : ScreenViewModel<T>() {
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
