package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.Event
import com.example.myapplication.data.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EventViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = EventRepository(app)
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    init {
        _events.value = repo.loadEvents()
    }

    fun saveEvent(event: Event) {
        val list = _events.value.toMutableList()
        val idx = list.indexOfFirst { it.id == event.id }
        if (idx >= 0) list[idx] = event else list.add(event)
        _events.value = list
        repo.saveEvents(list)
    }

    fun deleteEvent(id: String) {
        val list = _events.value.filter { it.id != id }
        _events.value = list
        repo.saveEvents(list)
    }
}
