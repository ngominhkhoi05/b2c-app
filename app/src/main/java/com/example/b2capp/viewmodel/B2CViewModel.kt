package com.example.b2capp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.b2capp.data.SampleData
import com.example.b2capp.model.CartItem
import com.example.b2capp.model.ProductItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.util.Locale

enum class PageState {
    SUCCESS,
    LOADING,
    EMPTY,
    ERROR
}

data class ShopChatInfo(
    val shopName: String = "Coolmate Official Store",
    val isMall: Boolean = true,
    val isOnline: Boolean = true,
    val product: ProductItem? = null
)

data class OrderSuccessInfo(
    val orderId: String = "ORD-20261105-0088",
    val paymentMethodName: String = "Cổng VNPAY",
    val isOnlinePaid: Boolean = true,
    val orderTime: String = "05/11/2026 14:20",
    val expectedDelivery: String = "14 Th04 – 16 Th04",
    val totalAmount: Long = 388200L,
    val recipientName: String = "Nguyễn Văn A",
    val recipientPhoneMasked: String = "090****123",
    val deliveryAddress: String = "Số 45 Lê Duẩn, Phường Bến Nghé, Q.1, TP. HCM"
)

data class UserAddress(
    val id: String,
    val recipientName: String,
    val phoneNumber: String,
    val addressType: String, // "Nhà riêng" hoặc "Văn phòng"
    val fullAddress: String,
    val isDefault: Boolean = false,
    val deliveryNote: String? = null
)

data class UserProfile(
    val fullName: String = "Nguyễn Văn A",
    val phoneNumber: String = "0987 654 321",
    val email: String = "nguyenvana@gmail.com",
    val gender: String = "Nam",
    val birthDate: String = "15/08/1995"
)

data class B2CUiState(
    val currentTab: Int = 0, // 0: Home, 1: Cart, 2: Design System Showcase
    val pageState: PageState = PageState.SUCCESS,
    val selectedCategory: String = "Tất cả",
    val searchQuery: String = "",
    val products: List<ProductItem> = SampleData.products,
    val cartItems: List<CartItem> = listOf(
        CartItem(SampleData.products[0], quantity = 1),
        CartItem(SampleData.products[1], quantity = 2)
    ),
    val selectedProductForSheet: ProductItem? = null,
    val detailedProductScreen: ProductItem? = null,
    val searchResultQuery: String? = null,
    val showShopMessages: Boolean = false,
    val activeShopChat: ShopChatInfo? = null,
    val showCheckoutScreen: Boolean = false,
    val showVnpayPaymentScreen: Boolean = false,
    val completedOrderSuccess: OrderSuccessInfo? = null,
    val detailedOrderTrackingScreen: String? = null,
    val isLoggedIn: Boolean = false,
    val showSignInScreen: Boolean = true,
    val showSignUpScreen: Boolean = false,
    val showForgotPasswordScreen: Boolean = false,
    val showProductReviewsScreen: Boolean = false,
    val showWishlistScreen: Boolean = false,
    val showAddressBookScreen: Boolean = false,
    val showAddressEditorScreen: Boolean = false,
    val editingAddressForScreen: UserAddress? = null,
    val showEditProfileScreen: Boolean = false,
    val showWriteReviewScreen: Boolean = false,
    val showChangePasswordScreen: Boolean = false,
    val userProfile: UserProfile = UserProfile(),
    val addresses: List<UserAddress> = listOf(
        UserAddress(
            id = "addr_1",
            recipientName = "Nguyễn Văn A",
            phoneNumber = "0987 654 321",
            addressType = "Nhà riêng",
            fullAddress = "Số 123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh",
            isDefault = true,
            deliveryNote = "Lưu ý: Giao giờ hành chính, gọi trước 15 phút"
        ),
        UserAddress(
            id = "addr_2",
            recipientName = "Nguyễn Văn A",
            phoneNumber = "0987 654 321",
            addressType = "Văn phòng",
            fullAddress = "Tòa nhà Bitexco Financial Tower, Tầng 18, 2 Hải Triều, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh",
            isDefault = false
        ),
        UserAddress(
            id = "addr_3",
            recipientName = "Trần Thị B (Mẹ)",
            phoneNumber = "0912 345 678",
            addressType = "Nhà riêng",
            fullAddress = "45 Lê Lợi, Phường Vĩnh Ninh, TP. Huế, Thừa Thiên Huế",
            isDefault = false
        )
    ),
    val detailSelectedQuantity: Int = 1,
    val interactiveRatingDemo: Float = 4.0f,
    val toastMessage: String? = null
) {
    val totalCartPrice: Long
        get() = cartItems.sumOf { it.totalPrice }

    val formattedCartTotal: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return "₫${formatter.format(totalCartPrice)}"
        }

    val totalCartCount: Int
        get() = cartItems.sumOf { it.quantity }

    val filteredProducts: List<ProductItem>
        get() = products.filter { product ->
            val matchCategory = selectedCategory == "Tất cả" || product.category == selectedCategory
            val matchQuery = searchQuery.isBlank() || product.name.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
}

class B2CViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(B2CUiState())
    val uiState: StateFlow<B2CUiState> = _uiState.asStateFlow()

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(currentTab = tabIndex) }
    }

    fun setPageState(state: PageState) {
        _uiState.update { it.copy(pageState = state) }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openSearchResults(query: String) {
        _uiState.update { it.copy(searchResultQuery = query.ifBlank { "áo thun nam" }) }
    }

    fun closeSearchResults() {
        _uiState.update { it.copy(searchResultQuery = null) }
    }

    fun openShopMessages() {
        _uiState.update { it.copy(showShopMessages = true) }
    }

    fun closeShopMessages() {
        _uiState.update { it.copy(showShopMessages = false) }
    }

    fun openShopChat(shopName: String = "Coolmate Official Store", product: ProductItem? = null) {
        _uiState.update { state ->
            val p = product ?: state.detailedProductScreen ?: SampleData.products[0]
            state.copy(
                activeShopChat = ShopChatInfo(
                    shopName = shopName,
                    isMall = true,
                    isOnline = true,
                    product = p
                )
            )
        }
    }

    fun closeShopChat() {
        _uiState.update { it.copy(activeShopChat = null) }
    }

    fun openProductDetailsScreen(product: ProductItem) {
        _uiState.update { it.copy(detailedProductScreen = product) }
    }

    fun closeProductDetailsScreen() {
        _uiState.update { it.copy(detailedProductScreen = null) }
    }

    fun openCheckoutScreen() {
        _uiState.update { it.copy(showCheckoutScreen = true) }
    }

    fun closeCheckoutScreen() {
        _uiState.update { it.copy(showCheckoutScreen = false) }
    }

    fun openVnpayPayment() {
        _uiState.update { it.copy(showVnpayPaymentScreen = true) }
    }

    fun closeVnpayPayment() {
        _uiState.update { it.copy(showVnpayPaymentScreen = false) }
    }

    fun completeVnpayPayment() {
        _uiState.update { state ->
            state.copy(
                showVnpayPaymentScreen = false,
                showCheckoutScreen = false,
                completedOrderSuccess = OrderSuccessInfo(
                    orderId = "ORD-20261105-0088",
                    paymentMethodName = "Cổng VNPAY",
                    isOnlinePaid = true,
                    orderTime = "05/11/2026 14:20",
                    expectedDelivery = "14 Th04 – 16 Th04",
                    totalAmount = 388200L,
                    recipientName = "Nguyễn Văn A",
                    recipientPhoneMasked = "090****123",
                    deliveryAddress = "Số 45 Lê Duẩn, Phường Bến Nghé, Q.1, TP. HCM"
                )
            )
        }
    }

    fun completeOrderPlacement() {
        val randomOrderCode = (100000..999999).random()
        _uiState.update { state ->
            state.copy(
                showCheckoutScreen = false,
                completedOrderSuccess = OrderSuccessInfo(
                    orderId = "ORD-20261105-0$randomOrderCode",
                    paymentMethodName = "Thanh toán khi nhận hàng (COD)",
                    isOnlinePaid = false,
                    orderTime = "05/11/2026 14:20",
                    expectedDelivery = "14 Th04 – 16 Th04",
                    totalAmount = 388200L,
                    recipientName = "Nguyễn Văn A",
                    recipientPhoneMasked = "090****123",
                    deliveryAddress = "Số 45 Lê Duẩn, Phường Bến Nghé, Q.1, TP. HCM"
                )
            )
        }
    }

    fun closeOrderSuccessScreen(navigateToOrdersTab: Boolean = false) {
        _uiState.update { state ->
            state.copy(
                completedOrderSuccess = null,
                detailedOrderTrackingScreen = if (navigateToOrdersTab) "ORD-20261105-0088" else null,
                currentTab = if (navigateToOrdersTab) 3 else 0
            )
        }
    }

    fun openOrderDetailsTrackingScreen(orderId: String = "ORD-20261105-0088") {
        _uiState.update { it.copy(detailedOrderTrackingScreen = orderId) }
    }

    fun closeOrderDetailsTrackingScreen() {
        _uiState.update { it.copy(detailedOrderTrackingScreen = null) }
    }

    fun login(email: String = "nguyenvana@gmail.com") {
        _uiState.update { state ->
            state.copy(
                isLoggedIn = true,
                showSignInScreen = false,
                showSignUpScreen = false,
                showForgotPasswordScreen = false,
                currentTab = 0,
                userProfile = state.userProfile.copy(email = email),
                toastMessage = "Đăng nhập thành công! Chào mừng bạn quay trở lại."
            )
        }
    }

    fun logout() {
        _uiState.update { state ->
            state.copy(
                isLoggedIn = false,
                showSignInScreen = true,
                showSignUpScreen = false,
                showForgotPasswordScreen = false,
                toastMessage = "Đã đăng xuất tài khoản"
            )
        }
    }

    fun openSignIn() {
        _uiState.update { it.copy(showSignInScreen = true, showSignUpScreen = false, showForgotPasswordScreen = false) }
    }

    fun closeSignIn() {
        _uiState.update { it.copy(showSignInScreen = false) }
    }

    fun openSignUp() {
        _uiState.update { it.copy(showSignUpScreen = true, showSignInScreen = false, showForgotPasswordScreen = false) }
    }

    fun closeSignUp() {
        _uiState.update { state ->
            state.copy(
                showSignUpScreen = false,
                showSignInScreen = !state.isLoggedIn
            )
        }
    }

    fun openForgotPassword() {
        _uiState.update { it.copy(showForgotPasswordScreen = true, showSignInScreen = false, showSignUpScreen = false) }
    }

    fun closeForgotPassword() {
        _uiState.update { state ->
            state.copy(
                showForgotPasswordScreen = false,
                showSignInScreen = !state.isLoggedIn
            )
        }
    }

    fun openProductReviewsScreen() {
        _uiState.update { it.copy(showProductReviewsScreen = true) }
    }

    fun closeProductReviewsScreen() {
        _uiState.update { it.copy(showProductReviewsScreen = false) }
    }

    fun openWishlist() {
        _uiState.update { it.copy(showWishlistScreen = true) }
    }

    fun closeWishlist() {
        _uiState.update { it.copy(showWishlistScreen = false) }
    }

    fun openAddressBook() {
        _uiState.update { it.copy(showAddressBookScreen = true) }
    }

    fun closeAddressBook() {
        _uiState.update { it.copy(showAddressBookScreen = false) }
    }

    fun openAddressEditor(address: UserAddress? = null) {
        _uiState.update {
            it.copy(
                showAddressEditorScreen = true,
                editingAddressForScreen = address
            )
        }
    }

    fun closeAddressEditor() {
        _uiState.update {
            it.copy(
                showAddressEditorScreen = false,
                editingAddressForScreen = null
            )
        }
    }

    fun openEditProfile() {
        _uiState.update { it.copy(showEditProfileScreen = true) }
    }

    fun closeEditProfile() {
        _uiState.update { it.copy(showEditProfileScreen = false) }
    }

    fun updateUserProfile(profile: UserProfile) {
        _uiState.update {
            it.copy(
                userProfile = profile,
                showEditProfileScreen = false,
                toastMessage = "Cập nhật hồ sơ cá nhân thành công!"
            )
        }
    }

    fun openWriteReview() {
        _uiState.update { it.copy(showWriteReviewScreen = true) }
    }

    fun closeWriteReview() {
        _uiState.update { it.copy(showWriteReviewScreen = false) }
    }

    fun openChangePassword() {
        _uiState.update { it.copy(showChangePasswordScreen = true) }
    }

    fun closeChangePassword() {
        _uiState.update { it.copy(showChangePasswordScreen = false) }
    }

    fun setDefaultAddress(addressId: String) {
        _uiState.update { state ->
            val updated = state.addresses.map { addr ->
                addr.copy(isDefault = (addr.id == addressId))
            }
            state.copy(addresses = updated, toastMessage = "Đã cập nhật địa chỉ mặc định!")
        }
    }

    fun deleteAddress(addressId: String) {
        _uiState.update { state ->
            val updated = state.addresses.filterNot { it.id == addressId }
            state.copy(addresses = updated, toastMessage = "Đã xóa địa chỉ khỏi sổ địa chỉ")
        }
    }

    fun addOrUpdateAddress(address: UserAddress) {
        _uiState.update { state ->
            val list = state.addresses.toMutableList()
            val existingIndex = list.indexOfFirst { it.id == address.id }

            var newAddress = address
            if (address.isDefault || list.isEmpty()) {
                newAddress = address.copy(isDefault = true)
                for (i in list.indices) {
                    list[i] = list[i].copy(isDefault = false)
                }
            }

            if (existingIndex >= 0) {
                list[existingIndex] = newAddress
            } else {
                list.add(newAddress)
            }

            state.copy(addresses = list, toastMessage = "Đã lưu địa chỉ thành công!")
        }
    }

    fun addProductToCart(product: ProductItem, color: String = "Trắng Tinh Khôi", size: String = "L", quantity: Int = 1) {
        _uiState.update { state ->
            val existingIndex = state.cartItems.indexOfFirst { it.product.id == product.id }
            val updatedList = state.cartItems.toMutableList()
            if (existingIndex >= 0) {
                val existing = updatedList[existingIndex]
                updatedList[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
            } else {
                updatedList.add(CartItem(product, quantity))
            }
            state.copy(
                cartItems = updatedList,
                toastMessage = "Đã thêm $quantity x ${product.name} ($color, Size $size) vào giỏ hàng!"
            )
        }
    }

    fun openProductDetail(product: ProductItem) {
        _uiState.update {
            it.copy(
                selectedProductForSheet = product,
                detailSelectedQuantity = 1
            )
        }
    }

    fun closeProductDetail() {
        _uiState.update { it.copy(selectedProductForSheet = null) }
    }

    fun setDetailQuantity(quantity: Int) {
        _uiState.update { it.copy(detailSelectedQuantity = quantity) }
    }

    fun addToCartFromDetail() {
        val currentProduct = _uiState.value.selectedProductForSheet ?: return
        val qty = _uiState.value.detailSelectedQuantity

        _uiState.update { state ->
            val existingIndex = state.cartItems.indexOfFirst { it.product.id == currentProduct.id }
            val updatedList = state.cartItems.toMutableList()
            if (existingIndex >= 0) {
                val existing = updatedList[existingIndex]
                updatedList[existingIndex] = existing.copy(quantity = existing.quantity + qty)
            } else {
                updatedList.add(CartItem(currentProduct, qty))
            }
            state.copy(
                cartItems = updatedList,
                selectedProductForSheet = null,
                toastMessage = "Đã thêm $qty sản phẩm vào giỏ hàng thành công!"
            )
        }
    }

    fun updateCartItemQuantity(productId: String, newQty: Int) {
        _uiState.update { state ->
            val updatedList = state.cartItems.map { item ->
                if (item.product.id == productId) item.copy(quantity = newQty) else item
            }
            state.copy(cartItems = updatedList)
        }
    }

    fun removeCartItem(productId: String) {
        _uiState.update { state ->
            state.copy(
                cartItems = state.cartItems.filterNot { it.product.id == productId },
                toastMessage = "Đã xóa sản phẩm khỏi giỏ hàng"
            )
        }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyList()) }
    }

    fun setInteractiveRating(rating: Int) {
        _uiState.update {
            it.copy(
                interactiveRatingDemo = rating.toFloat(),
                toastMessage = "Cảm ơn bạn đã đánh giá $rating sao!"
            )
        }
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
