package com.example.plyground.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.plyground.domain.repository.ProductRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductMviViewModel(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.DEFAULT)

    val state: StateFlow<ProductState> = combine(
        productRepository.observeProducts(),
        _searchQuery,
        _sortOrder
    ) { products, query, sortOrder ->
        ProductState(
            products = products,
            searchQuery = query,
            sortOrder = sortOrder,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductState(isLoading = true)
    )

    private val _effect = Channel<ProductEffect>(Channel.BUFFERED)
    val effect: Flow<ProductEffect> = _effect.receiveAsFlow()

    init {
        refreshProducts()
    }

    private fun refreshProducts() {
        viewModelScope.launch {
            productRepository.fetchProducts()
                .onFailure { e ->
                    _effect.send(ProductEffect.ShowError(e.message ?: "Unknown error"))
                }
        }
    }

    fun dispatch(intent: ProductIntent) {
        viewModelScope.launch {
            when (intent) {
                is ProductIntent.LoadProducts -> {
                    refreshProducts()
                }

                is ProductIntent.Search -> {
                    _searchQuery.update { intent.query }
                }

                is ProductIntent.SelectProduct -> {
                    _effect.send(ProductEffect.NavigateToDetail(intent.id))
                }

                is ProductIntent.ToggleFavorite -> {
                    try {
                        productRepository.toggleFavorite(intent.id)
                    } catch (e: Exception) {
                        _effect.send(
                            ProductEffect.ShowError(
                                e.message ?: "Failed to toggle favorite"
                            )
                        )
                    }
                }

                is ProductIntent.ChangeSortOrder -> {
                    _sortOrder.value = intent.order
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            productRepository: ProductRepository,
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProductMviViewModel(
                    productRepository = productRepository
                )
            }
        }
    }
}
