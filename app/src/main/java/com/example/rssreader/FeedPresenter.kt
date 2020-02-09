package com.example.rssreader

import com.example.rssreader.data.PaginationRepository
import com.example.rssreader.model.FeedCardViewModel
import com.example.rssreader.model.FeedItem
import com.example.rssreader.model.ListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FeedPresenter(
    private val view: FeedContract.View,
    private val repository: PaginationRepository<FeedItem, FeedCardViewModel>
) : FeedContract.Presenter {

    override fun loadFromScratch() {
        GlobalScope.launch(Dispatchers.Main) {
            view.showViewModel(ListViewModel.Progress())
            view.showViewModel(repository.loadFromScratch())
        }
    }

    override fun autoLoad() {
        GlobalScope.launch(Dispatchers.Main) {
            view.showViewModel(repository.autoLoad())
        }
    }
}
