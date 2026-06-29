package com.guestgallery.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guestgallery.core.theme.Dimens
import com.guestgallery.core.ui.components.GuestGalleryTopBar
import com.guestgallery.settings.ui.components.SettingsCategory
import com.guestgallery.settings.ui.sections.AccessibilitySettingsSection
import com.guestgallery.settings.ui.sections.AppearanceSettingsSection
import com.guestgallery.settings.ui.sections.PerformanceSettingsSection
import com.guestgallery.settings.ui.sections.PrivacySettingsSection
import com.guestgallery.settings.ui.sections.SecuritySettingsSection
import com.guestgallery.settings.ui.sections.ViewerSettingsSection
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var securityExpanded by remember { mutableStateOf(true) }
    var viewerExpanded by remember { mutableStateOf(false) }
    var appearanceExpanded by remember { mutableStateOf(false) }
    var privacyExpanded by remember { mutableStateOf(false) }
    var performanceExpanded by remember { mutableStateOf(false) }
    var accessibilityExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GuestGalleryTopBar(
                title = "Settings",
                onBackClick = onBackClick,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            // ── Security Category ────────────────────────────────────────────
            item {
                SettingsCategory(
                    title = "Security & Lockdown",
                    icon = Icons.Rounded.Security,
                    expanded = securityExpanded,
                    onToggle = { securityExpanded = !securityExpanded },
                )
            }
            item {
                AnimatedCategoryVisibility(visible = securityExpanded) {
                    SecuritySettingsSection(
                        settings = settings,
                        onUpdate = { key, value -> viewModel.updateBoolean(key, value) },
                    )
                }
            }

            // ── Viewer Category ──────────────────────────────────────────────
            item {
                SettingsCategory(
                    title = "Viewer Controls",
                    icon = Icons.Rounded.Visibility,
                    expanded = viewerExpanded,
                    onToggle = { viewerExpanded = !viewerExpanded },
                )
            }
            item {
                AnimatedCategoryVisibility(visible = viewerExpanded) {
                    ViewerSettingsSection(
                        settings = settings,
                        viewModel = viewModel,
                    )
                }
            }

            // ── Appearance Category ──────────────────────────────────────────
            item {
                SettingsCategory(
                    title = "Appearance & Theme",
                    icon = Icons.Rounded.Palette,
                    expanded = appearanceExpanded,
                    onToggle = { appearanceExpanded = !appearanceExpanded },
                )
            }
            item {
                AnimatedCategoryVisibility(visible = appearanceExpanded) {
                    AppearanceSettingsSection(
                        settings = settings,
                        viewModel = viewModel,
                    )
                }
            }

            // ── Privacy Category ─────────────────────────────────────────────
            item {
                SettingsCategory(
                    title = "Privacy & Erasure",
                    icon = Icons.Rounded.PrivacyTip,
                    expanded = privacyExpanded,
                    onToggle = { privacyExpanded = !privacyExpanded },
                )
            }
            item {
                AnimatedCategoryVisibility(visible = privacyExpanded) {
                    PrivacySettingsSection(
                        settings = settings,
                        viewModel = viewModel,
                    )
                }
            }

            // ── Performance Category ─────────────────────────────────────────
            item {
                SettingsCategory(
                    title = "Performance & Storage",
                    icon = Icons.Rounded.Speed,
                    expanded = performanceExpanded,
                    onToggle = { performanceExpanded = !performanceExpanded },
                )
            }
            item {
                AnimatedCategoryVisibility(visible = performanceExpanded) {
                    PerformanceSettingsSection(
                        settings = settings,
                        viewModel = viewModel,
                    )
                }
            }

            // ── Accessibility Category ───────────────────────────────────────
            item {
                SettingsCategory(
                    title = "Accessibility & Comfort",
                    icon = Icons.Rounded.Accessibility,
                    expanded = accessibilityExpanded,
                    onToggle = { accessibilityExpanded = !accessibilityExpanded },
                )
            }
            item {
                AnimatedCategoryVisibility(visible = accessibilityExpanded) {
                    AccessibilitySettingsSection(
                        settings = settings,
                        viewModel = viewModel,
                    )
                }
            }

            // ── Maintenance Tools ────────────────────────────────────────────
            item {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(Dimens.PaddingScreen)
                            .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm),
                ) {
                    Spacer(modifier = Modifier.height(Dimens.SpacingMd))

                    OutlinedButton(
                        onClick = {
                            viewModel.clearCache()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Temporary cache cleared")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    ) {
                        Text("Clear Cache Files")
                    }

                    TextButton(
                        onClick = {
                            viewModel.resetToDefaults()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Settings reset to defaults")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                    ) {
                        Text("Reset All to Defaults")
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedCategoryVisibility(
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        content()
    }
}
