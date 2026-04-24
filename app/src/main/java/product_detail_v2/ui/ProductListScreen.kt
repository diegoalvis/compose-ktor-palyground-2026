package product_detail_v2.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import product_detail_v2.data.model.ProductModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    onProductClick: (String) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Products (MVVM)") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ProductList(
                uiState = uiState,
                onSelected = { viewModel.onIntent(Intent.OnProductSelected(it)) },
                onDeselected = { viewModel.onIntent(Intent.OnProductSelected(it)) },
                onClick = onProductClick,
            )
        }
    }
}


@Composable
fun ProductList(
    uiState: ProductListUiState,
    onSelected: (String) -> Unit,
    onDeselected: (String) -> Unit,
    onClick: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = uiState.products, key = { it.id }) { product ->
            val id = product.id.toString()
            Box(
                Modifier.combinedClickable(
                    onClick = {
                        onClick(id)
                    },
                    onLongClick = {
                        if (uiState.selectedProductsIds.contains(id)) {
                            onDeselected(id)
                        } else {
                            onSelected(id)
                        }
                    }
                )
            ) {
                ProductItemList(product)
            }
        }
    }
}


@Composable
fun ProductItemList(
    product: ProductModel,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AsyncImage(
            modifier = Modifier.size(80.dp),
            model = product.thumbnail,
            contentDescription = null,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(product.title)
            Text("$%.2f".format(product.price))
        }
    }
}

