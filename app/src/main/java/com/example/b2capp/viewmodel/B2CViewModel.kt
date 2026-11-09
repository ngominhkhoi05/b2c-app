package com.example.b2capp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b2capp.core.model.CartItem
import com.example.b2capp.core.model.OrderSuccessInfo
import com.example.b2capp.core.model.PageState
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.core.model.ProductOptionItem
import com.example.b2capp.core.model.ProductOptionValueItem
import com.example.b2capp.core.model.ProductVariantItem
import com.example.b2capp.core.model.ShopChatInfo
import com.example.b2capp.core.model.UserAddress
import com.example.b2capp.core.model.UserProfile
import com.example.b2capp.core.model.toDomain
import com.example.b2capp.core.model.toUpdateRequest
import com.example.b2capp.core.ui.components.ToastData
import com.example.b2capp.core.ui.components.ToastType
import com.example.b2capp.core.ui.components.createToastData
import com.example.b2capp.data.SampleData
import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.remote.dto.CategoryDto
import com.example.b2capp.data.remote.dto.CartItemDto
import com.example.b2capp.data.remote.dto.ChangePasswordRequestDto
import com.example.b2capp.data.remote.dto.CreateOrderRequestDto
import com.example.b2capp.data.remote.dto.OrderDetailDto
import com.example.b2capp.data.remote.dto.OrderItemDto
import com.example.b2capp.data.remote.dto.OrderSummaryDto
import com.example.b2capp.data.repository.AuthRepository
import com.example.b2capp.data.repository.CartRepository
import com.example.b2capp.data.repository.HomeBannerItem
import com.example.b2capp.data.repository.HomeRepository
import com.example.b2capp.data.repository.OrderRepository
import com.example.b2capp.data.repository.UserRepository
import com.example.b2capp.features.home.HomeBadgeType
import com.example.b2capp.features.home.HomeBestSellerItem
import com.example.b2capp.features.home.HomeSuggestedProduct
import com.example.b2capp.features.home.QuickCategoryItem
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
    val currentToast: ToastData? = null,
    val homeCategories: List<QuickCategoryItem> = HomeRepository.getDefaultCategories(),
    val homeBestSellers: List<HomeBestSellerItem> = HomeRepository.getDefaultBestSellers(),
    val homeSuggestedProducts: List<HomeSuggestedProduct> = emptyList(),
    val homeBanners: List<HomeBannerItem> = HomeRepository.getDefaultBanners(),
    val isHomeLoading: Boolean = false,
    val isHomeRefreshing: Boolean = false,
    val isLoadingMoreSuggested: Boolean = false,
    val isLastSuggestedPage: Boolean = false,
    val homeSuggestedPage: Int = 0,
    val serverCartCount: Int? = null,
    val serverCartItems: List<CartItemDto> = emptyList(),
    val isCartLoading: Boolean = false,
    val isCartRefreshing: Boolean = false,
    val cartErrorMessage: String? = null,
    val isCheckingAuth: Boolean = false,
    val isProfileLoading: Boolean = false,
    val isUpdatingProfile: Boolean = false,
    val isChangingPassword: Boolean = false,
    val isUploadingAvatar: Boolean = false,
    val orders: List<OrderSummaryDto> = emptyList(),
    val isOrdersLoading: Boolean = false,
    val isOrdersRefreshing: Boolean = false,
    val ordersErrorMessage: String? = null,
    val selectedOrdersTab: String = "Tất cả",
    val activeOrderDetail: OrderDetailDto? = null,
    val isOrderDetailLoading: Boolean = false,
    val orderDetailErrorMessage: String? = null,
    val cancellingOrderId: Long? = null,
    val retryingOrderId: Long? = null,
    val browserCategories: List<CategoryDto> = HomeRepository.getDefaultCategoryDtos(),
    val selectedBrowserCategory: CategoryDto? = null,
    val selectedBrowserSubCategoryId: Long? = null,
    val browserProducts: List<ProductItem> = emptyList(),
    val isBrowserCategoriesLoading: Boolean = false,
    val isBrowserProductsLoading: Boolean = false,
    val browserCategoryErrorMessage: String? = null
) {
    val pendingOrdersCount: Int
        get() = orders.count { it.status.equals("PENDING", ignoreCase = true) }
    val totalCartPrice: Long
        get() = if (serverCartItems.isNotEmpty()) {
            serverCartItems.filter { it.isSelected }.sumOf { (it.lineTotal ?: ((it.priceSnapshot ?: 0.0) * it.quantity)).toLong() }
        } else {
            cartItems.sumOf { it.totalPrice }
        }

    val formattedCartTotal: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            return "₫${formatter.format(totalCartPrice)}"
        }

    val totalCartCount: Int
        get() = if (serverCartItems.isNotEmpty()) {
            serverCartItems.sumOf { it.quantity }
        } else {
            serverCartCount ?: cartItems.sumOf { it.quantity }
        }

    val filteredProducts: List<ProductItem>
        get() = products.filter { product ->
            val matchCategory = selectedCategory == "Tất cả" || product.category == selectedCategory
            val matchQuery = searchQuery.isBlank() || product.name.contains(searchQuery, ignoreCase = true)
            matchCategory && matchQuery
        }
}

class B2CViewModel : ViewModel() {
    private val authRepository: AuthRepository by lazy { AuthRepository() }
    private val homeRepository: HomeRepository by lazy { HomeRepository() }
    private val cartRepository: CartRepository by lazy { CartRepository() }
    private val userRepository: UserRepository by lazy { UserRepository() }
    private val orderRepository: OrderRepository by lazy { OrderRepository() }
    private val tokenManager: TokenManager by lazy { TokenManager.getInstance() }
    private val _uiState = MutableStateFlow(B2CUiState())
    val uiState: StateFlow<B2CUiState> = _uiState.asStateFlow()

    init {
        // Tự động tải dữ liệu Trang chủ từ API Backend (có fallback dữ liệu mẫu)
        loadHomeData()

        // Kiểm tra phiên đăng nhập và tính hợp lệ của token / user khi mở app
        validateSessionOnAppStart()

        // Lắng nghe sự kiện đăng xuất khi Refresh Token hết hạn từ TokenAuthenticator
        try {
            viewModelScope.launch {
                TokenManager.getInstance().isLoggedInState.collect { loggedIn ->
                    if (!loggedIn) {
                        _uiState.update {
                            it.copy(
                                isLoggedIn = false,
                                showSignInScreen = true,
                                serverCartItems = emptyList(),
                                serverCartCount = null
                            )
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Trường hợp chạy trong Preview hoặc Unit Test chưa khởi tạo context
        }
    }

    /**
     * Nạp dữ liệu Trang chủ (Danh mục, Sản phẩm bán chạy, Gợi ý hôm nay, Banners, Giỏ hàng)
     */
    fun loadHomeData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isHomeRefreshing = isRefresh,
                    isHomeLoading = !isRefresh && it.homeSuggestedProducts.isEmpty()
                )
            }

            val result = homeRepository.loadInitialHomeData()
            result.onSuccess { data ->
                _uiState.update { state ->
                    state.copy(
                        homeCategories = if (data.categories.isNotEmpty()) data.categories else state.homeCategories,
                        homeBestSellers = if (data.bestSellerProducts.isNotEmpty()) data.bestSellerProducts else state.homeBestSellers,
                        homeSuggestedProducts = if (data.suggestedProducts.isNotEmpty()) data.suggestedProducts else state.homeSuggestedProducts,
                        homeBanners = if (data.banners.isNotEmpty()) data.banners else state.homeBanners,
                        serverCartCount = if (state.isLoggedIn) data.cartCount else null,
                        isLastSuggestedPage = data.isLastSuggestedPage,
                        homeSuggestedPage = 1,
                        isHomeLoading = false,
                        isHomeRefreshing = false
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        isHomeLoading = false,
                        isHomeRefreshing = false
                    )
                }
            }
        }
    }

    /**
     * Nạp thêm sản phẩm gợi ý khi người dùng cuộn xuống dưới cùng (Infinite Scroll)
     */
    fun loadMoreSuggestedProducts() {
        val state = _uiState.value
        if (state.isLoadingMoreSuggested || state.isLastSuggestedPage) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMoreSuggested = true) }

            val currentPage = _uiState.value.homeSuggestedPage
            val (newItems, isLast) = homeRepository.loadMoreSuggested(page = currentPage, size = 10)

            _uiState.update { current ->
                val combined = if (newItems.isNotEmpty()) {
                    current.homeSuggestedProducts + newItems
                } else {
                    current.homeSuggestedProducts
                }
                current.copy(
                    homeSuggestedProducts = combined,
                    homeSuggestedPage = currentPage + 1,
                    isLastSuggestedPage = isLast,
                    isLoadingMoreSuggested = false
                )
            }
        }
    }

    /**
     * Cập nhật badge giỏ hàng từ API Backend
     */
    fun refreshCartBadgeCount() {
        viewModelScope.launch {
            try {
                if (_uiState.value.isLoggedIn) {
                    val count = homeRepository.getCartItemCount()
                    _uiState.update { it.copy(serverCartCount = count) }
                } else {
                    _uiState.update { it.copy(serverCartCount = null) }
                }
            } catch (_: Exception) {}
        }
    }

    /**
     * Kiểm tra phiên đăng nhập khi người dùng vừa mở app:
     * - Nếu chưa từng đăng nhập hoặc không có token: Đẩy ra trang đăng nhập (showSignInScreen = true).
     * - Nếu có token: Gọi API GET /api/v1/users/me để xác thực người dùng.
     *   + Nếu Access Token hết hạn, TokenAuthenticator sẽ tự động kích hoạt POST /api/v1/auth/refresh-token.
     *   + Nếu lấy được token mới thành công: phiên tiếp tục hợp lệ, cập nhật thông tin user.
     *   + Nếu Token hết hạn mà KHÔNG lấy được token mới (refresh token hết hạn/lỗi) hoặc User không tồn tại (401, 404):
     *     Xóa auth và đẩy ra trang đăng nhập ngay lập tức. Không để người dùng có token hết hạn hoặc user không tồn tại được ở trong app!
     */
    fun validateSessionOnAppStart() {
        val tokenManager = try {
            TokenManager.getInstance()
        } catch (_: Exception) {
            return
        }

        if (!tokenManager.isLoggedIn()) {
            _uiState.update {
                it.copy(
                    isLoggedIn = false,
                    showSignInScreen = true,
                    isCheckingAuth = false
                )
            }
            return
        }

        _uiState.update { it.copy(isCheckingAuth = true) }

        viewModelScope.launch {
            val result = authRepository.validateCurrentUser()
            result.onSuccess { profileDto ->
                val mappedProfile = profileDto.toDomain()
                tokenManager.saveUserProfile(mappedProfile)
                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
                        showSignInScreen = false,
                        isCheckingAuth = false,
                        userProfile = mappedProfile
                    )
                }
                refreshCartBadgeCount()
                loadCartItems()
                loadOrders()
            }.onFailure { error ->
                val errorMsg = error.message ?: ""
                val isAuthOrUserNotFound = errorMsg.contains("401") ||
                        errorMsg.contains("404") ||
                        errorMsg.contains("403") ||
                        !tokenManager.isLoggedIn() ||
                        tokenManager.isTokenExpired()

                if (isAuthOrUserNotFound) {
                    tokenManager.clearAuth()
                    _uiState.update {
                        it.copy(
                            isLoggedIn = false,
                            showSignInScreen = true,
                            isCheckingAuth = false,
                            serverCartItems = emptyList(),
                            serverCartCount = null,
                            currentToast = createToastData(
                                "Phiên đăng nhập đã hết hạn hoặc tài khoản không tồn tại. Vui lòng đăng nhập lại.",
                                ToastType.WARNING
                            )
                        )
                    }
                } else {
                    // Nếu lỗi mạng thuần túy nhưng access token chưa hết hạn theo JWT
                    if (!tokenManager.isTokenExpired()) {
                        _uiState.update {
                            it.copy(
                                isLoggedIn = true,
                                showSignInScreen = false,
                                isCheckingAuth = false,
                                userProfile = tokenManager.getUserProfile()
                            )
                        }
                        refreshCartBadgeCount()
                    } else {
                        tokenManager.clearAuth()
                        _uiState.update {
                            it.copy(
                                isLoggedIn = false,
                                showSignInScreen = true,
                                isCheckingAuth = false,
                                serverCartItems = emptyList(),
                                serverCartCount = null,
                                currentToast = createToastData(
                                    "Token hết hạn và không thể kết nối máy chủ. Vui lòng đăng nhập lại.",
                                    ToastType.WARNING
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Nạp danh sách giỏ hàng của người dùng hiện tại từ API (GET /api/v1/cart)
     */
    fun loadCartItems(isRefresh: Boolean = false) {
        if (!_uiState.value.isLoggedIn) {
            _uiState.update { it.copy(serverCartItems = emptyList(), isCartLoading = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCartLoading = !isRefresh && it.serverCartItems.isEmpty(),
                    isCartRefreshing = isRefresh,
                    cartErrorMessage = null
                )
            }

            val result = cartRepository.getCartItems()
            result.onSuccess { items ->
                _uiState.update {
                    it.copy(
                        serverCartItems = items,
                        serverCartCount = items.sumOf { item -> item.quantity },
                        isCartLoading = false,
                        isCartRefreshing = false,
                        cartErrorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isCartLoading = false,
                        isCartRefreshing = false,
                        cartErrorMessage = error.localizedMessage
                    )
                }
            }
        }
    }

    /**
     * Chọn hoặc bỏ chọn một sản phẩm trong giỏ hàng (PATCH /api/v1/cart/items/{id})
     */
    fun toggleCartItemSelection(itemId: Long, isSelected: Boolean) {
        _uiState.update { state ->
            val updated = state.serverCartItems.map {
                if (it.id == itemId) it.copy(isSelected = isSelected) else it
            }
            state.copy(serverCartItems = updated)
        }

        viewModelScope.launch {
            val result = cartRepository.updateCartItem(id = itemId, isSelected = isSelected)
            result.onFailure { error ->
                showErrorToast("Không thể cập nhật chọn sản phẩm: ${error.message}")
                loadCartItems()
            }
        }
    }

    /**
     * Chọn hoặc bỏ chọn tất cả sản phẩm của một Shop trong giỏ hàng
     */
    fun toggleShopItemsSelection(sellerId: Long?, shopName: String?, selectAll: Boolean) {
        val itemsToUpdate = _uiState.value.serverCartItems.filter {
            (sellerId != null && it.sellerId == sellerId) || (shopName != null && it.shopName == shopName)
        }

        _uiState.update { state ->
            val updated = state.serverCartItems.map {
                if ((sellerId != null && it.sellerId == sellerId) || (shopName != null && it.shopName == shopName)) {
                    it.copy(isSelected = selectAll)
                } else it
            }
            state.copy(serverCartItems = updated)
        }

        viewModelScope.launch {
            itemsToUpdate.forEach { item ->
                if (item.isSelected != selectAll) {
                    cartRepository.updateCartItem(id = item.id, isSelected = selectAll)
                }
            }
        }
    }

    /**
     * Chọn hoặc bỏ chọn toàn bộ sản phẩm trong giỏ hàng
     */
    fun toggleAllCartItemsSelection(selectAll: Boolean) {
        val itemsToUpdate = _uiState.value.serverCartItems

        _uiState.update { state ->
            val updated = state.serverCartItems.map { it.copy(isSelected = selectAll) }
            state.copy(serverCartItems = updated)
        }

        viewModelScope.launch {
            itemsToUpdate.forEach { item ->
                if (item.isSelected != selectAll) {
                    cartRepository.updateCartItem(id = item.id, isSelected = selectAll)
                }
            }
        }
    }

    /**
     * Cập nhật số lượng của sản phẩm trong giỏ hàng (PATCH /api/v1/cart/items/{id})
     */
    fun updateCartItemQuantity(itemId: Long, newQuantity: Int) {
        if (newQuantity < 1) {
            removeCartItem(itemId)
            return
        }

        _uiState.update { state ->
            val updated = state.serverCartItems.map {
                if (it.id == itemId) {
                    val unitPrice = it.priceSnapshot ?: 0.0
                    it.copy(quantity = newQuantity, lineTotal = unitPrice * newQuantity)
                } else it
            }
            state.copy(
                serverCartItems = updated,
                serverCartCount = updated.sumOf { it.quantity }
            )
        }

        viewModelScope.launch {
            val result = cartRepository.updateCartItem(id = itemId, quantity = newQuantity)
            result.onSuccess { updatedItem ->
                _uiState.update { state ->
                    val list = state.serverCartItems.map { if (it.id == itemId) updatedItem else it }
                    state.copy(serverCartItems = list, serverCartCount = list.sumOf { it.quantity })
                }
            }.onFailure { error ->
                showErrorToast("Không thể cập nhật số lượng: ${error.message}")
                loadCartItems()
            }
        }
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng (DELETE /api/v1/cart/items/{id})
     */
    fun removeCartItem(itemId: Long) {
        _uiState.update { state ->
            val updated = state.serverCartItems.filterNot { it.id == itemId }
            state.copy(
                serverCartItems = updated,
                serverCartCount = updated.sumOf { it.quantity },
                currentToast = createToastData("Đã xóa sản phẩm khỏi giỏ hàng", ToastType.INFO)
            )
        }

        viewModelScope.launch {
            val result = cartRepository.removeCartItem(itemId)
            result.onFailure { error ->
                showErrorToast("Không thể xóa sản phẩm: ${error.message}")
                loadCartItems()
            }
        }
    }

    /**
     * Xóa toàn bộ các sản phẩm đang được chọn khỏi giỏ hàng (DELETE /api/v1/cart/items)
     */
    fun deleteSelectedCartItems() {
        val selectedIds = _uiState.value.serverCartItems.filter { it.isSelected }.map { it.id }
        if (selectedIds.isEmpty()) {
            showErrorToast("Vui lòng chọn sản phẩm cần xóa")
            return
        }

        _uiState.update { state ->
            val updated = state.serverCartItems.filterNot { it.isSelected }
            state.copy(
                serverCartItems = updated,
                serverCartCount = updated.sumOf { it.quantity },
                currentToast = createToastData("Đã xóa ${selectedIds.size} sản phẩm khỏi giỏ hàng", ToastType.INFO)
            )
        }

        viewModelScope.launch {
            val result = cartRepository.bulkRemoveCartItems(selectedIds)
            result.onFailure { error ->
                showErrorToast("Lỗi xóa sản phẩm: ${error.message}")
                loadCartItems()
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(currentTab = tabIndex) }
        if (tabIndex == 1) {
            loadBrowserCategories()
        } else if (tabIndex == 2) {
            loadCartItems()
        } else if (tabIndex == 3) {
            loadOrders()
        } else if (tabIndex == 4) {
            loadUserProfile()
        }
    }

    fun setPageState(state: PageState) {
        _uiState.update { it.copy(pageState = state) }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        val match = _uiState.value.browserCategories.find {
            it.name.equals(category, ignoreCase = true) ||
            it.slug.contains(category, ignoreCase = true)
        }
        if (match != null) {
            selectBrowserCategory(match)
        }
    }

    /**
     * Nạp danh sách danh mục từ Database qua API cho Màn hình Duyệt danh mục (Cột trái)
     */
    fun loadBrowserCategories(autoSelectFirst: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBrowserCategoriesLoading = true, browserCategoryErrorMessage = null) }
            val categories = homeRepository.getCategoriesForBrowser()
            val currentSelected = _uiState.value.selectedBrowserCategory
            val toSelect = when {
                currentSelected != null && categories.any { it.id == currentSelected.id } ->
                    categories.first { it.id == currentSelected.id }
                autoSelectFirst && categories.isNotEmpty() ->
                    categories.first()
                else -> currentSelected
            }

            _uiState.update {
                it.copy(
                    browserCategories = categories,
                    selectedBrowserCategory = toSelect,
                    selectedBrowserSubCategoryId = null,
                    isBrowserCategoriesLoading = false
                )
            }

            if (toSelect != null && _uiState.value.browserProducts.isEmpty()) {
                loadBrowserProducts(toSelect.id)
            }
        }
    }

    /**
     * Người dùng click chọn một danh mục bên cột trái -> nạp sản phẩm tương ứng bên cột phải
     */
    fun selectBrowserCategory(category: CategoryDto) {
        if (_uiState.value.selectedBrowserCategory?.id == category.id && _uiState.value.browserProducts.isNotEmpty()) {
            return
        }
        _uiState.update {
            it.copy(
                selectedBrowserCategory = category,
                selectedBrowserSubCategoryId = null
            )
        }
        loadBrowserProducts(category.id)
    }

    /**
     * Người dùng click lọc theo danh mục con (hoặc "Tất cả")
     */
    fun selectBrowserSubCategory(subCategoryId: Long?) {
        _uiState.update { it.copy(selectedBrowserSubCategoryId = subCategoryId) }
        val targetCatId = subCategoryId ?: _uiState.value.selectedBrowserCategory?.id
        loadBrowserProducts(targetCatId)
    }

    /**
     * Nạp danh sách sản phẩm thuộc danh mục đang chọn từ Database qua API (Cột phải)
     */
    fun loadBrowserProducts(categoryId: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBrowserProductsLoading = true, browserCategoryErrorMessage = null) }
            val products = homeRepository.getProductsByCategory(categoryId)
            _uiState.update {
                it.copy(
                    browserProducts = products,
                    isBrowserProductsLoading = false
                )
            }
        }
    }

    fun refreshBrowserCategories() {
        val selectedId = _uiState.value.selectedBrowserSubCategoryId ?: _uiState.value.selectedBrowserCategory?.id
        loadBrowserCategories(autoSelectFirst = false)
        if (selectedId != null) {
            loadBrowserProducts(selectedId)
        }
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

        // Nếu ID là dạng số hợp lệ từ Backend, gọi API để cập nhật chi tiết đầy đủ nhất
        val numericId = product.id.toLongOrNull()
        if (numericId != null) {
            viewModelScope.launch {
                try {
                    val detail = homeRepository.getProductDetailById(numericId)
                    if (detail != null) {
                        _uiState.update { state ->
                            if (state.detailedProductScreen?.id == product.id) {
                                val priceLong = (detail.minPrice ?: product.price.toDouble()).toLong()
                                val origPriceLong = (detail.maxPrice ?: product.originalPrice?.toDouble() ?: priceLong.toDouble()).toLong()

                                val parsedImages = if (detail.images.isNotEmpty()) {
                                    detail.images.sortedBy { it.sortOrder ?: 0 }.map { it.imageUrl }
                                } else {
                                    listOfNotNull(detail.thumbnailUrl?.ifBlank { null } ?: product.imageUrl)
                                }

                                val parsedOptions = detail.options.sortedBy { it.sortOrder ?: 0 }.map { opt ->
                                    ProductOptionItem(
                                        id = opt.id,
                                        name = opt.name,
                                        sortOrder = opt.sortOrder ?: 0,
                                        values = opt.values.sortedBy { it.sortOrder ?: 0 }.map { v ->
                                            ProductOptionValueItem(
                                                id = v.id,
                                                value = v.value,
                                                imageUrl = v.imageUrl,
                                                sortOrder = v.sortOrder ?: 0
                                            )
                                        }
                                    )
                                }

                                val parsedVariants = detail.variants.map { v ->
                                    val vPrice = (v.price ?: origPriceLong.toDouble()).toLong()
                                    val vSalePrice = (v.salePrice ?: v.price ?: priceLong.toDouble()).toLong()
                                    ProductVariantItem(
                                        id = v.id,
                                        sku = v.sku,
                                        variantName = v.variantName,
                                        price = vPrice,
                                        salePrice = vSalePrice,
                                        stockQuantity = v.stockQuantity ?: 0,
                                        soldCount = v.soldCount ?: 0,
                                        imageUrl = v.imageUrl,
                                        optionValueIds = v.optionValueIds ?: emptyList(),
                                        attributes = v.attributes ?: emptyMap()
                                    )
                                }

                                val totalStock = if (parsedVariants.isNotEmpty()) {
                                    parsedVariants.sumOf { it.stockQuantity }
                                } else {
                                    product.stock
                                }

                                val enriched = state.detailedProductScreen!!.copy(
                                    name = detail.name.ifBlank { product.name },
                                    description = detail.description?.ifBlank { product.description } ?: product.description,
                                    imageUrl = detail.thumbnailUrl?.ifBlank { product.imageUrl } ?: product.imageUrl,
                                    price = priceLong,
                                    originalPrice = if (origPriceLong > priceLong) origPriceLong else product.originalPrice,
                                    shopName = detail.shopName?.ifBlank { product.shopName } ?: product.shopName,
                                    rating = (detail.ratingAvg ?: product.rating.toDouble()).toFloat(),
                                    soldCount = detail.soldCount ?: product.soldCount,
                                    stock = totalStock,
                                    images = parsedImages,
                                    options = parsedOptions,
                                    variants = parsedVariants
                                )
                                state.copy(detailedProductScreen = enriched)
                            } else {
                                state
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
        }
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
        val randomOrderCode = (100000..999999).random()
        _uiState.update { state ->
            state.copy(
                showVnpayPaymentScreen = false,
                showCheckoutScreen = false,
                completedOrderSuccess = OrderSuccessInfo(
                    orderId = "ORD-20261105-0$randomOrderCode",
                    paymentMethodName = "Cổng VNPAY",
                    isOnlinePaid = true,
                    orderTime = "05/11/2026 14:20",
                    expectedDelivery = "14 Th04 – 16 Th04",
                    totalAmount = state.totalCartPrice.takeIf { it > 0 } ?: 388200L,
                    recipientName = state.userProfile.fullName.ifBlank { "Nguyễn Văn A" },
                    recipientPhoneMasked = "090****123",
                    deliveryAddress = "Số 123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. HCM"
                )
            )
        }
        refreshCartBadgeCount()
        loadCartItems()
        loadOrders()
    }

    fun completeOrderPlacement(paymentMethod: String = "COD", sellerNote: String = "") {
        val randomOrderCode = (100000..999999).random()
        val cartItemIds = _uiState.value.serverCartItems.filter { it.isSelected }.map { it.id }

        if (cartItemIds.isNotEmpty() && _uiState.value.isLoggedIn) {
            viewModelScope.launch {
                val req = CreateOrderRequestDto(
                    cartItemIds = cartItemIds,
                    addressId = 1L,
                    paymentMethod = paymentMethod,
                    note = sellerNote.ifBlank { null }
                )
                val result = orderRepository.createOrder(req)
                result.onSuccess { created ->
                    _uiState.update { state ->
                        state.copy(
                            showCheckoutScreen = false,
                            completedOrderSuccess = OrderSuccessInfo(
                                orderId = created.orderCode ?: "ORD-20261105-0${created.id}",
                                paymentMethodName = if (paymentMethod == "VNPAY") "Cổng VNPAY" else "Thanh toán khi nhận hàng (COD)",
                                isOnlinePaid = paymentMethod == "VNPAY",
                                orderTime = created.createdAt ?: "05/11/2026 14:20",
                                expectedDelivery = "14 Th04 – 16 Th04",
                                totalAmount = created.totalAmount?.toLong() ?: 388200L,
                                recipientName = created.receiverName ?: state.userProfile.fullName.ifBlank { "Nguyễn Văn A" },
                                recipientPhoneMasked = created.receiverPhone ?: "090****123",
                                deliveryAddress = created.shippingAddress ?: "Số 123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. HCM"
                            )
                        )
                    }
                    refreshCartBadgeCount()
                    loadCartItems()
                    loadOrders()
                }.onFailure {
                    _uiState.update { state ->
                        state.copy(
                            showCheckoutScreen = false,
                            completedOrderSuccess = OrderSuccessInfo(
                                orderId = "ORD-20261105-0$randomOrderCode",
                                paymentMethodName = "Thanh toán khi nhận hàng (COD)",
                                isOnlinePaid = false,
                                orderTime = "05/11/2026 14:20",
                                expectedDelivery = "14 Th04 – 16 Th04",
                                totalAmount = state.totalCartPrice.takeIf { it > 0 } ?: 388200L,
                                recipientName = state.userProfile.fullName.ifBlank { "Nguyễn Văn A" },
                                recipientPhoneMasked = "090****123",
                                deliveryAddress = "Số 123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. HCM"
                            )
                        )
                    }
                    loadOrders()
                }
            }
        } else {
            _uiState.update { state ->
                state.copy(
                    showCheckoutScreen = false,
                    completedOrderSuccess = OrderSuccessInfo(
                        orderId = "ORD-20261105-0$randomOrderCode",
                        paymentMethodName = "Thanh toán khi nhận hàng (COD)",
                        isOnlinePaid = false,
                        orderTime = "05/11/2026 14:20",
                        expectedDelivery = "14 Th04 – 16 Th04",
                        totalAmount = state.totalCartPrice.takeIf { it > 0 } ?: 388200L,
                        recipientName = state.userProfile.fullName.ifBlank { "Nguyễn Văn A" },
                        recipientPhoneMasked = "090****123",
                        deliveryAddress = "Số 123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. HCM"
                    )
                )
            }
            loadOrders()
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
        if (navigateToOrdersTab) {
            loadOrders()
        }
    }

    fun openOrderDetailsTrackingScreen(orderId: String = "ORD-20261105-0088") {
        _uiState.update { it.copy(detailedOrderTrackingScreen = orderId, activeOrderDetail = null) }
        val numericId = orderId.toLongOrNull()
            ?: _uiState.value.orders.firstOrNull { it.orderCode == orderId || "#${it.orderCode}" == orderId }?.id
            ?: orderId.replace(Regex("[^0-9]"), "").toLongOrNull()
        if (numericId != null) {
            loadOrderDetail(numericId)
        }
    }

    fun closeOrderDetailsTrackingScreen() {
        _uiState.update { it.copy(detailedOrderTrackingScreen = null, activeOrderDetail = null) }
    }

    /**
     * Tải danh sách đơn hàng của người mua từ API Backend (GET /api/v1/buyer/orders)
     * Hỗ trợ lọc theo tab trạng thái và làm mới dữ liệu
     */
    fun loadOrders(tab: String = _uiState.value.selectedOrdersTab, isRefresh: Boolean = false) {
        if (isRefresh) {
            _uiState.update { it.copy(isOrdersRefreshing = true, ordersErrorMessage = null, selectedOrdersTab = tab) }
        } else {
            _uiState.update { it.copy(isOrdersLoading = true, ordersErrorMessage = null, selectedOrdersTab = tab) }
        }

        viewModelScope.launch {
            val backendStatus = mapTabToBackendStatus(tab)
            val result = orderRepository.getOrders(status = backendStatus, page = 0, size = 50)
            result.onSuccess { pageResponse ->
                _uiState.update { state ->
                    state.copy(
                        orders = pageResponse.content,
                        isOrdersLoading = false,
                        isOrdersRefreshing = false,
                        ordersErrorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update { state ->
                    state.copy(
                        isOrdersLoading = false,
                        isOrdersRefreshing = false,
                        ordersErrorMessage = err.message ?: "Không tải được danh sách đơn hàng"
                    )
                }
            }
        }
    }

    fun selectOrdersTab(tab: String) {
        loadOrders(tab = tab, isRefresh = false)
    }

    private fun mapTabToBackendStatus(tab: String): String? {
        return when (tab) {
            "Chờ thanh toán" -> "PENDING"
            "Đang chuẩn bị" -> "CONFIRMED"
            "Đang giao" -> "SHIPPING"
            "Đánh giá", "Đã giao" -> "DELIVERED"
            "Đã hủy" -> "CANCELLED"
            else -> null // "Tất cả"
        }
    }

    /**
     * Lấy chi tiết đơn hàng theo ID (GET /api/v1/buyer/orders/{id})
     */
    fun loadOrderDetail(orderId: Long) {
        _uiState.update { it.copy(isOrderDetailLoading = true, orderDetailErrorMessage = null) }
        viewModelScope.launch {
            val result = orderRepository.getOrderDetail(orderId)
            result.onSuccess { detail ->
                _uiState.update { state ->
                    state.copy(
                        activeOrderDetail = detail,
                        isOrderDetailLoading = false,
                        orderDetailErrorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update { state ->
                    state.copy(
                        isOrderDetailLoading = false,
                        orderDetailErrorMessage = err.message ?: "Không thể tải chi tiết đơn hàng"
                    )
                }
            }
        }
    }

    /**
     * Hủy đơn hàng (PATCH /api/v1/buyer/orders/{id}/cancel)
     */
    fun cancelOrder(orderId: Long, reason: String = "Người mua yêu cầu hủy đơn") {
        _uiState.update { it.copy(cancellingOrderId = orderId) }
        viewModelScope.launch {
            val result = orderRepository.cancelOrder(orderId, reason)
            result.onSuccess { cancelledDto ->
                showSuccessToast("Hủy đơn hàng #${cancelledDto.orderCode ?: orderId} thành công")
                loadOrders(_uiState.value.selectedOrdersTab)
                if (_uiState.value.activeOrderDetail?.id == orderId) {
                    _uiState.update { it.copy(activeOrderDetail = cancelledDto) }
                }
            }.onFailure { err ->
                showErrorToast(err.message ?: "Hủy đơn hàng thất bại")
            }
            _uiState.update { it.copy(cancellingOrderId = null) }
        }
    }

    /**
     * Lấy lại URL thanh toán VNPay cho đơn hàng PENDING chưa thanh toán (POST /api/v1/buyer/orders/{id}/pay)
     */
    fun retryOrderPayment(orderId: Long, onPaymentUrlReady: ((String) -> Unit)? = null) {
        _uiState.update { it.copy(retryingOrderId = orderId) }
        viewModelScope.launch {
            val result = orderRepository.retryPayment(orderId)
            result.onSuccess { paymentUrl ->
                showToast("Đang mở cổng thanh toán VNPay...", ToastType.INFO)
                if (onPaymentUrlReady != null) {
                    onPaymentUrlReady(paymentUrl)
                } else {
                    openVnpayPayment()
                }
            }.onFailure { err ->
                showErrorToast(err.message ?: "Không tạo được liên kết thanh toán")
            }
            _uiState.update { it.copy(retryingOrderId = null) }
        }
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
                    refreshCartBadgeCount()
                    loadCartItems()
                    loadOrders()
                    loadUserProfile()
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
                    refreshCartBadgeCount()
                    loadCartItems()
                    loadOrders()
                    loadUserProfile()
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
                serverCartCount = null,
                serverCartItems = emptyList(),
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
        if (_uiState.value.isLoggedIn) {
            _uiState.update { it.copy(showSignInScreen = false) }
        }
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

    /**
     * Nạp thông tin hồ sơ người dùng từ API Backend (GET /api/v1/users/me)
     */
    fun loadUserProfile(isRefresh: Boolean = false) {
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProfileLoading = !isRefresh) }
            val result = userRepository.getMyProfile()
            result.onSuccess { profileDto ->
                val domainProfile = profileDto.toDomain()
                tokenManager.saveUserProfile(domainProfile)
                _uiState.update {
                    it.copy(
                        userProfile = domainProfile,
                        isProfileLoading = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isProfileLoading = false) }
            }
        }
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng qua API Backend (PUT /api/v1/users/me)
     */
    fun updateUserProfile(profile: UserProfile, onComplete: ((Boolean) -> Unit)? = null) {
        if (!_uiState.value.isLoggedIn) {
            showErrorToast("Vui lòng đăng nhập để thực hiện chức năng này")
            onComplete?.invoke(false)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingProfile = true) }
            val requestDto = profile.toUpdateRequest()
            val result = userRepository.updateProfile(requestDto)
            result.onSuccess { updatedDto ->
                val updatedProfile = updatedDto.toDomain()
                tokenManager.saveUserProfile(updatedProfile)
                val msg = "Cập nhật hồ sơ cá nhân thành công!"
                _uiState.update {
                    it.copy(
                        userProfile = updatedProfile,
                        isUpdatingProfile = false,
                        showEditProfileScreen = false,
                        toastMessage = msg,
                        currentToast = createToastData(msg, ToastType.SUCCESS)
                    )
                }
                onComplete?.invoke(true)
            }.onFailure { error ->
                val errorMsg = error.message ?: "Cập nhật hồ sơ thất bại"
                _uiState.update {
                    it.copy(
                        isUpdatingProfile = false,
                        toastMessage = errorMsg,
                        currentToast = createToastData(errorMsg, ToastType.ERROR)
                    )
                }
                onComplete?.invoke(false)
            }
        }
    }

    /**
     * Tải lên ảnh đại diện mới qua API Backend (POST /api/v1/users/me/avatar)
     */
    fun uploadAvatar(
        bytes: ByteArray,
        mimeType: String = "image/jpeg",
        fileName: String = "avatar.jpg",
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        if (!_uiState.value.isLoggedIn) {
            showErrorToast("Vui lòng đăng nhập để đổi ảnh đại diện")
            onComplete?.invoke(false)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingAvatar = true) }
            val result = userRepository.uploadAvatar(bytes, mimeType, fileName)
            result.onSuccess { responseDto ->
                val newAvatarUrl = responseDto.avatarUrl
                val updated = _uiState.value.userProfile.copy(avatarUrl = newAvatarUrl)
                tokenManager.saveUserProfile(updated)
                val msg = "Cập nhật ảnh đại diện thành công!"
                _uiState.update {
                    it.copy(
                        userProfile = updated,
                        isUploadingAvatar = false,
                        toastMessage = msg,
                        currentToast = createToastData(msg, ToastType.SUCCESS)
                    )
                }
                onComplete?.invoke(true)
            }.onFailure { error ->
                val errorMsg = error.message ?: "Tải lên ảnh đại diện thất bại"
                _uiState.update {
                    it.copy(
                        isUploadingAvatar = false,
                        toastMessage = errorMsg,
                        currentToast = createToastData(errorMsg, ToastType.ERROR)
                    )
                }
                onComplete?.invoke(false)
            }
        }
    }

    /**
     * Đổi mật khẩu tài khoản qua API Backend (POST /api/v1/users/change-password)
     */
    fun changePassword(
        oldPass: String,
        newPass: String,
        confirmPass: String,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        if (!_uiState.value.isLoggedIn) {
            showErrorToast("Vui lòng đăng nhập để đổi mật khẩu")
            onComplete?.invoke(false)
            return
        }

        if (oldPass.isBlank()) {
            showErrorToast("Vui lòng nhập mật khẩu hiện tại")
            onComplete?.invoke(false)
            return
        }

        if (newPass.length < 8) {
            showErrorToast("Mật khẩu mới phải có tối thiểu 8 ký tự")
            onComplete?.invoke(false)
            return
        }

        if (newPass != confirmPass) {
            showErrorToast("Mật khẩu xác nhận không khớp")
            onComplete?.invoke(false)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isChangingPassword = true) }
            val request = ChangePasswordRequestDto(
                currentPassword = oldPass,
                newPassword = newPass,
                confirmPassword = confirmPass
            )
            val result = userRepository.changePassword(request)
            result.onSuccess {
                val msg = "Đổi mật khẩu thành công!"
                _uiState.update {
                    it.copy(
                        isChangingPassword = false,
                        showChangePasswordScreen = false,
                        toastMessage = msg,
                        currentToast = createToastData(msg, ToastType.SUCCESS)
                    )
                }
                onComplete?.invoke(true)
            }.onFailure { error ->
                val errorMsg = error.message ?: "Đổi mật khẩu thất bại"
                _uiState.update {
                    it.copy(
                        isChangingPassword = false,
                        toastMessage = errorMsg,
                        currentToast = createToastData(errorMsg, ToastType.ERROR)
                    )
                }
                onComplete?.invoke(false)
            }
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

    fun addProductToCart(
        product: ProductItem,
        color: String = "Trắng Tinh Khôi",
        size: String = "L",
        quantity: Int = 1,
        variant: ProductVariantItem? = null
    ) {
        val targetVariant = variant ?: product.variants.firstOrNull()
        val variantNameDisplay = targetVariant?.variantName
            ?: if (color.isNotBlank() && size.isNotBlank()) "$color, Size $size"
            else color.ifBlank { size }

        // Cập nhật giỏ hàng cục bộ để phản hồi ngay lập tức trên UI
        _uiState.update { state ->
            val existingIndex = state.cartItems.indexOfFirst { it.product.id == product.id }
            val updatedList = state.cartItems.toMutableList()
            if (existingIndex >= 0) {
                val existing = updatedList[existingIndex]
                updatedList[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
            } else {
                updatedList.add(CartItem(product, quantity))
            }
            val msg = "Đã thêm $quantity x ${product.name} ($variantNameDisplay) vào giỏ hàng!"
            state.copy(
                cartItems = updatedList,
                toastMessage = msg,
                currentToast = createToastData(msg, ToastType.SUCCESS)
            )
        }

        // Nếu đã đăng nhập và có variantId từ Backend, gọi API đồng bộ giỏ hàng
        val targetVariantId = targetVariant?.id
        if (_uiState.value.isLoggedIn && targetVariantId != null) {
            viewModelScope.launch {
                val result = cartRepository.addCartItem(variantId = targetVariantId, quantity = quantity)
                result.onSuccess {
                    loadCartItems()
                    refreshCartBadgeCount()
                }.onFailure { err ->
                    showErrorToast(err.message ?: "Không thể đồng bộ giỏ hàng với máy chủ")
                }
            }
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
