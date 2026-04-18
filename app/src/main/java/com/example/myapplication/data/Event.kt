package com.example.myapplication.data

data class Event(
    val id: String,
    val title: String,
    val emoji: String,
    val dateMillis: Long,
    val colorKey: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)
