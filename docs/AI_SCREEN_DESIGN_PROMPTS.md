# BỘ PROMPT THIẾT KẾ & LẬP TRÌNH GIAO DIỆN MOBILE (JETPACK COMPOSE)

> **Mục đích:** Cung cấp bộ Prompt chi tiết, chuẩn xác từ màu sắc, kích thước, thành phần UI đến trạng thái tương tác để bạn có thể sao chép (copy) và đưa cho bất kỳ AI nào (Claude, ChatGPT, Cursor, v0...) tạo ra mã nguồn Jetpack Compose chuẩn xác 100%.  
> **Nền tảng:** Android Native (Kotlin + Jetpack Compose + Material 3)  
> **Theme:** Concept 1 — Cam San Hô (`#EE4D2D`) & Xanh Navy (`#0E3B64`)

---

## MỤC LỤC
1. [PROMPT 0: MASTER SYSTEM PROMPT (DÙNG CHUNG CHO MỌI LẦN NHẬP)](http://_blank#prompt-0-master-system-prompt)
2. [PROMPT 1: THIẾT KẾ CÁC COMPONENT DÙNG CHUNG (DESIGN SYSTEM)](http://_blank#prompt-1-thiết-kế-các-component-dùng-chung)
3. [PROMPT 2: PHÂN HỆ XÁC THỰC (AUTH: LOGIN, REGISTER, FORGOT PASSWORD)](http://_blank#prompt-2-phân-hệ-xác-thực)
4. [PROMPT 3: TRANG CHỦ & DANH MỤC (HOME, CATEGORY TREE)](http://_blank#prompt-3-trang-chủ--danh-mục)
5. [PROMPT 4: TÌM KIẾM & BỘ LỌC NÂNG CAO (SEARCH, FILTER DRAWER)](http://_blank#prompt-4-tìm-kiếm--bộ-lọc-nâng-cao)
6. [PROMPT 5: CHI TIẾT SẢN PHẨM & CHỌN BIẾN THỂ (PRODUCT DETAIL, VARIANT SHEET)](http://_blank#prompt-5-chi-tiết-sản-phẩm--chọn-biến-thể)
7. [PROMPT 6: ĐÁNH GIÁ SẢN PHẨM (REVIEWS LIST, WRITE REVIEW)](http://_blank#prompt-6-đánh-giá-sản-phẩm)
8. [PROMPT 7: GIỎ HÀNG GOM NHÓM THEO SHOP (CART SCREEN)](http://_blank#prompt-7-giỏ-hàng-gom-nhóm-theo-shop)
9. [PROMPT 8: ĐẶT HÀNG & THANH TOÁN (CHECKOUT, VOUCHER, VNPAY WEBVIEW, SUCCESS)](http://_blank#prompt-8-đặt-hàng--thanh-toán)
10. [PROMPT 9: QUẢN LÝ & LỊCH SỬ ĐƠN HÀNG (ORDER HISTORY, ORDER DETAIL & TIMELINE)](http://_blank#prompt-9-quản-lý--lịch-sử-đơn-hàng)
11. [PROMPT 10: HỒ SƠ CÁ NHÂN & SỔ ĐỊA CHỈ (PROFILE, ADDRESS CRUD, WISHLIST)](http://_blank#prompt-10-hồ-sơ-cá-nhân--sổ-địa-chỉ)
12. [PROMPT 11: TIN NHẮN VỚI SHOP (CONVERSATION LIST, CHAT DETAIL)](http://_blank#prompt-11-tin-nhắn-với-shop)

---

## PROMPT 0: MASTER SYSTEM PROMPT (DÙNG ĐỂ GÁN BỐI CẢNH BAN ĐẦU)

> 💡 *Sao chép prompt này gửi cho AI ở đầu phiên làm việc để thiết lập tiêu chuẩn thiết kế và bảng màu.*

```markdown
Bạn là một Senior Android UI/UX Engineer và chuyên gia Jetpack Compose (Material 3). 
Nhiệm vụ của bạn là lập trình giao diện người dùng cho ứng dụng Mua sắm B2C (Buyer App) bằng Kotlin và Jetpack Compose.

HÃY TUÂN THỦ NGHIÊM NGẶT CÁC QUY TẮC THIẾT KẾ SAU:

1. BẢNG MÀU CHUẨN (CONCEPT 1 - CAM SAN HÔ & XANH NAVY):
   - Primary Brand (Màu chủ đạo): #EE4D2D (Cam san hô rực rỡ - dùng cho nút Mua ngay, Giá tiền, Icon điểm nhấn, Badge giảm giá).
   - Primary Pressed: #D73211 (Cam đậm khi nhấn).
   - Primary Container: #FFEBE7 (Cam nhạt - nền tag giảm giá, viền item đang chọn).
   - Secondary (Màu phụ): #0E3B64 (Xanh Navy đậm - dùng cho thanh BottomBar, Tiêu đề Shop, nút thứ cấp).
   - FreeShipping / Accent: #26AA99 (Xanh ngọc - nhãn Freeship, tag Giao hàng thành công).
   - Star Rating: #FFB800 (Vàng sao đánh giá).
   - Error / Cancel: #D32F2F (Đỏ cảnh báo lỗi, nút Hủy đơn, nhãn Hết hàng).
   - Background (Nền app): #F8F9FA (Xám trắng nhẹ tạo độ tương phản).
   - Surface (Nền Card / Sheet): #FFFFFF (Trắng tinh khiết).
   - Border / Divider: #E5E7EB (Viền xám mảnh 1.dp).
   - Text Primary: #222222 (Đen xám than).
   - Text Secondary: #888888 (Xám phụ).

2. QUY CHUẨN THIẾT KẾ (DESIGN TOKENS):
   - Typography: Font chữ hiện đại, phân cấp rõ ràng (Title: 18-20sp Bold; Body: 14-15sp Regular; Price: 16-20sp Bold #EE4D2D; Caption: 12sp Regular #888888).
   - Shapes (Bo góc): ProductCard bo 12.dp; Button bo 8.dp; BottomSheet bo 16.dp 2 góc trên; Tag/Badge bo 4.dp; Chips bo 16.dp.
   - Spacing: Chuẩn 8.dp grid (4.dp, 8.dp, 12.dp, 16.dp, 24.dp). Padding lề 2 bên màn hình luôn là 16.dp.
   - Trạng thái bắt buộc: Luôn xử lý 4 trạng thái: Loading (Shimmer/Indicator), Success (Dữ liệu), Empty (Trống có minh họa), Error (Mất mạng kèm nút Thử lại).

3. QUY TẮC CODE:
   - Sử dụng hoàn toàn Material 3 (`androidx.compose.material3.*`).
   - KHÔNG hardcode màu tùy tiện trong Composable; hãy ánh xạ qua MaterialTheme.colorScheme hoặc biến theme chung.
   - Sử dụng Icons.Default hoặc Icons.Outlined từ `androidx.compose.material.icons.*`.
   - Viết code sạch, kèm Preview Composable và ViewModel State interface rõ ràng.
```

---

## PROMPT 1: THIẾT KẾ CÁC COMPONENT DÙNG CHUNG (DESIGN SYSTEM)

> 💡 *Dùng prompt này để yêu cầu AI dựng bộ UI Kit tái sử dụng.*

```markdown
Dựa trên Master System Prompt, hãy viết trọn bộ mã nguồn Jetpack Compose cho các thành phần dùng chung (Reusable Components) trong thư mục `com.example.b2capp.core.ui.components`:

1. AppButton.kt:
   - Nhận vào: text: String, onClick: () -> Unit, modifier, enabled, isLoading: Boolean, variant: ButtonVariant (PRIMARY, SECONDARY, OUTLINED).
   - Style: Chiều cao tối thiểu 48.dp, bo góc 8.dp.
   - PRIMARY: Nền #EE4D2D, chữ trắng. Khi isLoading = true hiển thị CircularProgressIndicator trắng (size 20.dp).
   - SECONDARY: Nền #0E3B64, chữ trắng.
   - OUTLINED: Nền trong suốt, viền 1.dp #EE4D2D, chữ #EE4D2D.

2. AppTextField.kt:
   - Nhận vào: value, onValueChange, label, placeholder, leadingIcon, isPassword, isError, errorMessage.
   - Style: Bo góc 8.dp, nền #FFFFFF, viền #E5E7EB, khi focus đổi viền sang #EE4D2D. Nếu isPassword = true có icon mắt ẩn/hiện mật khẩu.

3. ProductCard.kt:
   - Thẻ sản phẩm dạng lưới (rộng ~170.dp).
   - Gồm: Ảnh tỷ lệ 1:1 (dùng Coil AsyncImage), bo góc trên 12.dp; Tên sản phẩm tối đa 2 dòng (14sp); Hàng giá: Giá hiện tại nổi bật (#EE4D2D, 16sp Bold), Giá gạch ngang mờ nếu có sale; Hàng phụ: Điểm sao (#FFB800) kèm số lượng "Đã bán X".
   - Nền thẻ #FFFFFF, đổ bóng nhẹ (shadow 2.dp) hoặc viền 1.dp #E5E7EB.

4. QuantityPicker.kt:
   - Bộ chọn số lượng: Nút [-] bo tròn viền xám, Số lượng ở giữa (14sp Bold), Nút [+] bo tròn viền xám.
   - Tự động disable nút [-] khi = 1, disable nút [+] khi đạt maxStock.

5. StarRatingBar.kt:
   - Hiển thị 5 ngôi sao #FFB800. Hỗ trợ 2 chế độ: Chỉ hiển thị (kèm số 4.8) hoặc Cho phép người dùng chạm để chấm sao (1 đến 5).

6. EmptyStateView.kt:
   - Icon minh họa lớn màu xám nhạt, Tiêu đề thông báo (16sp Bold), Dòng mô tả (14sp #888888), Nút hành động kêu gọi (ví dụ: "Tiếp tục mua sắm").
```

---

## PROMPT 2: PHÂN HỆ XÁC THỰC (AUTH: LOGIN, REGISTER, FORGOT PASSWORD)

```markdown
Dựa trên Master System Prompt và các Component của Design System, hãy lập trình trọn vẹn màn hình Đăng nhập (LoginScreen) và Đăng ký (RegisterScreen) trong package `com.example.b2capp.features.auth`:

1. LoginScreen.kt:
   - Bố cục: Scaffold nền #F8F9FA.
   - Logo thương hiệu ở trên cùng (icon túi mua sắm màu #EE4D2D).
   - Tiêu đề: "Chào mừng trở lại!" (24sp Bold #222222), phụ đề "Đăng nhập để tiếp tục mua sắm" (14sp #888888).
   - Form nhập:
     * AppTextField Email (icon email).
     * AppTextField Mật khẩu (icon khóa, nút ẩn/hiện).
     * TextButton "Quên mật khẩu?" căn phải (#0E3B64, 13sp).
   - AppButton: "ĐĂNG NHẬP" (Primary #EE4D2D, full width, cao 50.dp).
   - Chân trang: Dòng chữ "Chưa có tài khoản? " kèm nút bấm nổi bật "Đăng ký ngay" (#EE4D2D, Bold).
   - Kết nối ViewModel: Gọi `POST /api/v1/auth/login`, quản lý AuthState (Idle, Loading, Success, Error).

2. RegisterScreen.kt:
   - Form gồm: Họ và tên, Email, Mật khẩu (>= 8 ký tự), Xác nhận mật khẩu.
   - Checkbox: "Tôi đồng ý với Điều khoản dịch vụ & Chính sách bảo mật".
   - Nút "ĐĂNG KÝ" (Chỉ enable khi điền đủ và tích checkbox).
   - Kết nối ViewModel: Gọi `POST /api/v1/auth/register`.
```

---

## PROMPT 3: TRANG CHỦ & DANH MỤC (HOME, CATEGORY TREE)

```markdown
Hãy lập trình màn hình Trang chủ (HomeScreen) và Cây danh mục (CategoryTreeScreen) trong `com.example.b2capp.features.home` và `com.example.b2capp.features.catalog`:

1. HomeScreen.kt:
   - Header cố định: 
     * Nền #EE4D2D (hoặc nền trắng với search bar viền #EE4D2D).
     * Thanh tìm kiếm bo tròn 20.dp: Nền #F1F3F5, placeholder "Tìm kiếm trên B2C...", icon kính lúp. Bấm vào điều hướng sang SearchScreen.
     * Icon Giỏ hàng góc phải kèm Badge màu đỏ hiển thị số lượng món (`GET /api/v1/cart/count`).
   - Nội dung cuộn (LazyColumn):
     * Banner Carousel: Auto-scroll pager hiển thị banner quảng cáo (`GET /api/v1/promotions`), bo góc 8.dp, indicator chấm nhỏ ở dưới.
     * Danh mục nổi bật (Horizontal / Grid 2 hàng): Các icon tròn danh mục gốc (`GET /api/v1/categories/roots`) kèm tên danh mục 12sp.
     * Khối "Sản phẩm bán chạy" (Flash Sale / Featured): Header có thanh tiêu đề chữ cam #EE4D2D kèm icon tia sét, danh sách cuộn ngang các `ProductCard` (`GET /api/v1/products/featured`).
     * Khối "Gợi ý hôm nay": Lưới 2 cột các `ProductCard` (`GET /api/v1/products?sortBy=created&sortDir=desc`), hỗ trợ phân trang cuộn vô tận (Infinite Scroll) khi lướt tới cuối trang.

2. CategoryTreeScreen.kt:
   - Bố cục 2 cột chia đôi màn hình:
     * Cột trái (rộng 100.dp): Menu dọc danh mục cha cấp 1, nền #F1F3F5. Mục đang chọn có nền #FFFFFF, chữ in đậm #EE4D2D và vạch đỏ bên mép trái.
     * Vùng phải: Hiển thị các danh mục con cấp 2 và cấp 3 tương ứng (`GET /api/v1/categories/tree`), hiển thị dạng lưới icon nhỏ kèm tên. Bấm vào chuyển sang SearchScreen với `categoryId`.
```

---

## PROMPT 4: TÌM KIẾM & BỘ LỌC NÂNG CAO (SEARCH, FILTER DRAWER)

```markdown
Hãy lập trình màn hình Tìm kiếm (SearchScreen) kèm BottomSheet Bộ lọc (FilterBottomSheet) trong `com.example.b2capp.features.catalog`:

1. SearchScreen.kt:
   - Header: Ô tìm kiếm nhận từ khóa `q`, nút tìm kiếm, icon mở Filter.
   - Thanh Tab sắp xếp nhanh (Sort Bar):
     * Các tab: [Mới nhất (created)], [Bán chạy (sold)], [Giá ↑ (price asc)], [Giá ↓ (price desc)], [Đánh giá cao (rating)].
     * Tab đang chọn có chữ #EE4D2D và đường gạch chân cam #EE4D2D.
   - Lưới sản phẩm: Hiển thị 2 cột `ProductCard`.
   - Kết nối API: `GET /api/v1/products` với các query param: `q`, `categoryId`, `brand`, `minPrice`, `maxPrice`, `minRating`, `sortBy`, `sortDir`, `page`, `size`.
   - Xử lý trạng thái: Skeleton loading khi đang tải trang; `EmptyStateView` khi không tìm thấy kết quả.

2. FilterBottomSheet.kt:
   - Bộ lọc chi tiết trượt từ dưới lên:
     * Nhóm Danh mục: Danh sách Chips bấm chọn danh mục.
     * Nhóm Khoảng giá: 2 ô nhập Giá tối thiểu - Giá tối đa (có định dạng VNĐ).
     * Nhóm Thương hiệu (`brand`): Các chips thương hiệu (Nike, Coolmate, Samsung...).
     * Nhóm Đánh giá: 3 nút lựa chọn [Từ 5 sao], [Từ 4 sao], [Từ 3 sao] (icon sao #FFB800).
   - Bottom Action: Nút "Thiết lập lại" (Outlined viền xám) và Nút "Áp dụng" (Primary #EE4D2D).
```

---

## PROMPT 5: CHI TIẾT SẢN PHẨM & CHỌN BIẾN THỂ (PRODUCT DETAIL, VARIANT SHEET)

```markdown
Hãy lập trình màn hình Chi tiết sản phẩm (ProductDetailScreen) và BottomSheet chọn biến thể (VariantBottomSheet) trong `com.example.b2capp.features.product`:

1. ProductDetailScreen.kt:
   - TopBar trong suốt nổi trên ảnh: Nút Back, Nút Trái tim Yêu thích (màu đỏ #EE4D2D khi liked, gọi `POST /api/v1/wishlist/products/{id}`), Nút Chia sẻ, Icon Giỏ hàng (badge).
   - Pager Ảnh sản phẩm (Image Carousel): Vuốt xem ảnh (tỷ lệ 1:1), góc phải dưới có badge số trang "1/6".
   - Khối Giá & Tiêu đề:
     * Dải giá: Khoảng giá từ minPrice đến maxPrice (#EE4D2D, 22sp Bold).
     * Tên sản phẩm (18sp SemiBold #222222).
     * Hàng phụ: Điểm sao (#FFB800) + Số lượt đánh giá (gạch chân) + Số lượng "Đã bán X".
   - Khối Shop người bán (Seller Card):
     * Logo tròn, Tên Shop (Bold #0E3B64), Chấm xanh Online (nếu `shopIsOnline = true`).
     * Đánh giá shop, số sản phẩm của shop.
     * 2 nút phụ: "Xem Shop" và "Chat ngay" (Icon chat viền #EE4D2D).
   - Khối Chọn phân loại tóm tắt: Nút bấm mở VariantBottomSheet.
   - Khối Mô tả chi tiết: Thông số kích thước, khối lượng và bài viết chi tiết sản phẩm.
   - Khối Đánh giá tóm tắt: 2 nhận xét gần nhất, nút "Xem tất cả đánh giá >".
   - Bottom Action Bar cố định:
     * Icon "Chat" (nền trắng).
     * Nút "Thêm vào giỏ" (Nền cam nhạt #FFEBE7, chữ cam #EE4D2D).
     * Nút "Mua ngay" (Nền cam #EE4D2D, chữ trắng).
   - API: `GET /api/v1/products/id/{id}` (hoặc `{slug}`).

2. VariantBottomSheet.kt:
   - Header Sheet: Thumbnail ảnh biến thể, Giá biến thể đang chọn (#EE4D2D), Kho còn lại ("Còn 15 sản phẩm").
   - Nhóm tùy chọn 1 (Màu sắc): Các chip nút bấm [Đỏ], [Xanh], [Đen].
   - Nhóm tùy chọn 2 (Kích cỡ): Các chip nút bấm [S], [M], [L], [XL].
   - *Logic khớp biến thể:* Sử dụng mảng `optionValueIds` bên trong từng `ProductVariantDto` để xác định biến thể tương ứng và kiểm tra `stockQuantity > 0`. Nếu hết hàng thì disable nút chip đó.
   - `QuantityPicker`: Bộ đếm số lượng (giới hạn theo stock).
   - Nút "XÁC NHẬN" (Primary #EE4D2D, gọi `POST /api/v1/cart/items`).
```

---

## PROMPT 6: ĐÁNH GIÁ SẢN PHẨM (REVIEWS LIST, WRITE REVIEW)

```markdown
Hãy lập trình màn hình Xem đánh giá (ReviewsListScreen) và Viết đánh giá (WriteReviewScreen) trong `com.example.b2capp.features.product` và `com.example.b2capp.features.order`:

1. ReviewsListScreen.kt:
   - Header: Điểm trung bình tổng quan (4.8 / 5) kèm 5 sao vàng #FFB800.
   - Filter Chips cuộn ngang: [Tất cả] | [5 sao] | [4 sao] | [3 sao] | [2 sao] | [1 sao].
   - Danh sách nhận xét (LazyColumn):
     * Avatar người mua, Tên người mua (hoặc "Ẩn danh"), Dải sao đánh giá.
     * Ngày đánh giá (12sp #888888), Dòng phân loại đã mua ("Phân loại: Đen / L" chữ xám nhạt).
     * Nội dung bình luận.
     * Hộp phản hồi của Người bán (nếu có): Nền xám nhạt #F1F3F5, có nhãn "Phản hồi từ người bán:".
   - API: `GET /api/v1/products/{id}/reviews?rating={star}&page={page}&size=10`.

2. WriteReviewScreen.kt:
   - Thông tin sản phẩm vừa mua (Ảnh nhỏ, tên, phân loại).
   - Chấm sao tương tác: 5 ngôi sao lớn ở giữa, bấm chọn từ 1 đến 5 sao (#FFB800).
   - AppTextField đa dòng: Ô nhập nhận xét (tối thiểu 10 ký tự, placeholder "Hãy chia sẻ cảm nhận về sản phẩm...").
   - Switch: "Đánh giá ẩn danh" (Bảo vệ thông tin cá nhân).
   - AppButton: "GỬI ĐÁNH GIÁ" (Gọi `POST /api/v1/buyer/reviews` với body: `{ orderItemId, rating, comment, isAnonymous }`).
```

---

## PROMPT 7: GIỎ HÀNG GOM NHÓM THEO SHOP (CART SCREEN)

```markdown
Hãy lập trình màn hình Giỏ hàng (CartScreen) trong `com.example.b2capp.features.cart`:

ĐẶC BIỆT LƯU Ý QUY TẮC BACKEND: Backend chỉ cho phép checkout sản phẩm của DUY NHẤT 1 SHOP (Single Seller Checkout). Vì vậy giỏ hàng PHẢI gom nhóm trực quan theo từng Shop.

1. CartScreen.kt:
   - Header: Tiêu đề "Giỏ hàng", nút "Xóa đã chọn" (gọi `DELETE /api/v1/cart/items` với list ID).
   - Danh sách món gom nhóm theo Shop (`CartShopSection`):
     * Header Shop: Checkbox chọn cả shop, Icon shop, Tên shop (#0E3B64, Bold).
     * Danh sách món hàng thuộc shop đó (`CartItemCard`):
       - Checkbox chọn món (Màu cam #EE4D2D khi chọn).
       - Ảnh thumbnail (bấm vào nhảy sang ProductDetailScreen theo `productId`).
       - Tên sản phẩm (2 dòng), Tên phân loại biến thể (nền xám nhạt).
       - Đơn giá (#EE4D2D, Bold).
       - `QuantityPicker`: Tăng giảm số lượng (gọi `PATCH /api/v1/cart/items/{id}`).
       - Nếu `isAvailable = false`: Làm mờ item và hiển thị nhãn đỏ #D32F2F "Hết hàng".
     * *RÀNG BUỘC CHỌN SHOP:* Khi người dùng tick chọn 1 món ở Shop B trong khi đang chọn món ở Shop A, hãy hiển thị Dialog xác nhận: "Bạn có muốn chuyển sang mua hàng tại Shop B không?" hoặc tự động bỏ chọn Shop cũ để tuân thủ rule của Backend.
   - Bottom Bar cố định:
     * Checkbox "Tất cả" (chọn tất cả món trong shop đang được chọn).
     * Tổng thanh toán tạm tính: Số tiền màu #EE4D2D (18sp Bold).
     * AppButton "MUA HÀNG" (Màu #EE4D2D, hiển thị số lượng món đang chọn, ví dụ: "Mua hàng (2)"). Bấm vào chuyển sang CheckoutScreen.
   - API: `GET /api/v1/cart`, `PATCH /api/v1/cart/items/{id}`, `DELETE /api/v1/cart/items/{id}`.
```

---

## PROMPT 8: ĐẶT HÀNG & THANH TOÁN (CHECKOUT, VOUCHER, VNPAY WEBVIEW, SUCCESS)

```markdown
Hãy lập trình toàn bộ luồng Checkout trong `com.example.b2capp.features.checkout`:

1. CheckoutScreen.kt:
   - Khối 1: Địa chỉ nhận hàng
     * Icon định vị màu đỏ #EE4D2D.
     * Tên người nhận + SĐT (Bold), Dòng địa chỉ chi tiết.
     * Mũi tên `>` bấm chuyển sang AddressListScreen để đổi địa chỉ.
   - Khối 2: Danh sách món mua
     * Tên Shop (#0E3B64), danh sách các món đã tick chọn từ giỏ (ảnh, tên, phân loại, số lượng, giá).
   - Khối 3: Ghi chú cho Shop
     * AppTextField nhập ghi chú (`note`).
   - Khối 4: Voucher giảm giá
     * Icon vé giảm giá màu cam #EE4D2D.
     * Text hiển thị mã đang chọn hoặc "Chọn hoặc nhập mã voucher >". Bấm mở `VoucherSelectionSheet`.
   - Khối 5: Phương thức thanh toán (Radio button):
     * [💵 COD] Thanh toán khi nhận hàng.
     * [💳 VNPay] Cổng thanh toán quét mã QR / Thẻ ngân hàng.
   - Khối 6: Bảng tính tiền chi tiết (Tự động cập nhật qua API Preview):
     * Tiền hàng (Subtotal)
     * Phí vận chuyển: 30.000 ₫
     * Giảm giá voucher: -X ₫ (Màu xanh #26AA99)
     * Tổng thanh toán: #EE4D2D (20sp Bold)
   - Bottom Bar: Tổng tiền + Nút "ĐẶT HÀNG" (Primary #EE4D2D).
   - API:
     * Tính thử tiền: `POST /api/v1/buyer/orders/preview`
     * Tạo đơn: `POST /api/v1/buyer/orders` (Header: `Idempotency-Key`). Nếu phương thức là VNPay, mở `VnPayWebViewScreen` với `paymentUrl`.

2. VoucherSelectionSheet.kt:
   - Ô nhập mã voucher thủ công + Nút "Áp dụng".
   - Danh sách Voucher: Thẻ voucher viền nét đứt màu cam, hiển thị mã, mức giảm, hạn dùng, nút "Dùng ngay" (`POST /api/v1/buyer/promotions/validate`).

3. VnPayWebViewScreen.kt:
   - TopBar có nút "Hủy / Đóng".
   - WebView tải `paymentUrl`. Cài đặt bộ chặn `onNavigationStateChange`: Khi URL chứa `/payment/return`, bóc tách query param `status` và `orderCode`, đóng WebView và điều hướng sang OrderSuccessScreen.

4. OrderSuccessScreen.kt:
   - Icon dấu tích xanh lá lớn thành công (hoặc đỏ thất bại).
   - Tiêu đề "Đặt hàng thành công!".
   - Mã đơn hàng, Số tiền, Hình thức thanh toán.
   - 2 nút: "Xem đơn hàng" (#0E3B64) và "Về trang chủ" (#EE4D2D).
```

---

## PROMPT 9: QUẢN LÝ & LỊCH SỬ ĐƠN HÀNG (ORDER HISTORY, ORDER DETAIL & TIMELINE)

```markdown
Hãy lập trình màn hình Lịch sử đơn hàng (OrderHistoryScreen) và Chi tiết đơn hàng (OrderDetailScreen) trong `com.example.b2capp.features.order`:

1. OrderHistoryScreen.kt:
   - TabBar trạng thái cuộn ngang (ScrollableTabRow):
     * [Tất cả] | [Chờ xác nhận (PENDING)] | [Đang chuẩn bị (CONFIRMED)] | [Đang giao (SHIPPED)] | [Đã giao (DELIVERED)] | [Đã hủy (CANCELLED)] | [Trả hàng (RETURNED)].
     * Tab active có chữ và thanh chỉ báo màu cam #EE4D2D.
   - Danh sách thẻ đơn hàng (`OrderCardItem`):
     * Header thẻ: Icon shop, Tên shop (#0E3B64 Bold), Badge trạng thái đơn hàng (Cam cho PENDING, Xanh Navy cho CONFIRMED, Xanh ngọc cho DELIVERED, Đỏ cho CANCELLED).
     * Body thẻ: Ảnh đại diện món đầu tiên, Tên sản phẩm đầu tiên, Phân loại, Dòng chú thích *"và X sản phẩm khác"*.
     * Footer thẻ: Tổng số tiền (#EE4D2D 16sp Bold).
     * Các nút hành động nhanh dưới chân thẻ:
       - Nếu đơn PENDING (VNPay chưa thanh toán): Nút "Thanh toán ngay" (#EE4D2D).
       - Nếu đơn PENDING/CONFIRMED: Nút "Hủy đơn" (Outlined viền xám).
       - Nếu đơn DELIVERED: Nút "Đã nhận được hàng", Nút "Đánh giá" (Primary #EE4D2D), Nút "Trả hàng".
   - API: `GET /api/v1/buyer/orders?status={status}&page={page}&size=20`.

2. OrderDetailScreen.kt:
   - Banner trạng thái đơn hàng to nổi bật ở trên cùng.
   - Cây tiến trình hành trình đơn (Timeline Steps):
     * Các mốc: Đặt hàng thành công (`createdAt`) -> Shop xác nhận (`confirmedAt`) -> Đang vận chuyển (`shippedAt`) -> Giao thành công (`deliveredAt`). Các mốc đã hoàn thành có dấu tích xanh ngọc #26AA99.
   - Khối Địa chỉ nhận hàng (Tên, SĐT, Địa chỉ).
   - Danh sách tất cả sản phẩm trong đơn (ảnh, tên, số lượng, đơn giá, tổng dòng).
   - Khối Thông tin thanh toán (Mã đơn, Phương thức, Tiền hàng, Ship, Giảm giá, Tổng tiền).
   - Nút hành động: "Thanh toán lại" (gọi `POST /api/v1/buyer/orders/{id}/pay` lấy URL VNPay mới nếu đơn PENDING); "Hủy đơn" (Dialog nhập lý do, gọi `PATCH /api/v1/buyer/orders/{id}/cancel?reason=...`).
```

---

## PROMPT 10: HỒ SƠ CÁ NHÂN & SỔ ĐỊA CHỈ (PROFILE, ADDRESS CRUD, WISHLIST)

```markdown
Hãy lập trình phân hệ Tài khoản và Sổ địa chỉ trong `com.example.b2capp.features.profile`:

1. ProfileScreen.kt (Tab Tài khoản):
   - Header: Avatar tròn viền trắng, Tên hiển thị (20sp Bold), Email, Badge "Thành viên Buyer".
   - Khối lối tắt Đơn mua: 4 Icon tròn bấm nhanh: [Chờ xác nhận], [Đang chuẩn bị], [Đang giao], [Đánh giá].
   - Danh sách Menu tùy chọn (Card nền trắng):
     * 📍 Sổ địa chỉ nhận hàng (mở AddressListScreen)
     * ❤️ Sản phẩm yêu thích (mở WishlistScreen - gọi `GET /api/v1/wishlist`)
     * 🔒 Đổi mật khẩu (mở ChangePasswordScreen)
     * 💬 Trò chuyện với Shop (mở ConversationListScreen)
   - Nút "ĐĂNG XUẤT" ở dưới cùng: Chữ đỏ #D32F2F, có Dialog xác nhận trước khi xóa token.
   - API: `GET /api/v1/users/me`.

2. EditProfileScreen.kt:
   - Avatar lớn ở giữa màn hình: Icon máy ảnh góc dưới bấm để mở Photo Picker của Android -> Upload trực tiếp qua `POST /api/v1/users/me/avatar`.
   - AppTextField sửa Họ và tên, SĐT. Ô Email dạng read-only.
   - Nút "LƯU THAY ĐỔI" (`PUT /api/v1/users/me`).

3. AddressListScreen.kt & AddEditAddressScreen.kt:
   - Danh sách địa chỉ: Mỗi thẻ có Họ tên, SĐT, Địa chỉ đầy đủ, Tag đỏ "Mặc định", Nút "Sửa", Nút "Xóa", Nút "Đặt làm mặc định" (`PUT /addresses/{id}/default`).
   - Nút "Thêm địa chỉ mới" cố định ở đáy màn hình.
   - Màn hình Thêm/Sửa địa chỉ: Form nhập Họ tên, SĐT, Tỉnh/Thành, Quận/Huyện, Phường/Xã, Số nhà tên đường, Switch "Đặt làm mặc định". Gọi `POST /api/v1/users/addresses` hoặc `PUT /api/v1/users/addresses/{id}`.
```

---

## PROMPT 11: TIN NHẮN VỚI SHOP (CONVERSATION LIST, CHAT DETAIL)

```markdown
Hãy lập trình phân hệ Trò chuyện giữa Người mua và Người bán trong `com.example.b2capp.features.chat`:

1. ConversationListScreen.kt:
   - TopBar: Tiêu đề "Tin nhắn".
   - Danh sách cuộc trò chuyện: Avatar logo shop, Tên shop (#0E3B64 Bold), Dòng tin nhắn cuối cùng (13sp xám), Thời gian gửi, Badge tròn màu đỏ số tin nhắn chưa đọc.
   - Bấm vào mở ChatDetailScreen.
   - API: `GET /api/v1/buyer/conversations`.

2. ChatDetailScreen.kt:
   - TopBar: Logo shop nhỏ, Tên shop, Chấm xanh Online (nếu online), Nút "Xem Shop".
   - Vùng tin nhắn (LazyColumn cuộn ngược từ dưới lên):
     * Bong bóng chat người mua (bên phải): Nền cam #EE4D2D, chữ trắng, bo góc 16.dp (góc phải dưới bo 4.dp).
     * Bong bóng chat người bán (bên trái): Nền trắng #FFFFFF, viền xám nhẹ #E5E7EB, chữ #222222, bo góc 16.dp (góc trái dưới bo 4.dp).
     * Thời gian tin nhắn in nhỏ (11sp xám) bên dưới bong bóng.
   - Thanh nhập liệu đáy màn hình: Ô nhập text bo tròn 20.dp, nút Gửi icon phi cơ màu cam #EE4D2D.
   - API:
     * Tạo/Mở hội thoại: `POST /api/v1/conversations?sellerId={id}`
     * Lấy tin nhắn: `GET /api/v1/conversations/{id}/messages`
     * Gửi tin nhắn: `POST /api/v1/conversations/{id}/messages` (Body: `{ "content": "..." }`).
```

---

## HƯỚNG DẪN SỬ DỤNG BỘ PROMPT NÀY

1. **Khởi tạo dự án:** Copy `PROMPT 0` (Master System Prompt) gửi cho AI để thiết lập tư duy kiến trúc và quy chuẩn màu sắc Concept 1.
2. **Dựng nền móng Design System:** Copy `PROMPT 1` để AI code các Component dùng chung (`AppButton`, `ProductCard`, `AppTextField`...).
3. **Triển khai từng màn hình:** Copy lần lượt từ `PROMPT 2` đến `PROMPT 11` theo tiến độ công việc của bạn. Mỗi prompt đều độc lập, hoàn chỉnh và có đầy đủ thông số màu sắc, UI và API mapping để AI code ngay lập tức mà không cần hỏi lại.

