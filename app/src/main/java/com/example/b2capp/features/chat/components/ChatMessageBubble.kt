package com.example.b2capp.features.chat.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.b2capp.core.designsystem.theme.SurfaceCard
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.core.model.ProductItem
import com.example.b2capp.features.chat.ChatMessage
import com.example.b2capp.features.chat.DetailedChatMessage

/**
 * Bong bóng tin nhắn từ phía Người mua (Màu cam đỏ bên phải)
 */
@Composable
fun UserChatBubble(
    message: DetailedChatMessage,
    onProductClick: ((ProductItem) -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 285.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                .background(Color(0xFFEE4D2D))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Nếu có đính kèm thẻ sản phẩm bên trên nội dung
            message.attachedProduct?.let { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFD33618))
                        .clickable(enabled = onProductClick != null) { onProductClick?.invoke(product) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.folded_black_tshirt_1793956682242),
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = product.formattedPrice,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Nếu có đính kèm hình ảnh
            message.attachmentUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Ảnh đính kèm",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Văn bản tin nhắn
            if (message.text.isNotBlank() && (message.attachmentUrl == null || message.text != "[Hình ảnh]")) {
                Text(
                    text = message.text,
                    fontSize = 13.5.sp,
                    color = Color.White,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Mốc giờ + Dấu tích đôi ✓✓
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.time,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "✓✓",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Bong bóng tin nhắn từ phía Shop (Bong bóng trắng bên trái kèm Avatar shop/nhân viên)
 */
@Composable
fun ShopChatBubble(
    message: DetailedChatMessage,
    onClaimVoucher: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // Avatar Shop hoặc Nhân viên CSKH
        if (message.senderAvatarRes != null) {
            Image(
                painter = painterResource(id = message.senderAvatarRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(0.8.dp, Color(0xFFE2E8F0), CircleShape)
            )
        } else {
            val letter = message.avatarLetter ?: "S"
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(message.avatarBg ?: Color(0xFFF0FDF4))
                    .border(0.8.dp, Color(0xFFE2E8F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (letter.length > 3) letter.take(3) else letter,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = com.example.b2capp.core.designsystem.theme.NavySecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .widthIn(max = 285.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(SurfaceCard)
                .border(0.8.dp, Color(0xFFE2E8F0), RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Tên người gửi (Tên Shop)
            val displayName = message.senderName?.takeIf { it.isNotBlank() }
            if (displayName != null) {
                Text(
                    text = displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = com.example.b2capp.core.designsystem.theme.NavySecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Nếu Shop gửi hình ảnh
            message.attachmentUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Ảnh đính kèm từ Shop",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Văn bản tin nhắn
            if (message.text.isNotBlank() && (message.attachmentUrl == null || message.text != "[Hình ảnh]")) {
                Text(
                    text = message.text,
                    fontSize = 13.5.sp,
                    color = Color(0xFF1F2937),
                    lineHeight = 19.5.sp
                )
            }

            // Thẻ Voucher quà tặng nếu có
            if (message.isVoucher) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                    border = BorderStroke(1.dp, Color(0xFFFDBA74))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = Color(0xFFEA580C),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mã GIAM20K",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9A3412)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFEA580C))
                                .clickable { onClaimVoucher() }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Lưu mã",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Mốc giờ gửi
            Text(
                text = message.time,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

/**
 * Bong bóng tin nhắn rút gọn trong ConversationChatModal
 */
@Composable
fun ChatBubbleRow(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (message.isFromUser) 12.dp else 2.dp,
                            bottomEnd = if (message.isFromUser) 2.dp else 12.dp
                        )
                    )
                    .background(if (message.isFromUser) Color(0xFFBA2211) else Color.White)
                    .border(
                        width = if (message.isFromUser) 0.dp else 0.8.dp,
                        color = Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 9.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = if (message.isFromUser) Color.White else TextPrimary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = message.time,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

