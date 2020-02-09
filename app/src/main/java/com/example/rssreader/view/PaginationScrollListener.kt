package com.example.rssreader.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val THRESHOLD_DEFAULT = 50

internal class PaginationScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val threshold: Int = THRESHOLD_DEFAULT,
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
