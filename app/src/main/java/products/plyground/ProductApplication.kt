package products.plyground

import android.app.Application
import products.plyground.di.AppModule

class ProductApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppModule.initialize(this)
    }
}
