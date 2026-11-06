package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.b2capp.core.ui.components.AppBottomBar
import com.example.b2capp.ui.AccountScreen
import com.example.b2capp.ui.CartScreen
import com.example.b2capp.ui.CategoriesScreen
import com.example.b2capp.ui.CheckoutScreen
import com.example.b2capp.ui.HomeScreen
import com.example.b2capp.ui.OrdersScreen
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.ui.ProductDetailsScreen
import com.example.b2capp.ui.OrderSuccessScreen
import com.example.b2capp.ui.ShopChatScreen
import com.example.b2capp.ui.ShopMessagesScreen
import com.example.b2capp.ui.VnpaySandboxPaymentScreen
import com.example.ui.theme.B2CAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            B2CAppTheme {
                B2CMainApp()
            }
        }
    }
}

@Composable
fun B2CMainApp(
    viewModel: B2CViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Xử lý thông báo Toast/Snackbar
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearToast()
        }
    }

    // Khi người dùng mở màn hình Tạo Tài Khoản Mới (SignUpScreen)
    if (uiState.showSignUpScreen) {
        BackHandler {
            viewModel.closeSignUp()
        }
        com.example.b2capp.ui.SignUpScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackToSignIn = {
                viewModel.closeSignUp()
            },
            onSignUpSuccess = {
                viewModel.closeSignUp()
                viewModel.closeSignIn()
                viewModel.selectTab(0)
            }
        )
        return
    }

    // Khi người dùng mở màn hình Quên Mật Khẩu (ForgotPasswordScreen)
    if (uiState.showForgotPasswordScreen) {
        BackHandler {
            viewModel.closeForgotPassword()
        }
        com.example.b2capp.ui.ForgotPasswordScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackToSignIn = {
                viewModel.closeForgotPassword()
            }
        )
        return
    }

    // Khi vừa mở app hoặc khi hiển thị màn hình Đăng Nhập (SignInScreen)
    if (uiState.showSignInScreen) {
        BackHandler {
            viewModel.closeSignIn()
        }
        com.example.b2capp.ui.SignInScreen(
            uiState = uiState,
            viewModel = viewModel,
            onSignInSuccess = {
                viewModel.closeSignIn()
                viewModel.selectTab(0)
            },
            onSkipClick = {
                viewModel.closeSignIn()
                viewModel.selectTab(0)
            }
        )
        return
    }

    // Khi người dùng bấm vào 1 sản phẩm -> Mở toàn màn hình Chi tiết sản phẩm
    if (uiState.detailedProductScreen != null) {
        BackHandler {
            viewModel.closeProductDetailsScreen()
        }
        ProductDetailsScreen(
            product = uiState.detailedProductScreen!!,
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeProductDetailsScreen() },
            onNavigateToCart = {
                viewModel.closeProductDetailsScreen()
                viewModel.selectTab(2)
            }
        )
        return
    }

    // Khi người dùng tìm kiếm từ khóa -> Mở màn hình Kết quả tìm kiếm
    if (uiState.searchResultQuery != null) {
        BackHandler {
            viewModel.closeSearchResults()
        }
        com.example.b2capp.ui.SearchResultsScreen(
            query = uiState.searchResultQuery!!,
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeSearchResults() },
            onNavigateToCart = {
                viewModel.closeSearchResults()
                viewModel.selectTab(2)
            },
            onProductClick = { product ->
                viewModel.openProductDetailsScreen(product)
            }
        )
        return
    }

    // Khi người dùng mở cuộc trò chuyện trực tiếp với Shop (ShopChatScreen)
    if (uiState.activeShopChat != null) {
        BackHandler {
            viewModel.closeShopChat()
        }
        ShopChatScreen(
            chatInfo = uiState.activeShopChat!!,
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeShopChat() }
        )
        return
    }

    // Khi người dùng mở màn hình Tin nhắn với Shop
    if (uiState.showShopMessages) {
        BackHandler {
            viewModel.closeShopMessages()
        }
        ShopMessagesScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeShopMessages() }
        )
        return
    }

    // Khi đơn hàng đặt thành công (COD hoặc Online VNPAY) -> Hiển thị Màn hình Đặt Hàng Thành Công
    if (uiState.completedOrderSuccess != null) {
        BackHandler {
            viewModel.closeOrderSuccessScreen(navigateToOrdersTab = false)
        }
        OrderSuccessScreen(
            orderInfo = uiState.completedOrderSuccess!!,
            uiState = uiState,
            viewModel = viewModel
        )
        return
    }

    // Khi người dùng xem chi tiết đơn hàng & lộ trình vận chuyển (OrderDetailsAndTrackingScreen)
    if (uiState.detailedOrderTrackingScreen != null) {
        BackHandler {
            viewModel.closeOrderDetailsTrackingScreen()
        }
        com.example.b2capp.ui.OrderDetailsAndTrackingScreen(
            orderId = uiState.detailedOrderTrackingScreen!!,
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeOrderDetailsTrackingScreen() }
        )
        return
    }

    // Khi người dùng mở màn hình Đổi Mật Khẩu (ChangePasswordScreen)
    if (uiState.showChangePasswordScreen) {
        BackHandler {
            viewModel.closeChangePassword()
        }
        com.example.b2capp.ui.ChangePasswordScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeChangePassword() }
        )
        return
    }

    // Khi người dùng mở màn hình Viết Đánh Giá Sản Phẩm (WriteProductReviewScreen)
    if (uiState.showWriteReviewScreen) {
        BackHandler {
            viewModel.closeWriteReview()
        }
        com.example.b2capp.ui.WriteProductReviewScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeWriteReview() }
        )
        return
    }

    // Khi người dùng mở màn hình Chỉnh Sửa Hồ Sơ Cá Nhân (EditProfileScreen)
    if (uiState.showEditProfileScreen) {
        BackHandler {
            viewModel.closeEditProfile()
        }
        com.example.b2capp.ui.EditProfileScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeEditProfile() }
        )
        return
    }

    // Khi người dùng mở màn hình Thêm/Sửa địa chỉ giao hàng (AddressEditorScreen)
    if (uiState.showAddressEditorScreen) {
        BackHandler {
            viewModel.closeAddressEditor()
        }
        com.example.b2capp.ui.AddressEditorScreen(
            initialAddress = uiState.editingAddressForScreen,
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeAddressEditor() }
        )
        return
    }

    // Khi người dùng mở màn hình Sổ Địa Chỉ / Địa Chỉ Của Tôi (AddressBookScreen)
    if (uiState.showAddressBookScreen) {
        BackHandler {
            viewModel.closeAddressBook()
        }
        com.example.b2capp.ui.AddressBookScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeAddressBook() }
        )
        return
    }

    // Khi người dùng mở màn hình Danh Sách Yêu Thích (WishlistScreen)
    if (uiState.showWishlistScreen) {
        BackHandler {
            viewModel.closeWishlist()
        }
        com.example.b2capp.ui.WishlistScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeWishlist() }
        )
        return
    }

    // Khi người dùng mở màn hình Đánh Giá Sản Phẩm (ProductReviewsScreen)
    if (uiState.showProductReviewsScreen) {
        BackHandler {
            viewModel.closeProductReviewsScreen()
        }
        com.example.b2capp.ui.ProductReviewsScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeProductReviewsScreen() }
        )
        return
    }

    // Khi người dùng mở màn hình Cổng thanh toán VNPAY-QR (VnpaySandboxPaymentScreen)
    if (uiState.showVnpayPaymentScreen) {
        BackHandler {
            viewModel.closeVnpayPayment()
        }
        VnpaySandboxPaymentScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeVnpayPayment() }
        )
        return
    }

    // Khi người dùng mở màn hình Thanh toán (CheckoutScreen) khi ấn Mua ngay hoặc Mua từ giỏ hàng
    if (uiState.showCheckoutScreen) {
        BackHandler {
            viewModel.closeCheckoutScreen()
        }
        CheckoutScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeCheckoutScreen() }
        )
        return
    }

    // Xử lý nút Back hệ thống
    if (uiState.currentTab != 0) {
        BackHandler {
            viewModel.selectTab(0)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Thanh BottomBar 5 tabs chuẩn theo ảnh đính kèm của người dùng:
            // 1. Trang chủ | 2. Danh mục | 3. Giỏ hàng (badge 3) | 4. Đơn hàng | 5. Tài khoản
            AppBottomBar(
                selectedTab = uiState.currentTab,
                onTabSelected = { tabId -> viewModel.selectTab(tabId) },
                cartBadgeCount = uiState.totalCartCount
            )
        }
    ) { innerPadding ->
        when (uiState.currentTab) {
            0 -> HomeScreen(
                uiState = uiState,
                viewModel = viewModel,
                onNavigateToCart = { viewModel.selectTab(2) },
                modifier = Modifier.padding(innerPadding)
            )
            1 -> CategoriesScreen(
                uiState = uiState,
                viewModel = viewModel,
                onNavigateToCart = { viewModel.selectTab(2) },
                onBackClick = { viewModel.selectTab(0) },
                modifier = Modifier.padding(innerPadding)
            )
            2 -> CartScreen(
                uiState = uiState,
                viewModel = viewModel,
                onNavigateToHome = { viewModel.selectTab(0) },
                modifier = Modifier.padding(innerPadding)
            )
            3 -> OrdersScreen(
                uiState = uiState,
                viewModel = viewModel,
                onExploreShop = { viewModel.selectTab(0) },
                modifier = Modifier.padding(innerPadding)
            )
            4 -> AccountScreen(
                uiState = uiState,
                viewModel = viewModel,
                onNavigateToOrders = { viewModel.selectTab(3) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
