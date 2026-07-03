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
            // Explicitly filter out any offline channels and dynamically compact the remaining list
            val liveOnlyList = updatedList.filter { it.isOnline }
            filterChannels(liveOnlyList, tab, query)
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

    private fun processAndFilterChannel(channel: Channel): Channel? {
        val nameLower = channel.name.lowercase()
        val catLower = channel.category.lowercase()

        val isBangladesh = catLower.contains("bangladesh") || catLower.contains("bangla") || catLower.contains("bd") ||
                nameLower.contains("bangla") || nameLower.contains("bd") || nameLower.contains("dhaka") || 
                nameLower.contains("somoy") || nameLower.contains("jamuna") || nameLower.contains("gazi") || 
                nameLower.contains("gtv") || nameLower.contains("t-sports") || nameLower.contains("tsports") || 
                nameLower.contains("b tv") || nameLower.contains("btv") || nameLower.contains("channel i") || 
                nameLower.contains("ntv") || nameLower.contains("r tv") || nameLower.contains("rtv") || 
                nameLower.contains("atn") || nameLower.contains("ekattor") || nameLower.contains("independent") || 
                nameLower.contains("news24") || nameLower.contains("deepto") || nameLower.contains("boishakhi") || 
                nameLower.contains("asian tv") || nameLower.contains("sa tv") || nameLower.contains("duronto") || 
                nameLower.contains("maasranga") || nameLower.contains("nagorik") || nameLower.contains("news 24") || 
                nameLower.contains("news1") || nameLower.contains("channel24") || nameLower.contains("channel 24") || 
                nameLower.contains("desh") || nameLower.contains("bijoy") || nameLower.contains("my tv") || 
                nameLower.contains("mytv") || nameLower.contains("mohana") || nameLower.contains("ananda") || 
                nameLower.contains("dusk") || nameLower.contains("silk") || nameLower.contains("bongo") || 
                nameLower.contains("chorki") || nameLower.contains("toffee")

        val isSports = catLower.contains("sport") || nameLower.contains("sport") || 
                nameLower.contains("espn") || nameLower.contains("dazn") || nameLower.contains("tudn") || 
                nameLower.contains("bein") || nameLower.contains("tyc") || nameLower.contains("win") || 
                nameLower.contains("dsports") || nameLower.contains("golf") || nameLower.contains("eurosport") || 
                nameLower.contains("match") || nameLower.contains("lig") || nameLower.contains("caze") || 
                nameLower.contains("telefe") || nameLower.contains("teletrak") || nameLower.contains("suspis") || 
                nameLower.contains("rtsh") || nameLower.contains("arena") || nameLower.contains("canal") || 
                nameLower.contains("setenta") || nameLower.contains("digi") || nameLower.contains("m4") || 
                nameLower.contains("ziggo") || nameLower.contains("cna") || nameLower.contains("deport") || 
                nameLower.contains("fifa") || nameLower.contains("cricket") || nameLower.contains("ipl") || 
                nameLower.contains("willow") || nameLower.contains("ten ") || nameLower.contains("ten1") || 
                nameLower.contains("ten2") || nameLower.contains("ten3") || nameLower.contains("ten4") || 
                nameLower.contains("ten5") || nameLower.contains("star sport") || nameLower.contains("sony sport") || 
                nameLower.contains("astro") || nameLower.contains("skysport") || nameLower.contains("super sport") || 
                nameLower.contains("supersport") || nameLower.contains("adrenalina") || nameLower.contains("ct sport") || 
                nameLower.contains("čt sport")

        return when {
            isBangladesh -> channel.copy(category = "Bangladesh")
            isSports -> channel.copy(category = "Sports")
            else -> null
        }
    }

    fun loadPlaylist() {
        _playlistState.value = PlaylistState.Loading
        _channelPings.value = emptyMap()
        _currentChannel.value = null
        viewModelScope.launch(Dispatchers.IO) {
            // Parse local Cricfy playlist for immediate, robust sports-rich data
            val localChannels = try {
                M3UParser.parse(com.example.playlist.CricfyPlaylist.M3U_CONTENT).mapNotNull { channel ->
                    processAndFilterChannel(channel)
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
                                        val parsed = M3UParser.parse(body).mapNotNull { channel ->
                                            processAndFilterChannel(channel)
                                        }
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
            val tempMap = java.util.Collections.synchronizedMap(mutableMapOf<String, Pair<Int, Boolean>>())
            var lastUpdateTime = System.currentTimeMillis()

            channels.map { channel ->
                launch {
                    semaphore.withPermit {
                        val result = checkPing(channel.url)
                        tempMap[channel.id] = result

                        // Batch update flow every 800ms to avoid overwhelming Compose with constant recompositions
                        val now = System.currentTimeMillis()
                        if (now - lastUpdateTime > 800) {
                            _channelPings.value = tempMap.toMap()
                            lastUpdateTime = now
                        }
                    }
                }
            }.forEach { it.join() }

            // Ensure everything is caught in the final update
            _channelPings.value = tempMap.toMap()
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
                channel.category.equals(tab, ignoreCase = true)
            }
        }
        
        // Search query filtering
        if (query.isNotEmpty()) {
            filtered = filtered.filter { it.name.lowercase().contains(query.lowercase()) }
        }
        
        return filtered
    }
}
