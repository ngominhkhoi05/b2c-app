package com.example.b2capp.features.chat.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b2capp.core.designsystem.theme.NavySecondary
import com.example.b2capp.core.designsystem.theme.StarRatingGold
import com.example.b2capp.core.designsystem.theme.TextPrimary
import com.example.b2capp.core.designsystem.theme.TextSecondary
import com.example.b2capp.features.chat.CheckStatus
import com.example.b2capp.features.chat.PrefixIconType
import com.example.b2capp.features.chat.ShopConversation

/**
 * Hàng hiển thị một cuộc hội thoại trong danh sách tin nhắn
 */
@Composable
fun ShopConversationItemRow(
    conversation: ShopConversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar Shop có chấm Online/Offline
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(conversation.avatarBg)
                .border(0.8.dp, Color(0xFFE5E7EB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (conversation.avatarLetter.length > 3) conversation.avatarLetter.take(3) else conversation.avatarLetter,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (conversation.avatarBg == Color(0xFF1E1E1E)) Color.White else NavySecondary
            )

            // Chấm trạng thái (Online = xanh lá, Offline = xám)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(11.dp)
                    .background(
                        if (conversation.isOnline) Color(0xFF10B981) else Color(0xFFD1D5DB),
                        CircleShape
                    )
                    .border(1.5.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Cột nội dung (Tên shop, tin nhắn mới nhất)
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = conversation.shopName,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavySecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    conversation.tagText?.let { tag ->
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .background(conversation.tagBg, RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = tag,
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = conversation.time,
                    fontSize = 11.5.sp,
                    fontWeight = if (conversation.isTimeRed) FontWeight.Bold else FontWeight.Normal,
                    color = if (conversation.isTimeRed) Color(0xFFBA2211) else TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Icon tiền tố (hình ảnh hoặc voucher)
                    when (conversation.prefixIconType) {
                        PrefixIconType.IMAGE -> {
                            Icon(
                                imageVector = Icons.Filled.Image,
                                contentDescription = null,
                                tint = Color(0xFFBA2211),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }
                        PrefixIconType.VOUCHER -> {
                            Icon(
                                imageVector = Icons.Filled.ConfirmationNumber,
                                contentDescription = null,
                                tint = StarRatingGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }
                        PrefixIconType.NONE -> {}
                    }

                    Text(
                        text = conversation.lastMessage,
                        fontSize = 12.sp,
                        fontWeight = if (conversation.isLastMessageBold) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (conversation.isLastMessageBold) TextPrimary else TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Huy hiệu số tin chưa đọc (đỏ) hoặc dấu tích gửi thành công
                if (conversation.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(17.dp)
                            .background(Color(0xFFBA2211), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    when (conversation.checkStatus) {
                        CheckStatus.DOUBLE_CHECK_READ -> {
                            Icon(
                                imageVector = Icons.Filled.DoneAll,
                                contentDescription = "Đã xem",
                                tint = Color(0xFF1E8275),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        CheckStatus.SINGLE_CHECK -> {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Đã gửi",
                                tint = TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        CheckStatus.NONE -> {}
                    }
                }
            }
        }
    }
}

