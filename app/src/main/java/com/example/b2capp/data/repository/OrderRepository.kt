package com.example.b2capp.data.repository

import com.example.b2capp.data.remote.ApiClient
import com.example.b2capp.data.remote.api.OrderApiService
import com.example.b2capp.data.remote.dto.CartItemDto
import com.example.b2capp.data.remote.dto.CreateOrderRequestDto
import com.example.b2capp.data.remote.dto.OrderDetailDto
import com.example.b2capp.data.remote.dto.OrderPreviewResponseDto
import com.example.b2capp.data.remote.dto.OrderSummaryDto
import com.example.b2capp.data.remote.dto.PageResponse
import com.example.b2capp.data.remote.dto.PreviewOrderRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository phụ trách quản lý tương tác dữ liệu Đơn hàng với Backend:
 * - Lấy danh sách đơn hàng của người mua (có lọc trạng thái & phân trang)
 * - Lấy chi tiết đơn hàng theo ID
 * - Hủy đơn hàng
 * - Lấy URL thanh toán VNPay khi thanh toán lại đơn PENDING
 * - Tạo đơn hàng mới từ giỏ hàng
 */
class OrderRepository(
    private val apiServiceHolder: () -> OrderApiService = {
        ApiClient.createAuthenticatedService(OrderApiService::class.java)
    }
) {

    /**
     * Lấy danh sách đơn hàng của Buyer
     * GET /api/v1/buyer/orders
     */
    suspend fun getOrders(
        status: String? = null,
        search: String? = null,
        paymentMethod: String? = null,
        fromDate: String? = null,
        toDate: String? = null,
        minAmount: Double? = null,
        maxAmount: Double? = null,
        sortBy: String? = null,
        sortDir: String? = null,
        page: Int = 0,
        size: Int = 50
    ): Result<PageResponse<OrderSummaryDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().getOrders(
                status = status,
                search = search,
                paymentMethod = paymentMethod,
                fromDate = fromDate,
                toDate = toDate,
                minAmount = minAmount,
                maxAmount = maxAmount,
                sortBy = sortBy,
                sortDir = sortDir,
                page = page,
                size = size
            )
            if (response.isSuccessful) {
                val data = response.body()?.data ?: PageResponse()
                Result.success(data)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy chi tiết một đơn hàng theo ID
     * GET /api/v1/buyer/orders/{id}
     */
    suspend fun getOrderDetail(id: Long): Result<OrderDetailDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().getOrderDetail(id)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Hủy đơn hàng
     * PATCH /api/v1/buyer/orders/{id}/cancel
     */
    suspend fun cancelOrder(id: Long, reason: String? = null): Result<OrderDetailDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().cancelOrder(id = id, reason = reason)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy liên kết thanh toán VNPay lại cho đơn PENDING
     * POST /api/v1/buyer/orders/{id}/pay
     */
    suspend fun retryPayment(id: Long): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().retryPayment(id)
            if (response.isSuccessful) {
                val url = response.body()?.data?.get("paymentUrl")
                if (!url.isNullOrBlank()) {
                    Result.success(url)
                } else {
                    Result.failure(Exception("Không nhận được liên kết thanh toán VNPay"))
                }
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Xem trước tính toán chi phí đơn hàng (không lưu DB)
     * POST /api/v1/buyer/orders/preview
     */
    suspend fun previewOrder(
        cartItemIds: List<Long>,
        addressId: Long?,
        voucherCode: String?
    ): Result<OrderPreviewResponseDto> = withContext(Dispatchers.IO) {
        try {
            val req = PreviewOrderRequestDto(
                cartItemIds = cartItemIds,
                addressId = addressId,
                voucherCode = voucherCode?.ifBlank { null }
            )
            val response = apiServiceHolder().previewOrder(req)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Xác nhận đã nhận được hàng và hoàn thành đơn hàng (PATCH /api/v1/buyer/orders/{id}/complete)
     */
    suspend fun completeOrder(orderId: Long): Result<OrderDetailDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().completeOrder(orderId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Yêu cầu trả hàng / hoàn tiền (POST /api/v1/buyer/orders/{id}/return-request)
     */
    suspend fun requestReturn(
        orderId: Long,
        reason: String,
        description: String? = null,
        orderItemIds: List<Long>? = null
    ): Result<OrderDetailDto> = withContext(Dispatchers.IO) {
        try {
            val req = com.example.b2capp.data.remote.dto.ReturnOrderRequestDto(
                reason = reason,
                description = description,
                orderItemIds = orderItemIds
            )
            val response = apiServiceHolder().requestReturn(orderId, req)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mua lại đơn hàng cũ:
     * Backend kiểm tra các sản phẩm trong đơn còn tồn tại, đang mở bán và còn tồn kho hay không.
     * Nếu hợp lệ, trả về danh sách sản phẩm đã chuẩn bị để tạo đơn mới.
     * POST /api/v1/buyer/orders/{id}/reorder
     */
    suspend fun reorder(orderId: Long): Result<List<CartItemDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().reorder(orderId)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Tạo đơn hàng mới từ giỏ hàng
     * POST /api/v1/buyer/orders
     */
    suspend fun createOrder(
        request: CreateOrderRequestDto,
        idempotencyKey: String? = null
    ): Result<OrderDetailDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiServiceHolder().createOrder(request, idempotencyKey)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
