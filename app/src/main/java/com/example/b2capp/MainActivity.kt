package com.example.b2capp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.b2capp.core.ui.components.B2CToastHost
import com.example.b2capp.viewmodel.B2CUiState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.b2capp.core.designsystem.theme.B2CAppTheme
import com.example.b2capp.core.ui.navigation.AppBottomBar
import com.example.b2capp.features.auth.ForgotPasswordScreen
import com.example.b2capp.features.auth.SignInScreen
import com.example.b2capp.features.auth.SignUpScreen
import com.example.b2capp.features.cart.CartScreen
import com.example.b2capp.features.catalog.CategoriesScreen
import com.example.b2capp.features.catalog.SearchResultsScreen
import com.example.b2capp.features.chat.ShopChatScreen
import com.example.b2capp.features.chat.ShopMessagesScreen
import com.example.b2capp.features.checkout.CheckoutScreen
import com.example.b2capp.features.checkout.OrderSuccessScreen
import com.example.b2capp.features.checkout.VnpaySandboxPaymentScreen
import com.example.b2capp.features.home.HomeScreen
import com.example.b2capp.features.order.OrderDetailsAndTrackingScreen
import com.example.b2capp.features.order.OrdersScreen
import com.example.b2capp.features.product.ProductDetailsScreen
import com.example.b2capp.features.product.ProductReviewsScreen
import com.example.b2capp.features.product.WriteProductReviewScreen
import com.example.b2capp.features.profile.AccountScreen
import com.example.b2capp.features.profile.AddressBookScreen
import com.example.b2capp.features.profile.AddressEditorScreen
import com.example.b2capp.features.profile.ChangePasswordScreen
import com.example.b2capp.features.profile.EditProfileScreen
import com.example.b2capp.features.profile.WishlistScreen
import com.example.b2capp.viewmodel.B2CViewModel

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

    Box(modifier = Modifier.fillMaxSize()) {
        B2CMainScreens(uiState = uiState, viewModel = viewModel)

        // Custom Toast thông báo nổi ở cuối màn hình (chia loại Đỏ, Xanh, Vàng, vuốt để xóa, hiển thị ~2s)
        B2CToastHost(
            currentToast = uiState.currentToast,
            onDismiss = { viewModel.clearToast() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 76.dp)
        )
    }
}

@Composable
private fun B2CMainScreens(
    uiState: B2CUiState,
    viewModel: B2CViewModel
) {

    // Khi người dùng mở màn hình Tạo Tài Khoản Mới (SignUpScreen)
    if (uiState.showSignUpScreen) {
        BackHandler {
            viewModel.closeSignUp()
        }
        SignUpScreen(
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
        ForgotPasswordScreen(
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
        SignInScreen(
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

    // Khi người dùng mở màn hình Danh Sách Yêu Thích (WishlistScreen)
    if (uiState.showWishlistScreen) {
        BackHandler {
            viewModel.closeWishlist()
        }
        WishlistScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeWishlist() }
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

    // Khi người dùng mở màn hình Đánh Giá Sản Phẩm (ProductReviewsScreen)
    if (uiState.showProductReviewsScreen) {
        BackHandler {
            viewModel.closeProductReviewsScreen()
        }
        ProductReviewsScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeProductReviewsScreen() }
        )
        return
    }

    // Khi người dùng mở màn hình Viết Đánh Giá Sản Phẩm (WriteProductReviewScreen)
    if (uiState.showWriteReviewScreen) {
        BackHandler {
            viewModel.closeWriteReview()
        }
        WriteProductReviewScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeWriteReview() }
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
        OrderDetailsAndTrackingScreen(
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
        ChangePasswordScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeChangePassword() }
        )
        return
    }

    // Khi người dùng mở màn hình Chỉnh Sửa Hồ Sơ Cá Nhân (EditProfileScreen)
    if (uiState.showEditProfileScreen) {
        BackHandler {
            viewModel.closeEditProfile()
        }
        EditProfileScreen(
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
        AddressEditorScreen(
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
        AddressBookScreen(
            uiState = uiState,
            viewModel = viewModel,
            onBackClick = { viewModel.closeAddressBook() }
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
        bottomBar = {
            // Thanh BottomBar 5 tabs chuẩn:
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

