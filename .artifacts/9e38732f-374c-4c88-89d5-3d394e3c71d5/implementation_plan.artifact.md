# Performance Optimization Plan for Catering Menu and Card Screens

This plan addresses the lagging animations and slow screen opening reported by the user. The primary focus is on reducing initial composition overhead, optimizing custom drawing operations, and improving memory management.

## User Review Required

> [!IMPORTANT]
> The optimizations involve changing how custom shadows and shimmer effects are drawn. This will improve performance but requires verification to ensure the visual appearance remains identical.

> [!NOTE]
> I will be replacing `SubcomposeAsyncImage` with `AsyncImage` in the `ItemDetailsSheetContent`. This improves performance by avoiding subcomposition, but might slightly change how "loading" and "error" states are rendered (using standard `AsyncImage` painter states instead of a dedicated Composable).

## Proposed Changes

### [Core UI Optimizations]

#### [MODIFY] [ModifierExt.kt](file:///E:/AndroidStudioProjects/Jasnify/app/src/main/java/com/harshdeep/jasnify/presentation/utils/ModifierExt.kt)
- Rewrite `pill360Shadow` to use `drawWithCache`.
- Avoid object allocation (`Paint`, `BlurMaskFilter`) during the draw phase by caching them.
- Use `Paint().asFrameworkPaint()` only when needed and cache the framework paint properties.

#### [MODIFY] [LoadingStates.kt](file:///E:/AndroidStudioProjects/Jasnify/app/src/main/java/com/harshdeep/jasnify/presentation/components/states/LoadingStates.kt)
- Optimize `shimmerBrush` by using `remember` for the color list.
- Ensure the `shimmerBrush` doesn't trigger unnecessary recompositions by stabilizing its inputs.

---

### [Catering Menu Screen]

#### [MODIFY] [CateringMenu.kt](file:///E:/AndroidStudioProjects/Jasnify/app/src/main/java/com/harshdeep/jasnify/presentation/screens/catering/CateringMenu.kt)
- **Refactor Large Composable**: Extract sub-screen contents (like `CateringRoomContent`) into separate files or smaller composables to improve skip-ability.
- **Optimize `remember` blocks**: Combine or simplify `remember` and `derivedStateOf` blocks where redundant.
- **Replace `SubcomposeAsyncImage`**: Use `AsyncImage` in `ItemDetailsSheetContent` to reduce subcomposition overhead during the first render of the details sheet.
- **Lazy Loading of Carousels**: Ensure that `HighlightedVendors` (the carousel) is handled efficiently by the `LazyColumn`.

---

### [Invitation Cards Screen]

#### [MODIFY] [CardsScreen.kt](file:///E:/AndroidStudioProjects/Jasnify/app/src/main/java/com/harshdeep/jasnify/presentation/screens/invitation_cards/CardsScreen.kt)
- **Shared Transition Optimization**: Review the use of `SharedTransitionLayout` to ensure it's not being over-triggered on the initial frame.
- **State Deferral**: Defer the initialization of heavy states that are only needed for specific views (like `EDIT_DETAILS`) until those views are actually active.

---

### [Verification Plan]

### Automated Tests
- Run the app and monitor the "Profile: GPU Rendering" (or "GPU Overdraw") tools in Android Developer Options.
- Use `Macrobenchmark` (if set up in the project) to measure frame timing during screen navigation.

### Manual Verification
- Verify that the "Catering Menu" opens smoothly on the first attempt after app launch.
- Confirm that navigation between `MENU`, `VENDOR_DETAIL`, and `MANAGE_ROOM_ACCESS` within `CateringMenuScreen` is fluid.
- Check that the `pill360Shadow` and shimmer effects still look as expected.
