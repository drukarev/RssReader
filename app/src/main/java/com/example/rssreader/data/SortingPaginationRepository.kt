package com.example.rssreader.data

import com.example.rssreader.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.Comparator

class SortingPaginationRepository<T : Any>(
    private val repository: ListRepository<T>,
    private val sortingComparator: Comparator<T>,
    private val formatFailureItem: (Failure) -> ListItemViewModel.Error,
    private val formatFailureFullScreen: (Failure) -> ListErrorViewModel<T>,
    private val formatFeedItem: (item: T) -> ListItemViewModel.Data
) : PaginationListRepository<T> {

    private var listState: ListState<T> = ListState.Start(sortingComparator)

    override suspend fun loadFromScratch(): ListViewModel<T> {
        return withContext(Dispatchers.IO) {
            listState = ListState.Start(sortingComparator)
            loadData()
        }
    }

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

sealed class ListState<T> {
    abstract val loadedItems: TreeSet<T>

    class Start<T>(
        sortingComparator: Comparator<T>
    ) : ListState<T>() {
        override val loadedItems: TreeSet<T> = TreeSet(sortingComparator)
    }

    data class Middle<T>(
        override val loadedItems: TreeSet<T>
    ) : ListState<T>()

    data class End<T>(
        override val loadedItems: TreeSet<T>
    ) : ListState<T>()
}
