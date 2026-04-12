package com.example.plyground.ui.mvi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.plyground.ui.mvvm.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductMviScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: ProductMviViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProductEffect.NavigateToDetail -> onNavigateToDetail(effect.productId)
                is ProductEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(title = { Text("Products (MVI)") })
                SortOrderRow(
                    current = state.sortOrder,
                    onSortChange = { viewModel.dispatch(ProductIntent.ChangeSortOrder(it)) }
                )
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.dispatch(ProductIntent.Search(it)) },
                    placeholder = { Text("Search…") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                ProductList(state, viewModel::dispatch)
            }
        }
    }
}

@Composable
private fun ProductList(
    state: ProductState,
    onAction: (ProductIntent)->Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.filteredProducts, key = { it.id }) { product ->
            ProductCard(
                product = product,
                isSelected = false,
                onClick = { onAction(ProductIntent.SelectProduct(product.id)) },
                onFavoriteToggle = {
                    onAction(
                        ProductIntent.ToggleFavorite(
                            product.id
                        )
                    )
                },
            )
        }
    }
}

@Composable
private fun SortOrderRow(current: SortOrder, onSortChange: (SortOrder) -> Unit) {
    val options = listOf(
        SortOrder.DEFAULT to "Default",
        SortOrder.PRICE_ASC to "Price ↑",
        SortOrder.PRICE_DESC to "Price ↓",
        SortOrder.NAME to "A-Z"
    )
    PrimaryScrollableTabRow(
        selectedTabIndex = options.indexOfFirst { it.first == current },
        edgePadding = 16.dp
    ) {
        options.forEach { (order, label) ->
            Tab(
                selected = current == order,
                onClick = { onSortChange(order) },
                text = { Text(label, style = MaterialTheme.typography.labelSmall) }
            )
        }
    }
}
