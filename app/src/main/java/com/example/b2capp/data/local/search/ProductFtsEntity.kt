package com.example.b2capp.data.local.search

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.example.b2capp.core.model.ProductItem

/**
 * Bảng ảo Room FTS4 tối ưu Full-Text Search cục bộ trên Android.
 * Hỗ trợ tìm kiếm theo tên có dấu, tên không dấu, mô tả, danh mục, shop và thương hiệu.
 */
@Entity(tableName = "products_fts")
@Fts4
data class ProductFtsEntity(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowid: Int = 0,

    val productId: String,
    val name: String,
    val normalizedName: String,
    val description: String,
    val category: String,
    val shopName: String,
    val shopLocation: String,
    val price: Long,
    val originalPrice: Long? = null,
    val rating: Float = 5.0f,
    val soldCount: Int = 0,
    val imageUrl: String = "",
    val isFreeShipping: Boolean = true
) {
    fun toProductItem(): ProductItem = ProductItem(
        id = productId,
        name = name,
        imageUrl = imageUrl,
        price = price,
        originalPrice = originalPrice,
        rating = rating,
        soldCount = soldCount,
        isFreeShipping = isFreeShipping,
        category = category,
        description = description,
        shopName = shopName,
        shopLocation = shopLocation
    )

    companion object {
        fun fromProductItem(item: ProductItem, rowId: Int = 0): ProductFtsEntity {
            return ProductFtsEntity(
                rowid = rowId,
                productId = item.id,
                name = item.name,
                normalizedName = VietnameseNormalizer.removeAccents(item.name),
                description = item.description,
                category = item.category,
                shopName = item.shopName,
                shopLocation = item.shopLocation,
                price = item.price,
                originalPrice = item.originalPrice,
                rating = item.rating,
                soldCount = item.soldCount,
                imageUrl = item.imageUrl,
                isFreeShipping = item.isFreeShipping
            )
        }
    }
}
