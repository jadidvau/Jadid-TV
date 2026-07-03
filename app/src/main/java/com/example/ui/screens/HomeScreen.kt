package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.OrientationEventListener
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
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
import coil.compose.AsyncImage
import com.example.model.Channel
import com.example.ui.components.ChannelCard
import com.example.ui.components.VideoPlayer
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.PlaylistState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    isInPiPMode: Boolean = false,
    enterPiPMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val playlistState by viewModel.playlistState.collectAsState()
    val filteredChannels by viewModel.filteredChannels.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentChannel by viewModel.currentChannel.collectAsState()
    val bottomTab by viewModel.bottomTab.collectAsState()
    val channelPings by viewModel.channelPings.collectAsState()

    // Clean early-exit for Picture-in-Picture mode
    if (isInPiPMode && currentChannel != null) {
        VideoPlayer(
            channel = currentChannel!!,
            isFullscreen = true,
            isInPiPMode = true,
            enterPiPMode = enterPiPMode,
            onDismiss = {
                viewModel.selectChannel(null)
            },
            onFullscreenToggle = {},
            onPlaybackSuccess = {},
            onPlaybackFailed = {},
            onRetry = {},
            networkError = null,
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    var isSearchExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    val activity = context as? Activity
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var isManualFullscreen by rememberSaveable { mutableStateOf(false) }
    val isFullscreenMode = isManualFullscreen || isLandscape

    var consecutiveFailures by rememberSaveable { mutableStateOf(0) }
    var networkErrorMsg by rememberSaveable { mutableStateOf<String?>(null) }

    val selectChannelManually: (Channel) -> Unit = { channel ->
        consecutiveFailures = 0
        networkErrorMsg = null
        viewModel.selectChannel(channel)
    }

    DisposableEffect(currentChannel != null) {
        if (currentChannel == null || activity == null) {
            onDispose {}
        } else {
            val listener = object : OrientationEventListener(context) {
                override fun onOrientationChanged(orientation: Int) {
                    if (orientation == ORIENTATION_UNKNOWN) return
                    val isPhysicallyPortrait = (orientation in 0..45) || (orientation in 315..360)
                    if (isPhysicallyPortrait && isManualFullscreen) {
                        isManualFullscreen = false
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
                }
            }
            if (listener.canDetectOrientation()) {
                listener.enable()
            }
            onDispose {
                listener.disable()
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    // High-end OLED Cinema Ambient background colors
    val appBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF07060B), // Ultra-dark Obsidian
            Color(0xFF030205)  // Near Black
        )
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(appBackground),
        containerColor = Color.Transparent,
        topBar = {
            if ((currentChannel == null || !isFullscreenMode) && bottomTab != "About") {
                // Floating Translucent Header with elegant borders
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF040407).copy(alpha = 0.85f))
                        .border(width = 1.dp, color = Color.White.copy(alpha = 0.04f))
                        .padding(top = 40.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { /* Menu placeholder */ },
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1.0f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFF2D55).copy(alpha = 0.15f))
                                            .border(1.5.dp, Color(0xFFFF2D55), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tv,
                                            contentDescription = null,
                                            tint = Color(0xFFFF2D55),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "ErrorXMollik",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-0.5).sp,
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
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.setSearch(it) },
                                    placeholder = { Text("Search streaming channels...", color = Color(0xFF64748B)) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF0F0E13),
                                        unfocusedContainerColor = Color(0xFF0B0A0F),
                                        focusedBorderColor = Color(0xFFD91B5C),
                                        unfocusedBorderColor = Color(0xFF1E1C25)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .height(50.dp)
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

                        // Luxury OTT Pill Tabs instead of standard underlined tabs
                        if (bottomTab == "Live") {
                            val tabs = listOf("Bangladesh", "Sports", "All")
                            val selectedIndex = tabs.indexOf(activeTab).coerceAtLeast(0)

                            Spacer(modifier = Modifier.height(10.dp))

                            ScrollableTabRow(
                                selectedTabIndex = selectedIndex,
                                containerColor = Color.Transparent,
                                contentColor = Color.White,
                                edgePadding = 4.dp,
                                divider = {},
                                indicator = {}, // Remove standard cursor underline
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("category_tabs_row")
                            ) {
                                tabs.forEachIndexed { index, title ->
                                    val isSelected = activeTab == title
                                    Tab(
                                        selected = isSelected,
                                        onClick = { viewModel.setTab(title) },
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(
                                                if (isSelected) Color(0xFF00FF66).copy(alpha = 0.15f)
                                                else Color.White.copy(alpha = 0.04f)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) Color(0xFF00FF66).copy(alpha = 0.4f)
                                                else Color.White.copy(alpha = 0.06f),
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .padding(horizontal = 14.dp, vertical = 6.dp),
                                        text = {
                                            Text(
                                                text = title,
                                                fontSize = 12.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color(0xFF00FF66) else Color.White.copy(alpha = 0.8f)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (currentChannel == null || !isFullscreenMode) {
                // Prime Video / Cinema Floating Glass Bottom Navigation Bar
                NavigationBar(
                    containerColor = Color(0xFF050509).copy(alpha = 0.9f),
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 18.dp, top = 4.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(24.dp)
                        )
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
                        label = { Text("Home", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00FF66),
                            selectedTextColor = Color(0xFF00FF66),
                            unselectedIconColor = Color.White.copy(alpha = 0.50f),
                            unselectedTextColor = Color.White.copy(alpha = 0.50f),
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
                        label = { Text("Categories", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00FF66),
                            selectedTextColor = Color(0xFF00FF66),
                            unselectedIconColor = Color.White.copy(alpha = 0.50f),
                            unselectedTextColor = Color.White.copy(alpha = 0.50f),
                            indicatorColor = Color(0xFF00FF66).copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("bottom_tab_channel")
                    )

                    // About Me tab
                    NavigationBarItem(
                        selected = bottomTab == "About",
                        onClick = { viewModel.setBottomTab("About") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "About Creator profile"
                            )
                        },
                        label = { Text("About Me", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00FF66),
                            selectedTextColor = Color(0xFF00FF66),
                            unselectedIconColor = Color.White.copy(alpha = 0.50f),
                            unselectedTextColor = Color.White.copy(alpha = 0.50f),
                            indicatorColor = Color(0xFF00FF66).copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("bottom_tab_aboutme")
                    )
                }
            }
        }
    ) { paddingValues ->
        val contentPadding = if (currentChannel == null || !isFullscreenMode) paddingValues else PaddingValues(0.dp)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Portrait docked video reserve space
                if (currentChannel != null && !isFullscreenMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    )

                    ActiveChannelDetailsPanel(
                        channel = currentChannel!!,
                        onClose = { viewModel.selectChannel(null) }
                    )
                }

                // Normal tabs content area
                Box(modifier = Modifier.weight(1f)) {
                    when (bottomTab) {
                        "Live" -> {
                            LiveTabContent(
                                state = playlistState,
                                channels = filteredChannels,
                                fifaChannels = emptyList(),
                                activeTab = activeTab,
                                showFifaShelf = false,
                                selectedChannel = currentChannel,
                                onChannelClick = { selectChannelManually(it) },
                                onRefresh = { viewModel.loadPlaylist() }
                            )
                        }
                        "Channel" -> ChannelsTabContent(
                            state = playlistState,
                            onChannelClick = {
                                selectChannelManually(it)
                                viewModel.setBottomTab("Live")
                            },
                            onRefresh = { viewModel.loadPlaylist() }
                        )
                        "About" -> AboutMeScreen()
                    }
                }
            }

            // Expanded Video Player
            if (currentChannel != null) {
                val playerModifier = if (isFullscreenMode) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                }

                VideoPlayer(
                    channel = currentChannel!!,
                    isFullscreen = isFullscreenMode,
                    isInPiPMode = isInPiPMode,
                    enterPiPMode = enterPiPMode,
                    onDismiss = {
                        if (isFullscreenMode) {
                            isManualFullscreen = false
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        } else {
                            viewModel.selectChannel(null)
                        }
                    },
                    onFullscreenToggle = {
                        if (isFullscreenMode) {
                            isManualFullscreen = false
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        } else {
                            isManualFullscreen = true
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }
                    },
                    onPlaybackSuccess = {
                        consecutiveFailures = 0
                        networkErrorMsg = null
                    },
                    onPlaybackFailed = { reason ->
                        if (consecutiveFailures < 3) {
                            val nextFailCount = consecutiveFailures + 1
                            consecutiveFailures = nextFailCount
                            
                            if (nextFailCount >= 3) {
                                networkErrorMsg = "Connection failed for 3 consecutive streams. Mobile network or source may be offline."
                            } else {
                                val channelsToSearch = if (filteredChannels.isNotEmpty()) {
                                    filteredChannels
                                } else {
                                    (playlistState as? PlaylistState.Success)?.channels ?: emptyList()
                                }
                                
                                val currentIdx = channelsToSearch.indexOfFirst { it.url == currentChannel?.url }
                                val nextChannel = if (currentIdx != -1 && channelsToSearch.isNotEmpty()) {
                                    channelsToSearch[(currentIdx + 1) % channelsToSearch.size]
                                } else if (channelsToSearch.isNotEmpty()) {
                                    channelsToSearch[0]
                                } else {
                                    null
                                }
                                
                                if (nextChannel != null) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Current stream unavailable. Switching to ${nextChannel.name}...",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    viewModel.selectChannel(nextChannel)
                                }
                            }
                        }
                    },
                    onRetry = {
                        consecutiveFailures = 0
                        networkErrorMsg = null
                        val chan = currentChannel
                        if (chan != null) {
                            viewModel.selectChannel(null)
                            viewModel.selectChannel(chan)
                        }
                    },
                    networkError = networkErrorMsg,
                    modifier = playerModifier
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTabContent(
    state: PlaylistState,
    channels: List<Channel>,
    fifaChannels: List<Channel>,
    activeTab: String,
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
                            color = Color(0xFF00FF66),
                            strokeWidth = 3.dp,
                            modifier = Modifier
                                .size(56.dp)
                                .scale(scale)
                                .alpha(alpha)
                        )
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = null,
                            tint = Color(0xFFFF2D55),
                            modifier = Modifier
                                .size(24.dp)
                                .scale(scale)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Loading Premium Streams...",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
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
                        contentDescription = "No Connection",
                        tint = Color(0xFFFF2D55),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.message,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF00FF66), RoundedCornerShape(8.dp))
                            .clickable { onRefresh() }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .testTag("refresh_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Retry Loading",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        is PlaylistState.Success -> {
            PullToRefreshBox(
                isRefreshing = false,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                if (channels.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                            .testTag("empty_search_state"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No channels match your selection. Pull down to reload.",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(bottom = 96.dp) // Provide space for floating nav
                    ) {
                        // 1. Prime Video Spotlight Featured Hero Billboard
                        val featuredChannel = remember(channels) {
                            channels.firstOrNull { it.isOnline && it.category.lowercase() != "all" } ?: channels.firstOrNull()
                        }
                        if (featuredChannel != null && activeTab == "All") {
                            SpotlightHeroBanner(
                                channel = featuredChannel,
                                onPlayClick = { onChannelClick(featuredChannel) }
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // 2. Structured Category Carousels (Amazon/Netflix Style)
                        if (activeTab == "All") {
                            // If user is on the main landing "All" stream category, present dynamic horizontal layers
                            val categories = remember(channels) {
                                channels.map { it.category }.distinct().filter { it.isNotEmpty() }
                            }

                            categories.forEach { category ->
                                val categoryChannels = remember(channels) {
                                    channels.filter { it.category == category }
                                }
                                if (categoryChannels.isNotEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 24.dp)
                                    ) {
                                        // Header Row for category shelf with premium glowing bullet
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF00FF66))
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = category,
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.weight(1.0f))
                                            Text(
                                                text = "${categoryChannels.size} active",
                                                color = Color(0xFF64748B),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Horizontal Carousels Swipe Row (with card peak inviting horizontal swipes)
                                        LazyRow(
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("carousel_row_$category")
                                        ) {
                                            items(
                                                items = categoryChannels,
                                                key = { it.id }
                                            ) { channel ->
                                                Box(
                                                    modifier = Modifier.width(230.dp) // Beautiful widescreen width peeking
                                                ) {
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
                        } else {
                            // If user selected a specific Category (e.g. Sports), present list in clean cinematic 16:9 widescreen horizontal carousel
                            val liveSubChannels = remember(channels) { channels.filter { it.isOnline } }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00FF66))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "$activeTab Collections",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "${liveSubChannels.size} active",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // Horizontal Carousels Swipe Row (with card peak inviting horizontal swipes)
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("carousel_row_selected_$activeTab")
                                ) {
                                    items(
                                        items = liveSubChannels,
                                        key = { it.id }
                                    ) { channel ->
                                        Box(
                                            modifier = Modifier.width(230.dp) // Beautiful widescreen width peeking
                                        ) {
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
                }
            }
        }
    }
}

@Composable
fun SpotlightHeroBanner(
    channel: Channel,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 16:9 Stunning Spotlight Hero Banner (Prime Video Showcase)
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B0B0F)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .aspectRatio(16f / 9f)
            .border(width = 1.dp, color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(16.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            // Dynamic colorful abstract gradient background representing cinema streams
            val neonBrush = Brush.sweepGradient(
                colors = listOf(Color(0xFFFF2D55), Color(0xFF5856D6), Color(0xFF007AFF), Color(0xFF00FF66), Color(0xFFFF2D55))
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(neonBrush)
                    .blur(16.dp)
                    .alpha(0.18f)
            )

            // Blur backdrop image
            var isImageError by remember { mutableStateOf(false) }
            if (!channel.logoUrl.isNullOrEmpty() && !isImageError) {
                AsyncImage(
                    model = channel.logoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    onError = { isImageError = true },
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.3f)
                )
            }

            // Cinematic Vignette gradient overlays
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            // Overlaid display content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top header of banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFF2D55), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SPOTLIGHT",
                            color = Color.White,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(6.dp))
                            .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "4K HDR",
                            color = Color(0xFFE2E8F0),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.4.sp
                        )
                    }
                }

                // Title details and playback triggers at the bottom
                Column {
                    Text(
                        text = "Now Recommended",
                        color = Color(0xFF00FF66),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = channel.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // High-end glass play action trigger button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .clickable { onPlayClick() }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "WATCH NOW",
                                color = Color.Black,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.2.sp
                            )
                        }

                        // Category capsule indicator
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = channel.category,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsTabContent(
    state: PlaylistState,
    onChannelClick: (Channel) -> Unit,
    onRefresh: () -> Unit
) {
    when (state) {
        is PlaylistState.Success -> {
            PullToRefreshBox(
                isRefreshing = false,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                val categories = remember(state.channels) {
                    state.channels.map { it.category }.distinct().filter { it.isNotEmpty() }
                }
                
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("channel_browser_root")
                ) {
                    item {
                        Text(
                            text = "Streaming Categories",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        Text(
                            text = "Discover worldwide feeds and entertainment lists",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
    
                    items(categories) { category ->
                        val catChannels = remember(state.channels) {
                            state.channels.filter { it.category == category }
                        }
                        
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Section header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00FF66))
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
                                    text = "${catChannels.size} channels",
                                    color = Color(0xFF00FF66),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Horizontal Swipe Carousel
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(catChannels) { channel ->
                                    Box(
                                        modifier = Modifier.width(220.dp)
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
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom bar
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
fun ActiveChannelDetailsPanel(
    channel: Channel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B0A0F).copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color(0xFF00FF66).copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                val firstLetter = channel.name.trim().take(1).uppercase()
                val fallbackGradients = listOf(
                    listOf(Color(0xFF7F52FF), Color(0xFFC084FC)),
                    listOf(Color(0xFF00FF66), Color(0xFF10B981)),
                    listOf(Color(0xFFFF0D55), Color(0xFFFB7185)),
                    listOf(Color(0xFF0D5EFF), Color(0xFF60A5FA))
                )
                val gradientIndex = (channel.name.hashCode() and 0x7FFFFFFF) % fallbackGradients.size
                val fallbackGradient = Brush.linearGradient(fallbackGradients[gradientIndex])

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(fallbackGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = firstLetter,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = channel.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00FF66))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE",
                        color = Color(0xFF00FF66),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•  ${channel.category}",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(18.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss Player",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
