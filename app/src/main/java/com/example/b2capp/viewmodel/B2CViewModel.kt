package com.example.b2capp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b2capp.core.model.CartItem
import com.example.b2capp.core.model.OrderSuccessInfo
import com.example.b2capp.core.model.PageState
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.core.model.ShopChatInfo
import com.example.b2capp.core.model.UserAddress
import com.example.b2capp.core.model.UserProfile
import com.example.b2capp.core.ui.components.ToastData
import com.example.b2capp.core.ui.components.ToastType
import com.example.b2capp.core.ui.components.createToastData
import com.example.b2capp.data.SampleData
import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

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
    val isAuthLoading: Boolean = false,
    val authErrorMessage: String? = null,
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
    val toastMessage: String? = null,
    val currentToast: ToastData? = null
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
    private val authRepository: AuthRepository by lazy { AuthRepository() }
    private val _uiState = MutableStateFlow(B2CUiState())
    val uiState: StateFlow<B2CUiState> = _uiState.asStateFlow()

    init {
        // Kiểm tra xem đã có phiên đăng nhập đã lưu trong TokenManager chưa
        try {
            if (TokenManager.getInstance().isLoggedIn()) {
                val savedProfile = TokenManager.getInstance().getUserProfile()
                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
                        showSignInScreen = false,
                        userProfile = savedProfile
                    )
                }
            }

            // Lắng nghe sự kiện đăng xuất khi Refresh Token hết hạn từ TokenAuthenticator
            viewModelScope.launch {
                TokenManager.getInstance().isLoggedInState.collect { loggedIn ->
                    if (!loggedIn && _uiState.value.isLoggedIn) {
                        _uiState.update {
                            it.copy(
                                isLoggedIn = false,
                                showSignInScreen = true,
                                toastMessage = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.",
                                currentToast = createToastData(
                                    "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.",
                                    ToastType.WARNING
                                )
                            )
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Trường hợp chạy trong Preview hoặc Unit Test chưa khởi tạo context
        }
    }

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

    /**
     * Đăng nhập qua API Backend với cơ chế Access/Refresh Token
     */
    fun loginWithApi(
        email: String,
        password: String,
        onSuccess: () -> Unit = {}
    ) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank()) {
            showErrorToast("Vui lòng nhập email hoặc số điện thoại")
            return
        }
        if (trimmedPassword.isBlank()) {
            showErrorToast("Vui lòng nhập mật khẩu")
            return
        }

        _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.login(trimmedEmail, trimmedPassword)
            result.fold(
                onSuccess = { auth ->
                    val displayName = auth.fullName?.ifBlank { null } ?: trimmedEmail
                    _uiState.update { state ->
                        state.copy(
                            isLoggedIn = true,
                            isAuthLoading = false,
                            authErrorMessage = null,
                            showSignInScreen = false,
                            showSignUpScreen = false,
                            showForgotPasswordScreen = false,
                            userProfile = state.userProfile.copy(
                                fullName = auth.fullName ?: state.userProfile.fullName,
                                email = auth.email ?: trimmedEmail
                            ),
                            toastMessage = "Đăng nhập thành công! Chào mừng $displayName.",
                            currentToast = createToastData(
                                "Đăng nhập thành công! Chào mừng $displayName.",
                                ToastType.SUCCESS
                            )
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    val errorMsg = error.message ?: "Đăng nhập thất bại"
                    _uiState.update { state ->
                        state.copy(
                            isAuthLoading = false,
                            authErrorMessage = errorMsg,
                            toastMessage = errorMsg,
                            currentToast = createToastData(errorMsg, ToastType.ERROR)
                        )
                    }
                }
            )
        }
    }

    /**
     * Đăng ký tài khoản người mua mới qua API Backend
     */
    fun registerWithApi(
        fullName: String,
        email: String,
        password: String,
        phone: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isBlank()) {
            showErrorToast("Vui lòng nhập họ và tên")
            return
        }
        if (trimmedEmail.isBlank()) {
            showErrorToast("Vui lòng nhập email")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            showErrorToast("Định dạng email không hợp lệ")
            return
        }
        if (password.length < 8) {
            showErrorToast("Mật khẩu phải có tối thiểu 8 ký tự")
            return
        }

        _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.register(
                email = trimmedEmail,
                password = password,
                fullName = trimmedName,
                phone = phone
            )
            result.fold(
                onSuccess = { auth ->
                    val displayName = auth.fullName ?: trimmedName
                    _uiState.update { state ->
                        state.copy(
                            isLoggedIn = true,
                            isAuthLoading = false,
                            authErrorMessage = null,
                            showSignInScreen = false,
                            showSignUpScreen = false,
                            showForgotPasswordScreen = false,
                            userProfile = state.userProfile.copy(
                                fullName = displayName,
                                email = auth.email ?: trimmedEmail
                            ),
                            toastMessage = "Đăng ký tài khoản thành công! Chào mừng $displayName.",
                            currentToast = createToastData(
                                "Đăng ký tài khoản thành công! Chào mừng $displayName.",
                                ToastType.SUCCESS
                            )
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    val errorMsg = error.message ?: "Đăng ký thất bại"
                    _uiState.update { state ->
                        state.copy(
                            isAuthLoading = false,
                            authErrorMessage = errorMsg,
                            toastMessage = errorMsg,
                            currentToast = createToastData(errorMsg, ToastType.ERROR)
                        )
                    }
                }
            )
        }
    }

    /**
     * Cập nhật Base URL của API Backend tại runtime
     */
    fun setApiBaseUrl(newUrl: String) {
        try {
            TokenManager.getInstance().saveCustomBaseUrl(newUrl)
            showSuccessToast("Đã chuyển API Base URL: $newUrl")
        } catch (_: Exception) {
            com.example.b2capp.core.network.NetworkConfig.setBaseUrl(newUrl)
        }
    }

    /**
     * Giữ lại hàm login mock cũ cho backward-compatibility
     */
    fun login(email: String = "nguyenvana@gmail.com") {
        _uiState.update { state ->
            state.copy(
                isLoggedIn = true,
                showSignInScreen = false,
                showSignUpScreen = false,
                showForgotPasswordScreen = false,
                currentTab = 0,
                userProfile = state.userProfile.copy(email = email),
                toastMessage = "Đăng nhập thành công! Chào mừng bạn quay trở lại.",
                currentToast = createToastData("Đăng nhập thành công! Chào mừng bạn quay trở lại.", ToastType.SUCCESS)
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
            } catch (_: Exception) {}
        }
        _uiState.update { state ->
            state.copy(
                isLoggedIn = false,
                showSignInScreen = true,
                showSignUpScreen = false,
                showForgotPasswordScreen = false,
                toastMessage = "Đã đăng xuất tài khoản",
                currentToast = createToastData("Đã đăng xuất tài khoản", ToastType.INFO)
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
            val msg = "Cập nhật hồ sơ cá nhân thành công!"
            it.copy(
                userProfile = profile,
                showEditProfileScreen = false,
                toastMessage = msg,
                currentToast = createToastData(msg, ToastType.SUCCESS)
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
            val msg = "Đã cập nhật địa chỉ mặc định!"
            state.copy(addresses = updated, toastMessage = msg, currentToast = createToastData(msg, ToastType.SUCCESS))
        }
    }

    fun deleteAddress(addressId: String) {
        _uiState.update { state ->
            val updated = state.addresses.filterNot { it.id == addressId }
            val msg = "Đã xóa địa chỉ khỏi sổ địa chỉ"
            state.copy(addresses = updated, toastMessage = msg, currentToast = createToastData(msg, ToastType.SUCCESS))
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

            val msg = "Đã lưu địa chỉ thành công!"
            state.copy(addresses = list, toastMessage = msg, currentToast = createToastData(msg, ToastType.SUCCESS))
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
            val msg = "Đã thêm $quantity x ${product.name} ($color, Size $size) vào giỏ hàng!"
            state.copy(
                cartItems = updatedList,
                toastMessage = msg,
                currentToast = createToastData(msg, ToastType.SUCCESS)
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
            val msg = "Đã thêm $qty sản phẩm vào giỏ hàng thành công!"
            state.copy(
                cartItems = updatedList,
                selectedProductForSheet = null,
                toastMessage = msg,
                currentToast = createToastData(msg, ToastType.SUCCESS)
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
            val msg = "Đã xóa sản phẩm khỏi giỏ hàng"
            state.copy(
                cartItems = state.cartItems.filterNot { it.product.id == productId },
                toastMessage = msg,
                currentToast = createToastData(msg, ToastType.SUCCESS)
            )
        }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyList()) }
    }

    fun setInteractiveRating(rating: Int) {
        val msg = "Cảm ơn bạn đã đánh giá $rating sao!"
        _uiState.update {
            it.copy(
                interactiveRatingDemo = rating.toFloat(),
                toastMessage = msg,
                currentToast = createToastData(msg, ToastType.SUCCESS)
            )
        }
    }

    fun showToast(message: String, type: ToastType? = null) {
        _uiState.update {
            it.copy(
                toastMessage = message,
                currentToast = createToastData(message, type)
            )
        }
    }

    fun showErrorToast(message: String) = showToast(message, ToastType.ERROR)
    fun showSuccessToast(message: String) = showToast(message, ToastType.SUCCESS)
    fun showWarningToast(message: String) = showToast(message, ToastType.WARNING)

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null, currentToast = null) }
    }
}
