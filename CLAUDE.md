# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected emulator/device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run a single unit test class
./gradlew test --tests "com.example.myapplication.ExampleUnitTest"
```

## Architecture

The app follows a simple unidirectional data flow: `Repository → ViewModel (StateFlow) → Composable screens`.

**Data layer** (`data/`):
- `Event` — plain data class (id, title, emoji, dateMillis, colorKey)
- `EventRepository` — reads/writes a JSON array to `SharedPreferences` ("conta_dias"). First launch seeds 7 example events.
- `CountUnit` — enum (SECONDS → YEARS) with `diffIn()`, `bestUnit()`, and pt-BR formatting utilities. All time math lives here.
- `EventColor` — enum mapping color keys (e.g. `"terracotta"`) to Compose `Color` triples (container, on, main).

**UI layer** (`ui/`):
- `EventViewModel` — `AndroidViewModel` exposing `events: StateFlow<List<Event>>`. Calls `repo.saveEvents()` on every mutation.
- Screens use `vm.events.collectAsState()` and call `vm.saveEvent()` / `vm.deleteEvent()`.

**Navigation** (`MainActivity.kt`):
Routes: `event_list` → `event_detail/{eventId}` → `edit_event/{eventId}`, and `event_list` → `add_event`. The `NavHost` is in `ContaDiasApp()`.

**Widget** (`widget/`):
- `ContaDiasWidget` (Glance) reads events directly from `EventRepository` (bypasses ViewModel) and renders the nearest future event.
- `ContaDiasWidgetReceiver` registers the Glance widget. Declared in `AndroidManifest.xml` with `@xml/conta_dias_widget_info`.

## Key Conventions

- All user-facing strings are in Portuguese (pt-BR). Date/number formatting uses `Locale("pt", "BR")`.
- Event colors are referenced by string key (`colorKey` in `Event`), resolved via `EventColor.fromKey()`.
- `compileSdk` uses the extension SDK syntax: `release(36) { minorApiLevel = 1 }` — do not change this to a plain integer.
- The `Icons.*` references require `material-icons-core` and `material-icons-extended` dependencies (both declared in `libs.versions.toml`).
