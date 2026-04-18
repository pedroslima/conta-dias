package com.example.myapplication.data

import androidx.compose.ui.graphics.Color

enum class EventColor(
    val key: String,
    val containerColor: Color,
    val onColor: Color,
    val mainColor: Color,
) {
    TERRACOTTA("terracotta", Color(0xFFFFDBC8), Color(0xFF3B1200), Color(0xFFC56A3E)),
    APRICOT("apricot",      Color(0xFFFFDBC5), Color(0xFF2D1600), Color(0xFFE08656)),
    AMBER("amber",          Color(0xFFF3E0B0), Color(0xFF3B2A00), Color(0xFFC69A3E)),
    OLIVE("olive",          Color(0xFFE5E5B0), Color(0xFF2A2A00), Color(0xFF8A8A3E)),
    SAGE("sage",            Color(0xFFD5E5C8), Color(0xFF1A2A10), Color(0xFF6A8A5E)),
    TEAL("teal",            Color(0xFFB0DEDE), Color(0xFF002A2A), Color(0xFF3E8A8A)),
    PLUM("plum",            Color(0xFFFFD6E8), Color(0xFF3B0A22), Color(0xFF8A4E6E)),
    COCOA("cocoa",          Color(0xFFE5D3C4), Color(0xFF241408), Color(0xFF6A4E3E));

    companion object {
        fun fromKey(key: String) = entries.find { it.key == key } ?: TERRACOTTA
    }
}
