package com.example.b2capp.data.local.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductFtsDao {

    /**
     * Tìm kiếm sản phẩm theo Full-Text Search (FTS4 MATCH).
     */
    @Query("""
        SELECT rowid, * FROM products_fts 
        WHERE products_fts MATCH :ftsQuery
        LIMIT :limit
    """)
    suspend fun searchFts(ftsQuery: String, limit: Int = 100): List<ProductFtsEntity>

    /**
     * Lấy toàn bộ danh sách cache FTS.
     */
    @Query("SELECT rowid, * FROM products_fts LIMIT :limit")
    suspend fun getAll(limit: Int = 100): List<ProductFtsEntity>

    /**
     * Đếm số lượng sản phẩm đang có trong cache FTS.
     */
    @Query("SELECT COUNT(*) FROM products_fts")
    suspend fun getCount(): Int

    /**
     * Thêm hoặc cập nhật danh sách sản phẩm vào bảng FTS.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductFtsEntity>)

    /**
     * Xóa sạch cache FTS.
     */
    @Query("DELETE FROM products_fts")
    suspend fun clearAll()
}
