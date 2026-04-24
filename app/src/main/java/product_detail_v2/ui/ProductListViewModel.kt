package product_detail_v2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import product_detail_v2.data.ProductErrors
import product_detail_v2.data.ProductRepository
import product_detail_v2.data.model.ProductModel

class ProductListViewModel(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<Effects>()
    val effects = _effects.asSharedFlow()

    init {
        loadProducts()
    }


    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isRetryVisible = false)
            val result = productRepository.getProducts()
            result.onSuccess { products ->
                _uiState.update {
                    it.copy(
                        products = products
                    )
                }
            }
                .onFailure {
                    if (it is ProductErrors) {
                        when (it) {
                            is ProductErrors.LocalSourceError -> {
                                _effects.emit(Effects.ShowError("Products not found"))
                            }

                            is ProductErrors.NetworkError -> {
                                _uiState.update { it.copy(isRetryVisible = true) }
                                _effects.emit(Effects.ShowError("Connectivity issues"))
                            }

                            is ProductErrors.UnknownError -> {
                                _effects.emit(Effects.ShowError("Something went wrong. Try again"))
                            }
                        }
                    } else {
                        _uiState.update { it.copy(isRetryVisible = true) }
                        _effects.emit(Effects.ShowError("Error loading products"))
                    }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }


    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.OnProductSelected -> {
                _uiState.update {
                    it.copy(
                        selectedProductsIds = it.selectedProductsIds + intent.productId
                    )
                }
            }

            is Intent.OnProductDeselected -> {
                _uiState.update {
                    it.copy(
                        selectedProductsIds = it.selectedProductsIds - intent.productId
                    )
                }
            }
        }
    }


}


//
data class ProductListUiState(
    val isLoading: Boolean = false,
    val isRetryVisible: Boolean = false,
    val products: List<ProductModel> = emptyList(),
    val selectedProductsIds: Set<String> = emptySet(),
    val error: String? = null,
)

sealed interface Intent {
    data class OnProductSelected(val productId: String) : Intent
    data class OnProductDeselected(val productId: String) : Intent
}

sealed interface Effects {
    data class ShowError(val message: String) : Effects
    data class NavigateToDetails(val productId: String) : Effects
}
