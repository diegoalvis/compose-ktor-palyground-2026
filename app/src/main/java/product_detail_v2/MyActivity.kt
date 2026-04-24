package product_detail_v2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import product_detail_v2.ui.ProductListScreen
import product_detail_v2.ui.ProductListViewModel


class MyActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    ProductNavGraph(navController)
                }
            }
        }
    }
}



@Composable
fun ProductNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.ProductList
    ) {

        composable<Routes.ProductList> {
            val viewModel: ProductListViewModel =
                viewModel { ProductListViewModel(AppModule.productRepository) }
            ProductListScreen(viewModel) {
                navController.navigate(Routes.ProductDetail(it))
            }
        }

        composable<Routes.ProductDetail> { entry ->
            val detail: Routes.ProductDetail = entry.toRoute()
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("${detail.productId}")
            }
        }
    }
}

sealed interface Routes {
    @Serializable
    object ProductList : Routes

    @Serializable
    data class ProductDetail(val productId: String) : Routes

}


@Composable
fun DummyScreen2() {

}