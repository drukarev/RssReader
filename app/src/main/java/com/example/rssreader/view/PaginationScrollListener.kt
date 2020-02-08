package com.example.rssreader.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class PaginationScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val threshold: Int,
    private val loadNextPage: () -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        val totalItemCount = layoutManager.itemCount
        if (totalItemCount - lastVisiblePosition < threshold) {
            loadNextPage()
            recyclerView.removeOnScrollListener(this)
        }
    }
}
