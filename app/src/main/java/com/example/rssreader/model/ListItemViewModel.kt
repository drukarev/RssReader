package com.example.rssreader.model

sealed class ListItemViewModel<T> {

    data class Data<T>(val data: FeedCardViewModel) : ListItemViewModel<T>()
    data class Error<T>(val errorText: CharSequence) : ListItemViewModel<T>()
    class Progress<T> : ListItemViewModel<T>() {
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
