package com.example.myapplication.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class EventRepository(private val context: Context) {

    private val prefs get() = context.getSharedPreferences("conta_dias", Context.MODE_PRIVATE)

    fun loadEvents(): List<Event> {
        val json = prefs.getString("events", null) ?: return seedAndSave()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Event(
                    id = o.getString("id"),
                    title = o.getString("title"),
                    emoji = o.getString("emoji"),
                    dateMillis = o.getLong("dateMillis"),
                    colorKey = o.getString("colorKey"),
                    createdAtMillis = o.getLong("createdAtMillis"),
                )
            }
        } catch (_: Exception) {
            seedAndSave()
        }
    }

    fun saveEvents(events: List<Event>) {
        val arr = JSONArray()
        events.forEach { e ->
            arr.put(JSONObject().apply {
                put("id", e.id)
                put("title", e.title)
                put("emoji", e.emoji)
                put("dateMillis", e.dateMillis)
                put("colorKey", e.colorKey)
                put("createdAtMillis", e.createdAtMillis)
            })
        }
        prefs.edit().putString("events", arr.toString()).apply()
    }

    private fun seedAndSave(): List<Event> {
        val events = listOf(
            Event("evt-viagem",    "Viagem ao Japão",              "🗼", iso("2026-10-12T09:00:00"), "terracotta"),
            Event("evt-niver",     "Aniversário da Luiza",         "🎂", iso("2026-07-03T18:00:00"), "apricot"),
            Event("evt-bebe",      "Idade do Téo",                 "👶", iso("2025-09-14T06:22:00"), "amber"),
            Event("evt-sobrio",    "Sem açúcar",                   "🌱", iso("2026-01-05T00:00:00"), "sage"),
            Event("evt-deadline",  "Entrega do TCC",               "📚", iso("2026-06-28T23:59:00"), "plum"),
            Event("evt-casamento", "Aniversário de casamento",     "💍", iso("2019-11-23T16:00:00"), "cocoa"),
            Event("evt-show",      "Show do Caetano",              "🎸", iso("2026-05-02T21:00:00"), "teal"),
        )
        saveEvents(events)
        return events
    }

    private fun iso(s: String): Long =
        java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).parse(s)?.time
            ?: System.currentTimeMillis()
}
