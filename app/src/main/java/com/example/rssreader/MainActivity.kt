package com.example.rssreader

import android.os.Bundle
import android.view.View
import android.widget.ViewAnimator
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rssreader.data.ApiFeedRepository
import com.example.rssreader.data.PaginationListRepository
import com.example.rssreader.data.SortingPaginationRepository
import com.example.rssreader.model.*
import com.example.rssreader.pagination.*
import com.example.rssreader.view.FeedItemDecoration
import com.example.rssreader.view.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: FeedAdapter
    private lateinit var repository: PaginationListRepository<FeedItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = SortingPaginationRepository(
            formatFailureItem = {
                ListItemViewModel.Error(
                    errorText = "Error",
                    refreshText = "Retry"
                )
            },
            repository = ApiFeedRepository(),
            toViewModel = {
                ListItemViewModel.Data(
                    FeedCardViewModel(
                        id = it.id,
                        title = it.title ?: "No title",
                        author = it.author ?: "No author",
                        date = it.date?.format(DateTimeFormatter.ofPattern("dd MMMM, HH:mm")) ?: "No date",
                        sourceFeedName = it.sourceFeedName ?: "SourceFeedName"
                    )
                )
            }
        )

        setUpFeed()
        loadFromScratch()
    }

    override fun onDestroy() {
        refreshContainer.setOnRefreshListener(null)
        super.onDestroy()
    }

    private fun setUpFeed() {
        adapter = FeedAdapter {
            autoLoad()
        }
        feed.addItemDecoration(FeedItemDecoration(resources.getDimensionPixelOffset(R.dimen.spaceM)))
        feed.adapter = adapter

        refreshContainer.setOnRefreshListener {
            loadFromScratch()
        }
    }

    private fun loadFromScratch() {
        GlobalScope.launch(Dispatchers.Main) {
            refreshContainer.isRefreshing = true
            adapter.submitList(emptyList())
            showViewModel(repository.loadFromScratch())
            refreshContainer.isRefreshing = false
        }
    }

    private fun autoLoad() {
        GlobalScope.launch(Dispatchers.Main) {
            showViewModel(repository.autoLoad())
        }
    }

    private fun showViewModel(viewModel: ListViewModel<FeedItem>) {
        when (viewModel) {
            is ListErrorViewModel -> {
                errorTitle.text = viewModel.title
                errorSubtitle.text = viewModel.subtitle
                dataContainer.showChild(errorView)
            }
            is ListDataViewModel -> {
                dataContainer.showChild(refreshContainer)
                adapter.submitList(viewModel.items)
                feed.addOnScrollListener(PaginationScrollListener(feed.layoutManager as LinearLayoutManager, 50) {
                    autoLoad()
                })
            }
        }
    }

    private fun ViewAnimator.showChild(view: View) {
        indexOfChild(view).also { indexOfChild ->
            if (displayedChild != indexOfChild) {
                displayedChild = indexOfChild
            }
        }
    }
}
