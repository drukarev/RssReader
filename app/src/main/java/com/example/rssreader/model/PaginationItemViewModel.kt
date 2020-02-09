package com.example.rssreader.model

/**
 * View representation of one item in the list. Can be data, error or progress.
 */
sealed class PaginationItemViewModel<T> {

    data class Data<T>(val data: T) : PaginationItemViewModel<T>()
    data class Error<T>(val errorText: CharSequence) : PaginationItemViewModel<T>()
    class Progress<T> : PaginationItemViewModel<T>() {
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
