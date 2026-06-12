package com.example.model

import java.util.UUID

data class Channel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val url: String,
    val logoUrl: String? = null,
    val category: String = "All",
    val ping: Int = 0,
    val isOnline: Boolean = true,
    val pinged: Boolean = false
)
