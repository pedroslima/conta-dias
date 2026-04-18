package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.EventViewModel
import com.example.myapplication.ui.screens.AddEditEventScreen
import com.example.myapplication.ui.screens.EventDetailScreen
import com.example.myapplication.ui.screens.EventListScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ContaDiasApp()
            }
        }
    }
}

@Composable
fun ContaDiasApp(vm: EventViewModel = viewModel()) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "event_list") {
        composable("event_list") {
            EventListScreen(
                vm = vm,
                onOpenEvent = { id -> nav.navigate("event_detail/$id") },
                onAddEvent = { nav.navigate("add_event") },
            )
        }
        composable(
            "event_detail/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType }),
        ) { back ->
            val eventId = back.arguments!!.getString("eventId")!!
            EventDetailScreen(
                vm = vm,
                eventId = eventId,
                onBack = { nav.popBackStack() },
                onEdit = { nav.navigate("edit_event/$eventId") },
                onDelete = {
                    vm.deleteEvent(eventId)
                    nav.popBackStack("event_list", false)
                },
            )
        }
        composable("add_event") {
            AddEditEventScreen(
                vm = vm,
                eventId = null,
                onBack = { nav.popBackStack() },
                onSaved = { nav.popBackStack() },
            )
        }
        composable(
            "edit_event/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType }),
        ) { back ->
            val eventId = back.arguments!!.getString("eventId")!!
            AddEditEventScreen(
                vm = vm,
                eventId = eventId,
                onBack = { nav.popBackStack() },
                onSaved = { nav.popBackStack() },
            )
        }
    }
}
