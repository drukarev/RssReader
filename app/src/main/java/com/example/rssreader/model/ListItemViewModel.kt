package com.example.rssreader.model

sealed class ListItemViewModel {

    data class Data(val data: FeedCardViewModel) : ListItemViewModel()
    data class Error(val errorText: CharSequence) : ListItemViewModel()
    object Progress : ListItemViewModel()
}
