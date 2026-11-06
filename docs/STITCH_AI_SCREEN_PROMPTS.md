# BỘ PROMPT THIẾT KẾ ĐỘC LẬP CHO STITCH AI (UI/UX DESIGN)

> **Mục đích:** Bộ prompt thiết kế giao diện dành riêng cho **Stitch AI** (hoặc các AI Design Tool như v0, Galileo, Uizard, Figma AI).  
> **Nguyên tắc cốt lõi:** **Mỗi prompt là một khối độc lập (Self-contained)**. Bạn chỉ cần sao chép (copy) đúng một prompt và gửi cho Stitch AI theo từng lượt, AI sẽ có đầy đủ bối cảnh, bảng màu Concept 1, kích thước, thành phần và style UI mà không cần đọc lại các prompt trước đó.

---

## MỤC LỤC BỘ PROMPT TỪNG MÀN HÌNH

| STT | Tên màn hình | Phân hệ |
| :---: | :--- | :--- |
| **01** | [Màn hình Đăng nhập (Login)](#01-màn-hình-đăng-nhập-login-screen) | Xác thực (Auth) |
| **02** | [Màn hình Đăng ký (Register)](#02-màn-hình-đăng-ký-register-screen) | Xác thực (Auth) |
| **03** | [Màn hình Quên mật khẩu (Forgot Password)](#03-màn-hình-quên-mật-khẩu-forgot-password-screen) | Xác thực (Auth) |
| **04** | [Màn hình Trang chủ (Home)](#04-màn-hình-trang-chủ-home-screen) | Khám phá (Catalog) |
| **05** | [Màn hình Cây danh mục (Category Tree)](#05-màn-hình-cây-danh-mục-category-tree-screen) | Khám phá (Catalog) |
| **06** | [Màn hình Tìm kiếm & Kết quả lọc (Search & Catalog)](#06-màn-hình-tìm-kiếm--kết-quả-lọc-search--catalog-screen) | Tìm kiếm (Search) |
| **07** | [Modal Bộ lọc nâng cao (Filter Bottom Sheet)](#07-modal-bộ-lọc-nâng-cao-filter-bottom-sheet) | Tìm kiếm (Search) |
| **08** | [Màn hình Chi tiết sản phẩm (Product Detail)](#08-màn-hình-chi-tiết-sản-phẩm-product-detail-screen) | Sản phẩm (Product) |
| **09** | [Bottom Sheet Chọn biến thể (Variant Selector)](#09-bottom-sheet-chọn-biến-thể-variant-selector) | Sản phẩm (Product) |
| **10** | [Màn hình Danh sách Đánh giá (Reviews List)](#10-màn-hình-danh-sách-đánh-giá-reviews-list-screen) | Đánh giá (Reviews) |
| **11** | [Màn hình Viết đánh giá sản phẩm (Write Review)](#11-màn-hình-viết-đánh-giá-sản-phẩm-write-review-screen) | Đánh giá (Reviews) |
| **12** | [Màn hình Giỏ hàng gom nhóm Shop (Cart Screen)](#12-màn-hình-giỏ-hàng-gom-nhóm-shop-cart-screen) | Giỏ hàng (Cart) |
| **13** | [Màn hình Xác nhận Đặt hàng (Checkout Screen)](#13-màn-hình-xác-nhận-đặt-hàng-checkout-screen) | Thanh toán (Checkout) |
| **14** | [Bottom Sheet Chọn Voucher giảm giá (Voucher Sheet)](#14-bottom-sheet-chọn-voucher-giảm-giá-voucher-sheet) | Thanh toán (Checkout) |
| **15** | [Màn hình Cổng thanh toán VNPay (VNPay WebView)](#15-màn-hình-cổng-thanh-toán-vnpay-vnpay-webview) | Thanh toán (Checkout) |
| **16** | [Màn hình Đặt hàng thành công (Order Success)](#16-màn-hình-đặt-hàng-thành-công-order-success-screen) | Thanh toán (Checkout) |
| **17** | [Màn hình Lịch sử đơn hàng (Order History)](#17-màn-hình-lịch-sử-đơn-hàng-order-history-screen) | Đơn hàng (Orders) |
| **18** | [Màn hình Chi tiết đơn & Lộ trình (Order Detail & Tracking)](#18-màn-hình-chi-tiết-đơn--lộ-trình-order-detail--tracking) | Đơn hàng (Orders) |
| **19** | [Màn hình Tài khoản cá nhân (Profile / Me)](#19-màn-hình-tài-khoản-cá-nhân-profile--me-screen) | Tài khoản (User) |
| **20** | [Màn hình Chỉnh sửa hồ sơ & Avatar (Edit Profile)](#20-màn-hình-chỉnh-sửa-hồ-sơ--avatar-edit-profile-screen) | Tài khoản (User) |
| **21** | [Màn hình Đổi mật khẩu (Change Password)](#21-màn-hình-đổi-mật-khẩu-change-password-screen) | Tài khoản (User) |
| **22** | [Màn hình Sổ địa chỉ nhận hàng (Address List)](#22-màn-hình-sổ-địa-chỉ-nhận-hàng-address-list-screen) | Tài khoản (User) |
| **23** | [Màn hình Thêm / Sửa địa chỉ (Add/Edit Address)](#23-màn-hình-thêm--sửa-địa-chỉ-addedit-address-screen) | Tài khoản (User) |
| **24** | [Màn hình Sản phẩm yêu thích (Wishlist)](#24-màn-hình-sản-phẩm-yêu-thích-wishlist-screen) | Khám phá (Wishlist) |
| **25** | [Màn hình Danh sách tin nhắn Shop (Conversations)](#25-màn-hình-danh-sách-tin-nhắn-shop-conversations-screen) | Trò chuyện (Chat) |
| **26** | [Màn hình Chat trực tiếp với Shop (Chat Detail)](#26-màn-hình-chat-trực-tiếp-với-shop-chat-detail-screen) | Trò chuyện (Chat) |

---

### 01. MÀN HÌNH ĐĂNG NHẬP (LOGIN SCREEN)

```markdown
Design a modern, clean mobile screen for an E-Commerce Login page (iOS/Android, 390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Used for the main CTA button and active focus borders).
  * Secondary: Deep Navy #0E3B64 (Used for header accents and secondary links).
  * Background: Clean Soft Gray #F8F9FA, Card surface: Pure White #FFFFFF.
  * Text: Dark Charcoal #222222, Placeholder/Muted: #888888.
- Layout & Elements (from top to bottom):
  1. Top Branding: A modern shopping bag logo icon in #EE4D2D with a subtle glow, followed by a bold headline "Chào mừng trở lại!" (24px, bold) and a friendly subtitle "Đăng nhập để tiếp tục trải nghiệm mua sắm" (14px, #888888).
  2. Input Form (Card style with 12px rounded corners and subtle shadow):
     - Email/Username field: Leading mail icon, rounded 8px border #E5E7EB, placeholder "Email hoặc số điện thoại".
     - Password field: Leading lock icon, trailing eye toggle icon, rounded 8px border #E5E7EB, placeholder "Mật khẩu".
     - Text link: "Quên mật khẩu?" right-aligned below the password field (#0E3B64, 13px semi-bold).
  3. Action CTA:
     - Prominent Button "ĐĂNG NHẬP" in solid Coral Orange #EE4D2D, 50px height, 8px rounded corners, white bold text.
  4. Footer:
     - Centered text: "Chưa có tài khoản? " with a clickable link "Đăng ký ngay" in #EE4D2D (bold, underlined).
- Style: Minimalist, spacious padding (20px), highly polished Material 3 design.
```

---

### 02. MÀN HÌNH ĐĂNG KÝ (REGISTER SCREEN)

```markdown
Design a modern, user-friendly mobile Registration screen for an E-Commerce app (390x844px).
- Theme & Colors:
  * Primary Brand: Coral Orange #EE4D2D (Primary button and focus highlight).
  * Secondary: Deep Navy #0E3B64 (Header accents and link color).
  * Background: Soft Neutral #F8F9FA, Surface: #FFFFFF.
  * Border: #E5E7EB, Text: Primary #222222, Secondary #888888.
- Layout & Elements:
  1. Header: Back button arrow top-left. Title "Tạo tài khoản mới" (22px bold) with subtitle "Đăng ký nhanh chóng chỉ trong vài bước".
  2. Form Fields (Vertical stack, 8px rounded borders, clean white inputs):
     - "Họ và tên": leading user icon, placeholder "Ví dụ: Nguyễn Văn A".
     - "Email": leading mail icon, placeholder "name@example.com".
     - "Mật khẩu": leading lock icon, trailing eye toggle, helper caption "Tối thiểu 8 ký tự".
     - "Xác nhận mật khẩu": leading lock icon, trailing checkmark icon.
  3. Terms Checkbox:
     - Checkbox with text: "Tôi đồng ý với Điều khoản dịch vụ và Chính sách bảo mật của B2C" (13px, #888888 with #0E3B64 links).
  4. CTA Button:
     - Full-width button "ĐĂNG KÝ" in solid Coral Orange #EE4D2D, 50px height, white bold text, rounded 8px.
  5. Bottom Navigation:
     - "Đã có tài khoản? " with clickable "Đăng nhập" in #EE4D2D bold.
- Style: Clean form validation styling, comfortable touch targets, modern mobile e-commerce look.
```

---

### 03. MÀN HÌNH QUÊN MẬT KHẨU (FORGOT PASSWORD SCREEN)

```markdown
Design a clean, secure Password Recovery mobile screen for an E-Commerce app (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D, Secondary: Deep Navy #0E3B64.
  * Background: #F8F9FA, Card surface: #FFFFFF, Text: #222222.
- Layout & Elements:
  1. Header: Top-left back icon. Center title "Quên mật khẩu".
  2. Hero Illustration: A friendly minimalist vector icon of a security key or envelope with lock in #EE4D2D and soft coral container #FFEBE7.
  3. Description: "Nhập địa chỉ email đã đăng ký của bạn. Chúng tôi sẽ gửi mã xác nhận để đặt lại mật khẩu mới." (14px, #888888, centered).
  4. Input Field:
     - Email text input with mail icon, rounded 8px, border #E5E7EB, active border #EE4D2D.
  5. Action Button:
     - "GỬI MÃ XÁC NHẬN" in #EE4D2D solid, white text, 50px height, 8px rounded corners.
  6. Sub-action:
     - "Quay lại Đăng nhập" link in #0E3B64 bold centered at bottom.
- Style: Focused single-purpose layout, zero visual clutter, reassuring security feel.
```

---

### 04. MÀN HÌNH TRANG CHỦ (HOME SCREEN)

```markdown
Design a vibrant, high-conversion Home Screen for a B2C E-Commerce mobile app (390x844px).
- Theme & Colors:
  * Primary Accent: Coral Orange #EE4D2D (Search icon, flash sale tags, cart badge).
  * Secondary Navy: #0E3B64 (Top app bar accent, section highlights).
  * Supporting: Mint Green #26AA99 (Freeship badge), Amber #FFB800 (Star ratings).
  * Background: #F8F9FA, Cards: #FFFFFF with subtle borders #E5E7EB.
- Layout & Components:
  1. Top App Bar (Sticky header, white background with subtle shadow):
     - Search Bar (Pill shape, 40px height, #F1F3F5 background): Leading magnifying glass, placeholder "Tìm sản phẩm, thương hiệu...".
     - Right Actions: Shopping Cart icon with a red circle notification badge showing "3" in #EE4D2D, Chat icon.
  2. Hero Promotion Banner (Carousel Slider):
     - Wide banner (16:9 ratio, 8px rounded corners) showcasing a "Mega Sale 50% Off" with vibrant coral & navy graphics, with 3 dot pagination indicators below.
  3. Category Quick Access (Horizontal Grid / 2 rows of 4 icons):
     - Circular pastel icons: "Thời trang", "Điện tử", "Gia dụng", "Làm đẹp", "Giày dép", "Đồng hồ", "Mẹ & Bé", "Xem tất cả".
  4. Flash Sale Section:
     - Header: Flame icon + "FLASH SALE" in bold #EE4D2D, Countdown timer badge "02 : 45 : 12" in dark background, "Xem tất cả >" link.
     - Horizontal scrolling row of product cards with discount badge "-35%" in red-orange, sale price #EE4D2D, and mini stock progress bar "Đã bán 80%".
  5. "Gợi ý hôm nay" (Daily Discovery) Section:
     - 2-Column masonry product grid.
     - Each Product Card: 1:1 square image, 12px rounded corners, product title (2 lines max), price #EE4D2D (16px bold), star rating (4.8 ★ in #FFB800), and sold count ("Đã bán 1.2k").
  6. Bottom Navigation Bar (5 tabs):
     - [Trang chủ - Active in #EE4D2D], [Danh mục], [Giỏ hàng], [Đơn hàng], [Tài khoản].
- Style: Highly visual, polished e-commerce feed, clear visual hierarchy.
```

---

### 05. MÀN HÌNH CÂY DANH MỤC (CATEGORY TREE SCREEN)

```markdown
Design a split-screen Category Browsing mobile interface for an E-Commerce app (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Active category tab indicator & accents).
  * Left sidebar: Light Cool Gray #F1F3F5, Right content area: Pure White #FFFFFF.
  * Text: #222222, Subtitle: #888888.
- Layout:
  1. Top Header: Search input pill "Tìm kiếm trong danh mục..." with back arrow.
  2. Split View Layout:
     - Left Sidebar (Width: 96px, vertical scroll):
       * List of Level-1 root categories ("Thời trang nam", "Điện thoại & Tablet", "Mỹ phẩm", "Nhà cửa", "Thể thao", "Mẹ & Bé").
       * Active item has a pure white background, bold text in #EE4D2D, and a 3px vertical accent bar in #EE4D2D on the left edge.
       * Inactive items have gray background #F1F3F5 with subtle icons.
     - Right Panel (Level-2 and Level-3 Subcategories):
       * Top promotional banner for the selected category ("Ưu đãi thời trang nam up to 40%").
       * Subcategory sections with titles (e.g. "Áo nam", "Quần nam", "Phụ kiện").
       * 3-Column icon grid under each section: Clean round product thumbnail + category label below (e.g., "Áo thun", "Áo sơ mi", "Áo polo", "Áo khoác").
- Style: Ultra-intuitive browsing, high contrast, clean separation between levels.
```

---

### 06. MÀN HÌNH TÌM KIẾM & KẾT QUẢ LỌC (SEARCH & CATALOG SCREEN)

```markdown
Design an E-Commerce Search & Filter Results mobile screen (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Active sort tab, filter indicator, price tag).
  * Secondary: Deep Navy #0E3B64, Background: #F8F9FA, Card: #FFFFFF.
  * Rating: #FFB800, Text: #222222 / #888888.
- Layout:
  1. Header Bar:
     - Back arrow, Search input box containing the keyword "áo thun nam" with clear 'X' button, right Filter icon button with badge dot.
  2. Sorting Tab Bar (Horizontal, 44px height, white background):
     - Tabs: [Mới nhất], [Bán chạy - Active], [Giá ↑], [Giá ↓], [Đánh giá cao].
     - Active tab has bold text in #EE4D2D with an orange underline indicator.
  3. Filter Chips Row:
     - Scrollable pills: "Freeship Extra" (teal badge #26AA99), "Đánh giá 4★+", "Dưới 200k", "Coolmate".
  4. Search Results Grid (2 Columns, padding 12px):
     - Product Cards: Crisp photo, 12px rounded corners, official shop badge "Mall" in red, product name, Price #EE4D2D bold, rating 4.9 ★ (#FFB800), sold count ("Đã bán 3.4k"), shipping location ("TP. Hồ Chí Minh").
  5. Empty state variation note: If no results, show cute magnifying glass graphic with "Không tìm thấy sản phẩm phù hợp" and a "Thiết lập lại bộ lọc" button.
- Style: Fast, clear, high item density without feeling cluttered.
```

---

### 07. MODAL BỘ LỌC NÂNG CAO (FILTER BOTTOM SHEET)

```markdown
Design a comprehensive Filter Bottom Sheet modal for an E-Commerce app (390x600px overlay).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Selected chips, apply button).
  * Surface: #FFFFFF with 16px top rounded corners and a top drag handle.
  * Chip background: Inactive #F1F3F5, Active #FFEBE7 with #EE4D2D border.
- Layout:
  1. Header: Drag handle pill, Title "Bộ lọc tìm kiếm" (18px bold), right "Đóng" (X) icon.
  2. Scrollable Filter Content:
     - Category Section: Horizontal chips ("Áo thun", "Áo sơ mi", "Áo polo", "Áo hoodie"). Selected chip has orange border and orange text.
     - Price Range Section: Slider + 2 text input boxes [Từ ₫ 100.000] - [Đến ₫ 500.000] with formatted currency.
     - Brand Section: Grid chips ("Coolmate [Selected]", "Uniqlo", "Nike", "Adidas", "Anker").
     - Rating Section: 3 options with star icons [Từ 5 sao ★★★★★], [Từ 4 sao ★★★★☆], [Từ 3 sao ★★★☆☆].
     - Shipping & Promo Section: Checkboxes for "Miễn phí vận chuyển" and "Đang giảm giá".
  3. Sticky Footer Actions (Two buttons):
     - Left: "Thiết lập lại" button (Outlined gray border #E5E7EB, text #222222, 45% width).
     - Right: "ÁP DỤNG (124 sản phẩm)" button (Solid Coral Orange #EE4D2D, white bold text, 55% width).
- Style: Modern sheet, clear active states, smooth spacing.
```

---

### 08. MÀN HÌNH CHI TIẾT SẢN PHẨM (PRODUCT DETAIL SCREEN)

```markdown
Design an engaging, high-conversion Product Detail mobile screen (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Price, Buy Now button, Wishlist active).
  * Secondary: Deep Navy #0E3B64 (Shop name, chat icon, technical labels).
  * Star: #FFB800, Freeship: #26AA99, Background: #F8F9FA, Card: #FFFFFF.
- Layout:
  1. Floating Top Navigation (Transparent overlay on image):
     - Left: Circular back button.
     - Right: Share icon, Heart Wishlist icon (active filled in #EE4D2D), Cart icon with red badge.
  2. Image Gallery Pager:
     - 1:1 square high-res product photo. Bottom-right page counter pill "1/6".
  3. Product Info Card (White surface):
     - Price block: Bold prominent price range "₫199.000 - ₫249.000" in #EE4D2D (24px bold) with strikethrough original price "₫350.000" and discount badge "-29%".
     - Product Title: "Áo Thun Nam Cotton Compact Siêu Thoáng Khí Cao Cấp" (18px bold, 2 lines).
     - Rating & Social Proof: Row with "4.9 ★★★★★ (#FFB800)", "342 Đánh giá", and "1.5k Đã bán".
  4. Variant Quick Selection Row:
     - Tap-to-select bar: "Chọn phân loại: Màu sắc, Kích cỡ >" with thumbnail previews.
  5. Seller Card:
     - Shop logo avatar, Shop Name "Coolmate Official" with green online dot, rating "4.9/5 (12k đánh giá)".
     - Actions: "Xem Shop" button (outlined navy #0E3B64) and "Chat ngay" button (outlined coral #EE4D2D).
  6. Product Specifications & Description:
     - Specs table: Chất liệu: 100% Cotton, Xuất xứ: Việt Nam, Kiểu dáng: Regular fit.
     - Collapsible description section with "Xem thêm v".
  7. Bottom Fixed Action Bar (Sticky at bottom, 60px height):
     - Left: Chat with seller icon button (White bg, badge).
     - Middle: "Thêm vào giỏ" button (Soft coral bg #FFEBE7, text #EE4D2D, cart icon).
     - Right: "MUA NGAY" button (Solid Coral Orange #EE4D2D, white bold text).
- Style: Professional e-commerce standards, premium feel, clear CTAs.
```

---

### 09. BOTTOM SHEET CHỌN BIẾN THỂ (VARIANT SELECTOR)

```markdown
Design a Variant & Option Selection Bottom Sheet for an E-Commerce app (390x520px modal).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Selected option border, price text, confirm button).
  * Inactive Chip: Background #F1F3F5, Text #222222.
  * Disabled Chip: Strikethrough text #BBBBBB, background #FAFAFA (Out of stock).
- Layout:
  1. Header Preview:
     - Square thumbnail photo of the selected variant (72x72px, 8px rounded).
     - Price: "₫199.000" in bold #EE4D2D (20px).
     - Stock indicator: "Kho: Còn 15 sản phẩm" (13px, #888888).
     - Close (X) icon top-right.
  2. Option Group 1 - "Màu sắc":
     - Title: "Màu sắc (3)"
     - Chips row: [Đen - Selected (Coral border, #FFEBE7 bg)], [Trắng], [Xanh Navy - Disabled/Out of stock].
  3. Option Group 2 - "Kích cỡ":
     - Title: "Kích cỡ (4)"
     - Chips row: [S], [M - Selected], [L], [XL].
  4. Quantity Selector Row:
     - Label "Số lượng" on the left.
     - Quantity Picker on the right: [-] [ 2 ] [+] with circular buttons and clear counter.
  5. Full-width Confirm CTA:
     - Solid button "XÁC NHẬN" in Coral Orange #EE4D2D, 48px height, white bold text.
- Style: Clear disabled/selected states, instant visual feedback, modern sheet curves (16px).
```

---

### 10. MÀN HÌNH DANH SÁCH ĐÁNH GIÁ (REVIEWS LIST SCREEN)

```markdown
Design an authentic Product Reviews & Ratings mobile screen (390x844px).
- Theme & Colors:
  * Star Rating: Vibrant Gold #FFB800, Brand Accent: Coral Orange #EE4D2D.
  * Background: #F8F9FA, Review cards: #FFFFFF, Text: #222222 / #888888.
- Layout:
  1. Top App Bar: Back arrow, Title "Đánh giá sản phẩm (342)".
  2. Rating Summary Card:
     - Left: Big average score "4.9" (32px bold) with 5 gold stars #FFB800.
     - Right: Horizontal rating breakdown bars (5★, 4★, 3★, 2★, 1★ progress bars).
  3. Filter Tabs (Horizontal chips):
     - [Tất cả (342) - Active #EE4D2D], [5 Sao (310)], [4 Sao (25)], [Có hình ảnh (180)].
  4. Reviews Feed (Card list with 12px rounded corners):
     - Reviewer avatar, Name "nguyen***a" (or "Ẩn danh"), Verified Buyer badge.
     - 5 gold stars, Date "12/04/2026", Variant purchased label: "Phân loại: Đen, Size L" (gray 12px).
     - Review text: "Chất vải cotton dày dặn, form áo mặc rất vừa vặn và thoáng mát. Giao hàng cực nhanh!".
     - Review photos: 2 small user-submitted photos (64x64px, rounded).
     - Seller Reply Box (nested card with gray background #F1F3F5):
       * "Phản hồi từ người bán: Dạ shop cảm ơn bạn đã tin tưởng ủng hộ shop ạ!".
- Style: Trustworthy, credible social proof layout, high legibility.
```

---

### 11. MÀN HÌNH VIẾT ĐÁNH GIÁ SẢN PHẨM (WRITE REVIEW SCREEN)

```markdown
Design an interactive "Write a Review" mobile screen for an e-commerce order item (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Submit button), Stars: Gold #FFB800.
  * Surface: #FFFFFF, Background: #F8F9FA, Text: #222222.
- Layout:
  1. Top Bar: Back arrow, Title "Đánh giá sản phẩm".
  2. Order Item Card:
     - Mini product photo (48x48px), Product Name "Áo Thun Nam Cotton Compact", variant "Đen / L".
  3. Interactive Star Rating Section (Centered):
     - "Chất lượng sản phẩm thế nào?" (16px semi-bold).
     - 5 large interactive star icons (36px, Gold #FFB800).
     - Rating mood label below: "Tuyệt vời" (Green #26AA99 when 5 stars).
  4. Detailed Review Input:
     - Multi-line text area (min height 120px, rounded 8px border #E5E7EB, placeholder "Hãy chia sẻ cảm nhận thực tế về chất lượng vải, form dáng và trải nghiệm giao hàng...").
  5. Anonymous Toggle Switch:
     - Row with text "Đánh giá ẩn danh" (Tên của bạn sẽ hiển thị dưới dạng a***z), trailing Switch toggle in #EE4D2D.
  6. Submit CTA:
     - Bottom-pinned Button "GỬI ĐÁNH GIÁ" in solid Coral Orange #EE4D2D, 50px height, white text.
- Style: Encouraging, user-friendly, zero friction.
```

---

### 12. MÀN HÌNH GIỎ HÀNG GOM NHÓM SHOP (CART SCREEN)

```markdown
Design an E-Commerce Shopping Cart mobile screen strictly featuring Single-Seller Shop Grouping (390x844px).
- Backend Constraint: Orders can only be created for ONE shop at a time. The UI must group items visually by Shop and only allow selecting items from a single shop.
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Checkboxes, Total amount, Checkout button).
  * Secondary: Deep Navy #0E3B64 (Shop names, Shop icon).
  * Error: Red #D32F2F (Out of stock labels, delete icon), Background: #F8F9FA.
- Layout:
  1. Header: Title "Giỏ hàng (5)", right Text button "Xóa đã chọn" (#D32F2F).
  2. Cart Items Grouped by Shop (Card style with 12px rounded corners):
     - Shop Section 1 ("Coolmate Official Store" - Active Selection):
       * Header row: Round Checkbox [Checked #EE4D2D], Shop icon, Shop Name bold in #0E3B64, Chevron arrow >.
       * Item 1: Checkbox [Checked], square thumbnail, Product name (2 lines), variant pill "Đen / L >", Unit price ₫199.000 in #EE4D2D bold, Quantity Picker [- 2 +].
       * Item 2: Checkbox [Checked], thumbnail, Product name, variant "Trắng / XL", Price ₫219.000, Quantity Picker [- 1 +].
     - Shop Section 2 ("Anker Audio Flagship" - Inactive/Unselected Shop):
       * Header row: Round Checkbox [Unchecked], Shop Name.
       * Item 3: Checkbox [Unchecked], Headphone image, Price ₫890.000, Quantity [- 1 +].
       * Notice Banner below: "Chỉ có thể chọn thanh toán các sản phẩm từ 1 Shop cùng lúc".
  3. Sticky Bottom Checkout Bar:
     - Left: Checkbox "Tất cả (shop này)".
     - Middle: Column with "Tổng thanh toán: " and big total price "₫617.000" in bold #EE4D2D.
     - Right: Button "MUA HÀNG (3)" in solid Coral Orange #EE4D2D, 48px height, white bold text.
- Style: Clear grouping, unmistakable shop boundaries, prevents order creation errors.
```

---

### 13. MÀN HÌNH XÁC NHẬN ĐẶT HÀNG (CHECKOUT SCREEN)

```markdown
Design a professional Order Checkout mobile screen with price preview and payment options (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Delivery pin, voucher icon, final total, Place Order CTA).
  * Secondary: Deep Navy #0E3B64 (Shop title, payment badge).
  * Accent: Mint Green #26AA99 (Discount deduction).
  * Background: #F8F9FA, Cards: #FFFFFF.
- Layout:
  1. Header: Back arrow, Title "Thanh toán".
  2. Delivery Address Card (White card with top colorful striped border):
     - Red location pin icon, Recipient name "Nguyễn Văn A | 0987 654 321" (bold), Full address "Số 123 Đường Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh", chevron arrow >.
  3. Ordered Items Card:
     - Shop Name "Coolmate Official Store" (#0E3B64).
     - Compact item list: thumbnail, item name, variant label, quantity "x2", line price #222222.
  4. Note for Seller:
     - Clean input field: "Lời nhắn cho người bán...".
  5. Voucher & Promotions Bar:
     - Coupon ticket icon in #EE4D2D, label "Mã giảm giá B2C", active badge "-₫39.800 (GIAM10)", chevron arrow >.
  6. Payment Method Selector (Radio card group):
     - Option 1: [💵 Thanh toán khi nhận hàng (COD) - Checked #EE4D2D].
     - Option 2: [💳 Cổng thanh toán VNPAY (Thẻ ATM / QR / Mobile Banking)].
  7. Payment Summary Card:
     - Tổng tiền hàng: ₫398.000
     - Phí vận chuyển: ₫30.000
     - Giảm giá voucher: -₫39.800 (in Green #26AA99)
     - Tổng thanh toán: ₫388.200 (in bold Coral #EE4D2D, 20px).
  8. Fixed Bottom Bar:
     - Left: Label "Tổng thanh toán" + "₫388.200".
     - Right: Prominent Button "ĐẶT HÀNG" in Coral Orange #EE4D2D, 50px height, white bold text.
- Style: Trustworthy, unambiguous fee breakdown, seamless spacing.
```

---

### 14. BOTTOM SHEET CHỌN VOUCHER GIẢM GIÁ (VOUCHER SHEET)

```markdown
Design a Voucher & Coupon Selection Bottom Sheet for checkout (390x600px overlay).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Voucher border, Apply button).
  * Ticket Card: Soft Coral #FFEBE7 with perforated ticket cutout circles on left/right edges.
- Layout:
  1. Top Header: Title "Chọn Mã Giảm Giá", close button.
  2. Manual Code Input Row:
     - Text field: "Nhập mã voucher...", Right button "Áp dụng" (solid #0E3B64).
  3. Voucher List (Scrollable ticket cards):
     - Ticket 1 (Applicable):
       * Left: Orange badge icon "%".
       * Middle: "GIẢM 10% TỐI ĐA 50K", "Đơn tối thiểu ₫200.000", "HSD: 15/11/2026".
       * Right: Radio button [Selected in #EE4D2D].
     - Ticket 2 (Free Shipping):
       * Left: Teal badge icon "FREESHIP".
       * Middle: "Miễn phí vận chuyển 30k", "Đơn tối thiểu ₫150.000".
       * Right: Radio button [Selectable].
     - Ticket 3 (Ineligible):
       * Muted gray ticket, "Mua thêm ₫120.000 để sử dụng voucher này".
  4. Bottom Action:
     - Button "XÁC NHẬN ÁP DỤNG" in Coral Orange #EE4D2D.
- Style: Realistic voucher ticket motif, clear eligibility feedback.
```

---

### 15. MÀN HÌNH CỔNG THANH TOÁN VNPAY (VNPAY WEBVIEW)

```markdown
Design a realistic mobile payment gateway WebView interface for VNPay sandbox (390x844px).
- Theme & Colors:
  * Top navigation: Pure white with security lock icon and "Đóng / Hủy" button.
  * Gateway Brand: VNPay official colors (Blue & Red logo).
- Layout:
  1. Header Bar:
     - Left: Close (X) icon.
     - Center: Lock icon + "sandbox.vnpayment.vn" (13px, secure green lock).
     - Right: Reload icon.
  2. Mock Payment Screen (Within WebView container):
     - VNPay header logo.
     - Order Info Box: "Đơn hàng: ORD-20261105-0088", "Số tiền: ₫388.200".
     - Payment Method Options:
       * [VNPAY-QR: Quét mã QR qua ứng dụng ngân hàng (Show QR Code mock)].
       * [Thẻ ATM và tài khoản ngân hàng nội địa].
       * [Thẻ quốc tế (Visa, Mastercard, JCB)].
     - Countdown timer pill: "Giao dịch hết hạn trong 14:59".
     - Button "HỦY GIAO DỊCH" at bottom.
- Style: Authentic banking gateway experience, clear security cues.
```

---

### 16. MÀN HÌNH ĐẶT HÀNG THÀNH CÔNG (ORDER SUCCESS SCREEN)

```markdown
Design a celebratory Order Success confirmation screen for an e-commerce app (390x844px).
- Theme & Colors:
  * Success Accent: Mint Green #26AA99 (Checkmark), Coral Orange #EE4D2D (Main button).
  * Secondary Navy: #0E3B64 (Order code, secondary CTA).
  * Background: Clean White #FFFFFF.
- Layout:
  1. Hero Animation Area:
     - Large circle with an animated green checkmark icon #26AA99 surrounded by soft festive confetti.
     - Headline: "Đặt hàng thành công!" (24px bold #222222).
     - Subtitle: "Cảm ơn bạn đã mua sắm. Đơn hàng của bạn đang được người bán xử lý." (14px #888888, centered).
  2. Order Summary Receipt Card (8px rounded, light gray border #E5E7EB, background #F8F9FA):
     - Mã đơn hàng: ORD-20261105-0088 (Bold #0E3B64 with copy icon).
     - Phương thức: VNPAY (Đã thanh toán).
     - Tổng thanh toán: ₫388.200 (#EE4D2D bold).
     - Thời gian đặt: 05/11/2026 14:20.
  3. Action Buttons (Two full-width buttons):
     - Button 1: "XEM CHI TIẾT ĐƠN HÀNG" in Outlined Navy #0E3B64, 48px height.
     - Button 2: "TIẾP TỤC MUA SẮM" in Solid Coral Orange #EE4D2D, 48px height, white text.
- Style: Reassuring, joyful celebration, clear next steps.
```

---

### 17. MÀN HÌNH LỊCH SỬ ĐƠN HÀNG (ORDER HISTORY SCREEN)

```markdown
Design an Order History & Tracking mobile screen with status tabs (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Active tab indicator, primary action buttons).
  * Status Badges:
    - PENDING: Orange #FF9800 ("Chờ thanh toán")
    - CONFIRMED: Navy #0E3B64 ("Đang chuẩn bị")
    - SHIPPED: Blue #1A56DB ("Đang giao")
    - DELIVERED: Green #26AA99 ("Đã giao")
    - CANCELLED: Red #D32F2F ("Đã hủy")
  * Background: #F8F9FA, Cards: #FFFFFF.
- Layout:
  1. Top App Bar: Back arrow, Title "Đơn mua", Search order icon.
  2. Scrollable Status Tab Bar (44px height):
     - Tabs: [Tất cả] | [Chờ xác nhận] | [Đang chuẩn bị] | [Đang giao] | [Đã giao] | [Đã hủy].
     - Active tab highlighted in #EE4D2D.
  3. Order Cards List:
     - Card 1 (Status: DELIVERED):
       * Header: Shop icon, "Coolmate Official Store", Status badge "Giao hàng thành công" in Green #26AA99.
       * Body: Representative product thumbnail (64x64px), Title "Áo Thun Nam Cotton Compact", variant "Đen / L", "và 2 sản phẩm khác" (muted 12px).
       * Total row: "Thành tiền: ₫388.200" (#EE4D2D bold).
       * Action buttons: Outlined "Yêu cầu trả hàng" + Solid Coral "ĐÁNH GIÁ".
     - Card 2 (Status: PENDING - Unpaid VNPay):
       * Header: "Anker Official", Status badge "Chờ thanh toán" in Orange #FF9800.
       * Body: Headphone image, "Tai nghe Bluetooth...", Total: ₫890.000.
       * Action buttons: Outlined "Hủy đơn" + Solid Coral "THANH TOÁN NGAY".
- Style: High scanability, immediate clarity on order status and actions.
```

---

### 18. MÀN HÌNH CHI TIẾT ĐƠN & LỘ TRÌNH (ORDER DETAIL & TRACKING)

```markdown
Design a comprehensive Order Details & Timeline Tracking mobile screen (390x844px).
- Theme & Colors:
  * Timeline line & Completed steps: Mint Green #26AA99.
  * Primary: Coral Orange #EE4D2D (Order total, key action).
  * Secondary: Deep Navy #0E3B64, Background: #F8F9FA, Card: #FFFFFF.
- Layout:
  1. Header: Back arrow, Title "Chi tiết đơn hàng", Order Code "#ORD-20261105-0088".
  2. Status Banner (Top prominent card):
     - Big status icon, Headline "Đơn hàng đang được giao đến bạn", sub-text "Dự kiến giao ngày 07/11/2026".
  3. Order Journey Timeline Card:
     - Vertical step indicator with green checkmarks #26AA99:
       * Step 1: Đặt hàng thành công - 05/11/2026 14:20 [Checked]
       * Step 2: Người bán đã xác nhận - 05/11/2026 16:00 [Checked]
       * Step 3: Đã giao cho đơn vị vận chuyển - 06/11/2026 08:30 [Active Blue pulse]
       * Step 4: Giao hàng thành công - Dự kiến 07/11/2026 [Pending gray]
  4. Delivery Address Box:
     - Recipient: Nguyễn Văn A (0987 654 321), Address details.
  5. Ordered Items List:
     - Full list of items in the order with thumbnails, names, variants, individual quantities, and unit prices.
  6. Financial Breakdown Box:
     - Tiền hàng: ₫398.000, Phí ship: ₫30.000, Voucher: -₫39.800, Tổng thanh toán: ₫388.200 (Bold #EE4D2D).
     - Phương thức: VNPAY (Mã giao dịch: VNP1458291).
  7. Footer Actions:
     - Secondary "Liên hệ Shop" button + Primary "ĐÃ NHẬN ĐƯỢC HÀNG" button.
- Style: Informative, transparent tracking, builds strong buyer confidence.
```

---

### 19. MÀN HÌNH TÀI KHOẢN CÁ NHÂN (PROFILE / ME SCREEN)

```markdown
Design a clean, feature-rich User Account mobile profile screen (390x844px).
- Theme & Colors:
  * Header Gradient: Deep Navy #0E3B64 to Royal Blue #1B4F72.
  * Primary Accent: Coral Orange #EE4D2D (Avatar badge, order badge, logout button).
  * Surface: #FFFFFF, Background: #F8F9FA, Text: #222222.
- Layout:
  1. User Header Section (Navy Gradient background):
     - Large circular Avatar with white border (Camera edit badge).
     - User name "Nguyễn Văn A" (20px bold white), email "nguyenvana@gmail.com" (14px white/80%).
     - Role Badge: "Thành viên B2C Buyer" (pill shape with gold crown icon).
  2. Quick Order Access Card (Floating card with shadow):
     - Title: "Đơn mua của tôi", link "Xem lịch sử đơn >".
     - 4 Circular icon shortcuts with labels:
       * [Chờ xác nhận (icon đồng hồ)]
       * [Đang chuẩn bị (icon gói hàng)]
       * [Đang giao (icon xe tải)]
       * [Đánh giá (icon ngôi sao)]
  3. Settings & Features Menu List (White card, grouped rows with leading icons and trailing chevrons >):
     - 📍 Sổ địa chỉ nhận hàng
     - ❤️ Sản phẩm yêu thích (Wishlist)
     - 💬 Tin nhắn với Shop (Chat)
     - 🔒 Đổi mật khẩu
     - ❓ Trung tâm trợ giúp & Điều khoản
  4. Logout Section:
     - Centered Text Button "ĐĂNG XUẤT" in Red #D32F2F (semi-bold).
  5. Bottom Navigation Bar:
     - [Trang chủ], [Danh mục], [Giỏ hàng], [Đơn hàng], [Tài khoản - Active in #EE4D2D].
- Style: Clean separation between personal info, transaction shortcuts, and app settings.
```

---

### 20. MÀN HÌNH CHỈNH SỬA HỒ SƠ & AVATAR (EDIT PROFILE SCREEN)

```markdown
Design an Edit User Profile mobile screen with avatar upload (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Camera badge icon, Save button).
  * Surface: #FFFFFF, Background: #F8F9FA, Text: #222222.
- Layout:
  1. Top Bar: Back arrow, Title "Chỉnh sửa hồ sơ".
  2. Avatar Centerpiece:
     - 100x100px circular photo with a floating camera button in #EE4D2D at bottom-right corner ("Chạm để đổi ảnh đại diện").
  3. Form Inputs (Card style):
     - Họ và tên: editable field prefilled with "Nguyễn Văn A".
     - Số điện thoại: editable field prefilled with "0987 654 321".
     - Email: read-only grayed-out field "nguyenvana@gmail.com" with a lock icon and hint "Email không thể thay đổi".
  4. Save CTA:
     - Button "LƯU THAY ĐỔI" in solid Coral Orange #EE4D2D, 50px height, white bold text.
- Style: Straightforward, zero friction, clear distinction between editable and locked fields.
```

---

### 21. MÀN HÌNH ĐỔI MẬT KHẨU (CHANGE PASSWORD SCREEN)

```markdown
Design a secure Password Change mobile screen (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D, Background: #F8F9FA, Surface: #FFFFFF.
- Layout:
  1. Top Bar: Back arrow, Title "Đổi mật khẩu".
  2. Security Note: "Mật khẩu mới phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số."
  3. Three Password Fields (with eye toggle icons):
     - "Mật khẩu hiện tại"
     - "Mật khẩu mới"
     - "Xác nhận mật khẩu mới"
  4. Password Strength Meter:
     - 3-segment bar (Yếu - Vàng, Mạnh - Xanh lá).
  5. Action CTA:
     - Button "CẬP NHẬT MẬT KHẨU" in solid Coral Orange #EE4D2D, 50px height.
- Style: High security feel, instant validation feedback.
```

---

### 22. MÀN HÌNH SỔ ĐỊA CHỈ NHẬN HÀNG (ADDRESS LIST SCREEN)

```markdown
Design a Shipping Address Management mobile screen (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Default tag, Add Address CTA).
  * Surface: #FFFFFF, Background: #F8F9FA, Border: #E5E7EB.
- Layout:
  1. Top Bar: Back arrow, Title "Địa chỉ của tôi".
  2. Address Cards List:
     - Card 1 (Default Address):
       * Header: Recipient Name "Nguyễn Văn A" (bold), Phone "0987 654 321".
       * Red Tag "MẶC ĐỊNH" in #EE4D2D container #FFEBE7.
       * Address: "123 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh".
       * Action row: Text button "Sửa" (#0E3B64).
     - Card 2 (Secondary Address):
       * Name: "Nguyễn Văn A (Văn phòng)", Phone "0987 654 321".
       * Address: "Tòa nhà Bitexco, 2 Hải Triều, Bến Nghé, Quận 1".
       * Action row: "Thiết lập mặc định" link + "Sửa" + "Xóa" (#D32F2F).
  3. Fixed Bottom Bar:
     - Button "+ THÊM ĐỊA CHỈ MỚI" in solid Coral Orange #EE4D2D, 50px height, white bold text.
- Style: Organized card list, clear default highlight, accessible management actions.
```

---

### 23. MÀN HÌNH THÊM / SỬA ĐỊA CHỈ (ADD/EDIT ADDRESS SCREEN)

```markdown
Design an Address Input & Picker mobile form screen (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Active focus, switch, save button).
  * Background: #F8F9FA, Form cards: #FFFFFF.
- Layout:
  1. Top Bar: Back arrow, Title "Thêm địa chỉ mới" (or "Chỉnh sửa địa chỉ").
  2. Section 1 - Thông tin liên hệ:
     - "Họ và tên người nhận" text field.
     - "Số điện thoại" numeric field.
  3. Section 2 - Địa chỉ giao hàng:
     - Dropdown picker: "Tỉnh / Thành phố" (Tap opens select list).
     - Dropdown picker: "Quận / Huyện".
     - Dropdown picker: "Phường / Xã".
     - Text area: "Tên đường, Tòa nhà, Số nhà cụ thể".
  4. Section 3 - Cài đặt:
     - Row with Switch toggle: "Đặt làm địa chỉ mặc định" (Active in #EE4D2D).
  5. Action CTA:
     - Button "HOÀN THÀNH" in solid Coral Orange #EE4D2D, 50px height.
- Style: Familiar Vietnamese postal address hierarchy, clean dropdown indicators.
```

---

### 24. MÀN HÌNH SẢN PHẨM YÊU THÍCH (WISHLIST SCREEN)

```markdown
Design a Wishlist & Saved Items mobile screen (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Heart icons, price tag, Add to Cart).
  * Background: #F8F9FA, Cards: #FFFFFF.
- Layout:
  1. Top Bar: Back arrow, Title "Danh sách yêu thích (8 sản phẩm)".
  2. 2-Column Product Grid:
     - Product Cards:
       * Square image with a floating Red Heart icon in top-right corner (tap to remove).
       * Product name (2 lines).
       * Price #EE4D2D (bold 16px).
       * Button "Thêm vào giỏ" (Soft coral container #FFEBE7, text #EE4D2D, cart icon).
  3. Empty State note: If empty, show broken heart illustration with "Danh sách yêu thích đang trống" and a "Khám phá ngay" button.
- Style: Clean visual grid, effortless item management.
```

---

### 25. MÀN HÌNH DANH SÁCH TIN NHẮN SHOP (CONVERSATIONS SCREEN)

```markdown
Design a Buyer-Seller Chat List mobile screen (390x844px).
- Theme & Colors:
  * Primary: Coral Orange #EE4D2D (Unread badge), Secondary: Deep Navy #0E3B64 (Shop names).
  * Background: #FFFFFF, Divider: #E5E7EB.
- Layout:
  1. Top Bar: Back arrow, Title "Tin nhắn với Shop", search conversation icon.
  2. Conversation Rows List:
     - Row 1 (Unread):
       * Circular Shop Avatar with a green online dot.
       * Shop Name "Coolmate Official Store" (bold 15px #0E3B64).
       * Latest message: "Dạ sản phẩm này shop còn size L bạn nhé!" (13px bold #222222).
       * Timestamp: "10:30" (bold).
       * Unread pill badge: "2" in solid Coral Orange #EE4D2D with white text.
     - Row 2 (Read):
       * Shop Avatar, Name "Anker Audio Flagship".
       * Latest message: "Cảm ơn bạn đã đặt hàng..." (13px muted #888888).
       * Timestamp: "Hôm qua".
- Style: Crisp chat feed, instant read/unread recognition.
```

---

### 26. MÀN HÌNH CHAT TRỰC TIẾP VỚI SHOP (CHAT DETAIL SCREEN)

```markdown
Design a Realtime Chat Conversation mobile screen between a Buyer and an Official Shop (390x844px).
- Theme & Colors:
  * Buyer Bubble: Solid Coral Orange #EE4D2D with pure white text.
  * Seller Bubble: Pure White #FFFFFF with subtle border #E5E7EB and charcoal text #222222.
  * Chat Background: Soft neutral pattern #F8F9FA.
  * Send Button: Coral Orange #EE4D2D.
- Layout:
  1. Sticky Top Bar:
     - Left: Back arrow, Shop Avatar, Shop Name "Coolmate Official", Green online dot ("Đang hoạt động").
     - Right: "Xem Shop" button.
  2. Product Pinned Context Card (Top of chat):
     - Horizontal mini card showing the product being discussed: thumbnail, title "Áo Thun Cotton Compact", price "₫199.000", and button "Gửi link sản phẩm".
  3. Message Stream (Reverse scrolling feed):
     - Seller Bubble (Left-aligned):
       * "Chào bạn, shop có thể hỗ trợ gì cho bạn ạ?" (Timestamp 14:15 below).
     - Buyer Bubble (Right-aligned, Coral #EE4D2D background, 16px rounded corners, bottom-right corner 4px):
       * "Shop ơi cho mình hỏi size L 70kg mặc vừa không ạ?" (Timestamp 14:16, double checkmark icon).
     - Seller Bubble (Left-aligned):
       * "Dạ 70kg mặc size L là chuẩn form vừa vặn luôn ạ!" (Timestamp 14:17).
  4. Sticky Bottom Input Bar:
     - Left: Plus icon (add image), Camera icon.
     - Center: Rounded pill input box (height 40px, #F1F3F5 bg, placeholder "Nhập tin nhắn...").
     - Right: Send icon button (paper airplane in circle #EE4D2D).
- Style: Friendly, responsive e-commerce live chat layout.
```

---

## CÁCH SỬ DỤNG BỘ PROMPT NÀY

1. Mở bất kỳ công cụ AI thiết kế giao diện nào (Stitch AI, v0, Galileo, Cursor, ChatGPT...).
2. Cuộn đến màn hình bạn muốn tạo (từ **01** đến **26**).
3. **Chỉ cần sao chép toàn bộ khối code của đúng màn hình đó** và dán (paste) trực tiếp vào AI.
4. AI sẽ lập tức thiết kế chuẩn 100% về kích thước, màu sắc Concept 1 (Cam Coral & Xanh Navy) và bố cục mà không cần đọc lại các màn hình khác!

