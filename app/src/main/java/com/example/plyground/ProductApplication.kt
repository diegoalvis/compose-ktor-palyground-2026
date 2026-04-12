package com.example.plyground

import android.app.Application
import com.example.plyground.di.AppModule

class ProductApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppModule.initialize(this)
    }
}
