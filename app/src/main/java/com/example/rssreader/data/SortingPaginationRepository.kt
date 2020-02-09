package com.example.rssreader.data

import com.example.rssreader.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.Comparator

/**
 * This repository handles the pagination from provided [PageRepository].
 * After loading of each new page it sorts new elements into stored cache and always returns full sorted list of elements.
 * Since [PageRepository] doesn't guarantee an order of it's data, elements from the new pages can appear anywhere in the list.
 * Elements are sorted according to the [sortingComparator].
 */
class SortingPaginationRepository<T : Any>(
    private val repository: PageRepository<T>,
    private val sortingComparator: Comparator<T>,
    private val formatFailureItem: (Failure) -> ListItemViewModel.Error,
    private val formatFailureFullScreen: (Failure) -> ListErrorViewModel<T>,
    private val formatFeedItem: (item: T) -> ListItemViewModel.Data
) : PaginationRepository<T> {

    private var listState: ListState<T> = ListState.Start(sortingComparator)

    /**
     * Removes all stored data and returns first page.
     * All items are sorted according to the [sortingComparator].
     */
    override suspend fun loadFromScratch(): ListViewModel<T> {
        return withContext(Dispatchers.IO) {
            listState = ListState.Start(sortingComparator)
            loadData()
        }
    }

    /**
     * Returns all previous items + next page.
     * All items are sorted according to the [sortingComparator].
     */
    override suspend fun autoLoad(): ListViewModel<T> {
        return withContext(Dispatchers.IO) {
            loadData()
        }
    }

    private suspend fun loadData(): ListViewModel<T> {
        return when (listState) {
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
    }

    private suspend fun loadPage(): ListViewModel<T> {
        val items: TreeSet<T> = listState.loadedItems

        return when (val page = repository.getPage(loadFromScratch = listState is ListState.Start)) {
            is Response.Result -> {
                items.addAll(page.value.data)

                listState = if (page.value.hasMoreItems) {
                    ListState.Middle(items)
                } else {
                    ListState.End(items)
                }

                val viewModel: ListViewModel<T> = if (items.isEmpty()) {
                    formatFailureFullScreen(Failure.NoItems)
                } else {
                    val hasMoreItems = listState is ListState.Middle
                    val viewModels = items.map { formatFeedItem(it) }
                    val itemsWithProgress =
                        if (hasMoreItems) viewModels.plus(ListItemViewModel.Progress) else viewModels
                    ListDataViewModel(
                        items = itemsWithProgress,
                        hasMoreItems = hasMoreItems
                    )
                }
                viewModel
            }
            is Response.Fail -> {
                if (items.isEmpty()) {
                    formatFailureFullScreen(page.value)
                } else {
                    val itemsWithError =
                        items.map { formatFeedItem(it) }.toMutableList() + formatFailureItem(page.value)
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
