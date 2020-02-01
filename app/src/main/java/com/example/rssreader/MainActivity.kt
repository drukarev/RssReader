package com.example.rssreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.rssreader.data.ApiFeedRepository
import com.example.rssreader.model.FeedCardViewModel
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

    override fun onResume() {
        super.onResume()
        val repository = ApiFeedRepository()
        GlobalScope.launch(Dispatchers.Main) {
            val items = repository.getFeedItems(this@MainActivity.applicationContext)
                .mapIndexed { id, item ->
                    FeedCardViewModel(
                        id = id,
                        title = item.title ?: "No title",
                        author = item.author ?: "No author",
                        date = item.date ?: "No date",
                        sourceFeedName = item.sourceFeedName ?: "SourceFeedName"
                    )
                }
            Log.e("Items", items.toString())
            adapter.submitList(items)
        }
    }

    private fun setUpFeed() {
        adapter = FeedAdapter()
        feed.addItemDecoration(FeedItemDecoration(resources.getDimensionPixelOffset(R.dimen.spaceM)))
        feed.adapter = adapter
    }
}
