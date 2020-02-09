package com.example.rssreader

import com.example.rssreader.model.FeedCardViewModel
import com.example.rssreader.model.ScreenViewModel

interface FeedContract {

    interface Presenter {
        fun loadFromScratch()
        fun autoLoad()
        fun detach()
    }

    interface View {
        fun showViewModel(viewModel: ScreenViewModel<FeedCardViewModel>)
    }
}
