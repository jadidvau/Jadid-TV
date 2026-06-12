package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.FeaturedVideo
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Channel
import com.example.ui.components.ChannelCard
import com.example.ui.components.VideoPlayer
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.PlaylistState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val playlistState by viewModel.playlistState.collectAsState()
    val filteredChannels by viewModel.filteredChannels.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentChannel by viewModel.currentChannel.collectAsState()
    val bottomTab by viewModel.bottomTab.collectAsState()

    var isSearchExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Aesthetic dark navy atmospheric gradient background for Jadid TV style
    val appBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D142C), // Rich Deep Navy Blue
            Color(0xFF050816)  // Midnight Blue-black
        )
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(appBackground),
        containerColor = Color.Transparent,
        topBar = {
            if (bottomTab != "FIFA") {
                Column {
                    // Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 42.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { /* Could open a drawer or info slide out */ },
                        modifier = Modifier.testTag("menu_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu sidebar",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (!isSearchExpanded) {
                        // Logo Text JADID TV in Sleek Interface neon theme
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1.0f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF00FF66).copy(alpha = 0.15f))
                                    .border(1.dp, Color(0xFF00FF66), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tv,
                                    contentDescription = null,
                                    tint = Color(0xFF00FF66),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "JADID TV",
                                color = Color(0xFF00FF66),
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1.0).sp,
                                modifier = Modifier.testTag("app_title_logo")
                            )
                        }

                        IconButton(
                            onClick = { isSearchExpanded = true },
                            modifier = Modifier.testTag("search_reveal_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Open Search box",
                                tint = Color.White
                            )
                        }
                    } else {
                        // Expanded Search Text Field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearch(it) },
                            placeholder = { Text("Search TV channels...", color = Color(0xFFA5A0BC)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF1E1640),
                                unfocusedContainerColor = Color(0xFF130E2A),
                                focusedBorderColor = Color(0xFF00FF66),
                                unfocusedBorderColor = Color(0xFF33295D)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.0f)
                                .height(52.dp)
                                .testTag("search_text_input"),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        viewModel.setSearch("")
                                        isSearchExpanded = false
                                        focusManager.clearFocus()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close search",
                                        tint = Color.White
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                        )
                    }
                }

                // Filters Tabs (only drawn on Live bottom tab context)
                if (bottomTab == "Live") {
                    val tabs = listOf("FIFA World Cup", "Live", "Sports", "News", "Movies", "Bangladesh", "India", "All")
                    val selectedIndex = tabs.indexOf(activeTab).coerceAtLeast(0)

                    ScrollableTabRow(
                        selectedTabIndex = selectedIndex,
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        edgePadding = 12.dp,
                        divider = {},
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                                color = Color(0xFF00FF66), // Sleek Neon Green
                                height = 2.dp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("category_tabs_row")
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = activeTab == title,
                                onClick = { viewModel.setTab(title) },
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 14.sp,
                                        fontWeight = if (activeTab == title) FontWeight.Bold else FontWeight.Medium,
                                        color = if (activeTab == title) Color(0xFF00FF66) else Color.White.copy(alpha = 0.60f)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
        },
        bottomBar = {
            // Modern floating rounded bottom navigation styled with glowing border & neon green selections
            NavigationBar(
                containerColor = Color(0xFF090D1F),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 20.dp, top = 4.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(width = 1.dp, color = Color(0xFF223061).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .testTag("bottom_nav_bar")
            ) {
                // Live tab
                NavigationBarItem(
                    selected = bottomTab == "Live",
                    onClick = { viewModel.setBottomTab("Live") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.LiveTv,
                            contentDescription = "Live streams feed"
                        )
                    },
                    label = { Text("Live", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00FF66),
                        selectedTextColor = Color(0xFF00FF66),
                        unselectedIconColor = Color.White.copy(alpha = 0.40f),
                        unselectedTextColor = Color.White.copy(alpha = 0.40f),
                        indicatorColor = Color(0xFF00FF66).copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("bottom_tab_live")
                )

                // Channels browser tab
                NavigationBarItem(
                    selected = bottomTab == "Channel",
                    onClick = { viewModel.setBottomTab("Channel") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "Channels browser"
                        )
                    },
                    label = { Text("Channel", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00FF66),
                        selectedTextColor = Color(0xFF00FF66),
                        unselectedIconColor = Color.White.copy(alpha = 0.40f),
                        unselectedTextColor = Color.White.copy(alpha = 0.40f),
                        indicatorColor = Color(0xFF00FF66).copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("bottom_tab_channel")
                )

                // Highlights tab
                NavigationBarItem(
                    selected = bottomTab == "Highlights",
                    onClick = { viewModel.setBottomTab("Highlights") },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FeaturedVideo,
                            contentDescription = "Sports Highlights feed"
                        )
                    },
                    label = { Text("Highlights", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00FF66),
                        selectedTextColor = Color(0xFF00FF66),
                        unselectedIconColor = Color.White.copy(alpha = 0.40f),
                        unselectedTextColor = Color.White.copy(alpha = 0.40f),
                        indicatorColor = Color(0xFF00FF66).copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("bottom_tab_highlights")
                )

                // FIFA Broadcasters tab
                NavigationBarItem(
                    selected = bottomTab == "FIFA",
                    onClick = { viewModel.setBottomTab("FIFA") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "FIFA Broadcasters List"
                        )
                    },
                    label = { Text("FIFA", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00FF66),
                        selectedTextColor = Color(0xFF00FF66),
                        unselectedIconColor = Color.White.copy(alpha = 0.40f),
                        unselectedTextColor = Color.White.copy(alpha = 0.40f),
                        indicatorColor = Color(0xFF00FF66).copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("bottom_tab_fifa")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (bottomTab) {
                "Live" -> {
                    val allChannels = (playlistState as? PlaylistState.Success)?.channels ?: emptyList()
                    val fifaChannels = remember(allChannels) {
                        allChannels.filter { ch ->
                            val nameLower = ch.name.lowercase()
                            val catLower = ch.category.lowercase()
                            catLower.contains("fifa") || nameLower.contains("fifa") ||
                            nameLower.contains("fox sport") || nameLower.contains("fox deport") || nameLower.contains("fox soccer") ||
                            nameLower.contains("tudn") || nameLower.contains("telemundo") || nameLower.contains("tyc sport") ||
                            nameLower.contains("tyc") || nameLower.contains("tnt sport") || nameLower.contains("dazn") ||
                            nameLower.contains("bein") || nameLower.contains("espn") || nameLower.contains("eurosport") ||
                            nameLower.contains("match!") || nameLower.contains("match tv") || nameLower.contains("caze tv") ||
                            nameLower.contains("telefe") || nameLower.contains("dsports") || nameLower.contains("directv sports") ||
                            nameLower.contains("la liga") || nameLower.contains("sky sport") || nameLower.contains("m4 sport") ||
                            nameLower.contains("ziggo") || nameLower.contains("tf1") || nameLower.contains("bbc sport") ||
                            nameLower.contains("itv sport") || nameLower.contains("rtsh sport") || nameLower.contains("arena sport")
                        }
                    }
                    LiveTabContent(
                        state = playlistState,
                        channels = filteredChannels,
                        fifaChannels = fifaChannels,
                        showFifaShelf = activeTab.lowercase() != "fifa world cup",
                        selectedChannel = currentChannel,
                        onChannelClick = { viewModel.selectChannel(it) },
                        onRefresh = { viewModel.loadPlaylist() }
                    )
                }
                "Channel" -> ChannelsTabContent(
                    state = playlistState,
                    onChannelClick = {
                        viewModel.selectChannel(it)
                        viewModel.setBottomTab("Live") // Return to main player context
                    }
                )
                "Highlights" -> HighlightsTabContent()
                "FIFA" -> FifaBroadcastersScreen()
            }

            // Streaming overlay Player layer rendered on top
            AnimatedVisibility(
                visible = currentChannel != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                currentChannel?.let { channel ->
                    VideoPlayer(
                        channel = channel,
                        onDismiss = { viewModel.selectChannel(null) }
                    )
                }
            }
        }
    }
}

@Composable
fun LiveTabContent(
    state: PlaylistState,
    channels: List<Channel>,
    fifaChannels: List<Channel>,
    showFifaShelf: Boolean,
    selectedChannel: Channel?,
    onChannelClick: (Channel) -> Unit,
    onRefresh: () -> Unit
) {
    when (state) {
        is PlaylistState.Loading -> {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse_loader")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("channels_loading_indicator"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = Color(0xFF00FF66), // Vibrant Neon Green
                            strokeWidth = 4.dp,
                            modifier = Modifier
                                .size(64.dp)
                                .scale(scale)
                                .alpha(alpha)
                        )
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = null,
                            tint = Color(0xFF00FF66),
                            modifier = Modifier
                                .size(28.dp)
                                .scale(scale)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Fetching Jadid TV playlist...",
                        color = Color(0xFFA5A0BC),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.alpha(alpha)
                    )
                }
            }
        }
        is PlaylistState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .testTag("error_state_container"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SignalWifiOff,
                        contentDescription = "No Internet Connection",
                        tint = Color(0xFFFF003C),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.message,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Refresh Button
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFCCFF00), RoundedCornerShape(8.dp))
                            .clickable { onRefresh() }
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .testTag("refresh_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry load channels",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Refresh Now",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        is PlaylistState.Success -> {
            if (channels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .testTag("empty_search_state"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No TV channels found matching filter settings.",
                        color = Color(0xFFA5A0BC),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Vertical scrolling live channel list
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("live_channels_vertical_list")
                ) {
                    if (showFifaShelf && fifaChannels.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFCCFF00).copy(alpha = 0.15f))
                                            .border(1.dp, Color(0xFFCCFF00), RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color(0xFFCCFF00),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "FIFA WORLD CUP BROADCASTERS",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.weight(1.0f))
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFF0055).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .border(1.dp, Color(0xFFFF0055), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "FIFA 2026",
                                            color = Color(0xFFFF0055),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(fifaChannels) { channel ->
                                        Box(
                                            modifier = Modifier.width(280.dp)
                                        ) {
                                            ChannelCard(
                                                channel = channel,
                                                isSelected = selectedChannel?.id == channel.id,
                                                onClick = { onChannelClick(channel) }
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(14.dp))
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFFCCFF00).copy(alpha = 0.25f),
                                                    Color.White.copy(alpha = 0.05f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }

                    items(
                        items = channels,
                        key = { it.id }
                    ) { channel ->
                        ChannelCard(
                            channel = channel,
                            isSelected = selectedChannel?.id == channel.id,
                            onClick = { onChannelClick(channel) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelsTabContent(
    state: PlaylistState,
    onChannelClick: (Channel) -> Unit
) {
    when (state) {
        is PlaylistState.Success -> {
            val categories = state.channels.map { it.category }.distinct().filter { it.isNotEmpty() }
            
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("channel_browser_root")
            ) {
                item {
                    Text(
                        text = "Category Browser",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Discover live channels grouped by stream country & interest",
                        color = Color(0xFFA5A0BC),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(categories) { category ->
                    val catChannels = state.channels.filter { it.category == category }.take(10)
                    
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFCCFF00))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.weight(1.0f))
                            Text(
                                text = "${catChannels.size} live",
                                color = Color(0xFFCCFF00),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(catChannels) { channel ->
                                Box(
                                    modifier = Modifier.width(260.dp)
                                ) {
                                    ChannelCard(
                                        channel = channel,
                                        isSelected = false,
                                        onClick = { onChannelClick(channel) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF00FF66))
            }
        }
    }
}

@Composable
fun HighlightsTabContent() {
    // Elegant hardcoded mockup structures to showcase sports card grids matching custom colors perfectly!
    val dummyHighlights = listOf(
        HighlightItem(
            "Bangladesh vs India T20 Highlights",
            "Crushing victory highlights, game status and spectacular over highlights.",
            "T20 Cricket Series",
            "12:40",
            "https://images.unsplash.com/photo-1540747737956-378724044282?q=80&w=640"
        ),
        HighlightItem(
            "UEFA Champions League Final Rewind",
            "Comprehensive 10-minute game recap of top goals, counter attacks, and trophy ceremony.",
            "International Champions",
            "10:15",
            "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?q=80&w=640"
        ),
        HighlightItem(
            "F1 Monaco Grand Prix Highlights",
            "Breathtaking narrow overtaking, rain strategy highlights, and podium presentations.",
            "Motosport Racing",
            "08:45",
            "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?q=80&w=640"
        ),
        HighlightItem(
            "Premier League Top Goals Compilation",
            "Enjoy premium collection of spectacular volley goals, free-kicks and headers.",
            "Soccer League",
            "14:20",
            "https://images.unsplash.com/photo-1517649763962-0c623066013b?q=80&w=640"
        )
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("highlights_tab_root")
    ) {
        item {
            Text(
                text = "Premium Highlights",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Rewatch the most exciting moments, matches, and historical sports actions",
                color = Color(0xFFA5A0BC),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(dummyHighlights) { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF140F1F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFFFFFFF).copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(Color(0xFF0F0B1E))
                    ) {
                        // Soft elegant background overlay representing rich media banner
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.8f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFFCCFF00))
                                    .clickable { /* Video play callback */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play video highlights",
                                    tint = Color.Black,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        // Duration pill in corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.duration,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Category chip in corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .background(Color(0xFFCCFF00).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .border(1.dp, Color(0xFFCCFF00), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = item.eventCategory,
                                color = Color(0xFFCCFF00),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            color = Color(0xFFA5A0BC),
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

data class HighlightItem(
    val title: String,
    val description: String,
    val eventCategory: String,
    val duration: String,
    val imageUrl: String
)
