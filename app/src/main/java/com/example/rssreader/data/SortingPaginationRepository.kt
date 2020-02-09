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
class SortingPaginationRepository<T : Any, R : Any>(
    private val repository: PageRepository<T>,
    private val sortingComparator: Comparator<T>,
    private val formatFailureItem: (Failure) -> PaginationItemViewModel.Error<R>,
    private val formatFailureFullScreen: (Failure) -> ScreenViewModel.Error<R>,
    private val formatFeedItem: (item: T) -> PaginationItemViewModel.Data<R>
) : PaginationRepository<T, R> {

    private var state: State<T> = State.Start(sortingComparator)

    /**
     * Removes all stored data and returns first page.
     * All items are sorted according to the [sortingComparator].
     */
    override suspend fun loadFromScratch(): ScreenViewModel<R> {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "Removing previous data and loading from scratch")
            state = State.Start(sortingComparator)
            loadData()
        }
    }

    /**
     * Returns all previous items + next page.
     * All items are sorted according to the [sortingComparator].
     */
    override suspend fun autoLoad(): ScreenViewModel<R> {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "Loading next page")
            loadData()
        }
    }

    private suspend fun loadData(): ScreenViewModel<R> {
        Log.d(TAG, "Started loading data. State = ${state::class.java.simpleName}")
        val viewModel = when (state) {
            is State.Start, is State.Middle -> {
                loadPage()
            }
            is State.End -> {
                ScreenViewModel.Data(state.loadedItems.map { formatFeedItem(it) })
            }
        }
        Log.d(TAG, "Finished loading data. New state = ${state::class.java.simpleName}")
        return viewModel
    }

    private suspend fun loadPage(): ScreenViewModel<R> {
        val items: TreeSet<T> = state.loadedItems

        return when (val page = repository.getPage(loadFromScratch = state is State.Start)) {
            is Response.Result -> {
                items.addAll(page.value.data)

                state = if (page.value.hasMoreItems) {
                    State.Middle(items)
                } else {
                    State.End(items)
                }

                if (items.isEmpty()) {
                    Log.d(TAG, "Loaded successfully, but no items were found. Showing empty state")
                    formatFailureFullScreen(Failure.NoItems)
                } else {
                    val hasMoreItems = state is State.Middle
                    val viewModels = items.map { formatFeedItem(it) }
                    val viewModelsWithProgress =
                        if (hasMoreItems) viewModels.plus(PaginationItemViewModel.Progress<R>()) else viewModels

                    Log.d(TAG, "Loaded successfully. Showing items (hasMoreItems=$hasMoreItems)")
                    ScreenViewModel.Data(items = viewModelsWithProgress)
                }
            }
            is Response.Fail -> {
                if (items.isEmpty()) {
                    Log.d(TAG, "Failed to load new items. No cached items. Showing full screen error")
                    formatFailureFullScreen(page.value)
                } else {
                    val itemsWithError =
                        items.map { formatFeedItem(it) }.toMutableList() + formatFailureItem(page.value)
                    Log.d(TAG, "Failed to load new items. Showing cached items and an error item")
                    ScreenViewModel.Data(itemsWithError)
                }
            }
        }
    }

    /**
     * State of the pagination
     */
    private sealed class State<T> {
        abstract val loadedItems: TreeSet<T>

        /**
         * No items are loaded
         */
        class Start<T>(
            sortingComparator: Comparator<T>
        ) : State<T>() {
            override val loadedItems: TreeSet<T> = TreeSet(sortingComparator)
        }

        /**
         * Some items are loaded, there is next page
         */
        data class Middle<T>(
            override val loadedItems: TreeSet<T>
        ) : State<T>()

        /**
         * All items are loaded, there is no next page
         */
        data class End<T>(
            override val loadedItems: TreeSet<T>
        ) : State<T>()
    }
}
