package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
    // 1. Sleek Spring Hover & Glow Scale
    val scaleFactor by animateFloatAsState(
        targetValue = if (isSelected) 1.04f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "soft_hover_scale"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.95f else 0.12f,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "border_glow_alpha"
    )

    val borderStrokeColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF00FF66) else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(durationMillis = 250),
        label = "border_channel_color"
    )

    // Premium Zinc-950 deep dark neutral background or translucent glassmorphism
    val containerBgColor = if (isSelected) Color(0xFF09090B).copy(alpha = 0.95f) else Color(0xFF09090B).copy(alpha = 0.75f)

    // Dynamic glowing heartbeat animation for status indicators
    val infiniteTransition = rememberInfiniteTransition(label = "neon_active_glowing")
    val heartbeatGlowScale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_pulse_size"
    )

    // Dynamic Cinematic Gradient matching categories
    val categoryGradient = remember(channel.category, channel.name) {
        val hash = (channel.name + channel.category).hashCode() and Int.MAX_VALUE
        val themeId = hash % 5
        when (themeId) {
            0 -> Brush.linearGradient(listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121))) // Sunset Rose
            1 -> Brush.linearGradient(listOf(Color(0xFF00B4DB), Color(0xFF0083B0))) // Azure Wave
            2 -> Brush.linearGradient(listOf(Color(0xFF11998e), Color(0xFF38ef7d))) // Emerald Pulse
            3 -> Brush.linearGradient(listOf(Color(0xFFf12711), Color(0xFFf5af19))) // Solar Flare
            else -> Brush.linearGradient(listOf(Color(0xFF4e54c8), Color(0xFF8f94fb))) // Cosmic Indigo
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerBgColor),
        modifier = modifier
            .fillMaxWidth()
            .scale(scaleFactor)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Color(0xFF00FF66).copy(alpha = borderAlpha) else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("channel_card_${channel.id}")
    ) {
        // Netflix/Apple TV 16:9 Aspect Ratio Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            
            // 2. High-end Cinematic Ambient Backdrop
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(categoryGradient)
            )

            // Blur backdrop effect if we have a custom cover image or logo
            var isImageError by remember { mutableStateOf(false) }
            if (!channel.logoUrl.isNullOrEmpty() && !isImageError) {
                AsyncImage(
                    model = channel.logoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    onError = { isImageError = true },
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(8.dp)
                        .scale(1.2f)
                        .alpha(0.35f)
                )
            }

            // Stylized background display letter to make card feel tactile, cinematic and filled
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = channel.name.take(1).uppercase(),
                    color = Color.White.copy(alpha = 0.15f),
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // 3. Ambient glass/dark gradient to perfectly ground label content at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.85f)
                            ),
                            startY = 60f
                        )
                    )
            )

            // 4. Overlaid Content & Metadata Layer
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Dynamic badges (Category + Live Indicator)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.Black.copy(alpha = 0.65f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = channel.category.uppercase(),
                            color = Color(0xFFE2E8F0),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Minimal Glass LIVE Pill Badge (Bioscope / Prime style translucent red pill)
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (channel.isOnline) Color(0xFFFF2D55).copy(alpha = 0.18f) 
                                        else Color.Black.copy(alpha = 0.60f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 0.5.dp,
                                color = if (channel.isOnline) Color(0xFFFF2D55).copy(alpha = 0.5f) 
                                        else Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(8.dp)
                            ) {
                                // Pulse glow circle
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .scale(heartbeatGlowScale * 0.4f + 0.8f)
                                        .clip(CircleShape)
                                        .background(
                                            if (channel.isOnline) Color(0xFFFF2D55).copy(alpha = 0.4f) 
                                            else Color.Gray.copy(alpha = 0.4f)
                                        )
                                )
                                // Solid center
                                Box(
                                    modifier = Modifier
                                        .size(3.5.dp)
                                        .clip(CircleShape)
                                        .background(if (channel.isOnline) Color(0xFFFF2D55) else Color.Gray)
                                )
                            }
                            Text(
                                text = if (channel.isOnline) "LIVE" else "OFFLINE",
                                color = if (channel.isOnline) Color.White else Color(0xFF94A3B8),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                }

                // Bottom Row: Logo & Channel Title layout with subtle reflection
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    
                    // Floating Logo with drop shadow
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D0C10))
                            .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!channel.logoUrl.isNullOrEmpty() && !isImageError) {
                            AsyncImage(
                                model = channel.logoUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                onError = { isImageError = true },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Name and latency details
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = channel.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        // Minimizing technical latency info into premium OTT micro-status (no raw pings visible)
                        Text(
                            text = if (channel.isOnline) "1080p • UltraHD Stream" else "Stable Auto Stream",
                            color = if (channel.isOnline) Color(0xFF00FF66).copy(alpha = 0.9f) else Color.White.copy(alpha = 0.5f),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp
                        )
                    }

                    // Highlight indicator when selected
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Currently Playing",
                            tint = Color(0xFF00FF66),
                            modifier = Modifier
                                .size(14.dp)
                                .scale(heartbeatGlowScale * 0.15f + 0.9f)
                        )
                    }
                }
            }
        }
    }
}
