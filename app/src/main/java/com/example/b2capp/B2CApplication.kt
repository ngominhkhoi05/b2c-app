package com.example.b2capp

import android.app.Application
import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.local.search.AppDatabase
import com.example.b2capp.data.local.search.LocalSearchRepository

/**
 * Lớp Application toàn cục cho B2C App.
 * Khởi tạo TokenManager, Room Database (FTS4) và các thành phần dùng chung.
 */
class B2CApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val localSearchRepository: LocalSearchRepository by lazy {
        LocalSearchRepository(database.productFtsDao(), database.searchHistoryDao())
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Khởi tạo TokenManager với Application Context
        TokenManager.init(this)
    }

    companion object {
        lateinit var instance: B2CApplication
            private set

        fun getLocalSearchRepo(): LocalSearchRepository? {
            return try {
                if (::instance.isInitialized) instance.localSearchRepository else null
            } catch (_: Exception) {
                null
            }
        }
    }
}


