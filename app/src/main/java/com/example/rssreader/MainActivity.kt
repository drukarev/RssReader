package com.example.rssreader

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.rssreader.data.ApiFeedRepository
import com.example.rssreader.model.FeedViewModel
import com.example.rssreader.view.FeedAdapter
import com.example.rssreader.view.FeedItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: FeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpFeed()
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            val repository = ApiFeedRepository()
            repository.updateFeedItems(this@MainActivity.applicationContext)
        }
    }

    private fun setUpFeed() {
        val viewModel: FeedViewModel by viewModels()
        adapter = FeedAdapter()
        feed.addItemDecoration(FeedItemDecoration(resources.getDimensionPixelOffset(R.dimen.spaceM)))
        viewModel.feed.observe(this, Observer { adapter.submitList(it) })
        feed.adapter = adapter
    }
}
