package com.example.plyground.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.plyground.domain.model.Product
import com.example.plyground.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repository: ProductRepository,
    savedState: SavedStateHandle
) : ViewModel() {

    private val productId: String = checkNotNull(savedState["productId"])

    private val _product = MutableStateFlow<Product?>(null)
    val product = _product.asStateFlow()

    init {
        viewModelScope.launch {
            _product.value = repository.getProductById(productId)
        }
    }

    companion object {
        fun provideFactory(
            repository: ProductRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProductDetailViewModel(
                    repository = repository,
                    savedState = createSavedStateHandle()
                )
            }
        }
    }
}
