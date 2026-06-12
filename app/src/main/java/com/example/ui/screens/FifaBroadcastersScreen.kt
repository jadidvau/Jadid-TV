package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BroadcasterInfo(val country: String, val broadcasters: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FifaBroadcastersScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Premium elegant dark navy background linear gradient
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF090D1F), // Deep Navy Midnight
            Color(0xFF030612), // Deep Charcoal Blue
            Color(0xFF010206)  // Cosmic Pitch Black
        )
    )

    // Red-blue glow accents for premium aesthetic
    val headerGlowGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFF0D55).copy(alpha = 0.8f),
            Color(0xFF0D5EFF).copy(alpha = 0.8f)
        )
    )

    val broadcastersData = remember { getBroadcastersDataset() }
    val filteredData = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            broadcastersData
        } else {
            val q = searchQuery.trim().lowercase()
            broadcastersData.filter { item ->
                item.country.lowercase().contains(q) || item.broadcasters.lowercase().contains(q)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            // Welcome Header & Subtitle
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF140F2A).copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Title with football red/blue decoration line
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFFF0D55), Color(0xFF0D5EFF))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "FIFA World Cup Broadcasters",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "Country-wise TV and streaming partners",
                                color = Color(0xFFA5A0BC),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Decorative line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(headerGlowGradient)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Status and Ping (UI Only - Required)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Wifi,
                                contentDescription = "Online",
                                tint = Color(0xFF00FF66),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Status: Online",
                                color = Color(0xFF00FF66),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF0D5EFF).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFF0D5EFF).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Ping: 0ms",
                                color = Color(0xFF99CCFF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar Component
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by country or broadcaster...", color = Color(0xFFA5A0BC)) },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = Color(0xFFFF0D55)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF131B38),
                    unfocusedContainerColor = Color(0xFF0B1024),
                    focusedBorderColor = Color(0xFF00FF66),
                    unfocusedBorderColor = Color(0xFF223061).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fifa_broadcasters_search_field"),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Broadcasters scrolling list
            if (filteredData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No broadcasters found for \"$searchQuery\"",
                        color = Color(0xFFA5A0BC),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("fifa_broadcasters_list_scroll"),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredData) { info ->
                        val isTba = info.broadcasters.trim().uppercase() == "TBA"
                        val displayName = if (isTba) "Coming Soon" else info.broadcasters
                        val flagEmoji = getCountryFlagEmoji(info.country)

                        val cardPurpleGradient = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF231744), // Royal Purple
                                Color(0xFF120B27)  // Deep Midnight Indigo
                            )
                        )

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF6B4EE8).copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(cardPurpleGradient, RoundedCornerShape(16.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Country flag or globe placeholder
                                    Text(
                                        text = flagEmoji,
                                        fontSize = 24.sp,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = info.country,
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = displayName,
                                            color = if (isTba) Color(0xFFFFB300) else Color(0xFFE2E2E9),
                                            fontSize = 14.sp,
                                            fontWeight = if (isTba) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Small status badge beside each item
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (isTba) Color(0xFFFFB300).copy(alpha = 0.15f) else Color(0xFF00FF66).copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isTba) Color(0xFFFFB300).copy(alpha = 0.4f) else Color(0xFF00FF66).copy(alpha = 0.4f),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = if (isTba) "Coming Soon" else "Available",
                                            color = if (isTba) Color(0xFFFFB300) else Color(0xFF00FF66),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Developer Credits Info Card (Footer)
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Developer info",
                                        tint = Color(0xFF0D5EFF),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Official Jadid TV Partner Info",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Developer: Jadid Mollik",
                                    color = Color(0xFFA5A0BC),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Contact: jadid.mollik@yahoo.com",
                                    color = Color(0xFFA5A0BC),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Function mapper country Flag Emojis
private fun getCountryFlagEmoji(country: String): String {
    return when (country.trim().lowercase()) {
        "afghanistan" -> "🇦🇫"
        "albania" -> "🇦🇱"
        "algeria" -> "🇩🇿"
        "andorra" -> "🇦🇩"
        "argentina" -> "🇦🇷"
        "australia" -> "🇦🇺"
        "austria" -> "🇦🇹"
        "azerbaijan" -> "🇦🇿"
        "belgium" -> "🇧🇪"
        "bolivia" -> "🇧🇴"
        "bosnia and herzegovina" -> "🇧🇦"
        "brazil" -> "🇧🇷"
        "bulgaria" -> "🇧🇬"
        "cambodia" -> "🇰🇭"
        "canada" -> "🇨🇦"
        "chile" -> "🇨🇱"
        "china" -> "🇨🇳"
        "colombia" -> "🇨🇴"
        "costa rica" -> "🇨🇷"
        "croatia" -> "🇭🇷"
        "cyprus" -> "🇨🇾"
        "czechia" -> "🇨🇿"
        "denmark" -> "🇩🇰"
        "ecuador" -> "🇪🇨"
        "el salvador" -> "🇸🇻"
        "estonia" -> "🇪🇪"
        "fiji" -> "🇫🇯"
        "finland" -> "🇫🇮"
        "france" -> "🇫🇷"
        "germany" -> "🇩🇪"
        "greece" -> "🇬🇷"
        "guatemala" -> "🇬🇹"
        "honduras" -> "🇭🇳"
        "hong kong" -> "🇭🇰"
        "hungary" -> "🇭🇺"
        "iceland" -> "🇮🇸"
        "india" -> "🇮🇳"
        "indonesia" -> "🇮🇩"
        "iran" -> "🇮🇷"
        "ireland" -> "🇮🇪"
        "israel" -> "🇮🇱"
        "italy" -> "🇮🇹"
        "jamaica" -> "🇯🇲"
        "japan" -> "🇯🇵"
        "kazakhstan" -> "🇰🇿"
        "kosovo" -> "🇽🇰"
        "kyrgyzstan" -> "🇰🇬"
        "latvia" -> "🇱🇻"
        "liechtenstein" -> "🇱🇮"
        "lithuania" -> "🇱🇹"
        "luxembourg" -> "🇱🇺"
        "macau" -> "🇲🇴"
        "maldives" -> "🇲🇻"
        "malta" -> "🇲🇹"
        "mauritius" -> "🇲🇺"
        "mexico" -> "🇲🇽"
        "middle east and north africa" -> "🌍"
        "mongolia" -> "🇲🇳"
        "montenegro" -> "🇲🇪"
        "nepal" -> "🇳🇵"
        "netherlands" -> "🇳🇱"
        "new zealand" -> "🇳🇿"
        "nicaragua" -> "🇳🇮"
        "north macedonia" -> "🇲🇰"
        "norway" -> "🇳🇴"
        "panama" -> "🇵🇦"
        "paraguay" -> "🇵🇾"
        "peru" -> "🇵🇪"
        "philippines" -> "🇵🇭"
        "poland" -> "🇵🇱"
        "portugal" -> "🇵🇹"
        "romania" -> "🇷🇴"
        "russia" -> "🇷🇺"
        "san marino" -> "🇸🇲"
        "serbia" -> "🇷🇸"
        "singapore" -> "🇸🇬"
        "slovakia" -> "🇸🇰"
        "slovenia" -> "🇸🇮"
        "south africa" -> "🇿🇦"
        "south america" -> "🌎"
        "south korea" -> "🇰🇷"
        "spain" -> "🇪🇸"
        "sub-saharan africa" -> "🌍"
        "sweden" -> "🇸🇪"
        "switzerland" -> "🇨🇭"
        "taiwan" -> "🇹🇼"
        "tajikistan" -> "🇹🇯"
        "timor-leste" -> "🇹🇱"
        "turkey" -> "🇹🇷"
        "turkmenistan" -> "🇹🇲"
        "ukraine" -> "🇺🇦"
        "united kingdom" -> "🇬🇧"
        "united states" -> "🇺🇸"
        "uruguay" -> "🇺🇾"
        "uzbekistan" -> "🇺🇿"
        "venezuela" -> "🇻🇪"
        "vietnam" -> "🇻🇳"
        else -> "🏳️"
    }
}

private fun getBroadcastersDataset(): List<BroadcasterInfo> {
    return listOf(
        BroadcasterInfo("Afghanistan", "ATN"),
        BroadcasterInfo("Albania", "TV Klan"),
        BroadcasterInfo("Algeria", "ENTV"),
        BroadcasterInfo("Andorra", "RTVE, M6, DAZN"),
        BroadcasterInfo("Argentina", "Telefe, TV Pública"),
        BroadcasterInfo("Australia", "SBS"),
        BroadcasterInfo("Austria", "ORF, ServusTV"),
        BroadcasterInfo("Azerbaijan", "ITV"),
        BroadcasterInfo("Belgium", "VRT, RTBF"),
        BroadcasterInfo("Bolivia", "Red Uno, Unitel, Entel, Tigo Sports"),
        BroadcasterInfo("Bosnia and Herzegovina", "Arena Sport"),
        BroadcasterInfo("Brazil", "Grupo Globo, CazéTV, SBT/N Sports"),
        BroadcasterInfo("Bulgaria", "BNT"),
        BroadcasterInfo("Cambodia", "Hang Meas"),
        BroadcasterInfo("Canada", "Bell Media"),
        BroadcasterInfo("Chile", "Chilevisión"),
        BroadcasterInfo("China", "TBA"),
        BroadcasterInfo("Colombia", "Caracol, RCN, Win Sports"),
        BroadcasterInfo("Costa Rica", "Teletica, Tigo Sports"),
        BroadcasterInfo("Croatia", "HRT"),
        BroadcasterInfo("Cyprus", "Sigma TV"),
        BroadcasterInfo("Czechia", "CT, TV Nova"),
        BroadcasterInfo("Denmark", "DR, TV2"),
        BroadcasterInfo("Ecuador", "Teleamazonas"),
        BroadcasterInfo("El Salvador", "TCS, Tigo Sports"),
        BroadcasterInfo("Estonia", "TV3"),
        BroadcasterInfo("Fiji", "FBC"),
        BroadcasterInfo("Finland", "Yle, MTV3"),
        BroadcasterInfo("France", "M6, beIN Sports"),
        BroadcasterInfo("Germany", "ARD, ZDF, Magenta Sport"),
        BroadcasterInfo("Greece", "ERT"),
        BroadcasterInfo("Guatemala", "Albavisión, Tigo Sports"),
        BroadcasterInfo("Honduras", "Televicentro, Tigo Sports"),
        BroadcasterInfo("Hong Kong", "PCCW"),
        BroadcasterInfo("Hungary", "MTVA"),
        BroadcasterInfo("Iceland", "RÚV"),
        BroadcasterInfo("India", "TBA"),
        BroadcasterInfo("Indonesia", "TVRI, RRI"),
        BroadcasterInfo("Iran", "IRIB TV3"),
        BroadcasterInfo("Ireland", "RTÉ"),
        BroadcasterInfo("Israel", "KAN, Charlton"),
        BroadcasterInfo("Italy", "RAI, DAZN"),
        BroadcasterInfo("Jamaica", "TVJ"),
        BroadcasterInfo("Japan", "NHK, Nippon TV, Fuji TV, DAZN"),
        BroadcasterInfo("Kazakhstan", "QAZTRK"),
        BroadcasterInfo("Kosovo", "RTK, TV Vala, Arena Sport"),
        BroadcasterInfo("Kyrgyzstan", "KTRK"),
        BroadcasterInfo("Latvia", "TV3 Latvia"),
        BroadcasterInfo("Liechtenstein", "SRG SSR"),
        BroadcasterInfo("Lithuania", "TV3 Lithuania"),
        BroadcasterInfo("Luxembourg", "VRT, RTBF"),
        BroadcasterInfo("Macau", "TDM"),
        BroadcasterInfo("Maldives", "Medianet"),
        BroadcasterInfo("Malta", "PBS"),
        BroadcasterInfo("Mauritius", "MBC"),
        BroadcasterInfo("Mexico", "TelevisaUnivision, TV Azteca"),
        BroadcasterInfo("Middle East and North Africa", "beIN Sports"),
        BroadcasterInfo("Mongolia", "EduTV, National Television, Suld TV, MNB, mobihome VOO"),
        BroadcasterInfo("Montenegro", "RTCG, Arena Sport"),
        BroadcasterInfo("Nepal", "Acepro Media, Prime TV"),
        BroadcasterInfo("Netherlands", "NOS"),
        BroadcasterInfo("New Zealand", "TVNZ"),
        BroadcasterInfo("Nicaragua", "Grupo Ratensa, Tigo Sports"),
        BroadcasterInfo("North Macedonia", "Arena Sport"),
        BroadcasterInfo("Norway", "NRK, TV2"),
        BroadcasterInfo("Panama", "Medcom, TVN Media, Tigo Sports"),
        BroadcasterInfo("Paraguay", "Trece, GEN TV, Tigo Sports"),
        BroadcasterInfo("Peru", "América Televisión"),
        BroadcasterInfo("Philippines", "Aleph Group"),
        BroadcasterInfo("Poland", "TVP"),
        BroadcasterInfo("Portugal", "Sport TV, LiveModeTV"),
        BroadcasterInfo("Romania", "Antena"),
        BroadcasterInfo("Russia", "Match TV"),
        BroadcasterInfo("San Marino", "RAI, DAZN"),
        BroadcasterInfo("Serbia", "RTS, Arena Sport"),
        BroadcasterInfo("Singapore", "Mediacorp"),
        BroadcasterInfo("Slovakia", "STVR, TV JOJ"),
        BroadcasterInfo("Slovenia", "RTV SLO, Arena Sport"),
        BroadcasterInfo("South Africa", "SABC, SportyTV"),
        BroadcasterInfo("South America", "DSports, ESPN, Disney+"),
        BroadcasterInfo("South Korea", "JTBC, KBS, NAVER Sports/CHZZK"),
        BroadcasterInfo("Spain", "RTVE, Mediapro/DAZN"),
        BroadcasterInfo("Sub-Saharan Africa", "New World TV, SuperSport"),
        BroadcasterInfo("Sweden", "SVT, TV4"),
        BroadcasterInfo("Switzerland", "SRG SSR"),
        BroadcasterInfo("Taiwan", "ELTA, EBC, TTV"),
        BroadcasterInfo("Tajikistan", "Varzish TV, TV Football"),
        BroadcasterInfo("Timor-Leste", "ETO"),
        BroadcasterInfo("Turkey", "TRT"),
        BroadcasterInfo("Turkmenistan", "Turkmenistan Sport"),
        BroadcasterInfo("Ukraine", "MEGOGO"),
        BroadcasterInfo("United Kingdom", "BBC, ITV"),
        BroadcasterInfo("United States", "Fox Sports English, Telemundo Spanish"),
        BroadcasterInfo("Uruguay", "Canal 5, Antel TV"),
        BroadcasterInfo("Uzbekistan", "Zo’r TV"),
        BroadcasterInfo("Venezuela", "Televen"),
        BroadcasterInfo("Vietnam", "VTV, SCTV")
    )
}
