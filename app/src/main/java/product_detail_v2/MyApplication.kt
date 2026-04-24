package product_detail_v2

import android.app.Application
import android.content.Context
import io.ktor.http.ContentType
import product_detail_v2.data.ProductLocalSource
import product_detail_v2.data.ProductLocalSourceImpl
import product_detail_v2.data.ProductRepository
import product_detail_v2.data.ProductServiceApi
import product_detail_v2.data.ProductServiceApiImp

class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        AppModule.init(this)
    }


}


object AppModule {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }


    val productRepository: ProductRepository by lazy {
        ProductRepository(productLocalSource = localSource, productServiceApi = productServiceApi)
    }

    private val localSource: ProductLocalSource = ProductLocalSourceImpl()

    private val productServiceApi: ProductServiceApi = ProductServiceApiImp()
}