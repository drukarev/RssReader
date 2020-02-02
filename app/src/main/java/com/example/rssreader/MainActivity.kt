package com.example.rssreader

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rssreader.data.ApiFeedRepository
import com.example.rssreader.model.FeedViewModel
import com.example.rssreader.view.FeedAdapter
import com.example.rssreader.view.FeedItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: FeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpFeed()
    }

    override fun onDestroy() {
        refreshContainer.setOnRefreshListener(null)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        startUpdate()
        refreshContainer.isRefreshing = true
    }

    private fun setUpFeed() {
        updateButton.setOnClickListener {
            feed.scrollToPosition(0)
            updateButton.isVisible = false
        }

        val viewModel: FeedViewModel by viewModels()
        adapter = FeedAdapter {
            if ((feed.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 0) {
                updateButton.isVisible = true
            }
        }
        feed.addItemDecoration(FeedItemDecoration(resources.getDimensionPixelOffset(R.dimen.spaceM)))
        viewModel.feed.observe(this, Observer {
            adapter.submitList(it)
        })
        feed.adapter = adapter

        refreshContainer.setOnRefreshListener {
            updateButton.isVisible = false
            startUpdate()
        }
    }

    private fun startUpdate() {
        GlobalScope.launch(Dispatchers.Main) {
            val repository = ApiFeedRepository()
            repository.updateFeedItems(this@MainActivity.applicationContext)
            refreshContainer.isRefreshing = false
        }
    }
}
