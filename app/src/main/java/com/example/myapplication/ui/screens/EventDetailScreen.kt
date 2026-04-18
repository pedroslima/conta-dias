package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.*
import com.example.myapplication.ui.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    vm: EventViewModel,
    eventId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val events by vm.events.collectAsState()
    val event = events.find { it.id == eventId } ?: return

    val col = EventColor.fromKey(event.colorKey)
    val now = System.currentTimeMillis()
    var unit by remember { mutableStateOf(bestUnit(now, event.dateMillis)) }
    var dragAccum by remember { mutableFloatStateOf(0f) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val n = diffIn(now, event.dateMillis, unit)
    val future = isFuture(event.dateMillis)

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir evento?") },
            text = { Text("\"${event.title}\" será removido permanentemente.") },
            confirmButton = {
                TextButton(onClick = onDelete) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(col.containerColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = col.onColor)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = col.onColor)
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = col.onColor)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
        ) {
            Text(event.emoji, fontSize = 56.sp, modifier = Modifier.padding(top = 8.dp))
            Text(
                event.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = col.onColor,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                fmtDateWithTime(event.dateMillis),
                style = MaterialTheme.typography.bodyMedium,
                color = col.onColor.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp),
            )

            // Hero card — swipe to change unit
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .pointerInput(unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = { dragAccum = 0f },
                            onDragCancel = { dragAccum = 0f },
                        ) { _, delta ->
                            dragAccum += delta
                            if (dragAccum > 80f) { unit = unit.prev(); dragAccum = 0f }
                            else if (dragAccum < -80f) { unit = unit.next(); dragAccum = 0f }
                        }
                    },
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.3f),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = directionWord(event.dateMillis, n).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = col.onColor,
                        letterSpacing = 0.8.sp,
                    )
                    Text(
                        text = fmtNumber(n),
                        fontSize = 96.sp,
                        fontWeight = FontWeight.Medium,
                        color = col.onColor,
                        lineHeight = 88.sp,
                        letterSpacing = (-3).sp,
                    )
                    Text(
                        text = unitLabel(unit, n),
                        style = MaterialTheme.typography.headlineSmall,
                        color = col.onColor.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Normal,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        CountUnit.entries.forEach { u ->
                            val selected = u == unit
                            FilterChip(
                                selected = selected,
                                onClick = { unit = u },
                                label = { Text(u.labelPt, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = col.onColor,
                                    selectedLabelColor = col.containerColor,
                                    containerColor = Color.White.copy(alpha = 0.4f),
                                    labelColor = col.onColor,
                                ),
                                border = null,
                            )
                        }
                    }

                    Text(
                        text = "← Arraste para trocar a unidade →",
                        style = MaterialTheme.typography.labelSmall,
                        color = col.onColor.copy(alpha = 0.45f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // Breakdown grid
            val others = CountUnit.entries.filter { it != unit }.take(4)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    others.filterIndexed { i, _ -> i % 2 == 0 }.forEach { u ->
                        val sn = diffIn(now, event.dateMillis, u)
                        BreakdownCard(sn, unitLabel(u, sn), col.onColor)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    others.filterIndexed { i, _ -> i % 2 == 1 }.forEach { u ->
                        val sn = diffIn(now, event.dateMillis, u)
                        BreakdownCard(sn, unitLabel(u, sn), col.onColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakdownCard(n: Long, label: String, onColor: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.25f),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = fmtNumber(n),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = onColor,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = onColor.copy(alpha = 0.7f),
            )
        }
    }
}
