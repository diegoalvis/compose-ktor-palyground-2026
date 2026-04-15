package products.plyground.ui.mvvm

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import products.plyground.domain.model.Product
import products.plyground.domain.repository.ProductRepository
import products.plyground.ui.detail.ErrorCodes
import products.plyground.ui.detail.ProductDetailViewModel
import java.io.IOException


@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

    private val repository: ProductRepository = mockk()

    private lateinit var viewModel: ProductDetailViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val savedState: SavedStateHandle = mockk(relaxed = true)
        every { savedState.get<String>(any()) } returns ""

        viewModel = ProductDetailViewModel(testDispatcher, repository, savedState)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun loadProduct_Success() = runTest {
        // given
        val product = Product("productId", "Product 1", "Desc 1", 10.0, "Image 1", false)
        coEvery { repository.getProductById(any()) } returns Result.success(product)

        // when
        viewModel.loadProduct()
        
        // Use advanceUntilIdle to wait for all coroutines (including withContext(IO)) to finish
        advanceUntilIdle()

        // then
        val finalState = viewModel.productUi.value
        assert(!finalState.isLoading)
        assert(finalState.product == product)
        assert(finalState.error == null)
    }

    @Test
    fun loadProduct_Not_Found_Error() = runTest {
        // given
        coEvery { repository.getProductById(any()) } returns Result.failure(NullPointerException())

        // when
        viewModel.loadProduct()
        advanceUntilIdle()

        // then
        val finalState = viewModel.productUi.value
        assert(!finalState.isLoading)
        assert(finalState.product == null)
        assert(finalState.error == ErrorCodes.NotFound)
    }

    @Test
    fun loadProduct_Network_Error() = runTest {
        // given
        coEvery { repository.getProductById(any()) } returns Result.failure(IOException())

        // when
        viewModel.loadProduct()
        advanceUntilIdle()

        // then
        val finalState = viewModel.productUi.value
        assert(!finalState.isLoading)
        assert(finalState.product == null)
        assert(finalState.error ==  ErrorCodes.ConnectionProblem)
    }

}
