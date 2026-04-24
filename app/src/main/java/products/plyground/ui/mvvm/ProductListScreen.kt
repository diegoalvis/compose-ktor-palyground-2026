package products.plyground.ui.mvvm

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import product_detail_v2.Routes
import products.plyground.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    onProductClick: (Routes) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.errorEvents) {
        viewModel.errorEvents.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(title = { Text("Products (MVVM)") })
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                ProductList(
                    uiState = uiState,
                    onProductClick = { onProductClick(Routes.ProductDetail(it)) },
                    onLoadProductsClick = viewModel::refreshProducts,
                    onFavoriteToggle = viewModel::onFavoriteToggle,
                    onSelectedToggle = viewModel::onSelectedToggle
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search products…") },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
private fun ProductList(
    uiState: ProductListUiState,
    onProductClick: (String) -> Unit,
    onLoadProductsClick: () -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onSelectedToggle: (String) -> Unit
) {
    val products = uiState.products
    val selectedProductIds = uiState.selectedProductIds
    if (products.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                Text("No products found")
                Button(onClick = { onLoadProductsClick() }) {
                    Text("Load products")
                }
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = products,
                key = { it.id },
                contentType = { it.javaClass.name } ) { product ->
                ProductCard(
                    product,
                    isSelected = product.id in selectedProductIds,
                    onClick = { onProductClick(product.id) },
                    onFavoriteToggle = { onFavoriteToggle(product.id) },
                    onLongClick = { onSelectedToggle(product.id) },
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    isSelected: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .alpha(if (isSelected) 0.4f else 1.0f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text("$%.2f".format(product.price), style = MaterialTheme.typography.bodyLarge)
            }
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null
                )
            }
        }
    }
}
