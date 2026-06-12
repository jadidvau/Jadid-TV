package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Channel

@Composable
fun ChannelCard(
    channel: Channel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Beautiful royal purple to deep violet gradient for channel cards
    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF231744), // Royal Purple
            Color(0xFF120B27)  // Deep Midnight Indigo
        )
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF00FF66) else Color(0xFF6B4EE8).copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            )
            .background(cardGradient, RoundedCornerShape(16.dp))
            .testTag("channel_card_${channel.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Channel Logo / Fallback box with nice roundings
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!channel.logoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = channel.logoUrl,
                        contentDescription = "Channel Logo for ${channel.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    // Beautiful letter-gradient avatar placeholder
                    val firstLetter = channel.name.trim().take(1).uppercase()
                    val fallbackGradients = listOf(
                        listOf(Color(0xFF7F52FF), Color(0xFFC084FC)), // Purple-Violet
                        listOf(Color(0xFF00FF66), Color(0xFF10B981)), // Neon Green-Emerald
                        listOf(Color(0xFFFF0D55), Color(0xFFFB7185)), // Scarlet-Rose
                        listOf(Color(0xFF0D5EFF), Color(0xFF60A5FA))  // Royal-Sky Blue
                    )
                    val gradientIndex = (channel.name.hashCode() and 0x7FFFFFFF) % fallbackGradients.size
                    val fallbackGradient = Brush.linearGradient(fallbackGradients[gradientIndex])

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(fallbackGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = firstLetter,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Channel Names + Detailed Ping Stats
            Column(
                modifier = Modifier.weight(1.0f)
            ) {
                Text(
                    text = channel.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(3.dp))
                
                Text(
                    text = "Group: ${channel.category}",
                    color = Color(0xFFA5A0BC),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Ping Status representation matches requirements
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Glowing pulse dot represent online status
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (channel.isOnline) Color(0xFF00FF66) else Color(0xFFFF003C))
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    val pingLabel = when {
                        !channel.pinged -> "LIVE · Ping 0ms"
                        channel.isOnline -> "LIVE · Ping ${channel.ping}ms · Online"
                        else -> "LIVE · Offline"
                    }

                    Text(
                        text = pingLabel,
                        color = if (channel.isOnline) Color(0xFF00FF66) else Color(0xFFFF5252),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Beautiful premium Red LIVE Badge with white heartbeat dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF003C), // Vibrant Scarlet Red
                                Color(0xFFCC002B)  // Premium Deep Red
                            )
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "LIVE",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
