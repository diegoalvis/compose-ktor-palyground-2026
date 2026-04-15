package products.plyground.ui.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import products.plyground.domain.repository.ProductRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val productRepository: ProductRepository,
    started: SharingStarted = SharingStarted.WhileSubscribed(5000)
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    
    private val _errorEvents = Channel<String>(Channel.BUFFERED)
    val errorEvents = _errorEvents.receiveAsFlow()

    val uiState: StateFlow<ProductListUiState> = combine(
        productRepository.observeProducts(),
        _searchQuery,
        _selectedIds
    ) { products, query, selectedIds ->
        ProductListUiState(
            products = products,
            isLoading = false,
            searchQuery = query,
            selectedProductIds = selectedIds.toList(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = started,
        initialValue = ProductListUiState(isLoading = true)
    )

    init {
        refreshProducts()
    }

    fun refreshProducts() {
        viewModelScope.launch {
            productRepository.fetchProducts()
                .onFailure { e ->
                    _errorEvents.send(e.message ?: "Failed to refresh products")
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFavoriteToggle(productId: String) {
        viewModelScope.launch {
            try {
                productRepository.toggleFavorite(productId)
            } catch (e: Exception) {
                _errorEvents.send(e.message ?: "Failed to update favorite")
            }
        }
    }

    fun onSelectedToggle(productId: String) {
        _selectedIds.update { current ->
            if (current.contains(productId)) current - productId else current + productId
        }
    }

    companion object {
        fun provideFactory(
            productRepository: ProductRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProductListViewModel(
                    productRepository = productRepository
                )
            }
        }
    }
}
