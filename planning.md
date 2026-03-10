## Summary

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