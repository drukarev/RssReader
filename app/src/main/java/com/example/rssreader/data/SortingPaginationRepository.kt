package com.example.rssreader.data

import com.example.rssreader.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SortingPaginationRepository<T : Any>(
    private val formatFailureItem: (Failure) -> ListItemViewModel.Error,
    private val repository: ListRepository<T>,
    private val toViewModel: (item: T) -> ListItemViewModel.Data
) : PaginationListRepository<T> {

    private var listState: ListState<T> = ListState.Start()

    override suspend fun loadFromScratch(): ListViewModel<T> {
        return withContext(Dispatchers.IO) {
            listState = ListState.Start()
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
                    items = (listState.loadedItems.map { toViewModel(it) }),
                    hasMoreItems = false
                )
            }
        }
    }

    private suspend fun loadPage(): ListViewModel<T> {
        val oldItems: List<T> = listState.loadedItems

        return when (val page = repository.getPage(listState is ListState.Start)) {
            is Response.Result -> {
                //TODO: sort
                val allItems = mutableListOf<T>().apply {
                    addAll(oldItems)
                    addAll(page.value.data)
                }.toList()

                listState = if (page.value.hasMoreItems) {
                    ListState.Middle(allItems)
                } else {
                    ListState.End(allItems)
                }

                val viewModel: ListViewModel<T> = if (allItems.isEmpty()) {
                    ListErrorViewModel(
                        title = "No items",
                        subtitle = "Literally no items"
                    )
                } else {
                    val hasMoreItems = listState is ListState.Middle
                    val items = allItems.map { toViewModel(it) }
                    val itemsWithProgress = if (hasMoreItems) items.plus(ListItemViewModel.Progress) else items
                    ListDataViewModel(
                        items = itemsWithProgress,
                        hasMoreItems = hasMoreItems
                    )
                }
                viewModel
            }
            is Response.Fail -> {
                if (oldItems.isEmpty()) {
                    ListErrorViewModel(
                        title = "Error",
                        subtitle = "Try to Retry"
                    )
                } else {
                    val itemsWithError =
                        oldItems.map { toViewModel(it) }.toMutableList() + formatFailureItem(page.value)
                    ListDataViewModel(itemsWithError, hasMoreItems = false)
                }
            }
        }
    }
}

sealed class ListState<M> {
    abstract val loadedItems: List<M>

    class Start<M> : ListState<M>() {
        override val loadedItems: List<M> = emptyList()
    }

    data class Middle<M>(override val loadedItems: List<M>) : ListState<M>()
    data class End<M>(override val loadedItems: List<M>) : ListState<M>()
}
