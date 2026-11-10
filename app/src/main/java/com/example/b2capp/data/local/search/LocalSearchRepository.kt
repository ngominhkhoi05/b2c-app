package com.example.b2capp.data.local.search

import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.data.SampleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository xử lý tìm kiếm Full-Text Search cục bộ (Local FTS4) trên thiết bị.
 * Đảm bảo trải nghiệm tìm kiếm tức thì, hỗ trợ tiếng Việt không dấu, hoạt động cả khi offline.
 */
class LocalSearchRepository(
    private val productFtsDao: ProductFtsDao,
    private val searchHistoryDao: SearchHistoryDao
) {

    /**
     * Tìm kiếm sản phẩm bằng SQLite FTS4 kết hợp bộ lọc (giá, rating, danh mục).
     */
    suspend fun searchLocalProducts(
        query: String?,
        categoryId: Long? = null,
        categoryName: String? = null,
        minPrice: Long? = null,
        maxPrice: Long? = null,
        minRating: Float? = null
    ): List<ProductItem> = withContext(Dispatchers.IO) {
        // Đảm bảo có dữ liệu FTS ban đầu nếu database còn trống
        seedDefaultDataIfEmpty()

        val rawList = if (query.isNullOrBlank()) {
            productFtsDao.getAll(100).map { it.toProductItem() }
        } else {
            val ftsQuery = VietnameseNormalizer.buildFtsMatchQuery(query)
            if (ftsQuery.isBlank()) {
                productFtsDao.getAll(100).map { it.toProductItem() }
            } else {
                try {
                    val ftsResults = productFtsDao.searchFts(ftsQuery, 100)
                    if (ftsResults.isNotEmpty()) {
                        ftsResults.map { it.toProductItem() }
                    } else {
                        // Fallback tìm kiếm mờ theo chuỗi nếu FTS không trả về kết quả
                        fallbackSubstringSearch(query)
                    }
                } catch (_: Exception) {
                    fallbackSubstringSearch(query)
                }
            }
        }

        // Áp dụng các bộ lọc bổ sung
        rawList.filter { product ->
            val matchCategory = categoryName.isNullOrBlank() ||
                    product.category.equals(categoryName, ignoreCase = true)
            val matchMinPrice = minPrice == null || product.price >= minPrice
            val matchMaxPrice = maxPrice == null || product.price <= maxPrice
            val matchRating = minRating == null || product.rating >= minRating
            matchCategory && matchMinPrice && matchMaxPrice && matchRating
        }
    }

    /**
     * Fallback tìm kiếm không dấu theo chuỗi nếu truy vấn FTS gặp ký tự đặc biệt.
     */
    private suspend fun fallbackSubstringSearch(query: String): List<ProductItem> {
        val cleanQuery = VietnameseNormalizer.removeAccents(query)
        val all = productFtsDao.getAll(100)
        return all.filter {
            it.normalizedName.contains(cleanQuery, ignoreCase = true) ||
            it.name.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }.map { it.toProductItem() }
    }

    /**
     * Lưu danh sách sản phẩm vào cơ sở dữ liệu FTS4 để tìm kiếm offline / instant.
     */
    suspend fun indexProducts(products: List<ProductItem>) = withContext(Dispatchers.IO) {
        if (products.isEmpty()) return@withContext
        val entities = products.mapIndexed { index, item ->
            ProductFtsEntity.fromProductItem(item, rowId = index + 1)
        }
        productFtsDao.insertAll(entities)
    }

    /**
     * Nạp dữ liệu mẫu ban đầu nếu cơ sở dữ liệu FTS chưa có bản ghi nào.
     */
    suspend fun seedDefaultDataIfEmpty() = withContext(Dispatchers.IO) {
        val count = productFtsDao.getCount()
        if (count == 0) {
            indexProducts(SampleData.products)
        }
    }

    /**
     * Lưu từ khóa tìm kiếm vào lịch sử và gợi ý.
     */
    suspend fun saveSearchQuery(query: String) = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isNotBlank()) {
            searchHistoryDao.insertSearch(
                SearchHistoryEntity(
                    query = trimmed,
                    normalizedQuery = VietnameseNormalizer.removeAccents(trimmed)
                )
            )
        }
    }

    /**
     * Lấy danh sách tìm kiếm gần đây.
     */
    suspend fun getRecentSearches(limit: Int = 10): List<String> = withContext(Dispatchers.IO) {
        searchHistoryDao.getRecentSearches(limit)
    }

    /**
     * Gợi ý từ khóa tìm kiếm khi người dùng đang gõ (Typeahead / Autocomplete).
     */
    suspend fun getSuggestions(query: String, limit: Int = 8): List<String> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val normalized = VietnameseNormalizer.removeAccents(query)
        searchHistoryDao.getSuggestions(query.trim(), normalized, limit)
    }

    /**
     * Xóa một từ khóa khỏi lịch sử.
     */
    suspend fun deleteSearch(query: String) = withContext(Dispatchers.IO) {
        searchHistoryDao.deleteSearch(query)
    }

    /**
     * Xóa toàn bộ lịch sử tìm kiếm.
     */
    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        searchHistoryDao.clearHistory()
    }
}
