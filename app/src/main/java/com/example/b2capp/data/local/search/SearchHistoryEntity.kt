package com.example.b2capp.data.local.search

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity lưu lịch sử các từ khóa tìm kiếm của người dùng trên thiết bị.
 */
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey
    val query: String,
    val normalizedQuery: String,
    val timestamp: Long = System.currentTimeMillis()
)
