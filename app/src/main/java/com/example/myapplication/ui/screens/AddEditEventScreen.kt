package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.data.*
import com.example.myapplication.ui.EventViewModel
import java.util.Calendar

private val EMOJIS = listOf(
    "🎉", "🎂", "🗼", "🌱", "📚", "💍", "🎸", "👶", "🏖️", "✈️",
    "🎯", "🎵", "📷", "💪", "🏃", "🌎", "🎮", "📝", "💰", "🌺",
    "🎓", "🏆", "❤️", "🌟", "🎁", "🍕", "🚀", "🌈", "🐶", "⚽",
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditEventScreen(
    vm: EventViewModel,
    eventId: String?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val events by vm.events.collectAsState()
    val existing = eventId?.let { id -> events.find { it.id == id } }

    val defaultDate = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000

    var title by remember { mutableStateOf(existing?.title ?: "") }
    var emoji by remember { mutableStateOf(existing?.emoji ?: "🎉") }
    var selectedMillis by remember { mutableLongStateOf(existing?.dateMillis ?: defaultDate) }
    var colorKey by remember { mutableStateOf(existing?.colorKey ?: "terracotta") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedMillis)
    val cal = remember(selectedMillis) { Calendar.getInstance().apply { timeInMillis = selectedMillis } }
    val timePickerState = rememberTimePickerState(
        initialHour = cal[Calendar.HOUR_OF_DAY],
        initialMinute = cal[Calendar.MINUTE],
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { newDay ->
                        val c = Calendar.getInstance().apply {
                            timeInMillis = newDay
                            val old = Calendar.getInstance().apply { timeInMillis = selectedMillis }
                            set(Calendar.HOUR_OF_DAY, old[Calendar.HOUR_OF_DAY])
                            set(Calendar.MINUTE, old[Calendar.MINUTE])
                            set(Calendar.SECOND, 0)
                        }
                        selectedMillis = c.timeInMillis
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            },
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Selecionar hora", style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 20.dp))
                    TimeInput(state = timePickerState)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
                        TextButton(onClick = {
                            val c = Calendar.getInstance().apply {
                                timeInMillis = selectedMillis
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                                set(Calendar.SECOND, 0)
                            }
                            selectedMillis = c.timeInMillis
                            showTimePicker = false
                        }) { Text("OK") }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "Novo evento" else "Editar evento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50.dp),
                ) { Text("Cancelar") }
                Button(
                    onClick = {
                        val id = existing?.id ?: "evt-${System.currentTimeMillis()}"
                        vm.saveEvent(Event(
                            id = id,
                            title = title.ifBlank { "Novo evento" },
                            emoji = emoji,
                            dateMillis = selectedMillis,
                            colorKey = colorKey,
                            createdAtMillis = existing?.createdAtMillis ?: System.currentTimeMillis(),
                        ))
                        onSaved()
                    },
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(50.dp),
                ) { Text(if (existing == null) "Criar evento" else "Salvar") }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nome do evento") },
                placeholder = { Text("Ex.: Viagem ao Japão") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
            )

            FieldLabel("Emoji")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EMOJIS.forEach { em ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (em == emoji) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainerHigh,
                            )
                            .clickable { emoji = em },
                        contentAlignment = Alignment.Center,
                    ) { Text(em, fontSize = 22.sp) }
                }
            }

            FieldLabel("Data e hora")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(16.dp),
                ) { Text(fmtDateShort(selectedMillis)) }
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                ) { Text(fmtTime(selectedMillis)) }
            }

            FieldLabel("Cor")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                EventColor.entries.forEach { color ->
                    val selected = color.key == colorKey
                    Box(
                        modifier = Modifier.size(44.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (selected) {
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(2.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color.mainColor)
                                .clickable { colorKey = color.key },
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
    )
}
