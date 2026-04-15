package products.plyground.ui.mvvm

import products.plyground.domain.model.Product
import products.plyground.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    private val repository = mockk<ProductRepository>(relaxed = true)
    private lateinit var viewModel: ProductListViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testProducts = listOf(
        Product("1", "Product 1", "Desc 1", 10.0, "Image 1", false),
        Product("2", "Product 2", "Desc 2", 20.0, "Image 2", true)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.observeProducts() } returns flowOf(testProducts)
        coEvery { repository.fetchProducts() } returns Result.success(testProducts)
        
        viewModel = ProductListViewModel(repository, started = SharingStarted.Eagerly)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState initially reflects products from repository`() = runTest {
        val state = viewModel.uiState.value

        assertEquals(testProducts, state.products)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `onSearchQueryChange updates uiState`() = runTest {
        val query = "search"
        viewModel.onSearchQueryChange(query)
        
        assertEquals(query, viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `onSelectedToggle updates selected ids`() = runTest {
        val productId = "1"
        viewModel.onSelectedToggle(productId)
        
        assertEquals(listOf(productId), viewModel.uiState.value.selectedProductIds)
        
        viewModel.onSelectedToggle(productId)
        assertEquals(emptyList<String>(), viewModel.uiState.value.selectedProductIds)
    }
}
