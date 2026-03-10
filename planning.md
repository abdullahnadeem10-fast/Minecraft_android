# Catalog, Thumbnail, and Playback Fix Plan

## Summary

The three reported defects all trace back to the same practical reality: the app is currently operating from fallback content because the CloudFront catalog returns HTTP 403, so the bundled JSON has become the real production data path. The implementation should therefore prioritize replacing the fallback fixture with a complete Minecraft-themed catalog, then harden the two thumbnail surfaces so failed image requests render a visible fallback instead of an empty area, and finally correct playback so direct fallback MP4s render as video while resolved YouTube playback stops requesting audio-only streams. The current architecture already helps here: `CatalogRepositoryImpl` falls back cleanly on network failure, and `PlayerViewModel` already bypasses the YouTube resolver for non-YouTube URLs, so no navigation or domain-model changes are required unless the `best-video` resolver response shape differs from the current `tag.url` payload.

## Implementation Steps

1. Confirm the non-code assumptions that define scope before parallel work begins.
   Files reviewed only:
   - `app/src/main/java/com/example/minecraftandroid/data/repository/CatalogRepositoryImpl.kt`
   - `app/src/main/java/com/example/minecraftandroid/ui/player/PlayerViewModel.kt`
   Outcome:
   - Treat `catalog_fallback.json` as the effective catalog until the CloudFront 403 is solved.
   - Keep the existing JSON schema and field names so catalog parsing and category/video screens continue working unchanged.
   - Rely on the existing direct-URL short-circuit in `PlayerViewModel` so fallback MP4 links do not go through the YouTube resolver.
   Parallelization gate:
   - Once this scope is accepted, the data lane and UI/playback lane can proceed independently.

2. Phase 1: Replace the fallback catalog with complete, reliable content.
   Owner: Agent A
   Files:
   - `app/src/main/res/raw/catalog_fallback.json`
   Tasks:
   - Replace the current 2-category fake fixture with 8 categories: Survival Basics, Creative Builds, Redstone Mechanics, Mining & Resources, PvP & Combat, Farming & Food, Mods & Plugins, Adventure & Exploration.
   - Give each category 2 to 3 videos and keep every item aligned with the existing DTO/domain fields: `id`, `title`, `description`, `thumbnailUrl`, `youtubeUrl`, `categoryId`, `categoryTitle`, and `durationText`.
   - Use only `https://picsum.photos/seed/{unique-seed}/640/360` thumbnail URLs so image loading is deterministic and not tied to real YouTube thumbnail availability.
   - Use only the approved HTTPS MP4 sample URLs so fallback playback exercises real video streams instead of MP3/audio-only files.
   - Keep IDs and category relationships internally consistent so category detail filtering and navigation continue to work without mapper changes.
   Parallelization:
   - This task is fully isolated and can start immediately.

3. Phase 2: Make category thumbnails resilient to failed image loads.
   Owner: Agent B
   Files:
   - `app/src/main/java/com/example/minecraftandroid/ui/categories/CategoryCard.kt`
   Tasks:
   - Extend the category thumbnail rendering path so failed image requests show a visible fallback instead of a blank surface.
   - Preserve the current layout, aspect ratio, click behavior, and content descriptions.
   - Make the failure state visually match the current blank-URL placeholder behavior as closely as possible.
   Constraint to account for:
   - Coil `AsyncImage` supports `error` and `fallback` painters, but the existing placeholder is a composable `Box`, not a `Painter`. The implementation must choose one consistent approach before coding:
     - use a painter-backed placeholder that matches the current look, or
     - switch the image branch to state-based rendering so the same placeholder composable can be shown for both blank and failed URLs.
   Parallelization:
   - Can run in parallel with Steps 2, 4, and 5.

4. Phase 2: Make video-list thumbnails resilient to failed image loads.
   Owner: Agent C
   Files:
   - `app/src/main/java/com/example/minecraftandroid/ui/videos/VideoListItem.kt`
   Tasks:
   - Apply the same thumbnail failure strategy chosen in Step 3 to the video row component.
   - Keep the current 16:9 thumbnail frame, play icon affordance, and text layout unchanged.
   - Ensure null, blank, malformed, or unreachable thumbnail URLs all land on the same visible fallback state.
   Parallelization:
   - Can run in parallel with Steps 2, 3, and 5.
   Merge note:
   - Steps 3 and 4 should align on one thumbnail-failure pattern before merge so category cards and video rows do not diverge visually.

5. Phase 3: Correct playback so both fallback MP4s and resolved YouTube videos render as video.
   Owner: Agent D
   Files:
   - `app/src/main/java/com/example/minecraftandroid/data/remote/PlaybackApiService.kt`
   - `app/src/main/java/com/example/minecraftandroid/ui/player/VideoPlayerScreen.kt`
   - `app/src/test/java/com/example/minecraftandroid/data/repository/PlaybackRepositoryTest.kt`
   Conditional files if resolver payload changes:
   - `app/src/main/java/com/example/minecraftandroid/data/repository/PlaybackRepositoryImpl.kt`
   - `app/src/main/java/com/example/minecraftandroid/data/model/PlaybackResponseDto.kt`
   Tasks:
   - Change the resolver endpoint away from `api/youtube/best-audio` to the confirmed video-capable endpoint, expected to be `api/youtube/best-video`.
   - Disable artwork rendering on `PlayerView` so audio artwork or Android-logo fallback is not shown when a stream lacks video frames.
   - Keep the existing `VideoPlayerScreen` state flow intact: resolving, ready, retryable error.
   - Update playback unit tests so success cases reflect video-oriented resolver behavior rather than audio-only fixtures.
   Scope guard:
   - If the `best-video` endpoint still returns the same JSON shape with `tag.url`, this stays a two-file production change plus test updates.
   - If the endpoint response shape differs, expand this phase to the repository/DTO layer and keep those edits inside the same lane.
   Parallelization:
   - Can run in parallel with Steps 2, 3, and 4 after the endpoint contract is confirmed.

6. Phase 4: Verify the repaired fallback experience and resolver flow end to end.
   Owner: Agent E
   Files:
   - `app/src/androidTest/java/com/example/minecraftandroid/ui/player/VideoPlayerFlowTest.kt`
   - Optional existing UI tests if regressions are discovered during implementation:
     - `app/src/androidTest/java/com/example/minecraftandroid/ui/categories/CategoriesScreenTest.kt`
     - `app/src/androidTest/java/com/example/minecraftandroid/ui/categorydetail/CategoryDetailScreenTest.kt`
   Tasks:
   - Extend or adjust playback instrumentation coverage so it still validates the player loading and error states after the endpoint change.
   - Add a validation path for direct fallback media URLs if the current test strategy can cover it without depending on the real backend.
   - Perform manual QA focused on the real user-visible defects:
     - categories screen shows 8 categories,
     - each category opens a populated video list,
     - thumbnails never disappear silently on load failure,
     - fallback MP4 items play video without the Android logo,
     - YouTube-backed items resolve through the video endpoint and display video when the backend returns a playable stream.
   Parallelization:
   - Test authoring can begin after Step 5 freezes the resolver contract and player behavior.

7. Merge order and work split.
   Recommended parallel lanes:
   - Lane A: Step 2 only (`catalog_fallback.json`).
   - Lane B: Step 3 only (`CategoryCard.kt`).
   - Lane C: Step 4 only (`VideoListItem.kt`).
   - Lane D: Step 5 plus Step 6 playback tests (`PlaybackApiService.kt`, `VideoPlayerScreen.kt`, playback tests).
   Merge order:
   - Merge Lane A first because it is isolated and unblocks immediate QA.
   - Merge Lanes B and C together once the thumbnail failure strategy is consistent across both components.
   - Merge Lane D after the exact resolver endpoint contract is verified.
   - Run final manual QA after all lanes land together because the defects only fully clear when data, thumbnail handling, and playback changes are combined.

## Edge Cases To Handle

- The fallback catalog is now the primary runtime path while CloudFront returns 403, so any JSON syntax or schema mistake in `catalog_fallback.json` will break the whole browsing experience.
- The new fallback entries must keep `categoryId` and `categoryTitle` aligned with their parent category objects or category detail screens will show missing or mismatched content.
- `picsum.photos` is reliable but still network-backed; thumbnails must remain visible through the local placeholder path even if the device is offline or the remote image request fails.
- Blank and failed image URLs should produce the same user-visible fallback state so the UI does not look inconsistent across categories and videos.
- Disabling artwork on `PlayerView` prevents the Android logo symptom, but a truly audio-only stream would then render as a blank video surface; the endpoint change and fallback MP4 swap must therefore land together.
- If the backend does not actually expose `api/youtube/best-video`, the implementation must stop and confirm the real video-capable endpoint before changing app code.
- If the `best-video` endpoint returns a different JSON contract than `tag.url`, the scope expands beyond `PlaybackApiService.kt` into repository parsing and its tests.
- The app currently uses `youtubeUrl` as the field name for both actual YouTube links and direct MP4 fallback URLs; this is acceptable for now because `PlayerViewModel` already distinguishes direct media URLs from YouTube URLs, but the field naming remains semantically awkward.

## Open Questions

1. Is the backend endpoint definitely `api/youtube/best-video`, and does it still return the playable URL at `tag.url`?
2. For thumbnail failure UI, is a painter-based visual match acceptable, or must the current composable placeholder box be reused exactly?
3. Should Phase 4 add automated verification for the fallback catalog count and populated category lists, or is manual QA sufficient once the existing categories/category-detail tests still pass?## Summary

Phase 4 should replace the current placeholder video destination with a real playback flow built on the repo that already exists, not the earlier scaffold assumptions. The safest execution order is to lock the shared contracts first, then run resolver-state work and Media3 player UI work in parallel, merge them through navigation wiring, and finish with security validation and tests. Two implicit prerequisites need to be tracked even though they were not listed in the task table: add Media3 dependencies in `app/build.gradle.kts`, and thread `PlaybackRepository` from the app shell into navigation so the player route can create its ViewModel.

## Implementation Steps

1. Establish the shared Phase 4 contract before any parallel implementation starts.
   Scope:
   - Confirm that Phase 4 replaces the current placeholder route with a dedicated player route.
   - Freeze the route payload shape. Minimum payload is the encoded YouTube URL. Preferred payload is YouTube URL plus the video title so the player screen can render stable labels without guessing.
   - Add Media3 dependencies in `app/build.gradle.kts` because the current module does not include them.
   - Preserve the existing source layout convention: files live under `app/src/main/java/com/example/minecraftandroid/...` while package declarations remain `com.abdullahnadeem.minecraftandroid`.
   Sequential:
   - This step must finish first because every later lane depends on the route contract or Media3 availability.

2. Run the resolver-state lane and the Media3 UI lane in parallel.
   Agent A: Resolver and ViewModel lane.
   Owns:
   - `app/src/main/java/com/example/minecraftandroid/data/remote/PlaybackApiService.kt`
   - `app/src/main/java/com/example/minecraftandroid/data/repository/PlaybackRepository.kt`
   - `app/src/main/java/com/example/minecraftandroid/data/repository/PlaybackRepositoryImpl.kt`
   - `app/src/main/java/com/example/minecraftandroid/ui/player/PlayerUiState.kt`
   - `app/src/main/java/com/example/minecraftandroid/ui/player/PlayerViewModel.kt`
   - `app/src/main/java/com/example/minecraftandroid/ui/player/PlayerViewModelFactory.kt`
   - `app/src/test/java/com/example/minecraftandroid/data/repository/PlaybackRepositoryTest.kt`
   Deliverable:
   - A stable resolver contract that extracts `tag.url`, exposes loading, success, and failure UI state, and keeps resolver errors separate from actual player playback state.

   Agent B: Media3 player UI lane.
   Owns:
   - `app/build.gradle.kts`
   - `app/src/main/java/com/example/minecraftandroid/ui/player/VideoPlayerScreen.kt`
   - `app/src/main/java/com/example/minecraftandroid/ui/player/PlayerScaffold.kt`
   - `app/src/main/java/com/example/minecraftandroid/ui/player/rememberManagedPlayer.kt`
   - `app/src/main/res/values/strings.xml`
   Deliverable:
   - A player surface that can accept a resolved stream URL, creates an `ExoPlayer` on the main thread, prepares a `MediaItem`, and releases the player when the composable leaves scope.

3. Merge the two parallel lanes through navigation and screen wiring.
   Agent C: Navigation and integration lane.
   Owns:
   - `app/src/main/java/com/example/minecraftandroid/navigation/AppDestinations.kt`
   - `app/src/main/java/com/example/minecraftandroid/navigation/AppNavHost.kt`
   - `app/src/main/java/com/example/minecraftandroid/ui/categorydetail/CategoryDetailScreen.kt`
   - `app/src/main/java/com/example/minecraftandroid/ui/videos/VideoListItem.kt`
   - `app/src/main/java/com/example/minecraftandroid/MinecraftVideosApp.kt`
   Deliverable:
   - Tapping a video navigates to the real player route, `PlaybackRepository` is available where the player ViewModel is created, and the old placeholder destination is fully replaced in the nav graph.
   Sequential:
   - Agent C can prepare route constants early, but final integration should wait until Agent A publishes the ViewModel inputs and Agent B publishes the player-screen inputs.

4. Finalize platform security in parallel with late integration, but before device playback validation.
   Agent D: Manifest and network security lane.
   Owns:
   - `app/src/main/AndroidManifest.xml`
   - `app/src/main/res/xml/network_security_config.xml`
   Deliverable:
   - Cleartext remains denied by default and explicitly allowed only for `16.170.165.189`, with the manifest still pointing at the network security config.
   Sequential:
   - This can run in parallel with Agent C, but it must be merged before integrated playback testing on device or emulator.

5. Add the end-to-end playback test after navigation is stable.
   Agent E: Instrumentation test lane.
   Owns:
   - `app/src/androidTest/java/com/example/minecraftandroid/ui/player/VideoPlayerFlowTest.kt`
   Deliverable:
   - A flow test that verifies video selection reaches the player experience and renders the expected playback-state UI.
   Sequential:
   - This starts after Agent C because it depends on the final route, final nav behavior, and final strings.

6. Merge and validate in a fixed order.
   Order:
   - Merge Agent A and Agent B first.
   - Merge Agent C next against the published resolver and player interfaces.
   - Merge Agent D before any real playback checks.
   - Merge Agent E last.
   - Then run a full debug build plus unit and instrumentation tests.

## Parallelization Plan

Parallel block 1:
- Agent A and Agent B can run at the same time after the shared contract step is done.
- Agent D can also run during this block because its files do not overlap with the other agents.

Parallel block 2:
- Agent C starts after Agent A and Agent B freeze their public contracts.
- Agent E waits for Agent C, then runs alone or alongside manual playback verification.

Sequential gates:
- Gate 1: Route payload and Media3 dependency decision.
- Gate 2: Resolver contract and player-screen API freeze.
- Gate 3: Navigation merge.
- Gate 4: Device and emulator playback validation.

## Edge Cases To Handle

- Resolver success with missing or blank `tag.url` must produce a user-facing error state, not an empty player.
- Resolver HTTP failures and malformed JSON must stay in the ViewModel state layer and not crash the player UI.
- Videos with blank or null `youtubeUrl` should not navigate into playback without an explicit disabled or error path.
- Back navigation must release the player and avoid continuing playback after leaving the route.
- Configuration changes and process recreation must preserve route arguments and avoid rebuilding into an invalid player state.
- `strings.xml` is a merge hotspot and should stay owned by a single agent.
- `AppNavHost.kt` is the main integration hotspot and should stay owned by a single agent.
- `PlaybackApiService.kt` currently returns `Response<String>`. If that remains true, JSON decoding should stay inside the repository. If it changes to a typed DTO response, that parsing boundary still stays in the data layer.
- Integrated playback may still fail even with a valid resolver response if the returned stream is unsupported by the current Media3 setup, so playback errors need a distinct UI state from resolver errors.

## Open Questions

1. Should the player route carry only the encoded YouTube URL, or also the video title for better player labels and more stable test assertions?
2. Is `VideoPlaybackPlaceholder.kt` expected to be removed in Phase 4, or kept unused for history/reference?
3. Should `VideoPlayerFlowTest.kt` use the real resolver endpoint, or inject a fake `PlaybackRepository` so UI tests stay deterministic?
4. Is a dedicated full-screen player route the confirmed UX, or should playback appear inside the category detail screen instead?## Summary

This workspace is currently a blank starting point: there is no Android project, no source files, and no initialized Git repository. The implementation should therefore begin by scaffolding a single-module Android app in Kotlin with Jetpack Compose, then layering in a small data stack for category and video loading, Compose navigation, and Media3 playback. Two external integration risks need to be planned for up front: the catalog endpoint at `https://d37oz74k5exkdg.cloudfront.net/minecraft%20videos.json` returned `AccessDenied` from this environment, so its runtime accessibility and exact JSON schema are still assumptions, and the playback resolver uses plain HTTP (`http://16.170.165.189:8001/...`), which requires Android cleartext-network allowance for API 28+.

## Repo Assessment

- Workspace state: empty directory; no existing Android app structure to preserve.
- Git state: no Git repository detected, so every requested `commit and push` step depends on initializing Git and configuring a remote first.
- Architecture fit: Jetpack Compose is appropriate because the app only needs two main screens plus an embedded player surface.
- External services status:
  - The CloudFront catalog URL returned `AccessDenied` from this environment, so its Android accessibility and payload shape must be validated during Phase 1.
  - The playback resolver contract is assumed from the request: send a YouTube URL to `http://16.170.165.189:8001/api/youtube/best-audio?url=` and read the playable stream from `tag.url`.

## Phase 1 Plan: Initialize Project, Networking, Models, Data Loading, Git Setup

1. Scaffold the Android project and baseline module structure.
   Files:
   - New: `settings.gradle.kts`
   - New: `build.gradle.kts`
   - New: `gradle.properties`
   - New: `gradlew`
   - New: `gradlew.bat`
   - New: `gradle/wrapper/gradle-wrapper.properties`
   - New: `app/build.gradle.kts`
   - New: `app/proguard-rules.pro`
   - New: `app/src/main/AndroidManifest.xml`
   - New: `app/src/main/java/com/example/minecraftandroid/MainActivity.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/MinecraftVideosApp.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/theme/Color.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/theme/Theme.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/theme/Type.kt`
   - New: `app/src/main/res/values/strings.xml`
   - New: `app/src/main/res/values/themes.xml`
   - New: `app/src/main/res/xml/network_security_config.xml`
   Notes:
   - Include Compose, Navigation Compose, Retrofit, a JSON converter, Coil, Media3, lifecycle-viewmodel-compose, and coroutines dependencies.
   - Plan for `android:networkSecurityConfig` or `android:usesCleartextTraffic` because Phase 4 depends on an HTTP endpoint.

2. Define the network contract and domain models for categories, videos, and playback resolution.
   Files:
   - New: `app/src/main/java/com/example/minecraftandroid/data/model/CategoryDto.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/data/model/VideoDto.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/data/model/CatalogDto.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/data/model/PlaybackResponseDto.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/domain/model/Category.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/domain/model/Video.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/data/mapper/CatalogMappers.kt`
   Notes:
   - Model files should stay flexible until the catalog schema is confirmed.
   - Keep playback response parsing isolated so changes to the resolver API only affect one small area.

3. Create Retrofit services, repository layer, and app-level dependency wiring.
   Files:
   - New: `app/src/main/java/com/example/minecraftandroid/data/remote/CatalogApiService.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/data/remote/PlaybackApiService.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/data/remote/ApiModule.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/data/repository/CatalogRepository.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/data/repository/CatalogRepositoryImpl.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/data/repository/PlaybackRepository.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/data/repository/PlaybackRepositoryImpl.kt`
   Notes:
   - One Retrofit instance can serve the HTTPS catalog endpoint and another can serve the HTTP playback resolver to keep security and base URL concerns separate.
   - Use a repository boundary so UI phases do not depend on raw DTOs.

4. Add state holders for loading catalog data and proving the fetch path works before UI polish.
   Files:
   - New: `app/src/main/java/com/example/minecraftandroid/ui/catalog/CatalogUiState.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/catalog/CatalogViewModel.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/catalog/CatalogViewModelFactory.kt`
   Notes:
   - This task should expose loading, success, and failure states so later screens can reuse them.

5. Add a minimal smoke UI that exercises the loaded catalog data.
   Files:
   - New: `app/src/main/java/com/example/minecraftandroid/ui/catalog/CatalogDebugScreen.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/MinecraftVideosApp.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/MainActivity.kt`
   Notes:
   - This screen is temporary validation infrastructure for Phase 1 and can be folded into the categories screen in Phase 2.

6. Initialize Git and prepare the first commit and push path.
   Files:
   - New: `.gitignore`
   - Optional new: `README.md`
   Notes:
   - `git init`, initial branch naming, and remote configuration belong here.
   - Push cannot complete until the remote repository URL and credentials are available.

7. Phase 1 checkpoint: commit and push after the project builds and the catalog fetch path is validated.
   Files:
   - No source changes required beyond the files above.

Phase 1 dependencies:
- Task 1 is the prerequisite for all other tasks.
- Task 2 can run in parallel with Task 6 once package naming is decided.
- Task 3 depends on Tasks 1 and 2.
- Task 4 depends on Task 3.
- Task 5 depends on Task 4.
- Task 7 depends on Tasks 1 through 6.

Phase 1 verification:
- Sync Gradle successfully and build the debug app.
- Confirm the app launches on an emulator or device.
- Verify the catalog request either loads real data or fails with a controlled error state.
- If the CloudFront URL still denies access, capture the exact HTTP failure and decide whether headers, a proxy, or a bundled fallback fixture are needed before Phase 2.
- Verify Git repo initialization, branch creation, and that a push target exists before attempting `push`.

## Phase 2 Plan: Categories Grid Screen

1. Define the categories route and app navigation shell.
   Files:
   - New: `app/src/main/java/com/example/minecraftandroid/navigation/AppDestinations.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/navigation/AppNavHost.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/MinecraftVideosApp.kt`
   Notes:
   - Keep route definitions explicit because category selection will feed Phase 3.

2. Build reusable grid-card UI for category presentation.
   Files:
   - New: `app/src/main/java/com/example/minecraftandroid/ui/categories/CategoryCard.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/categories/CategoriesScreen.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/categories/CategoriesUiState.kt`
   - Update: `app/src/main/res/values/strings.xml`
   Notes:
   - Use Coil image loading for thumbnails and include empty, loading, and error visual states.

3. Connect catalog state to the categories grid.
   Files:
   - Update: `app/src/main/java/com/example/minecraftandroid/ui/catalog/CatalogViewModel.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/ui/catalog/CatalogUiState.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/ui/categories/CategoriesScreen.kt`
   Notes:
   - Avoid duplicating data fetch logic; the categories screen should consume the same catalog state created in Phase 1.

4. Add Compose previews and basic UI tests for category rendering.
   Files:
   - New: `app/src/debug/java/com/example/minecraftandroid/ui/categories/CategoriesPreviewData.kt`
   - New: `app/src/androidTest/java/com/example/minecraftandroid/ui/categories/CategoriesScreenTest.kt`

5. Phase 2 checkpoint: commit and push after categories render in a grid and category taps can be wired to a placeholder destination.
   Files:
   - No additional files required beyond the updates above.

Phase 2 dependencies:
- Task 1 depends on Phase 1 completion.
- Task 2 can run in parallel with Task 1 after the route names are agreed.
- Task 3 depends on Tasks 1 and 2.
- Task 4 can run in parallel with late Task 3 once stable sample data exists.
- Task 5 depends on Tasks 1 through 4.

Phase 2 verification:
- Confirm the categories screen is the start destination.
- Verify categories display in a grid with title and thumbnail.
- Verify loading, empty, and error states render without crashes.
- Verify category taps invoke navigation with the selected category identifier.
- Run UI tests or manual smoke checks across small and large screens.

## Phase 3 Plan: Category Details and Video Listing

1. Add a category details destination and argument passing.
   Files:
   - Update: `app/src/main/java/com/example/minecraftandroid/navigation/AppDestinations.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/navigation/AppNavHost.kt`
   Notes:
   - Pass a stable category identifier, not the whole object, to keep navigation resilient.

2. Create detail-screen state and lookup logic for the selected category.
   Files:
   - New: `app/src/main/java/com/example/minecraftandroid/ui/categorydetail/CategoryDetailUiState.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/categorydetail/CategoryDetailViewModel.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/categorydetail/CategoryDetailViewModelFactory.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/data/repository/CatalogRepository.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/data/repository/CatalogRepositoryImpl.kt`
   Notes:
   - The repository should expose category lookup by ID or slug so the detail screen can restore after process death.

3. Build the category detail UI and video row/card components.
   Files:
   - New: `app/src/main/java/com/example/minecraftandroid/ui/categorydetail/CategoryDetailScreen.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/videos/VideoListItem.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/videos/VideoPlaybackPlaceholder.kt`
   - Update: `app/src/main/res/values/strings.xml`
   Notes:
   - Each video item should show thumbnail, title, and an obvious play affordance even before real playback lands.

4. Wire category taps from the grid to the detail screen.
   Files:
   - Update: `app/src/main/java/com/example/minecraftandroid/ui/categories/CategoriesScreen.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/navigation/AppNavHost.kt`

5. Add previews and UI tests for detail rendering.
   Files:
   - New: `app/src/debug/java/com/example/minecraftandroid/ui/categorydetail/CategoryDetailPreviewData.kt`
   - New: `app/src/androidTest/java/com/example/minecraftandroid/ui/categorydetail/CategoryDetailScreenTest.kt`

6. Phase 3 checkpoint: commit and push after navigation and video listing work end to end.
   Files:
   - No additional files required beyond the updates above.

Phase 3 dependencies:
- Task 1 depends on Phase 2 navigation shell.
- Task 2 depends on Task 1 and the Phase 1 repository layer.
- Task 3 can start in parallel with Task 2 using preview data.
- Task 4 depends on Tasks 1 through 3.
- Task 5 can run in parallel with late Task 4.
- Task 6 depends on Tasks 1 through 5.

Phase 3 verification:
- Verify tapping a category opens the correct detail screen.
- Verify the selected category title and video list match the chosen category.
- Verify each video row shows its thumbnail and play-ready UI shell.
- Verify back navigation returns to the grid without losing state.
- Verify configuration changes do not break the selected-category state.

## Phase 4 Plan: Playback Resolver Integration and Media3 Player

1. Add playback-specific state and resolver flow.
   Files:
   - New: `app/src/main/java/com/example/minecraftandroid/ui/player/PlayerUiState.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/player/PlayerViewModel.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/player/PlayerViewModelFactory.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/data/repository/PlaybackRepository.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/data/repository/PlaybackRepositoryImpl.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/data/remote/PlaybackApiService.kt`
   Notes:
   - Keep resolver-request state separate from actual player state so network failures do not corrupt the player lifecycle.

2. Add Media3 player host UI.
   Files:
   - New: `app/src/main/java/com/example/minecraftandroid/ui/player/VideoPlayerScreen.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/player/PlayerScaffold.kt`
   - New: `app/src/main/java/com/example/minecraftandroid/ui/player/rememberManagedPlayer.kt`
   - Update: `app/src/main/res/values/strings.xml`
   Notes:
   - The player screen can open either as a dedicated route or an in-place overlay from the detail screen; decide once UX is agreed.

3. Wire video selection from the category details screen into playback.
   Files:
   - Update: `app/src/main/java/com/example/minecraftandroid/navigation/AppDestinations.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/navigation/AppNavHost.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/ui/categorydetail/CategoryDetailScreen.kt`
   - Update: `app/src/main/java/com/example/minecraftandroid/ui/videos/VideoListItem.kt`

4. Add manifest and network security updates required for the HTTP playback resolver.
   Files:
   - Update: `app/src/main/AndroidManifest.xml`
   - Update: `app/src/main/res/xml/network_security_config.xml`
   Notes:
   - Restrict cleartext allowance to the resolver host if possible instead of enabling global cleartext traffic.

5. Add playback-focused tests and manual diagnostics.
   Files:
   - New: `app/src/androidTest/java/com/example/minecraftandroid/ui/player/VideoPlayerFlowTest.kt`
   - New: `app/src/test/java/com/example/minecraftandroid/data/repository/PlaybackRepositoryTest.kt`
   Notes:
   - Repository tests should cover success, malformed resolver response, and network error handling.

6. Phase 4 checkpoint: commit and push after resolver lookup and Media3 playback succeed on a real device or emulator.
   Files:
   - No additional files required beyond the updates above.

Phase 4 dependencies:
- Task 1 depends on Phase 1 playback repository scaffolding.
- Task 2 can run in parallel with Task 1.
- Task 3 depends on Tasks 1 and 2.
- Task 4 should complete before integrated device testing.
- Task 5 can run in parallel with late Task 3 once interfaces are stable.
- Task 6 depends on Tasks 1 through 5.

Phase 4 verification:
- Confirm selecting a video triggers the resolver call with the expected YouTube URL.
- Confirm the resolver response is parsed and `tag.url` is used as the Media3 media source.
- Confirm playback starts, pauses, resumes, and releases correctly when leaving the screen.
- Verify user-facing handling for resolver failure, missing `tag.url`, unsupported streams, and slow network responses.
- Verify cleartext-network configuration is limited to the resolver host and does not accidentally open broader HTTP access.

## Cross-Phase Dependencies And Parallel Work

- Project scaffolding, package naming, and Gradle setup are the root dependency for all other work.
- Data-model definition and repository interfaces can run in parallel with Git setup once the app package name is fixed.
- Categories UI work can proceed in parallel with some testing and preview-data setup after the repository contract stabilizes.
- Category-detail UI composition can begin in parallel with detail-view-model implementation using preview fixtures.
- Media3 player UI can be built in parallel with resolver integration because they only meet at the final selected stream URL boundary.
- Every `commit and push` task depends on a valid Git remote and working authentication, which are external to app code.

## Likely Blockers And Assumptions

- Catalog endpoint accessibility is unverified for Android clients. From this environment the URL returned `AccessDenied`, so Phase 1 must validate whether the production app can call it directly or needs specific headers, a proxy, or a packaged fallback JSON for development.
- Catalog JSON schema is not confirmed. The plan assumes category entries contain enough information to render a title, thumbnail, and nested or related video items with a playable YouTube URL.
- The playback resolver is plain HTTP. Android 9+ blocks cleartext traffic by default, so the app must explicitly allow the resolver host via manifest or network security config.
- The playback resolver response shape is only partially known. The plan assumes a successful response always contains `tag.url`; if not, error handling will need to surface that mismatch clearly.
- `Push` steps are blocked until a remote Git repository exists and credentials are configured on the machine.
- The app may need placeholder artwork or fallback UI if thumbnails are missing or the JSON contains incomplete entries.
- If the catalog data set is large, pagination or lazy loading may become necessary, but the current request reads as a single fetch of a manageable JSON payload.

## Open Questions

1. What application ID and display name should the new Android project use?
2. Is there an existing Git remote URL that the new repository should push to, or should the push steps remain pending until one is provided?
3. Should the player open on a dedicated full-screen route, or should playback happen inline on the category details screen?
4. If the CloudFront catalog remains inaccessible from Android, is a temporary bundled sample JSON acceptable so UI phases can continue while the endpoint issue is resolved?