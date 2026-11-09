package com.example.b2capp.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.CartApiService
import com.example.b2capp.data.remote.api.HomeApiService
import com.example.b2capp.data.remote.dto.CategoryDto
import com.example.b2capp.data.remote.dto.ProductSummaryDto
import com.example.b2capp.data.remote.dto.PromotionDto
import com.example.b2capp.features.home.HomeBadgeType
import com.example.b2capp.features.home.HomeBestSellerItem
import com.example.b2capp.features.home.HomeSuggestedProduct
import com.example.b2capp.features.home.QuickCategoryItem
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.text.NumberFormat
import java.util.Locale

/**
 * Model biểu diễn Banner khuyến mãi trên Trang chủ
 */
data class HomeBannerItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val code: String? = null,
    val discountTag: String = "FREESHIP TOÀN QUỐC",
    val highlightText: String = "MEGA SALE 50% OFF",
    val bannerImageUrl: String? = null
)

/**
 * Dữ liệu nạp tổng hợp cho màn hình Trang chủ
 */
data class HomeCombinedData(
    val categories: List<QuickCategoryItem>,
    val bestSellerProducts: List<HomeBestSellerItem>,
    val suggestedProducts: List<HomeSuggestedProduct>,
    val banners: List<HomeBannerItem>,
    val isLastSuggestedPage: Boolean = false,
    val cartCount: Int = 0
)

/**
 * Repository chịu trách nhiệm gọi các API liên quan đến Trang chủ:
 * - Danh mục (roots / all)
 * - Sản phẩm bán chạy (featured)
 * - Sản phẩm gợi ý hôm nay (phân trang vô hạn)
 * - Khuyến mãi / Banners công khai
 * - Đếm số lượng món trong giỏ hàng
 *
 * Đồng thời có cơ chế fallback thông minh khi backend trả về rỗng hoặc ngoại lệ mạng.
 */
class HomeRepository(
    private val homeApiService: HomeApiService = ApiClient.createService(HomeApiService::class.java),
    private val cartApiService: CartApiService = ApiClient.createAuthenticatedService(CartApiService::class.java)
) {

    /**
     * Nạp toàn bộ dữ liệu ban đầu cho Trang chủ (chạy song song async/await)
     */
    suspend fun loadInitialHomeData(): Result<HomeCombinedData> = coroutineScope {
        try {
            val categoriesDeferred = async { fetchCategories() }
            val featuredDeferred = async { fetchFeaturedProducts() }
            val suggestedDeferred = async { fetchSuggestedProducts(page = 0, size = 10) }
            val promotionsDeferred = async { fetchPromotions() }
            val cartCountDeferred = async { fetchCartCount() }

            val categories = categoriesDeferred.await()
            val bestSellers = featuredDeferred.await()
            val (suggested, isLastPage) = suggestedDeferred.await()
            val banners = promotionsDeferred.await()
            val cartCount = cartCountDeferred.await()

            Result.success(
                HomeCombinedData(
                    categories = categories,
                    bestSellerProducts = bestSellers,
                    suggestedProducts = suggested,
                    banners = banners,
                    isLastSuggestedPage = isLastPage,
                    cartCount = cartCount
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Nạp trang sản phẩm gợi ý kế tiếp cho tính năng Cuộn Vô Hạn (Infinite Scroll)
     */
    suspend fun loadMoreSuggested(page: Int, size: Int = 10): Pair<List<HomeSuggestedProduct>, Boolean> {
        return fetchSuggestedProducts(page, size)
    }

    /**
     * Lấy số lượng giỏ hàng của tài khoản hiện tại
     */
    suspend fun getCartItemCount(): Int {
        return fetchCartCount()
    }

    /**
     * Lấy thông tin chi tiết đầy đủ của sản phẩm từ API
     */
    suspend fun getProductDetailById(id: Long): com.example.b2capp.data.remote.dto.ProductDetailDto? {
        return try {
            val response = homeApiService.getProductDetailById(id)
            if (response.isSuccessful) {
                response.body()?.data
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Nạp danh sách danh mục đa cấp từ Database qua API cho Màn hình Danh mục
     */
    suspend fun getCategoriesForBrowser(): List<CategoryDto> {
        return try {
            val treeResponse = homeApiService.getCategoryTree()
            if (treeResponse.isSuccessful && !treeResponse.body()?.data.isNullOrEmpty()) {
                treeResponse.body()!!.data!!
            } else {
                val rootsResponse = homeApiService.getRootCategories()
                if (rootsResponse.isSuccessful && !rootsResponse.body()?.data.isNullOrEmpty()) {
                    rootsResponse.body()!!.data!!
                } else {
                    val allResponse = homeApiService.getAllCategories()
                    if (allResponse.isSuccessful && !allResponse.body()?.data.isNullOrEmpty()) {
                        allResponse.body()!!.data!!.filter { it.parentId == null || it.level == 1 }
                    } else {
                        getDefaultCategoryDtos()
                    }
                }
            }
        } catch (_: Exception) {
            getDefaultCategoryDtos()
        }
    }

    /**
     * Nạp danh sách sản phẩm thuộc danh mục từ Database qua API
     */
    suspend fun getProductsByCategory(categoryId: Long?): List<ProductItem> {
        return try {
            if (categoryId != null) {
                val catResponse = homeApiService.getCategoryProducts(categoryId)
                if (catResponse.isSuccessful && catResponse.body()?.data != null && catResponse.body()!!.data!!.content.isNotEmpty()) {
                    return catResponse.body()!!.data!!.content.map { it.toProductItem() }
                }
            }
            val searchResponse = homeApiService.getProducts(page = 0, size = 30, categoryId = categoryId)
            if (searchResponse.isSuccessful && searchResponse.body()?.data != null) {
                searchResponse.body()!!.data!!.content.map { it.toProductItem() }
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    // ==================== INTERNAL HELPER METHODS ====================

    private suspend fun fetchCategories(): List<QuickCategoryItem> {
        return try {
            val response = homeApiService.getRootCategories()
            if (response.isSuccessful && response.body()?.data != null) {
                val dtos = response.body()!!.data!!
                if (dtos.isNotEmpty()) {
                    mapCategories(dtos)
                } else {
                    getDefaultCategories()
                }
            } else {
                getDefaultCategories()
            }
        } catch (_: Exception) {
            getDefaultCategories()
        }
    }

    private suspend fun fetchFeaturedProducts(): List<HomeBestSellerItem> {
        return try {
            val response = homeApiService.getFeaturedProducts(page = 0, size = 10)
            if (response.isSuccessful && response.body()?.data != null) {
                val pageData = response.body()!!.data!!
                if (pageData.content.isNotEmpty()) {
                    pageData.content.map { it.toHomeBestSellerItem() }
                } else {
                    getDefaultBestSellers()
                }
            } else {
                getDefaultBestSellers()
            }
        } catch (_: Exception) {
            getDefaultBestSellers()
        }
    }

    private suspend fun fetchSuggestedProducts(page: Int, size: Int): Pair<List<HomeSuggestedProduct>, Boolean> {
        return try {
            val response = homeApiService.getProducts(page = page, size = size, sortBy = "created", sortDir = "desc")
            if (response.isSuccessful && response.body()?.data != null) {
                val pageData = response.body()!!.data!!
                val mapped = pageData.content.map { it.toHomeSuggestedProduct() }
                Pair(mapped, pageData.last)
            } else {
                Pair(emptyList(), true)
            }
        } catch (_: Exception) {
            Pair(emptyList(), true)
        }
    }

    private suspend fun fetchPromotions(): List<HomeBannerItem> {
        return try {
            val response = homeApiService.getPublicPromotions()
            if (response.isSuccessful && response.body()?.data != null) {
                val dtos = response.body()!!.data!!
                if (dtos.isNotEmpty()) {
                    dtos.map { it.toHomeBannerItem() }
                } else {
                    getDefaultBanners()
                }
            } else {
                getDefaultBanners()
            }
        } catch (_: Exception) {
            getDefaultBanners()
        }
    }

    private suspend fun fetchCartCount(): Int {
        return try {
            if (!TokenManager.getInstance().isLoggedIn()) {
                return 0
            }
            val response = cartApiService.getCartCount()
            if (response.isSuccessful && response.body()?.data != null) {
                val countNumber = response.body()!!.data!!["count"] ?: 0L
                countNumber.toInt()
            } else {
                0
            }
        } catch (_: Exception) {
            0
        }
    }

    // ==================== MAPPING LOGIC ====================

    private fun mapCategories(dtos: List<CategoryDto>): List<QuickCategoryItem> {
        val result = mutableListOf<QuickCategoryItem>()

        // Lấy tối đa 7 danh mục từ backend, để dành ô thứ 8 cho "Xem tất cả"
        val topDtos = dtos.take(7)
        for ((index, dto) in topDtos.withIndex()) {
            val (icon, bg, tint) = resolveCategoryStyle(dto.slug, dto.name, index)
            result.add(
                QuickCategoryItem(
                    id = dto.id.toString(),
                    title = dto.name,
                    icon = icon,
                    containerBg = bg,
                    iconTint = tint
                )
            )
        }

        // Luôn bổ sung ô thứ 8 là "Xem tất cả"
        result.add(
            QuickCategoryItem(
                id = "qc_all",
                title = "Xem tất cả",
                icon = Icons.Outlined.Apps,
                containerBg = Color(0xFFF0F1F3),
                iconTint = Color(0xFF666666)
            )
        )

        return result
    }

    private fun resolveCategoryStyle(slug: String, name: String, index: Int): Triple<ImageVector, Color, Color> {
        val s = slug.lowercase(Locale.ROOT)
        val n = name.lowercase(Locale.ROOT)

        return when {
            s.contains("thoi-trang") || n.contains("thời trang") || n.contains("quần áo") ->
                Triple(Icons.Outlined.Checkroom, Color(0xFFFEECE8), Color(0xFFEE4D2D))

            s.contains("dien-tu") || n.contains("điện tử") || n.contains("công nghệ") ->
                Triple(Icons.Outlined.Devices, Color(0xFFEDF4FB), Color(0xFF1E75C4))

            s.contains("gia-dung") || n.contains("gia dụng") || n.contains("nhà cửa") ->
                Triple(Icons.Outlined.Weekend, Color(0xFFF3EFF8), Color(0xFF555555))

            s.contains("lam-dep") || n.contains("làm đẹp") || n.contains("mỹ phẩm") ->
                Triple(Icons.Outlined.Spa, Color(0xFFFDECEE), Color(0xFFD32F2F))

            s.contains("giay") || n.contains("giày") || n.contains("thể thao") ->
                Triple(Icons.Outlined.DirectionsRun, Color(0xFFEDF2FC), Color(0xFF3B82F6))

            s.contains("dong-ho") || n.contains("đồng hồ") || n.contains("phụ kiện") ->
                Triple(Icons.Outlined.Watch, Color(0xFFE0F7F3), Color(0xFF14B8A6))

            s.contains("be") || n.contains("mẹ") || n.contains("trẻ em") ->
                Triple(Icons.Outlined.ChildCare, Color(0xFFFEEFE8), Color(0xFFF97316))

            else -> {
                val palettes = listOf(
                    Triple(Icons.Outlined.ShoppingBag, Color(0xFFFEECE8), Color(0xFFEE4D2D)),
                    Triple(Icons.Outlined.Devices, Color(0xFFEDF4FB), Color(0xFF1E75C4)),
                    Triple(Icons.Outlined.Weekend, Color(0xFFF3EFF8), Color(0xFF6B7280)),
                    Triple(Icons.Outlined.Spa, Color(0xFFFDECEE), Color(0xFFD32F2F))
                )
                palettes[index % palettes.size]
            }
        }
    }

    private fun ProductSummaryDto.toHomeBestSellerItem(): HomeBestSellerItem {
        val priceLong = (minPrice ?: 0.0).toLong()
        val origPriceLong = (maxPrice ?: minPrice ?: 0.0).toLong()
        val discount = if (origPriceLong > priceLong && origPriceLong > 0) {
            (((origPriceLong - priceLong).toDouble() / origPriceLong) * 100).toInt()
        } else {
            0
        }

        return HomeBestSellerItem(
            id = id.toString(),
            name = name,
            imageUrl = thumbnailUrl?.ifBlank { DEFAULT_PRODUCT_IMAGE } ?: DEFAULT_PRODUCT_IMAGE,
            price = priceLong,
            originalPrice = origPriceLong,
            discountPercent = discount,
            rating = (ratingAvg ?: 5.0).toFloat(),
            soldCount = soldCount ?: 0,
            shopName = shopName ?: "Coolmate Official Store",
            shopLocation = "Việt Nam",
            description = "Sản phẩm chính hãng, bảo hành đổi trả trong 7 ngày."
        )
    }

    private fun ProductSummaryDto.toHomeSuggestedProduct(): HomeSuggestedProduct {
        val priceLong = (minPrice ?: 0.0).toLong()
        val ratingVal = (ratingAvg ?: 5.0).toFloat()
        val sold = soldCount ?: 0

        val soldStr = if (sold >= 1000) {
            String.format(Locale.US, "Đã bán %.1fk", sold / 1000.0)
        } else {
            "Đã bán $sold"
        }

        val badge = if (ratingVal >= 4.8f || sold >= 1000) {
            HomeBadgeType.YEU_THICH
        } else {
            HomeBadgeType.FREESHIP_XTRA
        }

        return HomeSuggestedProduct(
            id = id.toString(),
            name = name,
            imageUrl = thumbnailUrl?.ifBlank { DEFAULT_PRODUCT_IMAGE } ?: DEFAULT_PRODUCT_IMAGE,
            price = priceLong,
            badgeType = badge,
            rating = ratingVal,
            soldText = soldStr,
            stock = 50,
            description = "Sản phẩm phân phối chính hãng.",
            shopName = shopName ?: "Coolmate Official Store",
            shopLocation = "Việt Nam"
        )
    }

    private fun PromotionDto.toHomeBannerItem(): HomeBannerItem {
        val highlight = if (discountType.equals("PERCENT", ignoreCase = true)) {
            "GIẢM ${(discountValue ?: 0.0).toInt()}%"
        } else {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            "GIẢM ₫${formatter.format((discountValue ?: 0.0).toLong())}"
        }

        val tag = if (applyScope.equals("FREE_SHIPPING", ignoreCase = true)) {
            "FREESHIP TOÀN QUỐC"
        } else {
            "ƯU ĐÃI ĐỘC QUYỀN"
        }

        val codeNotice = if (!code.isNullOrBlank()) "Nhập mã: $code" else ""
        val minSpendNotice = if (minOrderValue != null && minOrderValue > 0) {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            "cho đơn từ ₫${formatter.format(minOrderValue.toLong())}"
        } else {
            "Áp dụng cho mọi đơn hàng"
        }

        val sub = listOf(codeNotice, minSpendNotice).filter { it.isNotBlank() }.joinToString(" - ")

        return HomeBannerItem(
            id = id,
            title = name ?: "SIÊU TIỆC MUA SẮM",
            subtitle = if (sub.isNotBlank()) sub else "Ưu đãi có hạn dành riêng cho bạn hôm nay",
            code = code,
            discountTag = tag,
            highlightText = highlight
        )
    }

    // ==================== DEFAULT FALLBACK DATA ====================

    companion object {
        private const val DEFAULT_PRODUCT_IMAGE =
            "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=400&auto=format&fit=crop&q=75"

        fun getDefaultCategories(): List<QuickCategoryItem> {
            return listOf(
                QuickCategoryItem("qc_fashion", "Thời trang", Icons.Outlined.Checkroom, Color(0xFFFEECE8), Color(0xFFEE4D2D)),
                QuickCategoryItem("qc_tech", "Điện tử", Icons.Outlined.Devices, Color(0xFFEDF4FB), Color(0xFF1E75C4)),
                QuickCategoryItem("qc_home", "Gia dụng", Icons.Outlined.Weekend, Color(0xFFF3EFF8), Color(0xFF555555)),
                QuickCategoryItem("qc_beauty", "Làm đẹp", Icons.Outlined.Spa, Color(0xFFFDECEE), Color(0xFFD32F2F)),
                QuickCategoryItem("qc_shoes", "Giày dép", Icons.Outlined.DirectionsRun, Color(0xFFEDF2FC), Color(0xFF3B82F6)),
                QuickCategoryItem("qc_watches", "Đồng hồ", Icons.Outlined.Watch, Color(0xFFE0F7F3), Color(0xFF14B8A6)),
                QuickCategoryItem("qc_baby", "Mẹ & Bé", Icons.Outlined.ChildCare, Color(0xFFFEEFE8), Color(0xFFF97316)),
                QuickCategoryItem("qc_all", "Xem tất cả", Icons.Outlined.Apps, Color(0xFFF0F1F3), Color(0xFF666666))
            )
        }

        fun getDefaultBestSellers(): List<HomeBestSellerItem> {
            return listOf(
                HomeBestSellerItem(
                    id = "bs_1",
                    name = "Tai nghe Bluetooth chống ồn Sony",
                    imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400&auto=format&fit=crop&q=75",
                    price = 299000L,
                    originalPrice = 460000L,
                    discountPercent = 35,
                    rating = 4.9f,
                    soldCount = 2400,
                    shopName = "Sony Official Store",
                    shopLocation = "TP. Hồ Chí Minh",
                    description = "Tai nghe không dây Bluetooth tích hợp chống ồn chủ động, âm bass sống động."
                ),
                HomeBestSellerItem(
                    id = "bs_2",
                    name = "Áo Thun Nam Cotton Compact Siêu Thoáng",
                    imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=400&auto=format&fit=crop&q=75",
                    price = 199000L,
                    originalPrice = 299000L,
                    discountPercent = 33,
                    rating = 5.0f,
                    soldCount = 5800,
                    shopName = "Coolmate Official Store",
                    shopLocation = "Hà Nội",
                    description = "Áo thun nam chất liệu 100% cotton compact mềm mịn, co giãn 4 chiều."
                ),
                HomeBestSellerItem(
                    id = "bs_3",
                    name = "Bình giữ nhiệt Lock&Lock nhiệt độ thông minh",
                    imageUrl = "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400&auto=format&fit=crop&q=75",
                    price = 149000L,
                    originalPrice = 298000L,
                    discountPercent = 50,
                    rating = 4.8f,
                    soldCount = 1850,
                    shopName = "Lock&Lock Official",
                    shopLocation = "Hà Nội",
                    description = "Bình giữ nhiệt chất liệu inox 316 cao cấp kèm màn hình cảm ứng hiển thị nhiệt độ."
                ),
                HomeBestSellerItem(
                    id = "bs_4",
                    name = "Bàn phím cơ Bluetooth kèm kê tay gỗ",
                    imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400&auto=format&fit=crop&q=75",
                    price = 590000L,
                    originalPrice = 739000L,
                    discountPercent = 20,
                    rating = 4.9f,
                    soldCount = 920,
                    shopName = "Logitech Flagship",
                    shopLocation = "TP. Hồ Chí Minh",
                    description = "Bàn phím cơ Bluetooth hỗ trợ kết nối đa thiết bị, phím gõ êm."
                )
            )
        }

        fun getDefaultBanners(): List<HomeBannerItem> {
            return listOf(
                HomeBannerItem(
                    id = 1L,
                    title = "MID-YEAR MEGA SALE",
                    subtitle = "Siêu tiệc công nghệ & Thời trang hè bùng nổ hôm nay",
                    code = "MEGASALE50",
                    discountTag = "FREESHIP TOÀN QUỐC",
                    highlightText = "MEGA SALE 50% OFF"
                )
            )
        }

        fun getDefaultCategoryDtos(): List<CategoryDto> {
            return listOf(
                CategoryDto(id = 1L, name = "Thời trang", slug = "thoi-trang", iconUrl = "bi bi-person", level = 1),
                CategoryDto(id = 2L, name = "Điện tử", slug = "dien-tu", iconUrl = "bi bi-laptop", level = 1),
                CategoryDto(id = 3L, name = "Gia dụng", slug = "gia-dung", iconUrl = "bi bi-house-door", level = 1),
                CategoryDto(id = 4L, name = "Làm đẹp", slug = "lam-dep", iconUrl = "bi bi-stars", level = 1),
                CategoryDto(id = 5L, name = "Giày dép", slug = "giay-dep", iconUrl = "bi bi-bicycle", level = 1),
                CategoryDto(id = 6L, name = "Đồng hồ", slug = "dong-ho", iconUrl = "bi bi-watch", level = 1),
                CategoryDto(id = 7L, name = "Mẹ & Bé", slug = "me-va-be", iconUrl = "bi bi-balloon-heart", level = 1),
                CategoryDto(id = 8L, name = "Bách hóa", slug = "bach-hoa", iconUrl = "bi bi-cup-hot", level = 1)
            )
        }
    }
}

/**
 * Chuyển đổi từ DTO tóm tắt sản phẩm của backend sang ProductItem
 */
fun ProductSummaryDto.toProductItem(): ProductItem {
    val priceLong = (minPrice ?: 0.0).toLong()
    val origPriceLong = (maxPrice ?: minPrice ?: 0.0).toLong()
    return ProductItem(
        id = id.toString(),
        name = name,
        imageUrl = thumbnailUrl?.ifBlank { "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&auto=format&fit=crop&q=80" }
            ?: "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&auto=format&fit=crop&q=80",
        price = priceLong,
        originalPrice = if (origPriceLong > priceLong) origPriceLong else null,
        rating = (ratingAvg ?: 5.0).toFloat(),
        soldCount = soldCount ?: 0,
        isFreeShipping = true,
        stock = 99,
        category = categoryName ?: "Danh mục",
        description = "Sản phẩm chính hãng chất lượng cao.",
        shopName = shopName ?: "Coolmate Official Store",
        shopLocation = "Việt Nam",
        images = listOfNotNull(thumbnailUrl)
    )
}

