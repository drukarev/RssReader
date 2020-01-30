package com.example.rssreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.rssreader.model.FeedCardModel
import com.example.rssreader.view.FeedAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var adapter: FeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpFeed()
    }

    override fun onResume() {
        super.onResume()
        val mockList = List(100) { id ->
            FeedCardModel(id, "Test title $id", "Author $id", "Date $id", "SourceFeedName $id")
        }
        adapter.submitList(mockList)
    }

    private fun setUpFeed() {
        adapter = FeedAdapter()
        feed.adapter = adapter
    }
}
