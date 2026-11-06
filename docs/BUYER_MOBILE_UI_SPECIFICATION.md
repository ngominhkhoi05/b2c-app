# ĐẶC TẢ KIẾN TRÚC GIAO DIỆN & TÍCH HỢP API (BUYER MOBILE APP)

> **Dự án:** B2C E-Commerce Application  
> **Nền tảng:** Android Native (Kotlin + Jetpack Compose + Material 3)  
> **Package:** `com.example.b2capp`  
> **Backend đối chiếu:** `b2c-be` (Tham chiếu chi tiết: `b2c-be/docs/BUYER_MOBILE_API_REQUIREMENTS.md`)  
> **Concept màu sắc:** Concept 1 — Modern Energetic (Cam San Hô `#EE4D2D` & Xanh Navy `#0E3B64`)

---

## MỤC LỤC
1. [Hệ thống Bảng màu & Thiết kế Cấu hình Tập trung (Centralized Theme)](#1-hệ-thống-bảng-màu--thiết-kế-cấu-hình-tập-trung-centralized-theme)
2. [Nguyên tắc Thiết kế & Tái sử dụng (Design Rules & Best Practices)](#2-nguyên-tắc-thiết-kế--tái-sử-dụng-design-rules--best-practices)
3. [Cấu trúc Thư mục Dự án (Package Architecture)](#3-cấu-trúc-thư-mục-dự-án-package-architecture)
4. [Danh sách Thư viện Cần thiết (Dependencies)](#4-danh-sách-thư-viện-cần-thiết-dependencies)
5. [Chi tiết Các Màn hình, Thành phần UI & Mapping API Backend](#5-chi-tiết-các-màn-hình-thành-phần-ui--mapping-api-backend)
   - [Phân hệ 1: Xác thực & Tài khoản (Auth Flow)](#phân-hệ-1-xác-thực--tài-khoản-auth-flow)
   - [Phân hệ 2: Khám phá, Trang chủ & Danh mục (Home & Catalog Flow)](#phân-hệ-2-khám-phá-trang-chủ--danh-mục-home--catalog-flow)
   - [Phân hệ 3: Chi tiết Sản phẩm & Đánh giá (Product Detail Flow)](#phân-hệ-3-chi-tiết-sản-phẩm--đánh-giá-product-detail-flow)
   - [Phân hệ 4: Giỏ hàng (Cart Flow)](#phân-hệ-4-giỏ-hàng-cart-flow)
   - [Phân hệ 5: Đặt hàng & Thanh toán (Checkout Flow)](#phân-hệ-5-đặt-hàng--thanh-toán-checkout-flow)
   - [Phân hệ 6: Quản lý & Lịch sử Đơn hàng (Order Management Flow)](#phân-hệ-6-quản-lý--lịch-sử-đơn-hàng-order-management-flow)
   - [Phân hệ 7: Hồ sơ Cá nhân & Sổ Địa chỉ (Profile & Address Flow)](#phân-hệ-7-hồ-sơ-cá-nhân--sổ-địa-chỉ-profile--address-flow)
   - [Phân hệ 8: Tin nhắn Trò chuyện với Shop (Chat Flow)](#phân-hệ-8-tin-nhắn-trò-chuyện-với-shop-chat-flow)

---

## 1. HỆ THỐNG BẢNG MÀU & THIẾT KẾ CẤU HÌNH TẬP TRUNG (CENTRALIZED THEME)

### 1.1. Nguyên tắc "Single Source of Truth" cho Màu sắc
* **Tuyệt đối KHÔNG hardcode mã màu** dạng `Color(0xFFEE4D2D)` rải rác bên trong các file giao diện (Screens / Components).
* **Mọi màu sắc phải được định nghĩa tập trung** tại thư mục `core/designsystem/theme/`:
  - `Color.kt`: Khai báo bảng màu thô (Palette tokens).
  - `AppColors.kt`: Khai báo semantic tokens (màu theo ngữ nghĩa nghiệp vụ: màu giá tiền, màu tag giảm giá, màu sao...).
  - `Theme.kt`: Gắn kết vào Material 3 `ColorScheme` và phân phối xuống UI qua `MaterialTheme.colorScheme` hoặc `LocalAppColors`.
* **Khả năng thay đổi nhận diện thương hiệu:** Sau này khi cần đổi sang màu khác (ví dụ: Xanh lá, Đỏ thẫm, Tím), lập trình viên **chỉ cần sửa mã màu tại đúng file `Color.kt`**, toàn bộ ứng dụng sẽ tự động cập nhật đồng bộ 100%.

### 1.2. Bảng phân bổ màu sắc (Concept 1 - Modern Energetic)

```kotlin
// Vị trí file: core/designsystem/theme/Color.kt
package com.example.b2capp.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// === 1. BẢNG MÀU GỐC (RAW PALETTE TOKENS) ===
val Coral500 = Color(0xFFEE4D2D)      // Cam san hô chủ đạo (Primary Brand)
val Coral600 = Color(0xFFD73211)      // Cam đậm khi bấm (Pressed / Focused)
val Coral50  = Color(0xFFFFEBE7)      // Cam nhạt (Container / Highlight tag)

val Navy900  = Color(0xFF0E3B64)      // Xanh Navy đậm (Secondary / Header / BottomBar)
val Navy800  = Color(0xFF1B4F72)      // Xanh Navy vừa

val Teal500  = Color(0xFF26AA99)      // Xanh ngọc (Freeship / Điểm nhấn tích cực)
val Amber500 = Color(0xFFFFB800)      // Vàng sao đánh giá (Rating Star)
val Red600   = Color(0xFFD32F2F)      // Đỏ báo lỗi / Hết hàng / Hủy đơn

val NeutralGray50  = Color(0xFFF8F9FA) // Nền app tổng thể (Background)
val NeutralGray100 = Color(0xFFF1F3F5) // Nền card phụ / Ô search
val NeutralGray200 = Color(0xFFE5E7EB) // Viền ngăn cách (Border / Divider)
val NeutralGray500 = Color(0xFF888888) // Chữ phụ (Subtitle / Placeholder)
val NeutralGray900 = Color(0xFF222222) // Chữ chính (Text Primary)
```

```kotlin
// Vị trí file: core/designsystem/theme/AppColors.kt
package com.example.b2capp.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic Color Scheme mở rộng cho TMĐT B2C.
 * Sử dụng: AppTheme.colors.priceTag hoặc MaterialTheme.colorScheme.primary
 */
data class AppExtendedColors(
    val priceTag: Color = Coral500,
    val discountBadge: Color = Coral500,
    val discountBadgeContainer: Color = Coral50,
    val freeShippingBadge: Color = Teal500,
    val starRating: Color = Amber500,
    val statusPending: Color = Amber500,
    val statusConfirmed: Color = Navy800,
    val statusDelivered: Color = Teal500,
    val statusCancelled: Color = Red600,
    val cardBorder: Color = NeutralGray200
)

val LocalAppExtendedColors = staticCompositionLocalOf { AppExtendedColors() }
```

---

## 2. NGUYÊN TẮC THIẾT KẾ & TÁI SỬ DỤNG (DESIGN RULES & BEST PRACTICES)

1. **Tái sử dụng Component tối đa (DRY - Don't Repeat Yourself):**
   * Tất cả các thành phần lặp lại từ 2 lần trở lên **BẮT BUỘC** phải đưa vào `core/ui/components/`:
     * Thẻ sản phẩm (`ProductCard`): Dùng chung cho Trang chủ, Màn Catalog, Màn Yêu thích.
     * Nút bấm hành động (`AppButton`): Chuẩn hóa kích thước, bo góc, tự động hiển thị `CircularProgressIndicator` khi `isLoading = true`.
     * Ô nhập liệu (`AppTextField`): Tích hợp sẵn icon, xử lý focus, nhãn lỗi validate.
     * Bộ chọn số lượng (`QuantityPicker`): Nút `[-] 1 [+]` dùng cho cả Bottom Sheet biến thể và Giỏ hàng.
     * Trạng thái rỗng (`EmptyStateView`): Dùng cho giỏ hàng rỗng, đơn hàng rỗng, không tìm thấy sản phẩm.
2. **Quản lý trạng thái theo luồng MVI / MVVM chuẩn Google:**
   * Mọi màn hình đều sở hữu một `ViewModel` tương ứng.
   * `UIState` được định nghĩa bằng `sealed interface` (ví dụ: `Loading`, `Success<T>`, `Error(message)`).
   * Lắng nghe State tại Composable bằng `collectAsStateWithLifecycle()`.
3. **Không tính nhẩm tiền ở Client:**
   * Các số liệu nhạy cảm (tổng tiền thanh toán, tiền giảm giá voucher, phí vận chuyển) luôn gọi API tính trước (`POST /api/v1/buyer/orders/preview`) để đảm bảo khớp hoàn toàn với Backend.
4. **Xử lý Token tự động:**
   * Tầng Network sử dụng `OkHttp Interceptor` để tự động gắn `Authorization: Bearer <token>` vào mọi request có yêu cầu đăng nhập.
   * Sử dụng `Authenticator` để khi nhận mã `401 Unauthorized` sẽ tự động gọi `/api/v1/auth/refresh-token` cấp lại access token mới mà không bắt người dùng đăng nhập lại giữa chừng.

---

## 3. CẤU TRÚC THƯ MỤC DỰ ÁN (PACKAGE ARCHITECTURE)

Dự án tổ chức theo cấu trúc **Feature-First kết hợp Clean Architecture**, đặt trong package `com.example.b2capp`:

```text
com.example.b2capp/
├── B2CApplication.kt                       # Application class
├── MainActivity.kt                         # Single Activity chứa Root NavHost
│
├── core/                                   # [TẦNG CỐT LÕI DÙNG CHUNG]
│   ├── network/                            # Cấu hình Retrofit & OkHttp
│   │   ├── ApiClient.kt                    # Base URL, Timeout, JSON Serializer
│   │   ├── AuthInterceptor.kt              # Tự gắn JWT Bearer token
│   │   └── TokenAuthenticator.kt           # Tự động refresh token khi nhận 401
│   ├── datastore/                          # Quản lý lưu trữ an toàn
│   │   └── TokenManager.kt                 # Lưu accessToken, refreshToken, userInfo
│   ├── common/                             # Class tiện ích & Wrapper chuẩn
│   │   ├── ApiResponse.kt                  # Model bọc: { code, message, data }
│   │   ├── PageResponse.kt                 # Model phân trang: { content, pageNumber, totalElements... }
│   │   ├── Resource.kt                     # Sealed class: Idle, Loading, Success, Error
│   │   └── Extensions.kt                   # formatVndCurrency(), formatDateTime()
│   ├── designsystem/                       # Hệ thống Theme & Token cấu hình màu
│   │   └── theme/
│   │       ├── Color.kt                    # TẬP TRUNG TOÀN BỘ MÃ MÀU TẠI ĐÂY
│   │       ├── AppColors.kt                # Extended Semantic Color Scheme (Giá, Tag, Sao...)
│   │       ├── Shape.kt                    # Quy chuẩn bo góc (Cards: 12.dp, Buttons: 8.dp)
│   │       ├── Type.kt                     # Quy chuẩn Typography (Font chữ, Size, Weight)
│   │       └── Theme.kt                    # AppTheme Provider
│   └── ui/                                 # Components tái sử dụng toàn app
│       └── components/
│           ├── AppButton.kt                # Primary / Secondary / Outlined Button
│           ├── AppTextField.kt             # Form text field
│           ├── ProductCard.kt              # Thẻ sản phẩm chuẩn
│           ├── StarRatingBar.kt            # Dải sao tương tác / chỉ đọc
│           ├── QuantityPicker.kt           # Tăng giảm số lượng [- 1 +]
│           ├── EmptyStateView.kt           # Khối hiển thị khi danh sách trống
│           ├── ErrorRetryView.kt           # Khối hiển thị khi mất mạng kèm nút "Thử lại"
│           └── LoadingOverlay.kt           # Vòng xoay loading phủ màn hình
│
├── data/                                   # [TẦNG DỮ LIỆU & REPOSITORY]
│   ├── remote/                             # API Interface & DTOs (khớp b2c-be)
│   │   ├── auth/ (AuthApi.kt, LoginRequest.kt, RegisterRequest.kt, AuthResponse.kt)
│   │   ├── catalog/ (ProductApi.kt, CategoryApi.kt, ProductDetailDto.kt, ProductSummaryDto.kt)
│   │   ├── cart/ (CartApi.kt, CartItemDto.kt, AddCartItemRequest.kt, UpdateCartItemRequest.kt)
│   │   ├── order/ (OrderApi.kt, OrderSummaryDto.kt, OrderDetailDto.kt, CreateOrderRequest.kt, OrderPreviewRequest.kt)
│   │   ├── payment/ (PaymentApi.kt, PaymentDto.kt)
│   │   ├── promotion/ (PromotionApi.kt, PromotionDto.kt, ApplyPromotionRequest.kt)
│   │   ├── review/ (ReviewApi.kt, ReviewDto.kt, CreateReviewRequest.kt)
│   │   ├── user/ (UserApi.kt, UserProfileDto.kt, AddressDto.kt, AddressRequest.kt)
│   │   ├── wishlist/ (WishlistApi.kt, WishlistItemDto.kt)
│   │   └── chat/ (ChatApi.kt, ConversationDto.kt, MessageDto.kt, SendMessageRequest.kt)
│   └── repository/                         # Implement tầng Repository kết nối API
│       ├── AuthRepository.kt
│       ├── CatalogRepository.kt
│       ├── CartRepository.kt
│       ├── OrderRepository.kt
│       ├── UserRepository.kt
│       ├── WishlistRepository.kt
│       └── ChatRepository.kt
│
├── features/                               # [TẦNG GIAO DIỆN THEO CHỨC NĂNG]
│   ├── auth/                               # Phân hệ Xác thực
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── ForgotPasswordScreen.kt
│   │   └── AuthViewModel.kt
│   ├── home/                               # Phân hệ Trang chủ
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   └── components/ (BannerSlider.kt, CategoryGrid.kt, HomeSearchBar.kt)
│   ├── catalog/                            # Phân hệ Tìm kiếm & Danh mục
│   │   ├── SearchScreen.kt
│   │   ├── CategoryTreeScreen.kt
│   │   ├── CatalogViewModel.kt
│   │   └── components/ (FilterBottomSheet.kt, SortTabBar.kt)
│   ├── product/                            # Phân hệ Chi tiết sản phẩm & Đánh giá
│   │   ├── ProductDetailScreen.kt
│   │   ├── ReviewsListScreen.kt
│   │   ├── ProductDetailViewModel.kt
│   │   └── components/ (VariantBottomSheet.kt, ImageGalleryPager.kt, SellerInfoCard.kt)
│   ├── cart/                               # Phân hệ Giỏ hàng
│   │   ├── CartScreen.kt
│   │   ├── CartViewModel.kt
│   │   └── components/ (CartShopSection.kt, CartItemCard.kt, CartBottomBar.kt)
│   ├── checkout/                           # Phân hệ Đặt hàng & Thanh toán
│   │   ├── CheckoutScreen.kt
│   │   ├── VnPayWebViewScreen.kt
│   │   ├── OrderSuccessScreen.kt
│   │   ├── CheckoutViewModel.kt
│   │   └── components/ (VoucherSelectionSheet.kt, PaymentMethodSelector.kt)
│   ├── order/                              # Phân hệ Quản lý & Lịch sử đơn hàng
│   │   ├── OrderHistoryScreen.kt
│   │   ├── OrderDetailScreen.kt
│   │   ├── WriteReviewScreen.kt
│   │   ├── OrderViewModel.kt
│   │   └── components/ (OrderCardItem.kt, OrderStatusTimeline.kt, CancelReasonDialog.kt)
│   ├── profile/                            # Phân hệ Tài khoản cá nhân
│   │   ├── ProfileScreen.kt
│   │   ├── EditProfileScreen.kt
│   │   ├── ChangePasswordScreen.kt
│   │   ├── AddressListScreen.kt
│   │   ├── AddEditAddressScreen.kt
│   │   ├── WishlistScreen.kt
│   │   └── ProfileViewModel.kt
│   └── chat/                               # Phân hệ Chat với Shop
│       ├── ConversationListScreen.kt
│       ├── ChatDetailScreen.kt
│       ├── ChatViewModel.kt
│       └── components/ (ChatMessageBubble.kt, ChatInputField.kt)
│
└── navigation/                             # [ĐIỀU HƯỚNG TOÀN ỨNG DỤNG]
    ├── AppNavHost.kt                       # Quản lý NavHost, liên kết toàn bộ route
    ├── Screen.kt                           # Định nghĩa sealed class / route name
    └── BottomNavBar.kt                     # Thanh Bottom Navigation Bar 5 tabs
```

---

## 4. DANH SÁCH THƯ VIỆN CẦN THIẾT (DEPENDENCIES)

Bổ sung các thư viện sau vào `app/build.gradle.kts`:

```kotlin
dependencies {
    // 1. Jetpack Compose BOM & Material 3
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.icons.extended) // Icon giỏ hàng, search, shop...

    // 2. Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // 3. Lifecycle & ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // 4. Mạng & Kết nối API (Retrofit + OkHttp)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 5. Tải ảnh từ URL (Cloudinary / Image CDN)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // 6. Lưu trữ an toàn Token & User Info
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // 7. WebView hỗ trợ thanh toán VNPay
    // Sử dụng AndroidView(factory = { WebView(it) }) hoặc accompanist-webview
}
```

---

## 5. CHI TIẾT CÁC MÀN HÌNH, THÀNH PHẦN UI & MAPPING API BACKEND

*(Tất cả API dưới đây đều khớp 100% với tài liệu đặc tả `BUYER_MOBILE_API_REQUIREMENTS.md`)*

### Phân hệ 1: Xác thực & Tài khoản (Auth Flow)

#### 1. Màn hình Đăng nhập (LoginScreen)
* **Thành phần UI:**
  * Logo thương hiệu, Tiêu đề "Đăng nhập".
  * `AppTextField` cho Email.
  * `AppTextField` cho Mật khẩu (có icon ẩn/hiện mật khẩu).
  * Text button "Quên mật khẩu?".
  * `AppButton` "Đăng nhập" (hỗ trợ loading state).
  * Nút chuyển nhanh "Chưa có tài khoản? Đăng ký ngay".
* **Mapping API Backend:**
  * `POST /api/v1/auth/login`
  * Body: `{ "email": "...", "password": "..." }`
  * Response: Nhận `accessToken`, `refreshToken`, `userId`, `role`. Lưu token vào `TokenManager`.

#### 2. Màn hình Đăng ký (RegisterScreen)
* **Thành phần UI:**
  * `AppTextField`: Họ và tên, Email, Mật khẩu (validate >= 8 ký tự), Xác nhận mật khẩu.
  * Checkbox điều khoản chính sách.
  * `AppButton` "Đăng ký tài khoản".
* **Mapping API Backend:**
  * `POST /api/v1/auth/register`
  * Body: `{ "fullName": "...", "email": "...", "password": "..." }`

#### 3. Màn hình Quên mật khẩu & Đặt lại (ForgotPasswordScreen)
* **Thành phần UI:**
  * Bước 1: Ô nhập Email nhận mã khôi phục.
  * Bước 2: Ô nhập Token khôi phục + Mật khẩu mới + Xác nhận mật khẩu mới.
* **Mapping API Backend:**
  * Bước 1: `POST /api/v1/auth/forgot-password` (Body: `{ "email": "..." }`)
  * Bước 2: `POST /api/v1/auth/reset-password` (Body: `{ "token": "...", "newPassword": "..." }`)

#### Đăng xuất (Logout Action)
* **Hành vi UI:** Người dùng bấm "Đăng xuất" tại trang cá nhân -> hiện Dialog xác nhận -> xóa sạch token trong `TokenManager` -> chuyển về `LoginScreen`.
* **Mapping API Backend:**
  * `POST /api/v1/auth/logout` (Stateless wipe token).

---

### Phân hệ 2: Khám phá, Trang chủ & Danh mục (Home & Catalog Flow)

#### 4. Màn hình Trang chủ (HomeScreen)
* **Thành phần UI:**
  * **Header:** Thanh tìm kiếm giả lập (bấm mở SearchScreen), Icon Giỏ hàng kèm Badge số lượng đỏ.
  * **Banner Slider:** Trượt ảnh khuyến mãi, sự kiện.
  * **Category Icons Grid:** Lưới danh mục gốc cấp 1 (icon + tên danh mục).
  * **Flash Sale / Featured Section:** Danh sách trượt ngang các sản phẩm bán chạy nhất.
  * **Danh sách sản phẩm gợi ý hôm nay:** Lưới 2 cột, cuộn vô tận (Infinite Scroll), hiển thị `ProductCard`.
* **Mapping API Backend:**
  * Top danh mục: `GET /api/v1/categories/roots`
  * Banner khuyến mãi: `GET /api/v1/promotions`
  * Sản phẩm nổi bật: `GET /api/v1/products/featured?page=0&size=10`
  * Sản phẩm gợi ý: `GET /api/v1/products?page=0&size=20&sortBy=created&sortDir=desc`
  * Badge giỏ hàng: `GET /api/v1/cart/count`

#### 5. Màn hình Tìm kiếm & Kết quả lọc (SearchScreen)
* **Thành phần UI:**
  * **Header:** Thanh nhập từ khóa tự động tìm kiếm, Nút icon mở `FilterBottomSheet`.
  * **Sort Bar:** Tab sắp xếp: [Mới nhất] | [Bán chạy] | [Giá ↑] | [Giá ↓] | [Đánh giá cao].
  * **Lưới kết quả:** Danh sách `ProductCard` có phân trang.
  * **Trạng thái:** `EmptyStateView` nếu không có sản phẩm nào khớp.
* **Mapping API Backend:**
  * `GET /api/v1/products?q={keyword}&categoryId={catId}&brand={brand}&minPrice={min}&maxPrice={max}&minRating={star}&sortBy={sort}&sortDir={dir}&page={page}&size=20`

#### 6. Modal Bộ lọc nâng cao (FilterBottomSheet)
* **Thành phần UI:**
  * Lọc theo Danh mục (Chips bấm chọn).
  * Khoảng giá: 2 ô nhập Giá từ - Giá đến.
  * Lọc theo Thương hiệu (`brand`).
  * Lọc theo Đánh giá sao: [Từ 5 sao], [Từ 4 sao], [Từ 3 sao].
  * 2 Nút: "Thiết lập lại" & "Áp dụng".

#### 7. Màn hình Cây danh mục (CategoryTreeScreen)
* **Thành phần UI:**
  * Cột bên trái: Danh sách danh mục cha cấp 1 (Sidebar dọc).
  * Vùng bên phải: Lưới danh mục con cấp 2 và cấp 3 tương ứng (kèm hình đại diện). Bấm vào sẽ mở `SearchScreen` với `categoryId`.
* **Mapping API Backend:**
  * `GET /api/v1/categories/tree`

---

### Phân hệ 3: Chi tiết Sản phẩm & Đánh giá (Product Detail Flow)

#### 8. Màn hình Chi tiết sản phẩm (ProductDetailScreen)
* **Thành phần UI:**
  * **Header:** Nút Back, Nút Yêu thích (Trái tim bấm toggle), Nút Chia sẻ, Icon Giỏ hàng (badge).
  * **Image Gallery Pager:** Lướt xem bộ ảnh sản phẩm, hiển thị số trang `1/5`.
  * **Thông tin giá & bán:** Khoảng giá `minPrice` - `maxPrice`, Tên sản phẩm, Đã bán, Sao trung bình.
  * **Thẻ Shop (SellerInfoCard):** Logo shop, Tên shop, Chấm xanh Online, Điểm rating shop, Nút "Xem Shop" và nút "Chat".
  * **Thẻ chọn phân loại tóm tắt:** Bấm mở `VariantBottomSheet`.
  * **Khối thông số:** Khối lượng, kích thước, bài viết mô tả chi tiết.
  * **Khối đánh giá tóm tắt:** 2 nhận xét mới nhất, nút "Xem tất cả đánh giá".
  * **Bottom Action Bar:** Nút "Chat", Nút "Thêm vào giỏ", Nút "Mua ngay".
* **Mapping API Backend:**
  * Chi tiết sản phẩm: `GET /api/v1/products/id/{id}` (hoặc `/api/v1/products/{slug}`)
  * Kiểm tra yêu thích: `GET /api/v1/wishlist/products/{id}/check`
  * Bật/tắt yêu thích: `POST /api/v1/wishlist/products/{id}`

#### 9. Bottom Sheet Chọn biến thể (VariantBottomSheet)
* **Thành phần UI:**
  * Ảnh và giá cụ thể của biến thể đang chọn.
  * Nhãn tồn kho khả dụng (`stockQuantity`).
  * Nhóm thuộc tính 1 (Màu sắc): Chips nút bấm.
  * Nhóm thuộc tính 2 (Kích cỡ): Chips nút bấm.
  * *(Logic: Sử dụng mảng `optionValueIds` trong mỗi variant để xác định chính xác `variantId` được chọn)*.
  * `QuantityPicker`: Nút `[-] 1 [+]` (khóa nút `+` khi chạm trần tồn kho).
  * Nút "Xác nhận".
* **Mapping API Backend:**
  * Thêm vào giỏ: `POST /api/v1/cart/items` (Body: `{ "variantId": 105, "quantity": 2 }`)

#### 10. Màn hình Tất cả Đánh giá sản phẩm (ReviewsListScreen)
* **Thành phần UI:**
  * Tổng điểm sao trung bình (4.8 / 5) & Thanh phân bố sao.
  * Thanh Filter sao: [Tất cả] | [5 sao] | [4 sao] | [3 sao] | [2 sao] | [1 sao].
  * Danh sách Review: Avatar, Tên buyer (hoặc "Ẩn danh"), Dải sao, Ngày đánh giá, Tên phân loại hàng ("Màu: Đen, Size: L"), Nội dung nhận xét, Phản hồi từ shop (nếu có).
* **Mapping API Backend:**
  * `GET /api/v1/products/{productId}/reviews?rating={star}&page={page}&size=10`

---

### Phân hệ 4: Giỏ hàng (Cart Flow)

#### 11. Màn hình Giỏ hàng (CartScreen)
* **Thành phần UI:**
  * **Header:** Tiêu đề "Giỏ hàng", nút "Xóa đã chọn".
  * **Danh sách sản phẩm GOM NHÓM THEO SHOP (`sellerId`):**
    * Tiêu đề Shop: Checkbox chọn cả shop, Icon shop, Tên Shop.
    * Danh sách món của shop: Checkbox chọn món, Ảnh thumbnail, Tên sản phẩm (bấm nhảy sang Product Detail), Tên biến thể, Giá snapshot, `QuantityPicker` tăng giảm số lượng.
    * Nhãn "Hết hàng" (nếu `isAvailable = false`).
    * *Ràng buộc UI:* Chỉ cho phép tick chọn các món hàng thuộc **cùng 1 shop** (nếu tick sang shop khác thì cảnh báo hoặc chuyển lựa chọn sang shop mới, tuân thủ ràng buộc Single Seller Checkout của Backend).
  * **Bottom Bar:** Checkbox "Chọn tất cả", Tổng tiền tạm tính, Nút "Mua hàng (Checkout)".
  * `EmptyStateView`: Khi giỏ hàng trống kèm nút "Mua sắm ngay".
* **Mapping API Backend:**
  * Danh sách giỏ: `GET /api/v1/cart`
  * Đổi số lượng / tick chọn: `PATCH /api/v1/cart/items/{id}` (Body: `{ "quantity": 3, "isSelected": true }`)
  * Xóa 1 món: `DELETE /api/v1/cart/items/{id}`
  * Xóa nhiều món: `DELETE /api/v1/cart/items` (Body: `{ "cartItemIds": [1, 2, 3] }`)

---

### Phân hệ 5: Đặt hàng & Thanh toán (Checkout Flow)

#### 12. Màn hình Xác nhận Đặt hàng (CheckoutScreen)
* **Thành phần UI:**
  * **Khối Địa chỉ nhận hàng:** Tên người nhận, SĐT, Địa chỉ chi tiết. Nút bấm chuyển sang `AddressListScreen` để đổi địa chỉ.
  * **Khối Danh sách món mua:** Tên shop, các mặt hàng đã chọn (ảnh, tên, số lượng, giá).
  * **Khối Lời nhắn:** Ô nhập ghi chú gửi shop.
  * **Khối Voucher:** Nút bấm mở `VoucherSelectionSheet`.
  * **Khối Phương thức thanh toán:** Radio button chọn:
    * 💵 COD (Thanh toán tiền mặt khi nhận hàng).
    * 💳 VNPay (Cổng thanh toán thẻ / QR ngân hàng).
  * **Khối Bảng kê chi phí (Tự động cập nhật qua API Preview):**
    * Tiền hàng (Subtotal)
    * Phí vận chuyển (Shipping fee: 30.000 VNĐ)
    * Giảm giá voucher (Discount amount)
    * **Tổng thanh toán (Final Total)**
  * **Bottom Bar:** Tổng thanh toán, Nút "Đặt hàng".
* **Mapping API Backend:**
  * Tính trước chi phí: `POST /api/v1/buyer/orders/preview` (Body: `{ "cartItemIds": [...], "addressId": 5, "voucherCode": "..." }`)
  * Đặt hàng chính thức: `POST /api/v1/buyer/orders`
    * Header: `Idempotency-Key: <UUID>`
    * Body: `{ "cartItemIds": [...], "addressId": 5, "paymentMethod": "VNPAY", "voucherCode": "...", "note": "...", "clientIp": "..." }`
    * Response: Nhận `OrderDetailDto`. Nếu `paymentMethod == VNPAY` thì lấy trường `paymentUrl` để mở `VnPayWebViewScreen`.

#### 13. Bottom Sheet Chọn Voucher (VoucherSelectionSheet)
* **Thành phần UI:**
  * Ô nhập mã thủ công + Nút "Áp dụng".
  * Danh sách Voucher hợp lệ: Mã code, Nội dung ưu đãi, Hạn dùng, Nút "Dùng ngay".
* **Mapping API Backend:**
  * Lấy danh sách voucher: `GET /api/v1/buyer/promotions`
  * Kiểm tra tính hợp lệ: `POST /api/v1/buyer/promotions/validate` (Body: `{ "code": "...", "subtotal": 500000 }`)

#### 14. Màn hình WebView VNPay (VnPayWebViewScreen)
* **Thành phần UI:**
  * Header có nút "Đóng / Hủy giao dịch".
  * Component WebView tải link `paymentUrl`.
  * Bộ chặn URL: Khi WebView load đến URL chứa `/payment/return`, Mobile trích xuất `status` và `orderCode` rồi đóng màn hình chuyển sang `OrderSuccessScreen`.

#### 15. Màn hình Kết quả Đặt hàng (OrderSuccessScreen)
* **Thành phần UI:**
  * Icon trạng thái (Thành công / Thất bại).
  * Mã đơn hàng (`orderCode`), Số tiền đã thanh toán, Hình thức thanh toán.
  * 2 Nút: "Xem chi tiết đơn hàng" hoặc "Về trang chủ".

---

### Phân hệ 6: Quản lý & Lịch sử Đơn hàng (Order Management Flow)

#### 16. Màn hình Lịch sử đơn hàng (OrderHistoryScreen)
* **Thành phần UI:**
  * **Tab Bar trạng thái (cuộn ngang):**
    * [Tất cả] | [Chờ xác nhận] | [Đang chuẩn bị] | [Đang giao] | [Đã giao] | [Đã hủy] | [Trả hàng]
  * **Danh sách thẻ đơn hàng (`OrderCardItem`):**
    * Tên shop, Badge trạng thái màu sắc.
    * Ảnh sản phẩm đầu tiên, Tên sản phẩm đầu tiên, Phân loại + *"và X sản phẩm khác"*.
    * Tổng số tiền (`totalAmount`).
    * **Nút hành động nhanh theo trạng thái:**
      * Đơn `PENDING` (chưa thanh toán VNPay): Nút "Thanh toán ngay".
      * Đơn `PENDING` hoặc `CONFIRMED`: Nút "Hủy đơn".
      * Đơn `DELIVERED`: Nút "Đánh giá", Nút "Yêu cầu trả hàng".
* **Mapping API Backend:**
  * `GET /api/v1/buyer/orders?status={status}&page={page}&size=20` (Sử dụng `OrderSummaryDto` đã có sẵn tóm tắt món).

#### 17. Màn hình Chi tiết & Lộ trình đơn hàng (OrderDetailScreen)
* **Thành phần UI:**
  * Banner trạng thái lớn.
  * **Timeline lộ trình đơn hàng:**
    * Đặt hàng (`createdAt`) -> Đã xác nhận (`confirmedAt`) -> Đang giao (`shippedAt`) -> Giao thành công (`deliveredAt`).
  * Thông tin người nhận (Tên, SĐT, Địa chỉ đầy đủ).
  * Danh sách toàn bộ sản phẩm trong đơn (ảnh, tên, số lượng, giá mua).
  * Chi tiết bảng kê tài chính (Tiền hàng, Ship, Giảm giá, Tổng tiền, Hình thức thanh toán).
  * Nút chức năng ở đáy màn hình: "Hủy đơn", "Thanh toán lại", "Đánh giá".
* **Mapping API Backend:**
  * Lấy chi tiết đơn: `GET /api/v1/buyer/orders/{id}`
  * Thanh toán lại VNPay: `POST /api/v1/buyer/orders/{id}/pay` -> Nhận `paymentUrl` mở lại WebView.

#### 18. Dialog Hủy đơn & Yêu cầu Trả hàng
* **Thành phần UI:**
  * Radio button danh sách lý do hủy có sẵn + Ô nhập lý do tùy chọn.
  * Nút "Xác nhận hủy".
* **Mapping API Backend:**
  * Hủy đơn: `PATCH /api/v1/buyer/orders/{id}/cancel?reason={reason}`
  * Yêu cầu trả hàng: `POST /api/v1/buyer/orders/{id}/return-request` (Body: `{ "reason": "...", "description": "..." }`)

#### 19. Màn hình Viết đánh giá sản phẩm (WriteReviewScreen)
* **Thành phần UI:**
  * Ảnh và tên sản phẩm được đánh giá.
  * `StarRatingBar` chọn từ 1 đến 5 sao.
  * `AppTextField` nhập nhận xét chi tiết.
  * Switch "Đánh giá ẩn danh".
  * `AppButton` "Gửi đánh giá".
* **Mapping API Backend:**
  * `POST /api/v1/buyer/reviews`
  * Body: `{ "orderItemId": 42, "rating": 5, "comment": "...", "isAnonymous": false }`

---

### Phân hệ 7: Hồ sơ Cá nhân & Sổ Địa chỉ (Profile & Address Flow)

#### 20. Màn hình Tài khoản cá nhân (ProfileScreen)
* **Thành phần UI:**
  * **Header:** Avatar lớn, Tên người dùng, Email/SĐT.
  * **Khối Đơn mua (Quick Access):** 4 icon lối tắt: [Chờ xác nhận], [Đang giao], [Đã giao], [Đánh giá].
  * **Danh sách menu tiện ích:**
    * 📍 Sổ địa chỉ nhận hàng (chuyển sang `AddressListScreen`)
    * ❤️ Sản phẩm yêu thích (chuyển sang `WishlistScreen`)
    * 🔒 Đổi mật khẩu (chuyển sang `ChangePasswordScreen`)
    * 💬 Tin nhắn với Shop (chuyển sang `ConversationListScreen`)
    * 🚪 Đăng xuất (Dialog xác nhận)
* **Mapping API Backend:**
  * Lấy thông tin cá nhân: `GET /api/v1/users/me`

#### 21. Màn hình Chỉnh sửa hồ sơ & Upload Avatar (EditProfileScreen)
* **Thành phần UI:**
  * Avatar tròn có icon máy ảnh: Bấm để mở Photo Picker của Android chọn ảnh.
  * `AppTextField`: Họ và tên, Số điện thoại.
  * Ô Email (Disabled / Read-only).
  * `AppButton`: "Lưu thay đổi".
* **Mapping API Backend:**
  * Cập nhật thông tin: `PUT /api/v1/users/me` (Body: `{ "fullName": "...", "phone": "..." }`)
  * Upload avatar: `POST /api/v1/users/me/avatar` (Multipart/form-data: `file`)

#### 22. Màn hình Đổi mật khẩu (ChangePasswordScreen)
* **Thành phần UI:**
  * 3 ô nhập: Mật khẩu hiện tại, Mật khẩu mới, Xác nhận mật khẩu mới.
  * `AppButton`: "Đổi mật khẩu".
* **Mapping API Backend:**
  * `POST /api/v1/users/change-password`
  * Body: `{ "currentPassword": "...", "newPassword": "...", "confirmPassword": "..." }`

#### 23. Màn hình Sổ địa chỉ (AddressListScreen)
* **Thành phần UI:**
  * Danh sách các địa chỉ đã lưu (Địa chỉ mặc định có badge "Mặc định").
  * Mỗi item có nút "Sửa", nút "Xóa", nút "Đặt làm mặc định".
  * Nút "Thêm địa chỉ mới" cố định ở đáy màn hình.
* **Mapping API Backend:**
  * Danh sách: `GET /api/v1/users/addresses`
  * Đặt mặc định: `PUT /api/v1/users/addresses/{id}/default`
  * Xóa địa chỉ: `DELETE /api/v1/users/addresses/{id}`

#### 24. Màn hình Thêm / Sửa địa chỉ (AddEditAddressScreen)
* **Thành phần UI:**
  * Họ tên người nhận, Số điện thoại.
  * Tỉnh / Thành phố, Quận / Huyện, Phường / Xã.
  * Địa chỉ cụ thể (Số nhà, tên đường).
  * Switch: "Đặt làm địa chỉ mặc định".
  * Nút "Lưu địa chỉ".
* **Mapping API Backend:**
  * Thêm mới: `POST /api/v1/users/addresses`
  * Cập nhật: `PUT /api/v1/users/addresses/{id}`
  * Body: `{ "recipientName": "...", "phone": "...", "province": "...", "district": "...", "ward": "...", "streetAddress": "...", "isDefault": true }`

#### 25. Màn hình Sản phẩm yêu thích (WishlistScreen)
* **Thành phần UI:**
  * Lưới 2 cột các sản phẩm đã bấm thích (`ProductCard`).
  * Nút icon trái tim để xóa nhanh khỏi danh sách yêu thích.
* **Mapping API Backend:**
  * Danh sách: `GET /api/v1/wishlist?page=0&size=20`
  * Xóa: `DELETE /api/v1/wishlist/products/{productId}`

---

### Phân hệ 8: Tin nhắn Trò chuyện với Shop (Chat Flow)

#### 26. Màn hình Danh sách Hội thoại (ConversationListScreen)
* **Thành phần UI:**
  * Danh sách cuộc trò chuyện với các Shop: Logo shop, Tên shop, Tin nhắn cuối cùng, Thời gian, Badge số tin nhắn chưa đọc.
* **Mapping API Backend:**
  * `GET /api/v1/buyer/conversations?page=0&size=20`

#### 27. Màn hình Chat chi tiết (ChatDetailScreen)
* **Thành phần UI:**
  * Header: Tên Shop, Chấm xanh online, Nút gọi/xem shop.
  * Danh sách tin nhắn cuộn từ dưới lên (`ChatMessageBubble` phân biệt người gửi / người nhận).
  * Thanh nhập liệu đáy màn hình: Ô nhập tin nhắn, nút gửi.
* **Mapping API Backend:**
  * Mở hoặc tạo hội thoại với shop: `POST /api/v1/conversations?sellerId={sellerId}`
  * Danh sách tin nhắn: `GET /api/v1/conversations/{id}/messages?page=0&size=50`
  * Gửi tin nhắn: `POST /api/v1/conversations/{id}/messages` (Body: `{ "content": "..." }`)

---

## 6. BẢNG TỔNG KẾT TIẾN ĐỘ & SỰ SẴN SÀNG

* **Tổng số màn hình & Bottom Sheet:** 27 màn hình/thành phần hoàn chỉnh.
* **Công nghệ cốt lõi:** Android Kotlin + Jetpack Compose (Material 3).
* **Màu sắc nhận diện:** Concept 1 (Cam Coral `#EE4D2D` + Xanh Navy `#0E3B64`), quản lý tập trung 100% tại `core/designsystem/theme/Color.kt`.
* **Sự đồng bộ Backend:** 100% các endpoint được liệt kê đều đã đối chiếu khớp với tài liệu `b2c-be/docs/BUYER_MOBILE_API_REQUIREMENTS.md` và cam kết **không cần thay đổi Database hiện tại**.

