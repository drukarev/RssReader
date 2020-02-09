package com.example.rssreader

import com.example.rssreader.model.FeedCardViewModel
import com.example.rssreader.model.ListViewModel

interface FeedContract {

    interface Presenter {
        fun loadFromScratch()
        fun autoLoad()
    }

    interface View {
        fun showViewModel(viewModel: ListViewModel<FeedCardViewModel>)
    }
}
