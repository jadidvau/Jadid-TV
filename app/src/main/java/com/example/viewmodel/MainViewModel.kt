package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Channel
import com.example.parser.M3UParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed interface PlaylistState {
    object Loading : PlaylistState
    data class Success(val channels: List<Channel>) : PlaylistState
    data class Error(val message: String) : PlaylistState
}

class MainViewModel : ViewModel() {

    private val playlistUrl = "https://raw.githubusercontent.com/sm-monirulislam/SM-Live-TV/refs/heads/main/Combined_Live_TV.m3u"
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val pingClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .build()

    private val _playlistState = MutableStateFlow<PlaylistState>(PlaylistState.Loading)
    val playlistState: StateFlow<PlaylistState> = _playlistState.asStateFlow()

    private val _activeTab = MutableStateFlow("All")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentChannel = MutableStateFlow<Channel?>(null)
    val currentChannel: StateFlow<Channel?> = _currentChannel.asStateFlow()

    private val _bottomTab = MutableStateFlow("Live")
    val bottomTab: StateFlow<String> = _bottomTab.asStateFlow()

    // Map of channelId to its updated Channel info (containing live ping)
    private val _channelPings = MutableStateFlow<Map<String, Pair<Int, Boolean>>>(emptyMap())
    val channelPings: StateFlow<Map<String, Pair<Int, Boolean>>> = _channelPings.asStateFlow()

    val filteredChannels: StateFlow<List<Channel>> = combine(
        _playlistState,
        _activeTab,
        _searchQuery,
        _channelPings
    ) { state, tab, query, pings ->
        if (state is PlaylistState.Success) {
            val updatedList = state.channels.map { ch ->
                val pingVal = pings[ch.id]
                if (pingVal != null) {
                    ch.copy(ping = pingVal.first, isOnline = pingVal.second, pinged = true)
                } else {
                    ch
                }
            }
            filterChannels(updatedList, tab, query)
        } else {
            emptyList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadPlaylist()
    }

    fun setTab(tab: String) {
        _activeTab.value = tab
    }

    fun setSearch(query: String) {
        _searchQuery.value = query
    }

    fun setBottomTab(tab: String) {
        _bottomTab.value = tab
    }

    fun selectChannel(channel: Channel?) {
        _currentChannel.value = channel
    }

    fun loadPlaylist() {
        _playlistState.value = PlaylistState.Loading
        _channelPings.value = emptyMap()
        _currentChannel.value = null
        viewModelScope.launch(Dispatchers.IO) {
            // Parse local Cricfy playlist for immediate, robust sports-rich data
            val localChannels = try {
                M3UParser.parse(com.example.playlist.CricfyPlaylist.M3U_CONTENT).map { channel ->
                    val nameLower = channel.name.lowercase()
                    val isSports = nameLower.contains("sport") || nameLower.contains("espn") || 
                            nameLower.contains("dazn") || nameLower.contains("tudn") || 
                            nameLower.contains("bein") || nameLower.contains("tyc") || 
                            nameLower.contains("win") || nameLower.contains("dsports") || 
                            nameLower.contains("golf") || nameLower.contains("eurosport") || 
                            nameLower.contains("match") || nameLower.contains("lig") || 
                            nameLower.contains("caze") || nameLower.contains("telefe") || 
                            nameLower.contains("teletrak") || nameLower.contains("suspis") ||
                            nameLower.contains("rtsh") || nameLower.contains("arena") ||
                            nameLower.contains("canal") || nameLower.contains("setenta") ||
                            nameLower.contains("digi") || nameLower.contains("m4") ||
                            nameLower.contains("ziggo") || nameLower.contains("cna") ||
                            nameLower.contains("deport")

                    if (isSports && (channel.category.isEmpty() || channel.category == "All")) {
                        channel.copy(category = "Sports")
                    } else {
                        channel
                    }
                }
            } catch (e: Exception) {
                emptyList()
            }

            try {
                val remoteUrls = listOf(
                    "https://raw.githubusercontent.com/sm-monirulislam/SM-Live-TV/refs/heads/main/Combined_Live_TV.m3u",
                    "https://iptv-org.github.io/iptv/index.m3u"
                )

                val allFetched = java.util.Collections.synchronizedList(mutableListOf<Channel>())
                allFetched.addAll(localChannels)

                val jobs = remoteUrls.map { url ->
                    launch {
                        try {
                            val request = Request.Builder().url(url).build()
                            httpClient.newCall(request).execute().use { response ->
                                if (response.isSuccessful) {
                                    val body = response.body?.string()
                                    if (!body.isNullOrEmpty()) {
                                        val parsed = M3UParser.parse(body)
                                        allFetched.addAll(parsed)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            // ignore and continue
                        }
                    }
                }

                jobs.forEach { it.join() }

                // Merge and de-duplicate by stream URL to keep the channel grid neat
                val merged = allFetched.distinctBy { it.url }
                _playlistState.value = PlaylistState.Success(merged)
                measureAllPings(merged)
            } catch (e: Exception) {
                // Network query failed/offline, fallback to the local Cricfy dataset gracefully
                _playlistState.value = PlaylistState.Success(localChannels)
                measureAllPings(localChannels)
            }
        }
    }

    private fun measureAllPings(channels: List<Channel>) {
        val semaphore = Semaphore(5) // Limit to 5 concurrent stream tests
        viewModelScope.launch(Dispatchers.IO) {
            channels.forEach { channel ->
                launch {
                    semaphore.withPermit {
                        val result = checkPing(channel.url)
                        // Update ping map in real-time
                        val currentMap = _channelPings.value.toMutableMap()
                        currentMap[channel.id] = result
                        _channelPings.value = currentMap
                    }
                }
            }
        }
    }

    private fun checkPing(streamUrl: String): Pair<Int, Boolean> {
        val startTime = System.currentTimeMillis()
        return try {
            val request = Request.Builder()
                .url(streamUrl)
                .head()
                .build()
            pingClient.newCall(request).execute().use { response ->
                val duration = (System.currentTimeMillis() - startTime).toInt().coerceAtLeast(1)
                // If responded, online (even if 403 or 404, the server itself is responsive)
                val isOnline = response.code in 200..499
                Pair(duration, isOnline)
            }
        } catch (e: Exception) {
            // Some servers block HEAD request, try simple socket connect
            try {
                val uri = java.net.URI(streamUrl)
                val host = uri.host
                if (host != null) {
                    val port = if (uri.port != -1) uri.port else if (uri.scheme == "https") 443 else 80
                    val socket = java.net.Socket()
                    val connectStart = System.currentTimeMillis()
                    socket.connect(java.net.InetSocketAddress(host, port), 2000)
                    val duration = (System.currentTimeMillis() - connectStart).toInt().coerceAtLeast(1)
                    socket.close()
                    Pair(duration, true)
                } else {
                    Pair(0, false)
                }
            } catch (ex: Exception) {
                Pair(0, false)
            }
        }
    }

    private fun filterChannels(channels: List<Channel>, tab: String, query: String): List<Channel> {
        var filtered = channels
        
        // Tab filtering
        if (tab != "All") {
            filtered = filtered.filter { channel ->
                val cat = channel.category.lowercase()
                val name = channel.name.lowercase()
                when (tab.lowercase()) {
                    "fifa world cup" -> {
                        cat.contains("fifa") || name.contains("fifa") ||
                        name.contains("fox sport") || name.contains("fox deport") || name.contains("fox soccer") ||
                        name.contains("tudn") || name.contains("telemundo") || name.contains("tyc sport") ||
                        name.contains("tyc") || name.contains("tnt sport") || name.contains("dazn") ||
                        name.contains("bein") || name.contains("espn") || name.contains("eurosport") ||
                        name.contains("match!") || name.contains("match tv") || name.contains("caze tv") ||
                        name.contains("telefe") || name.contains("dsports") || name.contains("directv sports") ||
                        name.contains("la liga") || name.contains("sky sport") || name.contains("m4 sport") ||
                        name.contains("ziggo") || name.contains("tf1") || name.contains("bbc sport") ||
                        name.contains("itv sport") || name.contains("rtsh sport") || name.contains("arena sport")
                    }
                    "live" -> cat.contains("live") || name.contains("live") || cat.contains("tv")
                    "sports" -> cat.contains("sports") || name.contains("sports") || name.contains("cricket") || name.contains("t-sports") || name.contains("tsports") || cat.contains("sport")
                    "news" -> cat.contains("news") || name.contains("news") || name.contains("khobor") || cat.contains("khobor") || name.contains("somoy") || name.contains("jamuna")
                    "movies" -> cat.contains("movies") || name.contains("movies") || cat.contains("cinema") || name.contains("cinema") || cat.contains("film") || name.contains("film")
                    "bangladesh" -> cat.contains("bangladesh") || name.contains("bangla") || name.contains("bd") || cat.contains("bd") || cat.contains("bangla") || name.contains("dhaka") || name.contains("somoy") || name.contains("jamuna")
                    "india" -> cat.contains("india") || name.contains("india") || cat.contains("hindi") || name.contains("hindi") || name.contains("sony") || name.contains("zee") || name.contains("star") || cat.contains("india")
                    else -> cat.contains(tab.lowercase())
                }
            }
        }
        
        // Search query filtering
        if (query.isNotEmpty()) {
            filtered = filtered.filter { it.name.lowercase().contains(query.lowercase()) }
        }
        
        return filtered
    }
}
