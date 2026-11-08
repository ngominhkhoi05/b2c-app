package com.example.b2capp.features.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.ui.components.AppButton
import com.example.b2capp.core.ui.components.AppTextField
import com.example.b2capp.core.ui.components.ButtonVariant
import com.example.b2capp.core.ui.components.DiscountBadge
import com.example.b2capp.core.ui.components.EmptyStateView
import com.example.b2capp.core.ui.components.FreeShippingBadge
import com.example.b2capp.core.ui.components.ProductCard
import com.example.b2capp.core.ui.components.QuantityPicker
import com.example.b2capp.core.ui.components.StarRatingBar
import com.example.b2capp.data.SampleData
import com.example.b2capp.viewmodel.B2CUiState
import com.example.b2capp.viewmodel.B2CViewModel
import com.example.b2capp.core.designsystem.theme.BackgroundApp
import com.example.b2capp.core.designsystem.theme.BorderDivider
import com.example.b2capp.core.designsystem.theme.CoralPrimary
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary

/**
 * Màn hình Showcase trọn bộ UI Kit / Design System Component (Prompt 1)
 */
@Composable
fun ComponentShowcaseScreen(
    uiState: B2CUiState,
    viewModel: B2CViewModel,
    modifier: Modifier = Modifier
) {
    var isButtonLoading by remember { mutableStateOf(false) }
    var inputNormal by remember { mutableStateOf("Nguyễn Văn A") }
    var inputPassword by remember { mutableStateOf("ShopeePass@2026") }
    var inputEmail by remember { mutableStateOf("user@invalid-domain") }
    var isEmailError by remember { mutableStateOf(true) }
    var pickerQty by remember { mutableIntStateOf(2) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundApp)
            .testTag("component_showcase_screen")
    ) {
        // Showcase Header
        Surface(
            color = NavySecondary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "B2C Design System Components",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Bộ UI Kit tái sử dụng (Coral #EE4D2D & Navy #0E3B64)",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. AppButton Section
            item {
                SectionCard(title = "1. AppButton.kt (PRIMARY, SECONDARY, OUTLINED)") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Bật chế độ isLoading:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = isButtonLoading,
                            onCheckedChange = { isButtonLoading = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CoralPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AppButton(
                        text = "Mua ngay (PRIMARY)",
                        onClick = { viewModel.addToCartFromDetail() },
                        isLoading = isButtonLoading,
                        variant = ButtonVariant.PRIMARY,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AppButton(
                        text = "Xem Shop (SECONDARY)",
                        onClick = {},
                        isLoading = isButtonLoading,
                        variant = ButtonVariant.SECONDARY,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AppButton(
                        text = "Thêm vào giỏ (OUTLINED)",
                        onClick = {},
                        isLoading = isButtonLoading,
                        variant = ButtonVariant.OUTLINED,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2. AppTextField Section
            item {
                SectionCard(title = "2. AppTextField.kt (Bo 8dp, focus #EE4D2D, Mật khẩu, Lỗi)") {
                    AppTextField(
                        value = inputNormal,
                        onValueChange = { inputNormal = it },
                        label = "Họ và tên (kèm Leading Icon)",
                        placeholder = "Nhập họ và tên người nhận",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AppTextField(
                        value = inputPassword,
                        onValueChange = { inputPassword = it },
                        label = "Mật khẩu (isPassword = true)",
                        placeholder = "Nhập mật khẩu của bạn",
                        isPassword = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AppTextField(
                        value = inputEmail,
                        onValueChange = {
                            inputEmail = it
                            isEmailError = !it.contains("@") || !it.contains(".")
                        },
                        label = "Email thông báo",
                        placeholder = "email@domain.com",
                        isError = isEmailError,
                        errorMessage = if (isEmailError) "Địa chỉ email không hợp lệ. Vui lòng kiểm tra lại." else null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 3. ProductCard Section
            item {
                SectionCard(title = "3. ProductCard.kt (Ảnh 1:1 Coil, Giá #EE4D2D, Sao #FFB800)") {
                    Text(
                        text = "Thẻ sản phẩm chuẩn lưới (~170.dp):",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProductCard(
                            product = SampleData.products[0],
                            onClick = { viewModel.openProductDetail(SampleData.products[0]) },
                            modifier = Modifier.weight(1f)
                        )

                        ProductCard(
                            product = SampleData.products[1],
                            onClick = { viewModel.openProductDetail(SampleData.products[1]) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 4. QuantityPicker Section
            item {
                SectionCard(title = "4. QuantityPicker.kt (Tự disable [-] khi min, [+] khi max)") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Chọn số lượng mua:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Giới hạn: Min = 1, Max = 5",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        QuantityPicker(
                            quantity = pickerQty,
                            onQuantityChange = { pickerQty = it },
                            minQuantity = 1,
                            maxStock = 5
                        )
                    }
                }
            }

            // 5. StarRatingBar Section
            item {
                SectionCard(title = "5. StarRatingBar.kt (Chỉ hiển thị hoặc Chạm để chấm sao)") {
                    Text(
                        text = "Chế độ chỉ đọc (Read-only, score 4.8f):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StarRatingBar(
                        rating = 4.8f,
                        onRatingChanged = null,
                        starSize = 22.dp,
                        showRatingText = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderDivider)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Chế độ chạm để chấm sao (Interactive 1 đến 5 sao):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    StarRatingBar(
                        rating = uiState.interactiveRatingDemo,
                        onRatingChanged = { newRating ->
                            viewModel.setInteractiveRating(newRating)
                        },
                        starSize = 32.dp,
                        showRatingText = true
                    )
                }
            }

            // 6. Badges & Tokens Section
            item {
                SectionCard(title = "6. Badges & Design Tokens (FreeShipping, Giảm Giá %)") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FreeShippingBadge(text = "Freeship Xtra")
                        DiscountBadge(percent = 35)
                        DiscountBadge(percent = 50)
                    }
                }
            }

            // 7. EmptyStateView Section
            item {
                SectionCard(title = "7. EmptyStateView.kt (Trạng thái trống có minh họa)") {
                    EmptyStateView(
                        title = "Chưa có đánh giá nào",
                        description = "Sản phẩm này chưa có bài đánh giá. Hãy là người đầu tiên chia sẻ cảm nhận của bạn!",
                        actionText = "Viết đánh giá ngay",
                        onActionClick = {}
                    )
                }
            }

            // Extra padding at bottom
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderDivider, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CoralPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
