package com.example.rssreader.data

import android.util.Log
import com.example.rssreader.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.Comparator

private const val TAG = "Pagination"

/**
 * This repository handles the pagination from provided [PageRepository].
 * After loading of each new page it sorts new elements into stored cache and always returns full sorted list of elements.
 * Since [PageRepository] doesn't guarantee an order of it's data, elements from the new pages can appear anywhere in the list.
 * Elements are sorted according to the [sortingComparator].
 */
class SortingPaginationRepository<T : Any, R: Any>(
    private val repository: PageRepository<T>,
    private val sortingComparator: Comparator<T>,
    private val formatFailureItem: (Failure) -> ListItemViewModel.Error<R>,
    private val formatFailureFullScreen: (Failure) -> ListErrorViewModel<R>,
    private val formatFeedItem: (item: T) -> ListItemViewModel.Data<R>
) : PaginationRepository<T, R> {

    private var listState: ListState<T> = ListState.Start(sortingComparator)

    /**
     * Removes all stored data and returns first page.
     * All items are sorted according to the [sortingComparator].
     */
    override suspend fun loadFromScratch(): ListViewModel<R> {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "Removing previous data and loading from scratch")
            listState = ListState.Start(sortingComparator)
            loadData()
        }
    }

    /**
     * Returns all previous items + next page.
     * All items are sorted according to the [sortingComparator].
     */
    override suspend fun autoLoad(): ListViewModel<R> {
        return withContext(Dispatchers.IO) {
            loadData()
        }
    }

    private suspend fun loadData(): ListViewModel<R> {
        Log.d(TAG, "Started loading data. ListState = ${listState::class.java.simpleName}")
        val viewModel = when (listState) {
            is ListState.Start -> {
                loadPage()
            }
            is ListState.Middle -> {
                loadPage()
            }
            is ListState.End -> {
                ListDataViewModel(
                    items = (listState.loadedItems.map { formatFeedItem(it) }),
                    hasMoreItems = false
                )
            }
        }
        Log.d(TAG, "Finished loading data. New listState = ${listState::class.java.simpleName}")
        return viewModel
    }

    private suspend fun loadPage(): ListViewModel<R> {
        val items: TreeSet<T> = listState.loadedItems

        return when (val page = repository.getPage(loadFromScratch = listState is ListState.Start)) {
            is Response.Result -> {
                items.addAll(page.value.data)

                listState = if (page.value.hasMoreItems) {
                    ListState.Middle(items)
                } else {
                    ListState.End(items)
                }

                val viewModel: ListViewModel<R> = if (items.isEmpty()) {
                    Log.d(TAG, "Loaded successfully, but no items were found. Showing empty state")
                    formatFailureFullScreen(Failure.NoItems)
                } else {
                    val hasMoreItems = listState is ListState.Middle
                    val viewModels = items.map { formatFeedItem(it) }
                    val itemsWithProgress =
                        if (hasMoreItems) viewModels.plus(ListItemViewModel.Progress<R>()) else viewModels
                    Log.d(TAG, "Loaded successfully. Showing items (hasMoreItems=$hasMoreItems)")
                    ListDataViewModel(
                        items = itemsWithProgress,
                        hasMoreItems = hasMoreItems
                    )
                }
                viewModel
            }
            is Response.Fail -> {
                if (items.isEmpty()) {
                    Log.d(TAG, "Failed to load new items. No cached items. Showing full screen error")
                    formatFailureFullScreen(page.value)
                } else {
                    val itemsWithError =
                        items.map { formatFeedItem(it) }.toMutableList() + formatFailureItem(page.value)
                    Log.d(TAG, "Failed to load new items. Showing cached items and an error item")
                    ListDataViewModel(itemsWithError, hasMoreItems = false)
                }
            }
        }
    }
}

/**
 * State of the pagination list
 */
sealed class ListState<T> {
    abstract val loadedItems: TreeSet<T>

    /**
     * No items are loaded
     */
    class Start<T>(
        sortingComparator: Comparator<T>
    ) : ListState<T>() {
        override val loadedItems: TreeSet<T> = TreeSet(sortingComparator)
    }

    /**
     * Some items are loaded, there is next page
     */
    data class Middle<T>(
        override val loadedItems: TreeSet<T>
    ) : ListState<T>()

    /**
     * All items are loaded, there is no next page
     */
    data class End<T>(
        override val loadedItems: TreeSet<T>
    ) : ListState<T>()
}
