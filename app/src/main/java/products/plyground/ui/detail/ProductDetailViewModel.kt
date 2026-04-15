package products.plyground.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import products.plyground.domain.model.Product
import products.plyground.domain.repository.ProductRepository
import java.io.IOException

class ProductDetailViewModel(
    private val productId: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val repository: ProductRepository,
) : ViewModel() {

    private val _productUi = MutableStateFlow(ProductDetailUi())
    val productUi = _productUi.asStateFlow()

    init {
        loadProduct()
    }

    fun loadProduct() {
        viewModelScope.launch {
            _productUi.value = _productUi.value.copy(isLoading = true)
            try {
                val product = withContext(dispatcher) {
                    delay(500L) // Simulate network delay
                    repository.getProductById(productId).getOrThrow()
                }
                _productUi.value = ProductDetailUi(
                    product = product,
                    error = null
                )
            } catch (e: NullPointerException) {
                _productUi.value = _productUi.value.copy(error = ErrorCodes.NotFound)
            } catch (e: IOException) {
                _productUi.value = _productUi.value.copy(error = ErrorCodes.ConnectionProblem)
            } finally {
                _productUi.value = _productUi.value.copy(isLoading = false)
            }
        }
    }

    companion object {
        fun provideFactory(
            productId: String,
            repository: ProductRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProductDetailViewModel(
                    productId = productId,
                    dispatcher = Dispatchers.IO,
                    repository = repository,
                )
            }
        }
    }
}

data class ProductDetailUi(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: ErrorCodes? = null
)

enum class ErrorCodes(val text: String) {
    NotFound("Product not found"),
    ConnectionProblem("Connection problem. Try again later")
}
