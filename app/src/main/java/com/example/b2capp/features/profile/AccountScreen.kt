package com.example.b2capp.features.profile

import com.example.b2capp.features.showcase.ComponentShowcaseScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.BackgroundApp
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.ErrorRed
import com.example.b2capp.core.designsystem.theme.FreeShippingAccent
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Màn hình Tài Khoản được thiết kế chính xác theo ảnh AccountScreen_89ea980c.png:
 * 1. Hero Card Navy Blue bo đáy 24.dp: Avatar bo góc kèm nút camera, Nguyễn Văn A + tích xanh,
 *    email, huy hiệu Thành viên B2C Buyer, cụm Ví Xu (200 Xu) & Voucher của tôi (5 mã giảm).
 * 2. Card Đơn mua của tôi: Xem lịch sử, 4 trạng thái (Chờ xác nhận, Đang chuẩn bị, Đang giao, Đánh giá) kèm badges.
 * 3. Hai thẻ ngang: Ví B2C Pay (₫1.250.000) & B2C Rewards (1.480 Điểm).
 * 4. Danh sách cài đặt & tiện ích: Địa chỉ, Yêu thích, Tin nhắn với Shop (badge 3), Đổi mật khẩu, Trợ giúp, Ngôn ngữ.
 * 5. Nút ĐĂNG XUẤT nền hồng nhạt, chữ đỏ nổi bật & Thông tin phiên bản ứng dụng.
 */
@Composable
fun AccountScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    onNavigateToOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showUiKitDialog by remember { mutableStateOf(false) }

    // Dialog mở bộ UI Kit Showcase từ mục Tiện ích
    if (showUiKitDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showUiKitDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundApp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavySecondary)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "B2C Components Showcase",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Đóng",
                            color = CoralPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showUiKitDialog = false }
                        )
                    }

                    ComponentShowcaseScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundApp)
            .testTag("account_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 2. Hero Profile Card màu Xanh Navy bo góc đáy 24.dp
            item {
                NavyHeroProfileCard(
                    onCameraClick = { viewModel.openEditProfile() },
                    onNotificationsClick = {},
                    onSettingsClick = { viewModel.openEditProfile() },
                    onMembershipClick = { showUiKitDialog = true }
                )
            }

            // 3. Card Đơn mua của tôi (Chờ xác nhận, Đang chuẩn bị, Đang giao, Đánh giá)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-20).dp)
                ) {
                    MyOrdersCard(
                        onViewHistoryClick = onNavigateToOrders,
                        onOrderStatusClick = { onNavigateToOrders() }
                    )
                }
            }

            // 4. Hàng 2 Card: Ví B2C Pay & B2C Rewards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-10).dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    B2CPayCard(
                        balance = "₫1.250.000",
                        modifier = Modifier.weight(1f)
                    )
                    B2CRewardsCard(
                        points = "1.480",
                        rank = "Hạng Bạch Kim",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 5. Card Danh sách Menu cài đặt & Tiện ích
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, BorderDivider.copy(alpha = 0.7f))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        MenuListItem(
                            icon = Icons.Outlined.LocationOn,
                            iconTint = CoralPrimary,
                            iconBg = Color(0xFFFDECEE),
                            title = "Sổ địa chỉ nhận hàng",
                            subtitle = "3 địa chỉ đã lưu (Mặc định: Q.1, TP. HCM)",
                            onClick = { viewModel.openAddressBook() }
                        )

                        HorizontalDivider(color = BorderDivider.copy(alpha = 0.5f), thickness = 0.8.dp)

                        MenuListItem(
                            icon = Icons.Filled.Favorite,
                            iconTint = CoralPrimary,
                            iconBg = Color(0xFFFDECEE),
                            title = "Sản phẩm yêu thích",
                            subtitle = "12 sản phẩm đang quan tâm",
                            onClick = { viewModel.openWishlist() }
                        )

                        HorizontalDivider(color = BorderDivider.copy(alpha = 0.5f), thickness = 0.8.dp)

                        MenuListItem(
                            icon = Icons.Outlined.ChatBubbleOutline,
                            iconTint = NavySecondary,
                            iconBg = Color(0xFFEAF2FA),
                            title = "Tin nhắn với Shop",
                            subtitle = "Hỗ trợ trực tuyến & giải đáp",
                            badgeCount = 3,
                            onClick = { viewModel.openShopMessages() }
                        )

                        HorizontalDivider(color = BorderDivider.copy(alpha = 0.5f), thickness = 0.8.dp)

                        MenuListItem(
                            icon = Icons.Outlined.LockReset,
                            iconTint = FreeShippingAccent,
                            iconBg = Color(0xFFE5FBF6),
                            title = "Đổi mật khẩu & Bảo mật",
                            subtitle = "Cập nhật mật khẩu cá nhân an toàn",
                            onClick = { viewModel.openChangePassword() }
                        )

                        HorizontalDivider(color = BorderDivider.copy(alpha = 0.5f), thickness = 0.8.dp)

                        MenuListItem(
                            icon = Icons.Outlined.HelpOutline,
                            iconTint = Color(0xFF555555),
                            iconBg = Color(0xFFF0F1F3),
                            title = "Trung tâm trợ giúp",
                            subtitle = "Câu hỏi thường gặp & Điều khoản",
                            onClick = {}
                        )

                        HorizontalDivider(color = BorderDivider.copy(alpha = 0.5f), thickness = 0.8.dp)

                        MenuListItem(
                            icon = Icons.Outlined.Tune,
                            iconTint = Color(0xFF555555),
                            iconBg = Color(0xFFF0F1F3),
                            title = "Cài đặt thông báo & Ngôn ngữ",
                            subtitle = "Tiếng Việt (Mặc định)",
                            onClick = { showUiKitDialog = true }
                        )
                    }
                }
            }

            // 6. Nút ĐĂNG XUẤT và Phiên bản ứng dụng
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFDECEE)) // Nền hồng nhạt chuẩn theo ảnh
                            .clickable { viewModel.logout() }
                            .testTag("logout_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = "Đăng xuất",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ĐĂNG XUẤT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Phiên bản ứng dụng 2.4.0 (Build 202611)",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}

/**
 * Thẻ Profile nền Xanh Navy gradient với các chi tiết cá nhân & Ví Xu
 */
@Composable
private fun NavyHeroProfileCard(
    onCameraClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMembershipClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0E3B64),
                        Color(0xFF0B2E4E),
                        Color(0xFF072138)
                    )
                )
            )
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 36.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Hàng nút chuông & cài đặt bên góc phải
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Nút chuông thông báo trong vòng tròn mờ có chấm đỏ
                Box(contentAlignment = Alignment.TopEnd) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.12f), CircleShape)
                            .clickable(onClick = onNotificationsClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Thông báo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Chấm đỏ thông báo
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .background(Color(0xFFD32F2F), CircleShape)
                            .border(1.dp, Color(0xFF0E3B64), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Nút bánh răng cài đặt trong vòng tròn mờ
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.12f), CircleShape)
                        .clickable(onClick = onSettingsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Cài đặt",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hàng thông tin cá nhân: Avatar + Tên + Email + Huy hiệu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Khung Avatar bo góc kèm nút camera
                Box(
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(14.dp))
                            .background(Color(0xFF2C4A6F))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.user_profile_avatar_1793954281058),
                            contentDescription = "Ảnh đại diện",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Nút chụp ảnh Camera đỏ ở góc dưới phải
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .background(CoralPrimary, CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                            .clickable(onClick = onCameraClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Đổi ảnh",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    // Tên + Tích xanh verified
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Nguyễn Văn A",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Đã xác thực",
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "nguyenvana@gmail.com",
                        fontSize = 12.5.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Tag "Thành viên B2C Buyer"
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF071C2F))
                            .border(0.8.dp, Color(0xFFD4AF37).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable(onClick = onMembershipClick)
                            .padding(horizontal = 9.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WorkspacePremium,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Thành viên B2C Buyer",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFFB800)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Hàng Ví Xu (200 Xu) & Voucher của tôi (5 mã giảm)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ví Xu
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Biểu tượng đồng xu vàng $
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFB8860B), CircleShape)
                            .border(1.5.dp, Color(0xFFFFD700), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Ví Xu",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        Text(
                            text = "200 Xu",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Dải ngăn cách mờ
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.width(14.dp))

                // Voucher của tôi
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFFD32F2F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ConfirmationNumber,
                            contentDescription = "Voucher",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Voucher của tôi",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        Text(
                            text = "5 mã giảm",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Thẻ "Đơn mua của tôi" với 4 nút trạng thái và badges
 */
@Composable
private fun MyOrdersCard(
    onViewHistoryClick: () -> Unit,
    onOrderStatusClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, BorderDivider.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Hàng tiêu đề: Đơn mua của tôi + Xem lịch sử >
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Storefront,
                        contentDescription = null,
                        tint = CoralPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Đơn mua của tôi",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row(
                    modifier = Modifier.clickable(onClick = onViewHistoryClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Xem lịch sử",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CoralPrimary
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = CoralPrimary,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Nút chức năng trạng thái đơn hàng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OrderStatusButton(
                    icon = Icons.Outlined.Schedule,
                    iconTint = Color(0xFFD32F2F),
                    boxBg = Color(0xFFFEECE8),
                    label = "Chờ xác nhận",
                    badgeCount = 1,
                    badgeColor = Color(0xFFD32F2F),
                    onClick = { onOrderStatusClick("Chờ xác nhận") }
                )

                OrderStatusButton(
                    icon = Icons.Outlined.Inventory2,
                    iconTint = NavySecondary,
                    boxBg = Color(0xFFE8F1FA),
                    label = "Đang chuẩn\nbị",
                    badgeCount = 0,
                    onClick = { onOrderStatusClick("Đang chuẩn bị") }
                )

                OrderStatusButton(
                    icon = Icons.Outlined.LocalShipping,
                    iconTint = Color(0xFF1E75C4),
                    boxBg = Color(0xFFE1EEF9),
                    label = "Đang giao",
                    badgeCount = 1,
                    badgeColor = Color(0xFF1E75C4),
                    onClick = { onOrderStatusClick("Đang giao") }
                )

                OrderStatusButton(
                    icon = Icons.Outlined.RateReview,
                    iconTint = FreeShippingAccent,
                    boxBg = Color(0xFFE3F7F4),
                    label = "Đánh giá",
                    badgeCount = 2,
                    badgeColor = Color(0xFF26AA99),
                    onClick = { onOrderStatusClick("Đánh giá") }
                )
            }
        }
    }
}

@Composable
private fun OrderStatusButton(
    icon: ImageVector,
    iconTint: Color,
    boxBg: Color,
    label: String,
    badgeCount: Int,
    badgeColor: Color = Color(0xFFD32F2F),
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.size(52.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(boxBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(17.dp)
                        .background(badgeColor, CircleShape)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 11.5.sp,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

/**
 * Thẻ Ví B2C Pay
 */
@Composable
private fun B2CPayCard(
    balance: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, BorderDivider.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFE8EEF5), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = NavySecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ví B2C Pay",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Số dư khả dụng",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = balance,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
            )
        }
    }
}

/**
 * Thẻ B2C Rewards
 */
@Composable
private fun B2CRewardsCard(
    points: String,
    rank: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, BorderDivider.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFFFF7DF), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = StarRatingGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "B2C Rewards",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = rank,
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = points,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Điểm",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
        }
    }
}

/**
 * Từng dòng trong Menu cài đặt
 */
@Composable
private fun MenuListItem(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color(0xFFD32F2F), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier.size(13.dp)
        )
    }
}
