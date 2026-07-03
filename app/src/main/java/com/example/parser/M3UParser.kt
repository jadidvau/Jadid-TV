package com.example.parser

import com.example.model.Channel
import com.example.util.LogoResolver
import java.io.BufferedReader
import java.io.StringReader

object M3UParser {
    fun parse(m3uContent: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val reader = BufferedReader(StringReader(m3uContent))
        var line = reader.readLine()
        var currentMetadata: String? = null
        
        while (line != null) {
            val trimmedLine = line.trim()
            if (trimmedLine.startsWith("#EXTINF:")) {
                currentMetadata = trimmedLine
            } else if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
                if (currentMetadata != null) {
                    val parsedChannel = parseExtInf(currentMetadata, trimmedLine)
                    channels.add(parsedChannel)
                    currentMetadata = null
                } else {
                    val nameFromUrl = trimmedLine.substringAfterLast("/").substringBefore("?")
                    val finalName = if (nameFromUrl.isNotEmpty()) nameFromUrl else "Live Stream"
                    channels.add(
                        Channel(
                            name = finalName,
                            url = trimmedLine,
                            logoUrl = LogoResolver.getLogoUrl(finalName),
                            category = "All"
                        )
                    )
                }
            }
            line = reader.readLine()
        }
        return channels
    }

    private fun parseExtInf(extInfLine: String, url: String): Channel {
        val logoUrl = extractAttribute(extInfLine, "tvg-logo")
        var category = extractAttribute(extInfLine, "group-title") 
            ?: extractAttribute(extInfLine, "tvg-country") 
            ?: "All"
        
        if (category.trim().isEmpty()) {
            category = "All"
        }

        // Name is after the comma
        val parts = extInfLine.split(",")
        val rawName = if (parts.size > 1) {
            parts.last().trim()
        } else {
            extractAttribute(extInfLine, "tvg-name") ?: "Live Channel"
        }
        
        val cleanName = if (rawName.isEmpty() || rawName.startsWith("#")) {
            extractAttribute(extInfLine, "tvg-name") ?: "Live Channel"
        } else {
            rawName
        }

        val resolvedLogoUrl = LogoResolver.getLogoUrl(cleanName) ?: logoUrl?.ifEmpty { null }

        return Channel(
            name = cleanName,
            url = url,
            logoUrl = resolvedLogoUrl,
            category = category
        )
    }

    private fun extractAttribute(line: String, attrName: String): String? {
        val key = "$attrName="
        val index = line.indexOf(key)
        if (index == -1) return null
        
        val remaining = line.substring(index + key.length)
        if (remaining.startsWith("\"")) {
            val nextQuote = remaining.indexOf("\"", 1)
            if (nextQuote != -1) {
                return remaining.substring(1, nextQuote)
            }
        } else {
            val spaceIndex = remaining.indexOf(" ")
            if (spaceIndex != -1) {
                return remaining.substring(0, spaceIndex)
            }
            return remaining
        }
        return null
    }
}
