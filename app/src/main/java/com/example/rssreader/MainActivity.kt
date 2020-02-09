package com.example.rssreader

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.widget.ViewAnimator
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rssreader.data.*
import com.example.rssreader.model.*
import com.example.rssreader.pagination.*
import com.example.rssreader.view.*
import com.example.rssreader.view.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: FeedAdapter
    private lateinit var repository: PaginationRepository<FeedItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = SortingPaginationRepository(
            repository = ApiFeedRepository { hasInternetConnection(this) },
            sortingComparator = Comparator { o1, o2 ->
                return@Comparator if (o1.id == o2.id) {
                    0
                } else {
                    o2.date.compareTo(o1.date) // reversed comparison
                }
            },
            formatFailureItem = { formatFailureItem(this, it) },
            formatFailureFullScreen = { formatFailureFullScreen(this, it) },
            formatFeedItem = { formatFeedItem(this, it) }
        )

        errorRefreshButton.setOnClickListener {
            refreshContainer.isRefreshing = true
            loadFromScratch()
        }
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
                feed.addOnScrollListener(PaginationScrollListener(feed.layoutManager as LinearLayoutManager) {
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

    private fun hasInternetConnection(context: Context): Boolean {
        val cm: ConnectivityManager? = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm?.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting ?: false
    }
}
