# Minecraft Android Phase 1

This workspace now contains a single-module Kotlin Android app using Jetpack Compose.

## Phase 1 scope

- Jetpack Compose application scaffold
- Retrofit and OkHttp setup for catalog and playback requests
- Coil image loading support
- Navigation Compose and lifecycle ViewModel wiring
- Repository and ViewModel layers for catalog loading
- Minimal smoke UI that shows loading, success, empty, or error states
- Android network security configuration that allowlists the Phase 4 HTTP playback host

## Current external constraints

- The catalog endpoint at `https://d37oz74k5exkdg.cloudfront.net/minecraft%20videos.json` currently returns `HTTP 403 AccessDenied` from this machine.
- Because the payload is not directly inspectable here, catalog parsing is intentionally tolerant and supports several likely object and array shapes.
- Phase 1 therefore proves the full request and error-handling path locally, and will render categories if the endpoint is reachable from the target device or emulator.

## Package name

The app namespace and application ID are `com.abdullahnadeem.minecraftandroid`.

## Notes about local verification

- `git` is available on this machine.
- `java` and `gradle` are not available on PATH in the current shell, so local builds are blocked until a JDK and Android/Gradle toolchain are available.