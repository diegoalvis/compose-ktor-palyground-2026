package products.plyground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import products.plyground.di.AppModule
import products.plyground.ui.detail.ProductDetailScreen
import products.plyground.ui.detail.ProductDetailViewModel
import products.plyground.ui.mvvm.ProductListScreen
import products.plyground.ui.mvvm.ProductListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    ProductNavGraph(navController = navController)
                }
            }
        }
    }
}

@Composable
fun ProductNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.ProductList
    ) {
        composable<Routes.ProductList> {
            // Get the ViewModel using the factory and repository from AppModule
            val viewModel: ProductListViewModel = viewModel(
                factory = ProductListViewModel.provideFactory(AppModule.productRepository)
            )
            ProductListScreen(
                viewModel = viewModel,
                onProductClick = { productId ->
                    navController.navigate(Routes.ProductDetail(productId))
                }
            )
        }

        composable<Routes.ProductDetail> { entry ->
            val detail: Routes.ProductDetail = entry.toRoute()
            val viewModel: ProductDetailViewModel = viewModel(
                factory =
                    ProductDetailViewModel.provideFactory(detail.productId, AppModule.productRepository)
            )
            ProductDetailScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel,
            )
        }
    }
}


sealed interface Routes {

    @Serializable
    object ProductList : Routes

    @Serializable
    data class ProductDetail(val productId: String) : Routes
}