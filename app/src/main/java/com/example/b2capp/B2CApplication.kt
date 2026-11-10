package com.example.b2capp

import android.app.Application
import com.example.b2capp.data.local.TokenManager

/**
 * Lớp Application toàn cục cho B2C App.
 * Khởi tạo TokenManager và các thành phần dùng chung.
 */
class B2CApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Khởi tạo TokenManager với Application Context
        TokenManager.init(this)
    }

    companion object {
        lateinit var instance: B2CApplication
            private set
    }
}

