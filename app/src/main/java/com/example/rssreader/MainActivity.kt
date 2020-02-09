package com.example.rssreader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rssreader.data.*
import com.example.rssreader.model.FeedCardViewModel
import com.example.rssreader.model.ScreenViewModel
import com.example.rssreader.pagination.FeedAdapter
import com.example.rssreader.utils.hasInternetConnection
import com.example.rssreader.utils.showChild
import com.example.rssreader.view.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_error.*

class MainActivity : AppCompatActivity(), FeedContract.View {

    private lateinit var adapter: FeedAdapter
    private lateinit var presenter: FeedContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpPresenter()
        setUpFeed()
        presenter.loadFromScratch()
    }

    override fun onDestroy() {
        refreshContainer.setOnRefreshListener(null)
        presenter.detach()
        super.onDestroy()
    }

    override fun showViewModel(viewModel: ScreenViewModel<FeedCardViewModel>) {
        when (viewModel) {
            is ScreenViewModel.Error -> {
                errorTitle.text = viewModel.title
                errorSubtitle.text = viewModel.subtitle
                refreshContainer.isRefreshing = false
                dataContainer.showChild(errorView)
                adapter.submitList(emptyList())
            }
            is ScreenViewModel.Data -> {
                feed.clearOnScrollListeners()
                adapter.submitList(viewModel.items)
                if (viewModel.hasMoreItems) {
                    feed.addOnScrollListener(PaginationScrollListener(feed.layoutManager as LinearLayoutManager) {
                        presenter.autoLoad()
                    })
                }
                refreshContainer.isRefreshing = false
                dataContainer.showChild(refreshContainer)
            }
            is ScreenViewModel.Progress -> {
                refreshContainer.isRefreshing = true
            }
        }
    }

    private fun setUpFeed() {
        adapter = FeedAdapter { presenter.autoLoad() }
        feed.addItemDecoration(FeedItemDecoration(resources.getDimensionPixelOffset(R.dimen.spaceM)))
        feed.adapter = adapter

        refreshContainer.setOnRefreshListener {
            presenter.loadFromScratch()
        }
        errorRefreshButton.setOnClickListener {
            presenter.loadFromScratch()
        }
    }

    private fun setUpPresenter() {
        presenter = FeedPresenter(
            view = this,
            repository = SortingPaginationRepository(
                repository = ApiFeedRepository(HttpClient(Android)) { hasInternetConnection(this) },
                sortingComparator = getSortByDateComparator(), // getSortByPageAndDateComparator() can also be used
                formatFailureItem = { formatFailureItem<FeedCardViewModel>(this, it) },
                formatFailureFullScreen = { formatFailureFullScreen(this, it) },
                formatFeedItem = { formatFeedItem(this, it) }
            )
        )
    }
}
