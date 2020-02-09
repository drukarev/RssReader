package com.example.rssreader

import com.example.rssreader.data.PaginationRepository
import com.example.rssreader.model.FeedCardViewModel
import com.example.rssreader.model.FeedItem
import com.example.rssreader.model.ScreenViewModel
import kotlinx.coroutines.*

class FeedPresenter(
    private var view: FeedContract.View?,
    private val repository: PaginationRepository<FeedItem, FeedCardViewModel>
) : FeedContract.Presenter, CoroutineScope by MainScope() {

    override fun loadFromScratch() {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showViewModel(ScreenViewModel.Progress())
            view?.showViewModel(repository.loadFromScratch())
        }
    }

    override fun autoLoad() {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showViewModel(repository.autoLoad())
        }
    }

    override fun detach() {
        view = null
        cancel()
    }
}
