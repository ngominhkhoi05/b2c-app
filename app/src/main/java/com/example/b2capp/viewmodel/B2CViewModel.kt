package com.example.b2capp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b2capp.B2CApplication
import com.example.b2capp.core.model.CartItem
import com.example.b2capp.core.model.OrderSuccessInfo
import com.example.b2capp.core.model.PageState
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.core.model.ProductOptionItem
import com.example.b2capp.core.model.ProductOptionValueItem
import com.example.b2capp.core.model.ProductVariantItem
import com.example.b2capp.core.model.ReviewItem
import com.example.b2capp.core.model.ShopChatInfo
import com.example.b2capp.core.model.UserAddress
import com.example.b2capp.core.model.UserProfile
import com.example.b2capp.core.model.toDomain
import com.example.b2capp.core.model.toRequestDto
import com.example.b2capp.core.model.toUpdateRequest
import com.example.b2capp.core.ui.components.ToastData
import com.example.b2capp.core.ui.components.ToastType
import com.example.b2capp.core.ui.components.createToastData
import com.example.b2capp.data.SampleData
import com.example.b2capp.data.local.TokenManager
import com.example.b2capp.data.remote.dto.CategoryDto
import com.example.b2capp.data.remote.dto.CartItemDto
import com.example.b2capp.data.remote.dto.shopDisplayName
import com.example.b2capp.data.remote.dto.ChangePasswordRequestDto
import com.example.b2capp.data.remote.dto.CreateOrderRequestDto
import com.example.b2capp.data.remote.dto.OrderDetailDto
import com.example.b2capp.data.remote.dto.OrderItemDto
import com.example.b2capp.data.remote.dto.OrderPreviewResponseDto
import com.example.b2capp.data.remote.dto.OrderSummaryDto
import com.example.b2capp.data.remote.dto.PromotionDto
import com.example.b2capp.data.remote.dto.WishlistItemDto
import com.example.b2capp.features.order.components.OrderFilterCriteria
import com.example.b2capp.data.repository.AuthRepository
import com.example.b2capp.data.repository.CartRepository
import com.example.b2capp.data.repository.ChatRepository
import com.example.b2capp.data.repository.HomeBannerItem
import com.example.b2capp.data.repository.HomeRepository
import com.example.b2capp.data.repository.OrderRepository
import com.example.b2capp.data.repository.PaymentRepository
import com.example.b2capp.data.repository.PromotionRepository
import com.example.b2capp.data.repository.ReviewRepository
import com.example.b2capp.data.repository.UserRepository
import com.example.b2capp.data.repository.WishlistRepository
import com.example.b2capp.features.chat.CheckStatus
import com.example.b2capp.features.chat.DetailedChatMessage
import com.example.b2capp.features.chat.ShopConversation
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

data class OrderStatusCounts(
    val pendingCount: Int = 0,     // Chờ xác nhận (PENDING)
    val processingCount: Int = 0,  // Đang chuẩn bị (CONFIRMED, PACKED)
    val shippingCount: Int = 0,    // Đang giao (SHIPPING)
    val toReviewCount: Int = 0     // Đánh giá (DELIVERED, COMPLETED)
)

data class B2CUiState(
    val currentTab: Int = 0, // 0: Home, 1: Cart, 2: Design System Showcase
    val pageState: PageState = PageState.SUCCESS,
    val selectedCategory: String = "Tất cả",
    val searchQuery: String = "",
    val products: List<ProductItem> = SampleData.products,
    val cartItems: List<CartItem> = emptyList(),
    val selectedProductForSheet: ProductItem? = null,
    val detailedProductScreen: ProductItem? = null,
    val isProductDetailLoading: Boolean = false,
    val searchResultQuery: String? = null,
    val searchResults: List<ProductItem> = emptyList(),
    val isSearching: Boolean = false,
    val searchSortBy: String = "created",
    val searchSortDir: String = "desc",
    val searchTotalElements: Long = 0L,
    val searchCategoryId: Long? = null,
    val searchMinPrice: Long? = null,
    val searchMaxPrice: Long? = null,
    val searchBrand: String? = null,
    val searchMinRating: Float? = null,
    val searchSuggestions: List<String> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val showShopMessages: Boolean = false,
    val shopConversations: List<ShopConversation> = emptyList(),
    val isShopConversationsLoading: Boolean = false,
    val shopConversationsError: String? = null,
    val activeShopChat: ShopChatInfo? = null,
    val showCheckoutScreen: Boolean = false,
    val showVnpayPaymentScreen: Boolean = false,
    val pendingVnpayOrder: OrderDetailDto? = null,
    val isConfirmingVnpayPayment: Boolean = false,
    val isCompletingOrder: Boolean = false,
    val isRequestingReturn: Boolean = false,
    val completedOrderSuccess: OrderSuccessInfo? = null,
    val detailedOrderTrackingScreen: String? = null,
    val isLoggedIn: Boolean = false,
    val isAuthLoading: Boolean = false,
    val authErrorMessage: String? = null,
    val showSignInScreen: Boolean = true,
    val showSignUpScreen: Boolean = false,
    val showForgotPasswordScreen: Boolean = false,
    val showProductReviewsScreen: Boolean = false,
    val productReviews: List<ReviewItem> = emptyList(),
    val isProductReviewsLoading: Boolean = false,
    val productReviewsTotalCount: Int = 0,
    val productReviewsError: String? = null,
    val selectedReviewFilter: String = "Tất cả",
    val showWishlistScreen: Boolean = false,
    val showAddressBookScreen: Boolean = false,
    val showAddressEditorScreen: Boolean = false,
    val editingAddressForScreen: UserAddress? = null,
    val isAddressLoading: Boolean = false,
    val isAddressSaving: Boolean = false,
    val addressErrorMessage: String? = null,
    val showEditProfileScreen: Boolean = false,
    val showWriteReviewScreen: Boolean = false,
    val reviewTargetOrder: OrderDetailDto? = null,
    val reviewTargetSummary: OrderSummaryDto? = null,
    val isReviewOrderLoading: Boolean = false,
    val selectedReviewOrderItem: OrderItemDto? = null,
    val isSubmittingReview: Boolean = false,
    val isReviewImageUploading: Boolean = false,
    val showChangePasswordScreen: Boolean = false,
    val userProfile: UserProfile = UserProfile(),
    val addresses: List<UserAddress> = emptyList(),
    val selectedCheckoutAddressId: String? = null,
    val availablePromotions: List<PromotionDto> = emptyList(),
    val isPromotionsLoading: Boolean = false,
    val selectedVoucher: PromotionDto? = null,
    val orderPreview: OrderPreviewResponseDto? = null,
    val isPreviewLoading: Boolean = false,
    val isPlacingOrder: Boolean = false,
    val checkoutSelectedPaymentMethod: String = "COD",
    val checkoutSellerNote: String = "",
    val detailSelectedQuantity: Int = 1,
    val interactiveRatingDemo: Float = 4.0f,
    val toastMessage: String? = null,
    val currentToast: ToastData? = null,
    val homeCategories: List<QuickCategoryItem> = HomeRepository.getDefaultCategories(),
    val homeBestSellers: List<HomeBestSellerItem> = emptyList(),
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
    val orderFilterCriteria: OrderFilterCriteria = OrderFilterCriteria(),
    val tabReloadKey: Int = 0, // Tăng lên mỗi lần bấm một tab ở BottomBar để dựng lại toàn bộ màn hình của tab đó
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
    val browserCategoryErrorMessage: String? = null,
    val wishlistItems: List<WishlistItemDto> = emptyList(),
    val wishlistProductIds: Set<Long> = emptySet(),
    val isWishlistLoading: Boolean = false,
    val isWishlistRefreshing: Boolean = false,
    val wishlistErrorMessage: String? = null,
    val wishlistCount: Int = 0,
    val wishlistPage: Int = 0,
    val isLastWishlistPage: Boolean = false,
    val isLoadingMoreWishlist: Boolean = false,
    val orderStatusCounts: OrderStatusCounts = OrderStatusCounts(),
    val isOrdersSummaryLoading: Boolean = false
) {
    val pendingOrdersCount: Int
        get() = if (orderStatusCounts.pendingCount > 0) orderStatusCounts.pendingCount else orders.count { it.status.equals("PENDING", ignoreCase = true) }
    val processingOrdersCount: Int
        get() = if (orderStatusCounts.processingCount > 0) orderStatusCounts.processingCount else orders.count { it.status.equals("CONFIRMED", ignoreCase = true) || it.status.equals("PACKED", ignoreCase = true) }
    val shippingOrdersCount: Int
        get() = if (orderStatusCounts.shippingCount > 0) orderStatusCounts.shippingCount else orders.count { it.status.equals("SHIPPING", ignoreCase = true) }
    val toReviewOrdersCount: Int
        get() = if (orderStatusCounts.toReviewCount > 0) orderStatusCounts.toReviewCount else orders.count { it.status.equals("DELIVERED", ignoreCase = true) || it.status.equals("COMPLETED", ignoreCase = true) }
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

    val activeCheckoutAddress: UserAddress?
        get() = addresses.find { it.id == selectedCheckoutAddressId }
            ?: addresses.firstOrNull { it.isDefault }
            ?: addresses.firstOrNull()

    val checkoutCartItems: List<CartItemDto>
        get() = serverCartItems.filter { it.isSelected }

    val checkoutItemsSubtotal: Long
        get() = if (checkoutCartItems.isNotEmpty()) {
            checkoutCartItems.sumOf { (it.lineTotal ?: ((it.priceSnapshot ?: 0.0) * it.quantity)).toLong() }
        } else {
            0L
        }
}

class B2CViewModel : ViewModel() {
    private val authRepository: AuthRepository by lazy { AuthRepository() }
    private val homeRepository: HomeRepository by lazy { HomeRepository() }
    private val cartRepository: CartRepository by lazy { CartRepository() }
    private val userRepository: UserRepository by lazy { UserRepository() }
    private val orderRepository: OrderRepository by lazy { OrderRepository() }
    private val paymentRepository: PaymentRepository by lazy { PaymentRepository() }
    private val promotionRepository: PromotionRepository by lazy { PromotionRepository() }
    private val chatRepository: ChatRepository by lazy { ChatRepository() }
    private val wishlistRepository: WishlistRepository by lazy { WishlistRepository() }
    private val reviewRepository: ReviewRepository by lazy { ReviewRepository() }
    private val tokenManager: TokenManager by lazy { TokenManager.getInstance() }
    private val _uiState = MutableStateFlow(B2CUiState())
    val uiState: StateFlow<B2CUiState> = _uiState.asStateFlow()

    init {
        // Tự động tải dữ liệu Trang chủ từ API Backend (có fallback dữ liệu mẫu)
        loadHomeData()

        // Nạp lịch sử tìm kiếm từ Room FTS
        loadRecentSearches()

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
                                serverCartCount = null,
                                wishlistItems = emptyList(),
                                wishlistProductIds = emptySet(),
                                wishlistCount = 0
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
                loadAddresses()
                refreshWishlistCount()
                loadWishlist()
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
                val selectedShops = items.filter { it.isSelected }.map { it.shopDisplayName }.distinct()
                val sanitizedItems = if (selectedShops.size > 1) {
                    val keepShop = selectedShops.first()
                    items.map { item ->
                        if (item.isSelected && item.shopDisplayName != keepShop) {
                            item.copy(isSelected = false)
                        } else {
                            item
                        }
                    }
                } else {
                    items
                }

                _uiState.update {
                    it.copy(
                        serverCartItems = sanitizedItems,
                        serverCartCount = sanitizedItems.sumOf { item -> item.quantity },
                        isCartLoading = false,
                        isCartRefreshing = false,
                        cartErrorMessage = null
                    )
                }

                if (selectedShops.size > 1) {
                    val keepShop = selectedShops.first()
                    items.filter { it.isSelected && it.shopDisplayName != keepShop }.forEach { item ->
                        cartRepository.updateCartItem(id = item.id, isSelected = false)
                    }
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
     * Nếu tích chọn sản phẩm này, tự động bỏ chọn các sản phẩm thuộc các shop khác.
     */
    fun toggleCartItemSelection(itemId: Long, isSelected: Boolean) {
        val targetItem = _uiState.value.serverCartItems.firstOrNull { it.id == itemId }
        val targetShop = targetItem?.shopDisplayName

        val otherShopSelectedItems = if (isSelected && targetShop != null) {
            _uiState.value.serverCartItems.filter { other ->
                other.isSelected && other.id != itemId && other.shopDisplayName != targetShop
            }
        } else {
            emptyList()
        }

        _uiState.update { state ->
            val updated = state.serverCartItems.map {
                when {
                    it.id == itemId -> it.copy(isSelected = isSelected)
                    isSelected && otherShopSelectedItems.any { other -> other.id == it.id } -> it.copy(isSelected = false)
                    else -> it
                }
            }
            state.copy(serverCartItems = updated)
        }

        viewModelScope.launch {
            otherShopSelectedItems.forEach { other ->
                cartRepository.updateCartItem(id = other.id, isSelected = false)
            }
            val result = cartRepository.updateCartItem(id = itemId, isSelected = isSelected)
            result.onFailure { error ->
                showErrorToast("Không thể cập nhật chọn sản phẩm: ${error.message}")
                loadCartItems()
            }
        }
    }

    /**
     * Đổi sang chọn một sản phẩm của shop mới, đồng thời bỏ chọn tất cả các sản phẩm đã chọn của shop cũ
     */
    fun switchSelectedCartItem(newItemId: Long) {
        val currentSelected = _uiState.value.serverCartItems.filter { it.isSelected && it.id != newItemId }

        _uiState.update { state ->
            val updated = state.serverCartItems.map { item ->
                when {
                    item.id == newItemId -> item.copy(isSelected = true)
                    item.isSelected -> item.copy(isSelected = false)
                    else -> item
                }
            }
            state.copy(serverCartItems = updated)
        }

        viewModelScope.launch {
            currentSelected.forEach { oldItem ->
                cartRepository.updateCartItem(id = oldItem.id, isSelected = false)
            }
            val result = cartRepository.updateCartItem(id = newItemId, isSelected = true)
            result.onFailure { error ->
                showErrorToast("Không thể cập nhật chọn sản phẩm: ${error.message}")
                loadCartItems()
            }
        }
    }

    /**
     * Chọn hoặc bỏ chọn tất cả sản phẩm của một Shop trong giỏ hàng.
     * Khi selectAll = true, tự động bỏ chọn toàn bộ sản phẩm của các shop khác.
     */
    fun toggleShopItemsSelection(sellerId: Long?, shopName: String?, selectAll: Boolean) {
        val isTargetShop: (CartItemDto) -> Boolean = { item ->
            (sellerId != null && item.sellerId == sellerId) || (shopName != null && item.shopName == shopName) || (shopName != null && item.shopDisplayName == shopName)
        }
        val itemsToUpdate = _uiState.value.serverCartItems.filter { isTargetShop(it) }
        val otherSelectedItems = if (selectAll) {
            _uiState.value.serverCartItems.filter { it.isSelected && !isTargetShop(it) }
        } else {
            emptyList()
        }

        _uiState.update { state ->
            val updated = state.serverCartItems.map {
                if (isTargetShop(it)) {
                    it.copy(isSelected = selectAll)
                } else if (selectAll && it.isSelected) {
                    it.copy(isSelected = false)
                } else it
            }
            state.copy(serverCartItems = updated)
        }

        viewModelScope.launch {
            otherSelectedItems.forEach { item ->
                cartRepository.updateCartItem(id = item.id, isSelected = false)
            }
            itemsToUpdate.forEach { item ->
                if (item.isSelected != selectAll) {
                    cartRepository.updateCartItem(id = item.id, isSelected = selectAll)
                }
            }
        }
    }

    /**
     * Đổi sang chọn tất cả sản phẩm của shop mới, đồng thời bỏ chọn tất cả sản phẩm của shop cũ
     */
    fun switchSelectedShop(sellerId: Long?, shopName: String?) {
        toggleShopItemsSelection(sellerId = sellerId, shopName = shopName, selectAll = true)
    }

    /**
     * Chọn hoặc bỏ chọn toàn bộ sản phẩm trong giỏ hàng
     * Vì chỉ cho phép chọn sản phẩm trong cùng 1 shop:
     * - Khi bỏ chọn (selectAll = false): Bỏ chọn toàn bộ sản phẩm
     * - Khi chọn tất cả (selectAll = true): Chọn toàn bộ sản phẩm của shop hiện tại (hoặc shop đầu tiên)
     */
    fun toggleAllCartItemsSelection(selectAll: Boolean) {
        if (!selectAll) {
            val selectedItems = _uiState.value.serverCartItems.filter { it.isSelected }
            _uiState.update { state ->
                val updated = state.serverCartItems.map { it.copy(isSelected = false) }
                state.copy(serverCartItems = updated)
            }
            viewModelScope.launch {
                selectedItems.forEach { item ->
                    cartRepository.updateCartItem(id = item.id, isSelected = false)
                }
            }
            return
        }

        val allItems = _uiState.value.serverCartItems
        if (allItems.isEmpty()) return

        // Tìm shop đang có sản phẩm được chọn (nếu có), hoặc chọn shop đầu tiên
        val currentSelectedShopItem = allItems.firstOrNull { it.isSelected } ?: allItems.first()
        switchSelectedShop(
            sellerId = currentSelectedShopItem.sellerId,
            shopName = currentSelectedShopItem.shopDisplayName
        )
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
            loadAddresses()
        }
    }

    /**
     * Reload lại toàn bộ trang khi bấm một tab ở BottomBar (Trang chủ, Danh mục, Giỏ hàng, Đơn mua, Tài khoản):
     * - Tăng tabReloadKey để màn hình của tab được dựng lại từ đầu (xóa ô tìm kiếm, cuộn về đầu,
     *   đóng dialog/bottom sheet...) và LaunchedEffect của màn hình tự gọi lại API
     * - Với các màn hình chỉ tải khi dữ liệu trống (Trang chủ, Danh mục) thì chủ động làm mới dữ liệu
     */
    fun reloadTab(tabIndex: Int) {
        _uiState.update { state ->
            val base = state.copy(currentTab = tabIndex, tabReloadKey = state.tabReloadKey + 1)
            when (tabIndex) {
                // Danh mục: xóa lựa chọn hiện tại -> CategoriesScreen tự tải lại danh mục và chọn danh mục đầu tiên
                1 -> base.copy(
                    selectedBrowserCategory = null,
                    selectedBrowserSubCategoryId = null,
                    browserProducts = emptyList(),
                    browserCategoryErrorMessage = null
                )
                // Đơn mua: đặt lại tab trạng thái về "Tất cả" và xóa bộ lọc nâng cao
                3 -> base.copy(
                    selectedOrdersTab = "Tất cả",
                    orderFilterCriteria = OrderFilterCriteria(),
                    ordersErrorMessage = null
                )
                else -> base
            }
        }
        when (tabIndex) {
            0 -> loadHomeData(isRefresh = true) // HomeScreen chỉ tự tải khi chưa có dữ liệu
            4 -> loadAddresses()                 // AccountScreen tự tải hồ sơ + tổng hợp đơn hàng
            // 1, 2, 3: màn hình tự gọi API trong LaunchedEffect khi được dựng lại
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

    fun openSearchResults(
        query: String,
        sortBy: String = "created",
        sortDir: String = "desc",
        categoryId: Long? = null,
        minPrice: Long? = null,
        maxPrice: Long? = null,
        brand: String? = null,
        minRating: Float? = null
    ) {
        val effectiveQuery = query.trim()
        val safeSortBy = when (sortBy.lowercase().trim()) {
            "price", "sold", "rating", "created", "name" -> sortBy.lowercase().trim()
            else -> "created"
        }
        val safeSortDir = if (sortDir.equals("asc", ignoreCase = true)) "asc" else "desc"
        var cleanMinPrice = minPrice?.takeIf { it >= 0 }
        var cleanMaxPrice = maxPrice?.takeIf { it >= 0 }
        if (cleanMinPrice != null && cleanMaxPrice != null && cleanMinPrice > cleanMaxPrice) {
            val temp = cleanMinPrice
            cleanMinPrice = cleanMaxPrice
            cleanMaxPrice = temp
        }
        val safeRating = minRating?.takeIf { it in 0.0f..5.0f }
        val safeBrand = brand?.trim()?.ifBlank { null }

        _uiState.update {
            it.copy(
                searchResultQuery = effectiveQuery,
                searchSortBy = safeSortBy,
                searchSortDir = safeSortDir,
                searchCategoryId = categoryId,
                searchMinPrice = cleanMinPrice,
                searchMaxPrice = cleanMaxPrice,
                searchBrand = safeBrand,
                searchMinRating = safeRating,
                isSearching = true
            )
        }
        performSearch(
            query = effectiveQuery,
            sortBy = safeSortBy,
            sortDir = safeSortDir,
            categoryId = categoryId,
            minPrice = cleanMinPrice,
            maxPrice = cleanMaxPrice,
            brand = safeBrand,
            minRating = safeRating
        )
    }

    fun performSearch(
        query: String? = _uiState.value.searchResultQuery,
        sortBy: String = _uiState.value.searchSortBy,
        sortDir: String = _uiState.value.searchSortDir,
        categoryId: Long? = _uiState.value.searchCategoryId,
        minPrice: Long? = _uiState.value.searchMinPrice,
        maxPrice: Long? = _uiState.value.searchMaxPrice,
        brand: String? = _uiState.value.searchBrand,
        minRating: Float? = _uiState.value.searchMinRating,
        page: Int = 0
    ) {
        // Chuẩn hóa và đặt giá trị mặc định để tránh lỗi khi thiếu trường
        val safeQuery = query?.trim()?.ifBlank { null }
        val safeSortBy = when (sortBy.lowercase().trim()) {
            "price", "sold", "rating", "created", "name" -> sortBy.lowercase().trim()
            else -> "created"
        }
        val safeSortDir = if (sortDir.equals("asc", ignoreCase = true)) "asc" else "desc"
        val safePage = maxOf(0, page)
        val safeRating = minRating?.takeIf { it in 0.0f..5.0f }
        val safeBrand = brand?.trim()?.ifBlank { null }

        var cleanMinPrice = minPrice?.takeIf { it >= 0 }
        var cleanMaxPrice = maxPrice?.takeIf { it >= 0 }
        if (cleanMinPrice != null && cleanMaxPrice != null && cleanMinPrice > cleanMaxPrice) {
            val temp = cleanMinPrice
            cleanMinPrice = cleanMaxPrice
            cleanMaxPrice = temp
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            val (products, total) = homeRepository.searchProducts(
                query = safeQuery,
                page = safePage,
                size = 30,
                sortBy = safeSortBy,
                sortDir = safeSortDir,
                categoryId = categoryId,
                minPrice = cleanMinPrice,
                maxPrice = cleanMaxPrice,
                brand = safeBrand,
                minRating = safeRating
            )
            _uiState.update {
                it.copy(
                    searchResults = products,
                    searchTotalElements = total,
                    searchSortBy = safeSortBy,
                    searchSortDir = safeSortDir,
                    searchCategoryId = categoryId,
                    searchMinPrice = cleanMinPrice,
                    searchMaxPrice = cleanMaxPrice,
                    searchBrand = safeBrand,
                    searchMinRating = safeRating,
                    isSearching = false
                )
            }
        }
    }

    fun closeSearchResults() {
        _uiState.update {
            it.copy(
                searchResultQuery = null,
                searchResults = emptyList(),
                searchTotalElements = 0L,
                isSearching = false
            )
        }
    }

    /**
     * Tải danh sách lịch sử tìm kiếm từ Room Database (FTS).
     */
    fun loadRecentSearches() {
        viewModelScope.launch {
            val localRepo = B2CApplication.getLocalSearchRepo()
            val recent = localRepo?.getRecentSearches() ?: emptyList()
            _uiState.update { it.copy(recentSearches = recent) }
        }
    }

    /**
     * Lấy gợi ý từ khóa thông minh (Autocomplete / Typeahead) theo thời gian thực từ Room FTS.
     */
    fun onSearchQueryInputChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            val localRepo = B2CApplication.getLocalSearchRepo()
            val suggestions = if (query.isNotBlank()) {
                localRepo?.getSuggestions(query) ?: emptyList()
            } else {
                emptyList()
            }
            _uiState.update { it.copy(searchSuggestions = suggestions) }
        }
    }

    /**
     * Xóa một từ khóa khỏi lịch sử tìm kiếm.
     */
    fun deleteRecentSearch(keyword: String) {
        viewModelScope.launch {
            val localRepo = B2CApplication.getLocalSearchRepo()
            localRepo?.deleteSearch(keyword)
            loadRecentSearches()
        }
    }

    /**
     * Xóa sạch toàn bộ lịch sử tìm kiếm trên thiết bị.
     */
    fun clearRecentSearches() {
        viewModelScope.launch {
            val localRepo = B2CApplication.getLocalSearchRepo()
            localRepo?.clearHistory()
            _uiState.update { it.copy(recentSearches = emptyList()) }
        }
    }

    fun openShopMessages() {
        _uiState.update { it.copy(showShopMessages = true) }
        loadShopConversations()
    }

    fun closeShopMessages() {
        _uiState.update { it.copy(showShopMessages = false) }
    }

    /**
     * Tải danh sách hội thoại tin nhắn với các Shop từ Backend API
     */
    fun loadShopConversations(forceRefresh: Boolean = false) {
        if (!forceRefresh && _uiState.value.shopConversations.isNotEmpty()) {
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isShopConversationsLoading = true, shopConversationsError = null) }
            val result = chatRepository.getConversations()
            result.fold(
                onSuccess = { dtoList ->
                    val mapped = dtoList
                        .filter { !it.lastMessage.isNullOrBlank() }
                        .map { chatRepository.mapDtoToShopConversation(it) }
                    _uiState.update {
                        it.copy(
                            shopConversations = mapped,
                            isShopConversationsLoading = false,
                            shopConversationsError = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            shopConversations = emptyList(),
                            isShopConversationsLoading = false,
                            shopConversationsError = if (_uiState.value.isLoggedIn) error.message else null
                        )
                    }
                }
            )
        }
    }

    fun openShopChat(
        shopName: String = "Shop",
        product: ProductItem? = null,
        conversationId: Long? = null,
        sellerId: Long? = null,
        avatarLetter: String? = null,
        avatarBg: androidx.compose.ui.graphics.Color? = null,
        avatarUrl: String? = null,
        isMall: Boolean = false
    ) {
        val letter = avatarLetter ?: chatRepository.extractInitials(shopName)
        val bg = avatarBg ?: chatRepository.generateAvatarColor(shopName)
        _uiState.update { state ->
            val p = product
            val resolvedSellerId = sellerId ?: p?.sellerId
            state.copy(
                activeShopChat = ShopChatInfo(
                    conversationId = conversationId,
                    sellerId = resolvedSellerId,
                    shopName = shopName,
                    avatarLetter = letter,
                    avatarBg = bg,
                    avatarUrl = avatarUrl,
                    isMall = isMall,
                    isOnline = true,
                    product = p
                )
            )
        }
    }

    suspend fun startOrGetConversation(sellerId: Long): Long? {
        return chatRepository.startConversation(sellerId).getOrNull()?.id
    }

    suspend fun loadMessagesForConversation(
        conversationId: Long,
        shopName: String,
        avatarLetter: String? = null,
        avatarBg: androidx.compose.ui.graphics.Color? = null
    ): List<DetailedChatMessage> {
        // Cập nhật trạng thái đã đọc cục bộ ngay lập tức để người dùng thấy unreadCount về 0
        _uiState.update { state ->
            val updated = state.shopConversations.map { conv ->
                if (conv.numericId == conversationId) {
                    conv.copy(
                        unreadCount = 0,
                        isTimeRed = false,
                        isLastMessageBold = false,
                        checkStatus = CheckStatus.DOUBLE_CHECK_READ
                    )
                } else conv
            }
            state.copy(shopConversations = updated)
        }

        // Gọi API tải danh sách tin nhắn (backend tự động đánh dấu đã đọc trong DB)
        val result = chatRepository.getMessages(conversationId)
        return result.fold(
            onSuccess = { dtos ->
                val currentUserId = _uiState.value.userProfile?.id
                dtos.map { dto ->
                    chatRepository.mapDtoToDetailedChatMessage(
                        dto = dto,
                        currentUserId = currentUserId,
                        shopName = shopName,
                        avatarLetter = avatarLetter,
                        avatarBg = avatarBg
                    )
                }
            },
            onFailure = { emptyList() }
        )
    }

    fun markShopConversationAsRead(conversationId: Long) {
        viewModelScope.launch {
            _uiState.update { state ->
                val updated = state.shopConversations.map { conv ->
                    if (conv.numericId == conversationId) {
                        conv.copy(
                            unreadCount = 0,
                            isTimeRed = false,
                            isLastMessageBold = false,
                            checkStatus = CheckStatus.DOUBLE_CHECK_READ
                        )
                    } else conv
                }
                state.copy(shopConversations = updated)
            }
            chatRepository.markConversationAsRead(conversationId)
        }
    }

    fun sendChatMessage(
        conversationId: Long?,
        sellerId: Long?,
        content: String,
        messageType: String = "TEXT",
        attachmentUrl: String? = null,
        refProductId: Long? = null,
        refOrderId: Long? = null,
        onMessageSent: ((com.example.b2capp.data.remote.dto.ChatMessageDto) -> Unit)? = null
    ) {
        if (content.isBlank() && attachmentUrl.isNullOrBlank()) return
        viewModelScope.launch {
            val result = chatRepository.sendMessage(
                conversationId = conversationId,
                sellerId = sellerId,
                content = content,
                messageType = messageType,
                attachmentUrl = attachmentUrl,
                refProductId = refProductId,
                refOrderId = refOrderId
            )
            result.onSuccess { msgDto ->
                onMessageSent?.invoke(msgDto)
                loadShopConversations(forceRefresh = true)
            }
            _uiState.update { state ->
                val displayMsg = content.ifBlank { if (messageType == "IMAGE") "[Hình ảnh]" else "Đã gửi tệp đính kèm" }
                val updatedConversations = state.shopConversations.map { conv ->
                    if ((conversationId != null && conv.numericId == conversationId) ||
                        (sellerId != null && conv.sellerId == sellerId)) {
                        conv.copy(
                            lastMessage = displayMsg,
                            time = "Vừa xong",
                            isTimeRed = false,
                            isLastMessageBold = false
                        )
                    } else {
                        conv
                    }
                }
                state.copy(shopConversations = updatedConversations)
            }
        }
    }

    fun closeShopChat() {
        _uiState.update { it.copy(activeShopChat = null) }
        loadShopConversations(forceRefresh = true)
    }

    fun openProductDetailsScreen(product: ProductItem) {
        val numericId = product.id.toLongOrNull()
        val isRealProduct = numericId != null
        val needsFetch = isRealProduct && product.options.isEmpty()
        _uiState.update {
            it.copy(
                detailedProductScreen = product,
                isProductDetailLoading = needsFetch,
                productReviews = emptyList(),
                isProductReviewsLoading = isRealProduct,
                productReviewsTotalCount = product.ratingCount,
                productReviewsError = null,
                selectedReviewFilter = "Tất cả"
            )
        }

        // Tải reviews từ API
        if (numericId != null) {
            loadProductReviews(numericId)
        }

        // Tải danh sách voucher/khuyến mãi khả dụng từ API
        loadCheckoutPromotions(product.price.toDouble())

        // Nếu ID là dạng số hợp lệ từ Backend, gọi API để cập nhật chi tiết đầy đủ nhất
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
                                    ratingCount = detail.ratingCount ?: product.ratingCount,
                                    soldCount = detail.soldCount ?: product.soldCount,
                                    stock = totalStock,
                                    images = parsedImages,
                                    options = parsedOptions,
                                    variants = parsedVariants,
                                    sellerId = detail.sellerId ?: product.sellerId
                                )
                                state.copy(detailedProductScreen = enriched, isProductDetailLoading = false)
                            } else {
                                state.copy(isProductDetailLoading = false)
                            }
                        }
                    } else {
                        _uiState.update { it.copy(isProductDetailLoading = false) }
                    }
                } catch (_: Exception) {
                    _uiState.update { it.copy(isProductDetailLoading = false) }
                }
            }
        }
    }

    fun loadProductReviews(productId: Long, ratingFilter: Int? = null) {
        _uiState.update { it.copy(isProductReviewsLoading = true, productReviewsError = null) }
        viewModelScope.launch {
            val result = reviewRepository.getProductReviews(productId, ratingFilter)
            result.onSuccess { (reviews, total) ->
                _uiState.update { state ->
                    state.copy(
                        productReviews = reviews,
                        productReviewsTotalCount = if (ratingFilter == null) total else state.productReviewsTotalCount,
                        isProductReviewsLoading = false,
                        productReviewsError = null
                    )
                }
            }.onFailure { err ->
                _uiState.update { state ->
                    state.copy(
                        isProductReviewsLoading = false,
                        productReviewsError = err.message
                    )
                }
            }
        }
    }

    fun setReviewFilter(filterName: String) {
        _uiState.update { it.copy(selectedReviewFilter = filterName) }
        val product = _uiState.value.detailedProductScreen ?: return
        val numericId = product.id.toLongOrNull() ?: return
        val ratingInt = when {
            filterName.contains("5") -> 5
            filterName.contains("4") -> 4
            filterName.contains("3") -> 3
            filterName.contains("2") -> 2
            filterName.contains("1") -> 1
            else -> null
        }
        loadProductReviews(numericId, ratingInt)
    }

    fun closeProductDetailsScreen() {
        _uiState.update {
            it.copy(
                detailedProductScreen = null,
                isProductDetailLoading = false,
                productReviews = emptyList(),
                productReviewsTotalCount = 0,
                isProductReviewsLoading = false
            )
        }
    }

    fun computeDiscountAmount(promo: PromotionDto, subtotal: Double): Double {
        val minOrder = promo.minOrderValue ?: 0.0
        if (subtotal < minOrder) return 0.0

        val discountType = promo.discountType?.uppercase() ?: "PERCENT"
        val value = promo.discountValue ?: 0.0
        var discount = when (discountType) {
            "PERCENT" -> (subtotal * value / 100.0)
            "FIXED" -> value
            "FREESHIP" -> 30000.0
            else -> 0.0
        }

        promo.maxDiscountAmount?.let { maxCap ->
            if (maxCap > 0 && discount > maxCap) {
                discount = maxCap
            }
        }

        if (discountType != "FREESHIP" && discount > subtotal) {
            discount = subtotal
        }

        return discount
    }

    fun findDeepestDiscountVoucher(promotions: List<PromotionDto>, subtotal: Double): PromotionDto? {
        val eligiblePromos = promotions.filter { promo ->
            val isActive = promo.status == null || promo.status.equals("ACTIVE", ignoreCase = true)
            val minOrder = promo.minOrderValue ?: 0.0
            val hasStock = (promo.remainingQuantity ?: 1) > 0
            isActive && hasStock && subtotal >= minOrder
        }

        return eligiblePromos
            .map { promo -> Pair(promo, computeDiscountAmount(promo, subtotal)) }
            .filter { it.second > 0.0 }
            .maxByOrNull { it.second }
            ?.first
    }

    fun openCheckoutScreen() {
        val subtotal = _uiState.value.checkoutItemsSubtotal.toDouble()
        _uiState.update {
            it.copy(
                showCheckoutScreen = true,
                orderPreview = null,
                isPlacingOrder = false
            )
        }

        if (_uiState.value.isLoggedIn) {
            loadAddresses()
            loadCheckoutPromotions(subtotal)
        }
    }

    fun closeCheckoutScreen() {
        _uiState.update { it.copy(showCheckoutScreen = false) }
    }

    fun selectCheckoutAddress(addressId: String) {
        _uiState.update { it.copy(selectedCheckoutAddressId = addressId) }
        refreshOrderPreview()
    }

    fun setCheckoutPaymentMethod(method: String) {
        _uiState.update { it.copy(checkoutSelectedPaymentMethod = method) }
    }

    fun setCheckoutSellerNote(note: String) {
        _uiState.update { it.copy(checkoutSellerNote = note) }
    }

    fun loadCheckoutPromotions(subtotal: Double = _uiState.value.checkoutItemsSubtotal.toDouble()) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPromotionsLoading = true) }
            val result = promotionRepository.getAvailablePromotions(_uiState.value.isLoggedIn)
            result.onSuccess { promos ->
                val bestVoucher = findDeepestDiscountVoucher(promos, subtotal)
                _uiState.update {
                    it.copy(
                        availablePromotions = promos,
                        isPromotionsLoading = false,
                        selectedVoucher = bestVoucher
                    )
                }
                refreshOrderPreview()
            }.onFailure {
                _uiState.update { it.copy(isPromotionsLoading = false) }
                refreshOrderPreview()
            }
        }
    }

    fun selectVoucher(voucher: PromotionDto?) {
        _uiState.update { it.copy(selectedVoucher = voucher) }
        refreshOrderPreview()
    }

    fun applyVoucherByCode(code: String, onComplete: ((Boolean, String) -> Unit)? = null) {
        val trimmed = code.trim().uppercase()
        if (trimmed.isBlank()) {
            val msg = "Vui lòng nhập mã giảm giá"
            showErrorToast(msg)
            onComplete?.invoke(false, msg)
            return
        }

        val subtotal = _uiState.value.checkoutItemsSubtotal.toDouble()
        viewModelScope.launch {
            val result = promotionRepository.validatePromotion(trimmed, subtotal)
            result.onSuccess { validation ->
                if (validation.success) {
                    val matchingPromo = _uiState.value.availablePromotions.find { it.code.equals(trimmed, ignoreCase = true) }
                        ?: PromotionDto(
                            id = 0L,
                            code = validation.code ?: trimmed,
                            name = validation.name ?: trimmed,
                            discountType = validation.discountType ?: "PERCENT",
                            discountValue = validation.discountAmount ?: 0.0,
                            maxDiscountAmount = validation.discountAmount,
                            minOrderValue = 0.0
                        )
                    _uiState.update { it.copy(selectedVoucher = matchingPromo) }
                    refreshOrderPreview()
                    val msg = validation.message ?: "Áp dụng mã giảm giá thành công!"
                    showToast(msg)
                    onComplete?.invoke(true, msg)
                } else {
                    val msg = validation.message ?: "Mã giảm giá không hợp lệ"
                    showErrorToast(msg)
                    onComplete?.invoke(false, msg)
                }
            }.onFailure { error ->
                val msg = error.message ?: "Không thể kiểm tra mã giảm giá"
                showErrorToast(msg)
                onComplete?.invoke(false, msg)
            }
        }
    }

    fun refreshOrderPreview() {
        val cartItemIds = _uiState.value.checkoutCartItems.map { it.id }
        if (cartItemIds.isEmpty() || !_uiState.value.isLoggedIn) return

        val addressId = _uiState.value.activeCheckoutAddress?.id?.toLongOrNull()
        val voucherCode = _uiState.value.selectedVoucher?.code

        viewModelScope.launch {
            _uiState.update { it.copy(isPreviewLoading = true) }
            val result = orderRepository.previewOrder(
                cartItemIds = cartItemIds,
                addressId = addressId,
                voucherCode = voucherCode
            )
            result.onSuccess { preview ->
                _uiState.update {
                    it.copy(
                        orderPreview = preview,
                        isPreviewLoading = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isPreviewLoading = false) }
            }
        }
    }

    fun openVnpayPayment() {
        _uiState.update { it.copy(showVnpayPaymentScreen = true) }
    }

    fun closeVnpayPayment() {
        val pending = _uiState.value.pendingVnpayOrder
        _uiState.update { it.copy(showVnpayPaymentScreen = false) }
        if (pending != null) {
            _uiState.update { it.copy(showCheckoutScreen = false, pendingVnpayOrder = null) }
            showToast("Đơn hàng #${pending.orderCode ?: pending.id} đã được lưu. Bạn có thể thanh toán sau trong mục Đơn mua.")
            refreshCartBadgeCount()
            loadCartItems()
            loadOrders()
            loadAccountOrderSummary()
        }
    }

    fun completeVnpayPayment() {
        confirmVnpayPayment()
    }

    /**
     * Xác nhận hoàn tất thanh toán VNPAY bằng cách gọi API callback thật (POST /api/v1/payments/callback)
     * Backend chuyển trạng thái đơn hàng sang PAID và CONFIRMED.
     */
    fun confirmVnpayPayment() {
        val pending = _uiState.value.pendingVnpayOrder
        if (pending == null) {
            showErrorToast("Không tìm thấy thông tin đơn hàng thanh toán")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isConfirmingVnpayPayment = true) }
            val txnId = "VNPAY_${System.currentTimeMillis()}"
            val result = paymentRepository.confirmPayment(
                orderId = pending.id,
                transactionId = txnId,
                status = "SUCCESS",
                provider = "VNPAY"
            )
            result.onSuccess {
                val recipient = pending.receiverName ?: _uiState.value.activeCheckoutAddress?.recipientName ?: ""
                val phone = pending.receiverPhone ?: _uiState.value.activeCheckoutAddress?.phoneNumber ?: ""
                val addressStr = pending.shippingAddress ?: _uiState.value.activeCheckoutAddress?.fullAddress ?: ""

                _uiState.update { state ->
                    state.copy(
                        isConfirmingVnpayPayment = false,
                        showVnpayPaymentScreen = false,
                        showCheckoutScreen = false,
                        selectedVoucher = null,
                        orderPreview = null,
                        pendingVnpayOrder = null,
                        completedOrderSuccess = OrderSuccessInfo(
                            rawOrderId = pending.id,
                            orderId = pending.orderCode ?: "ORD-${pending.id}",
                            paymentMethodName = "Cổng VNPAY",
                            isOnlinePaid = true,
                            orderTime = pending.createdAt ?: "",
                            expectedDelivery = "Nhận hàng sau 2-4 ngày (Đồng kiểm khi nhận)",
                            totalAmount = pending.totalAmount?.toLong() ?: 0L,
                            recipientName = recipient,
                            recipientPhoneMasked = phone,
                            deliveryAddress = addressStr,
                            paymentUrl = pending.paymentUrl
                        )
                    )
                }
                showToast("Thanh toán đơn hàng #${pending.orderCode ?: pending.id} thành công!")
                refreshCartBadgeCount()
                loadCartItems()
                loadOrders()
                loadAccountOrderSummary()
            }.onFailure { error ->
                _uiState.update { it.copy(isConfirmingVnpayPayment = false) }
                val errorMsg = error.message ?: "Xác nhận thanh toán VNPAY không thành công!"
                showErrorToast(errorMsg)
            }
        }
    }

    fun completeOrderPlacement(paymentMethod: String = "COD", sellerNote: String = "") {
        val cartItemIds = _uiState.value.checkoutCartItems.map { it.id }
        if (cartItemIds.isEmpty()) {
            showErrorToast("Vui lòng chọn ít nhất 1 sản phẩm trong giỏ hàng để thanh toán")
            return
        }

        if (!_uiState.value.isLoggedIn) {
            showErrorToast("Vui lòng đăng nhập để tiến hành đặt hàng")
            return
        }

        val targetAddress = _uiState.value.activeCheckoutAddress
        val targetAddressId = targetAddress?.id?.toLongOrNull()
        if (targetAddressId == null) {
            showErrorToast("Vui lòng chọn hoặc thêm địa chỉ nhận hàng trước khi đặt hàng")
            return
        }

        val voucherCode = _uiState.value.selectedVoucher?.code
        val isVnpay = paymentMethod.equals("VNPAY", ignoreCase = true)

        viewModelScope.launch {
            _uiState.update { it.copy(isPlacingOrder = true) }
            val req = CreateOrderRequestDto(
                cartItemIds = cartItemIds,
                addressId = targetAddressId,
                paymentMethod = if (isVnpay) "VNPAY" else "COD",
                voucherCode = voucherCode,
                note = sellerNote.ifBlank { null }
            )
            val result = orderRepository.createOrder(req)
            result.onSuccess { created ->
                if (isVnpay) {
                    // Nếu là VNPAY: Đơn hàng được tạo ở trạng thái chờ thanh toán
                    // Mở cổng thanh toán VNPAY với dữ liệu thật từ Backend
                    _uiState.update { state ->
                        state.copy(
                            isPlacingOrder = false,
                            pendingVnpayOrder = created,
                            showVnpayPaymentScreen = true
                        )
                    }
                    showToast("Đã khởi tạo đơn hàng #${created.orderCode ?: created.id}. Vui lòng hoàn tất thanh toán VNPAY.")
                } else {
                    // Nếu là COD: Đặt hàng thành công ngay lập tức
                    val recipient = created.receiverName ?: targetAddress.recipientName
                    val phone = created.receiverPhone ?: targetAddress.phoneNumber
                    val addressStr = created.shippingAddress ?: targetAddress.fullAddress

                    _uiState.update { state ->
                        state.copy(
                            isPlacingOrder = false,
                            showCheckoutScreen = false,
                            showVnpayPaymentScreen = false,
                            selectedVoucher = null,
                            orderPreview = null,
                            pendingVnpayOrder = null,
                            completedOrderSuccess = OrderSuccessInfo(
                                rawOrderId = created.id,
                                orderId = created.orderCode ?: "ORD-${created.id}",
                                paymentMethodName = "Thanh toán khi nhận hàng (COD)",
                                isOnlinePaid = false,
                                orderTime = created.createdAt ?: "",
                                expectedDelivery = "Nhận hàng sau 2-4 ngày (Đồng kiểm khi nhận)",
                                totalAmount = created.totalAmount?.toLong() ?: 0L,
                                recipientName = recipient,
                                recipientPhoneMasked = phone,
                                deliveryAddress = addressStr,
                                paymentUrl = null
                            )
                        )
                    }
                    refreshCartBadgeCount()
                    loadCartItems()
                    loadOrders()
                    loadAccountOrderSummary()
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isPlacingOrder = false) }
                val errorMsg = error.message ?: "Đặt hàng không thành công. Vui lòng thử lại!"
                showErrorToast(errorMsg)
            }
        }
    }

    fun closeOrderSuccessScreen(navigateToOrdersTab: Boolean = false) {
        val successInfo = _uiState.value.completedOrderSuccess
        val targetOrderKey = successInfo?.rawOrderId?.toString() ?: successInfo?.orderId
        _uiState.update { state ->
            state.copy(
                completedOrderSuccess = null,
                detailedOrderTrackingScreen = if (navigateToOrdersTab && !targetOrderKey.isNullOrBlank()) targetOrderKey else null,
                currentTab = if (navigateToOrdersTab) 3 else 0
            )
        }
        if (navigateToOrdersTab) {
            loadOrders()
            if (!targetOrderKey.isNullOrBlank()) {
                openOrderDetailsTrackingScreen(targetOrderKey)
            }
        }
    }

    fun openOrderDetailsTrackingScreen(orderId: String) {
        if (orderId.isBlank()) return
        _uiState.update {
            it.copy(
                detailedOrderTrackingScreen = orderId,
                activeOrderDetail = null,
                isOrderDetailLoading = true,
                orderDetailErrorMessage = null
            )
        }
        val numericId = orderId.toLongOrNull()
            ?: _uiState.value.orders.firstOrNull { it.orderCode == orderId || "#${it.orderCode}" == orderId }?.id
        if (numericId != null) {
            loadOrderDetail(numericId)
        } else {
            viewModelScope.launch {
                val foundId = orderRepository.getOrders(page = 0, size = 50).getOrNull()?.content
                    ?.firstOrNull { it.orderCode == orderId || "#${it.orderCode}" == orderId }?.id
                if (foundId != null) {
                    loadOrderDetail(foundId)
                } else {
                    _uiState.update {
                        it.copy(
                            isOrderDetailLoading = false,
                            orderDetailErrorMessage = "Không tìm thấy thông tin đơn hàng"
                        )
                    }
                }
            }
        }
    }

    fun closeOrderDetailsTrackingScreen() {
        _uiState.update { it.copy(detailedOrderTrackingScreen = null, activeOrderDetail = null) }
    }

    /**
     * Tải danh sách đơn hàng của người mua từ API Backend (GET /api/v1/buyer/orders)
     * Hỗ trợ lọc theo tab trạng thái, bộ lọc nâng cao và làm mới dữ liệu
     */
    fun loadOrders(
        tab: String = _uiState.value.selectedOrdersTab,
        filter: OrderFilterCriteria = _uiState.value.orderFilterCriteria,
        isRefresh: Boolean = false
    ) {
        if (isRefresh) {
            _uiState.update { it.copy(isOrdersRefreshing = true, ordersErrorMessage = null, selectedOrdersTab = tab, orderFilterCriteria = filter) }
        } else {
            _uiState.update { it.copy(isOrdersLoading = true, ordersErrorMessage = null, selectedOrdersTab = tab, orderFilterCriteria = filter) }
        }

        viewModelScope.launch {
            val backendStatus = if (filter.orderStatus != "Tất cả") {
                mapTabToBackendStatus(filter.orderStatus)
            } else {
                mapTabToBackendStatus(tab)
            }

            val paymentMethodParam = when (filter.paymentMethod) {
                "COD" -> "COD"
                "VNPay" -> "VNPAY"
                else -> null
            }

            val fromDateParam = when (filter.dateRange) {
                "30 ngày qua" -> java.time.LocalDateTime.now().minusDays(30).toString()
                "3 tháng qua" -> java.time.LocalDateTime.now().minusDays(90).toString()
                "6 tháng qua" -> java.time.LocalDateTime.now().minusDays(180).toString()
                "Năm nay" -> java.time.LocalDateTime.of(java.time.LocalDate.now().year, 1, 1, 0, 0).toString()
                else -> null
            }

            val sortByParam = when (filter.sortBy) {
                "Giá: Cao → Thấp", "Giá: Thấp → Cao" -> "totalAmount"
                else -> "createdAt"
            }

            val sortDirParam = when (filter.sortBy) {
                "Cũ nhất", "Giá: Thấp → Cao" -> "asc"
                else -> "desc"
            }

            val result = orderRepository.getOrders(
                status = backendStatus,
                paymentMethod = paymentMethodParam,
                fromDate = fromDateParam,
                minAmount = filter.effectiveMinPrice,
                maxAmount = filter.effectiveMaxPrice,
                sortBy = sortByParam,
                sortDir = sortDirParam,
                page = 0,
                size = 50
            )
            result.onSuccess { pageResponse ->
                val list = pageResponse.content
                _uiState.update { state ->
                    val updatedCounts = if (backendStatus == null || tab == "Tất cả") {
                        calculateOrderStatusCounts(list)
                    } else {
                        state.orderStatusCounts
                    }
                    state.copy(
                        orders = list,
                        orderStatusCounts = updatedCounts,
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

    fun applyOrderFilter(filter: OrderFilterCriteria) {
        val newTab = if (filter.orderStatus != "Tất cả") filter.orderStatus else _uiState.value.selectedOrdersTab
        _uiState.update { it.copy(orderFilterCriteria = filter, selectedOrdersTab = newTab) }
        loadOrders(tab = newTab, filter = filter, isRefresh = true)
        showToast("Đã áp dụng bộ lọc đơn hàng!")
    }

    fun resetOrderFilter() {
        val emptyFilter = OrderFilterCriteria()
        _uiState.update { it.copy(orderFilterCriteria = emptyFilter) }
        loadOrders(tab = _uiState.value.selectedOrdersTab, filter = emptyFilter, isRefresh = true)
        showToast("Đã đặt lại bộ lọc!")
    }

    /**
     * Tải tổng hợp số lượng đơn hàng theo từng trạng thái cho màn hình Tài khoản (GET /api/v1/buyer/orders)
     */
    fun loadAccountOrderSummary() {
        if (!_uiState.value.isLoggedIn) return
        viewModelScope.launch {
            _uiState.update { it.copy(isOrdersSummaryLoading = true) }
            val result = orderRepository.getOrders(status = null, page = 0, size = 100)
            result.onSuccess { pageResponse ->
                val list = pageResponse.content
                val counts = calculateOrderStatusCounts(list)
                _uiState.update { state ->
                    state.copy(
                        orderStatusCounts = counts,
                        isOrdersSummaryLoading = false,
                        orders = if (state.orders.isEmpty() || state.selectedOrdersTab == "Tất cả") list else state.orders
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isOrdersSummaryLoading = false) }
            }
        }
    }

    private fun calculateOrderStatusCounts(list: List<OrderSummaryDto>): OrderStatusCounts {
        return OrderStatusCounts(
            pendingCount = list.count { it.status.equals("PENDING", ignoreCase = true) },
            processingCount = list.count { it.status.equals("CONFIRMED", ignoreCase = true) || it.status.equals("PACKED", ignoreCase = true) },
            shippingCount = list.count { it.status.equals("SHIPPING", ignoreCase = true) },
            toReviewCount = list.count { it.status.equals("DELIVERED", ignoreCase = true) || it.status.equals("COMPLETED", ignoreCase = true) }
        )
    }

    fun selectOrdersTab(tab: String) {
        _uiState.update { it.copy(selectedOrdersTab = tab, orderFilterCriteria = it.orderFilterCriteria.copy(orderStatus = "Tất cả")) }
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
                loadAccountOrderSummary()
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
                val currentDetail = _uiState.value.activeOrderDetail
                    ?: orderRepository.getOrderDetail(orderId).getOrNull()
                _uiState.update { state ->
                    state.copy(
                        pendingVnpayOrder = currentDetail?.copy(paymentUrl = paymentUrl),
                        showVnpayPaymentScreen = true
                    )
                }
                if (onPaymentUrlReady != null) {
                    onPaymentUrlReady(paymentUrl)
                }
            }.onFailure { err ->
                showErrorToast(err.message ?: "Không tạo được liên kết thanh toán")
            }
            _uiState.update { it.copy(retryingOrderId = null) }
        }
    }

    /**
     * Xác nhận người mua đã nhận được hàng và hoàn tất đơn hàng (PATCH /api/v1/buyer/orders/{id}/complete)
     */
    fun confirmOrderDelivery(orderId: Long) {
        _uiState.update { it.copy(isCompletingOrder = true) }
        viewModelScope.launch {
            val result = orderRepository.completeOrder(orderId)
            result.onSuccess { completed ->
                showSuccessToast("Cảm ơn bạn đã xác nhận đã nhận hàng thành công!")
                _uiState.update { state ->
                    state.copy(
                        isCompletingOrder = false,
                        activeOrderDetail = completed
                    )
                }
                loadOrders(_uiState.value.selectedOrdersTab)
                loadAccountOrderSummary()
            }.onFailure { err ->
                _uiState.update { it.copy(isCompletingOrder = false) }
                showErrorToast(err.message ?: "Xác nhận nhận hàng thất bại")
            }
        }
    }

    /**
     * Gửi yêu cầu trả hàng / hoàn tiền cho đơn hàng đã nhận (POST /api/v1/buyer/orders/{id}/return-request)
     */
    fun requestOrderReturn(orderId: Long, reason: String, description: String? = null) {
        _uiState.update { it.copy(isRequestingReturn = true) }
        viewModelScope.launch {
            val result = orderRepository.requestReturn(orderId, reason, description)
            result.onSuccess { returned ->
                showSuccessToast("Yêu cầu trả hàng / hoàn tiền đã được gửi thành công!")
                _uiState.update { state ->
                    state.copy(
                        isRequestingReturn = false,
                        activeOrderDetail = returned
                    )
                }
                loadOrders(_uiState.value.selectedOrdersTab)
                loadAccountOrderSummary()
            }.onFailure { err ->
                _uiState.update { it.copy(isRequestingReturn = false) }
                showErrorToast(err.message ?: "Gửi yêu cầu trả hàng thất bại")
            }
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
                    loadAccountOrderSummary()
                    loadUserProfile()
                    loadAddresses()
                    refreshWishlistCount()
                    loadWishlist()
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
                    loadAccountOrderSummary()
                    loadUserProfile()
                    loadAddresses()
                    refreshWishlistCount()
                    loadWishlist()
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
        val product = _uiState.value.detailedProductScreen
        val numericId = product?.id?.toLongOrNull()
        if (numericId != null && _uiState.value.productReviews.isEmpty()) {
            loadProductReviews(numericId)
        }
        _uiState.update { it.copy(showProductReviewsScreen = true) }
    }

    fun closeProductReviewsScreen() {
        _uiState.update { it.copy(showProductReviewsScreen = false) }
    }

    fun openWishlist() {
        _uiState.update { it.copy(showWishlistScreen = true, detailedProductScreen = null) }
        loadWishlist(isRefresh = true)
    }

    fun closeWishlist() {
        _uiState.update { it.copy(showWishlistScreen = false) }
    }

    // ==========================================
    // WISHLIST (SẢN PHẨM YÊU THÍCH)
    // ==========================================

    fun loadWishlist(isRefresh: Boolean = false) {
        if (!_uiState.value.isLoggedIn) {
            _uiState.update {
                it.copy(
                    wishlistItems = emptyList(),
                    wishlistProductIds = emptySet(),
                    isWishlistLoading = false,
                    isWishlistRefreshing = false,
                    wishlistCount = 0,
                    wishlistPage = 0,
                    isLastWishlistPage = true,
                    isLoadingMoreWishlist = false
                )
            }
            return
        }

        _uiState.update {
            if (isRefresh) it.copy(isWishlistRefreshing = true, wishlistErrorMessage = null)
            else it.copy(isWishlistLoading = true, wishlistErrorMessage = null)
        }

        viewModelScope.launch {
            val result = wishlistRepository.getWishlist(page = 0, size = 20)
            result.onSuccess { pageResponse ->
                val items = pageResponse.content
                _uiState.update { state ->
                    state.copy(
                        wishlistItems = items,
                        wishlistProductIds = items.map { it.productId }.toSet(),
                        wishlistCount = pageResponse.totalElements.toInt(),
                        wishlistPage = 0,
                        isLastWishlistPage = pageResponse.last || items.size >= pageResponse.totalElements,
                        isWishlistLoading = false,
                        isWishlistRefreshing = false,
                        isLoadingMoreWishlist = false,
                        wishlistErrorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isWishlistLoading = false,
                        isWishlistRefreshing = false,
                        isLoadingMoreWishlist = false,
                        wishlistErrorMessage = error.message ?: "Không thể tải danh sách yêu thích"
                    )
                }
            }
        }
    }

    fun loadMoreWishlist() {
        val state = _uiState.value
        if (!state.isLoggedIn || state.isLoadingMoreWishlist || state.isLastWishlistPage) return

        val nextPage = state.wishlistPage + 1
        _uiState.update { it.copy(isLoadingMoreWishlist = true) }

        viewModelScope.launch {
            val result = wishlistRepository.getWishlist(page = nextPage, size = 20)
            result.onSuccess { pageResponse ->
                val newItems = pageResponse.content
                _uiState.update { current ->
                    val combined = (current.wishlistItems + newItems).distinctBy { it.productId }
                    current.copy(
                        wishlistItems = combined,
                        wishlistProductIds = combined.map { it.productId }.toSet(),
                        wishlistCount = pageResponse.totalElements.toInt(),
                        wishlistPage = nextPage,
                        isLastWishlistPage = pageResponse.last || newItems.isEmpty() || combined.size >= pageResponse.totalElements,
                        isLoadingMoreWishlist = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoadingMoreWishlist = false) }
            }
        }
    }

    fun refreshWishlistCount() {
        if (!_uiState.value.isLoggedIn) return
        viewModelScope.launch {
            wishlistRepository.getWishlistCount().onSuccess { count ->
                _uiState.update { it.copy(wishlistCount = count.toInt()) }
            }
        }
    }

    fun removeFromWishlist(productId: Long) {
        val previousItems = _uiState.value.wishlistItems
        _uiState.update { state ->
            val updated = state.wishlistItems.filterNot { it.productId == productId }
            state.copy(
                wishlistItems = updated,
                wishlistProductIds = updated.map { it.productId }.toSet(),
                wishlistCount = updated.size
            )
        }

        viewModelScope.launch {
            val result = wishlistRepository.removeFromWishlist(productId)
            result.onSuccess {
                showToast("Đã xóa khỏi danh sách yêu thích")
            }.onFailure { error ->
                showErrorToast("Không thể xóa sản phẩm: ${error.message}")
                _uiState.update { state ->
                    state.copy(
                        wishlistItems = previousItems,
                        wishlistProductIds = previousItems.map { it.productId }.toSet(),
                        wishlistCount = previousItems.size
                    )
                }
            }
        }
    }

    fun removeMultipleFromWishlist(productIds: List<Long>) {
        if (productIds.isEmpty()) return
        val previousItems = _uiState.value.wishlistItems
        _uiState.update { state ->
            val updated = state.wishlistItems.filterNot { productIds.contains(it.productId) }
            state.copy(
                wishlistItems = updated,
                wishlistProductIds = updated.map { it.productId }.toSet(),
                wishlistCount = updated.size
            )
        }

        viewModelScope.launch {
            var anyError = false
            for (id in productIds) {
                wishlistRepository.removeFromWishlist(id).onFailure { anyError = true }
            }
            if (anyError) {
                showErrorToast("Một số sản phẩm chưa được xóa, đang làm mới danh sách...")
                loadWishlist(true)
            } else {
                showToast("Đã xóa ${productIds.size} sản phẩm khỏi danh sách yêu thích")
            }
        }
    }

    fun toggleWishlist(productId: Long, productName: String? = null) {
        if (!_uiState.value.isLoggedIn) {
            showErrorToast("Vui lòng đăng nhập để lưu sản phẩm yêu thích")
            _uiState.update { it.copy(showSignInScreen = true) }
            return
        }

        val wasWishlisted = _uiState.value.wishlistProductIds.contains(productId)
        _uiState.update { state ->
            val newSet = state.wishlistProductIds.toMutableSet()
            if (wasWishlisted) {
                newSet.remove(productId)
                state.copy(
                    wishlistProductIds = newSet,
                    wishlistItems = state.wishlistItems.filterNot { it.productId == productId },
                    wishlistCount = (state.wishlistCount - 1).coerceAtLeast(0)
                )
            } else {
                newSet.add(productId)
                state.copy(
                    wishlistProductIds = newSet,
                    wishlistCount = state.wishlistCount + 1
                )
            }
        }

        viewModelScope.launch {
            val result = wishlistRepository.toggleWishlist(productId)
            result.onSuccess { response ->
                val nameDisplay = if (!productName.isNullOrBlank()) " \"$productName\"" else ""
                if (response.wishlisted) {
                    showToast("Đã thêm$nameDisplay vào danh sách yêu thích")
                } else {
                    showToast("Đã xóa$nameDisplay khỏi danh sách yêu thích")
                }
                loadWishlist(isRefresh = true)
            }.onFailure { error ->
                showErrorToast("Không thể cập nhật danh sách yêu thích: ${error.message}")
                loadWishlist(isRefresh = true)
            }
        }
    }

    fun openAddressBook() {
        _uiState.update { it.copy(showAddressBookScreen = true) }
        loadAddresses(isRefresh = true)
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
        loadUserProfile()
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
     * Tải lên hình ảnh lên Cloudinary qua API generic POST /api/v1/upload
     */
    fun uploadImage(
        bytes: ByteArray,
        mimeType: String = "image/jpeg",
        fileName: String = "image.jpg",
        purpose: String = "uploads",
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val result = userRepository.uploadImage(bytes, mimeType, fileName, purpose)
            result.onSuccess { responseDto ->
                val url = responseDto.secureUrl ?: responseDto.url ?: ""
                onSuccess(url)
            }.onFailure { error ->
                val errorMsg = error.message ?: "Tải ảnh lên thất bại"
                showErrorToast(errorMsg)
                onError(errorMsg)
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

        if (newPass == oldPass) {
            showErrorToast("Mật khẩu mới không được trùng với mật khẩu hiện tại")
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
                val rawMsg = error.message ?: "Đổi mật khẩu thất bại"
                val errorMsg = when {
                    rawMsg.contains("Current password is incorrect", ignoreCase = true) -> "Mật khẩu hiện tại không chính xác"
                    rawMsg.contains("New password must be different", ignoreCase = true) -> "Mật khẩu mới không được trùng với mật khẩu hiện tại"
                    rawMsg.contains("do not match", ignoreCase = true) -> "Mật khẩu xác nhận không khớp"
                    else -> rawMsg
                }
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

    /**
     * Mở màn hình Viết Đánh Giá cho đơn hàng từ danh sách Đơn Mua
     */
    fun openWriteReviewForOrder(order: OrderSummaryDto) {
        _uiState.update {
            it.copy(
                showWriteReviewScreen = true,
                reviewTargetSummary = order,
                reviewTargetOrder = null,
                selectedReviewOrderItem = null,
                isReviewOrderLoading = true
            )
        }
        viewModelScope.launch {
            val result = orderRepository.getOrderDetail(order.id)
            result.onSuccess { detail ->
                val items = detail.items ?: emptyList()
                val firstUnreviewed = items.firstOrNull { it.isReviewed != true } ?: items.firstOrNull()
                _uiState.update {
                    it.copy(
                        reviewTargetOrder = detail,
                        selectedReviewOrderItem = firstUnreviewed,
                        isReviewOrderLoading = false
                    )
                }
            }.onFailure { _ ->
                // Fallback nếu không tải được chi tiết đơn: dựng OrderDetail giả từ summary
                val fallbackItem = OrderItemDto(
                    id = null,
                    productName = order.firstProductName ?: "Sản phẩm B2C",
                    variantName = order.firstVariantName,
                    imageUrl = order.firstProductThumbnail,
                    quantity = 1,
                    unitPrice = order.totalAmount,
                    isReviewed = false
                )
                val fallbackDetail = OrderDetailDto(
                    id = order.id,
                    orderCode = order.orderCode,
                    status = order.status,
                    items = listOf(fallbackItem)
                )
                _uiState.update {
                    it.copy(
                        reviewTargetOrder = fallbackDetail,
                        selectedReviewOrderItem = fallbackItem,
                        isReviewOrderLoading = false
                    )
                }
            }
        }
    }

    /**
     * Mở màn hình Viết Đánh Giá từ chi tiết đơn hàng (OrderDetailDto)
     */
    fun openWriteReviewForOrderDetail(orderDetail: OrderDetailDto, targetItem: OrderItemDto? = null) {
        val items = orderDetail.items ?: emptyList()
        val chosen = targetItem ?: items.firstOrNull { it.isReviewed != true } ?: items.firstOrNull()
        _uiState.update {
            it.copy(
                showWriteReviewScreen = true,
                reviewTargetOrder = orderDetail,
                selectedReviewOrderItem = chosen,
                isReviewOrderLoading = false
            )
        }
    }

    /**
     * Chọn sản phẩm trong đơn để viết đánh giá
     */
    fun selectReviewOrderItem(item: OrderItemDto) {
        _uiState.update { it.copy(selectedReviewOrderItem = item) }
    }

    fun closeWriteReview() {
        _uiState.update {
            it.copy(
                showWriteReviewScreen = false,
                reviewTargetOrder = null,
                reviewTargetSummary = null,
                selectedReviewOrderItem = null,
                isReviewOrderLoading = false,
                isSubmittingReview = false
            )
        }
    }

    /**
     * Gửi đánh giá sản phẩm lên Backend: POST /api/v1/buyer/reviews
     */
    fun submitProductReview(
        orderItemId: Long,
        rating: Int,
        comment: String,
        imageUrls: List<String> = emptyList(),
        isAnonymous: Boolean = false,
        onSuccess: (() -> Unit)? = null
    ) {
        if (!_uiState.value.isLoggedIn) {
            showErrorToast("Vui lòng đăng nhập để gửi đánh giá")
            return
        }

        if (comment.isBlank()) {
            showErrorToast("Vui lòng chia sẻ thêm cảm nhận của bạn về sản phẩm")
            return
        }

        _uiState.update { it.copy(isSubmittingReview = true) }

        viewModelScope.launch {
            val jsonImageUrls = if (imageUrls.isNotEmpty()) {
                "[" + imageUrls.joinToString(",") { "\"$it\"" } + "]"
            } else {
                null
            }

            val result = reviewRepository.submitReview(
                orderItemId = orderItemId,
                rating = rating,
                comment = comment.trim(),
                imageUrls = jsonImageUrls,
                isAnonymous = isAnonymous
            )

            result.onSuccess {
                // Đánh dấu item này đã được review trong state chi tiết đơn hiện tại
                val currentDetail = _uiState.value.reviewTargetOrder
                val updatedItems = currentDetail?.items?.map { itm ->
                    if (itm.id == orderItemId) itm.copy(isReviewed = true) else itm
                }
                val updatedDetail = currentDetail?.copy(items = updatedItems)

                // Tự động chuyển qua item tiếp theo chưa review (nếu còn)
                val nextUnreviewed = updatedItems?.firstOrNull { it.isReviewed != true }

                _uiState.update { state ->
                    state.copy(
                        isSubmittingReview = false,
                        reviewTargetOrder = updatedDetail,
                        selectedReviewOrderItem = nextUnreviewed ?: state.selectedReviewOrderItem?.copy(isReviewed = true)
                    )
                }

                showSuccessToast("Gửi đánh giá thành công! Cảm ơn bạn đã phản hồi.")

                // Tải lại danh sách đơn hàng để cập nhật badge và tab Đánh giá
                loadOrders()

                onSuccess?.invoke()
            }.onFailure { err ->
                _uiState.update { it.copy(isSubmittingReview = false) }
                val errorMsg = err.message ?: "Không thể gửi đánh giá. Vui lòng thử lại sau."
                showErrorToast(errorMsg)
            }
        }
    }

    /**
     * Upload ảnh đánh giá lên Cloudinary
     */
    fun uploadReviewImage(
        bytes: ByteArray,
        mimeType: String = "image/jpeg",
        fileName: String = "review_image.jpg",
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isReviewImageUploading = true) }
            val result = userRepository.uploadImage(bytes, mimeType, fileName, "reviews")
            result.onSuccess { responseDto ->
                _uiState.update { it.copy(isReviewImageUploading = false) }
                val url = responseDto.secureUrl ?: responseDto.url ?: ""
                if (url.isNotBlank()) {
                    onSuccess(url)
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isReviewImageUploading = false) }
                showErrorToast(error.message ?: "Không thể tải ảnh lên")
            }
        }
    }

    fun openChangePassword() {
        _uiState.update { it.copy(showChangePasswordScreen = true) }
    }

    fun closeChangePassword() {
        _uiState.update { it.copy(showChangePasswordScreen = false) }
    }

    /**
     * Nạp danh sách địa chỉ nhận hàng từ API Backend (GET /api/v1/users/addresses)
     */
    fun loadAddresses(isRefresh: Boolean = false) {
        if (!_uiState.value.isLoggedIn) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAddressLoading = !isRefresh, addressErrorMessage = null) }
            val result = userRepository.getMyAddresses()
            result.onSuccess { dtoList ->
                val domainList = dtoList.map { it.toDomain() }
                _uiState.update {
                    it.copy(
                        addresses = domainList,
                        isAddressLoading = false,
                        addressErrorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isAddressLoading = false,
                        addressErrorMessage = error.message
                    )
                }
            }
        }
    }

    fun setDefaultAddress(addressId: String) {
        val idLong = addressId.toLongOrNull()
        if (_uiState.value.isLoggedIn && idLong != null) {
            viewModelScope.launch {
                // Optimistic UI update
                _uiState.update { state ->
                    val updated = state.addresses.map { addr ->
                        addr.copy(isDefault = (addr.id == addressId))
                    }
                    state.copy(addresses = updated)
                }

                val result = userRepository.setDefaultAddress(idLong)
                result.onSuccess {
                    val msg = "Đã cập nhật địa chỉ mặc định!"
                    _uiState.update { state ->
                        state.copy(toastMessage = msg, currentToast = createToastData(msg, ToastType.SUCCESS))
                    }
                    loadAddresses(isRefresh = true)
                    refreshOrderPreview()
                }.onFailure { error ->
                    val msg = error.message ?: "Không thể đặt địa chỉ mặc định"
                    _uiState.update { state ->
                        state.copy(toastMessage = msg, currentToast = createToastData(msg, ToastType.ERROR))
                    }
                    loadAddresses(isRefresh = true)
                    refreshOrderPreview()
                }
            }
        } else {
            _uiState.update { state ->
                val updated = state.addresses.map { addr ->
                    addr.copy(isDefault = (addr.id == addressId))
                }
                val msg = "Đã cập nhật địa chỉ mặc định!"
                state.copy(addresses = updated, toastMessage = msg, currentToast = createToastData(msg, ToastType.SUCCESS))
            }
        }
    }

    fun deleteAddress(addressId: String) {
        val idLong = addressId.toLongOrNull()
        if (_uiState.value.isLoggedIn && idLong != null) {
            viewModelScope.launch {
                // Optimistic UI update
                _uiState.update { state ->
                    val updated = state.addresses.filterNot { it.id == addressId }
                    state.copy(addresses = updated)
                }

                val result = userRepository.deleteAddress(idLong)
                result.onSuccess {
                    val msg = "Đã xóa địa chỉ thành công!"
                    _uiState.update { state ->
                        state.copy(toastMessage = msg, currentToast = createToastData(msg, ToastType.SUCCESS))
                    }
                    loadAddresses(isRefresh = true)
                    refreshOrderPreview()
                }.onFailure { error ->
                    val msg = error.message ?: "Không thể xóa địa chỉ"
                    _uiState.update { state ->
                        state.copy(toastMessage = msg, currentToast = createToastData(msg, ToastType.ERROR))
                    }
                    loadAddresses(isRefresh = true)
                    refreshOrderPreview()
                }
            }
        } else {
            _uiState.update { state ->
                val updated = state.addresses.filterNot { it.id == addressId }
                val msg = "Đã xóa địa chỉ khỏi sổ địa chỉ"
                state.copy(addresses = updated, toastMessage = msg, currentToast = createToastData(msg, ToastType.SUCCESS))
            }
        }
    }

    fun addOrUpdateAddress(address: UserAddress, onDone: (() -> Unit)? = null) {
        val idLong = address.id.toLongOrNull()
        if (_uiState.value.isLoggedIn) {
            viewModelScope.launch {
                _uiState.update { it.copy(isAddressSaving = true) }
                val requestDto = address.toRequestDto()

                val result = if (idLong != null) {
                    userRepository.updateAddress(idLong, requestDto)
                } else {
                    userRepository.createAddress(requestDto)
                }

                result.onSuccess { savedDto ->
                    val newAddressId = savedDto.id?.toString() ?: idLong?.toString()
                    val msg = if (idLong != null) "Cập nhật địa chỉ thành công!" else "Thêm địa chỉ mới thành công!"
                    _uiState.update { state ->
                        state.copy(
                            isAddressSaving = false,
                            showAddressEditorScreen = false,
                            editingAddressForScreen = null,
                            selectedCheckoutAddressId = newAddressId ?: state.selectedCheckoutAddressId,
                            toastMessage = msg,
                            currentToast = createToastData(msg, ToastType.SUCCESS)
                        )
                    }
                    loadAddresses(isRefresh = true)
                    refreshOrderPreview()
                    refreshOrderPreview()
                    onDone?.invoke()
                }.onFailure { error ->
                    val msg = error.message ?: "Không thể lưu địa chỉ. Vui lòng thử lại!"
                    _uiState.update { state ->
                        state.copy(
                            isAddressSaving = false,
                            toastMessage = msg,
                            currentToast = createToastData(msg, ToastType.ERROR)
                        )
                    }
                }
            }
        } else {
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
                state.copy(
                    addresses = list,
                    showAddressEditorScreen = false,
                    editingAddressForScreen = null,
                    toastMessage = msg,
                    currentToast = createToastData(msg, ToastType.SUCCESS)
                )
            }
            onDone?.invoke()
        }
    }

    fun addProductToCart(
        product: ProductItem,
        color: String = "",
        size: String = "",
        quantity: Int = 1,
        variant: ProductVariantItem? = null
    ) {
        val targetVariant = variant ?: product.variants.firstOrNull()
        val variantNameDisplay = targetVariant?.variantName
            ?: if (color.isNotBlank() && size.isNotBlank()) "$color, Size $size"
            else color.ifBlank { size }.ifBlank { "Tiêu chuẩn" }

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
