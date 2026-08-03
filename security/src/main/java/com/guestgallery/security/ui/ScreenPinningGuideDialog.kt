package com.guestgallery.security.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.guestgallery.core.theme.Dimens
import com.guestgallery.core.theme.motionDuration

/**
 * Data class for a single guide step.
 */
private data class GuideStep(
    val number: Int,
    val icon: ImageVector,
    val title: String,
    val description: String,
)

/** Steps for the screen-pinning setup guide. */
private val guideSteps =
    listOf(
        GuideStep(
            number = 1,
            icon = Icons.Outlined.Security,
            title = "Open Screen Pinning Settings",
            description = "Go to Settings → Security → Screen pinning (or App pinning).",
        ),
        GuideStep(
            number = 2,
            icon = Icons.Outlined.Lock,
            title = "Turn On Screen Pinning",
            description = "Toggle the Screen pinning switch to ON.",
        ),
        GuideStep(
            number = 3,
            icon = Icons.Outlined.PhoneAndroid,
            title = "Pin the App",
            description = "Return here and tap 'Pin App' to lock the gallery.",
        ),
    )

/** Delay before the stable dialog content starts fading in. */
private const val DIALOG_CONTENT_DELAY_MS = 80L

/** Stagger between the stable step fade-ins. */
private const val STEP_STAGGER_DELAY_MS = 35

/** Initial vertical offset for each step's fade-in. */
private const val STEP_INITIAL_OFFSET_PX = 14f

/**
 * A Material 3 [AlertDialog] that walks the user through enabling screen
 * pinning on their device. Steps appear with a staggered fade-in animation.
 *
 * @param onDismiss   Called when the user dismisses the dialog.
 * @param onPinApp    Called when the user taps the "Pin App" button.
 */
@Composable
fun ScreenPinningGuideDialog(
    onDismiss: () -> Unit,
    onPinApp: () -> Unit,
) {
    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(DIALOG_CONTENT_DELAY_MS)
        animationStarted = true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.IconLg),
            )
        },
        title = {
            Text(
                text = "Enable Screen Pinning",
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)) {
                Text(
                    text =
                        "Screen pinning locks the device to this app so " +
                            "guests can only view the images you've shared.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(Dimens.SpacingSm))

                guideSteps.forEachIndexed { index, step ->
                    val alpha by animateFloatAsState(
                        targetValue = if (animationStarted) 1f else 0f,
                        animationSpec =
                            tween(
                                durationMillis = motionDuration(260),
                                delayMillis = index * STEP_STAGGER_DELAY_MS,
                            ),
                        label = "pinning_step_alpha_$index",
                    )

                    StepRow(
                        step = step,
                        modifier =
                            Modifier.graphicsLayer {
                                this.alpha = alpha
                                translationY = (1f - alpha) * STEP_INITIAL_OFFSET_PX
                            },
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onPinApp) {
                Text("Pin App")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Later")
            }
        },
    )
}

/** A single numbered step with an icon, title, and description. */
@Composable
private fun StepRow(
    step: GuideStep,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.SpacingXs),
        verticalAlignment = Alignment.Top,
    ) {
        // Numbered circle indicator
        Box(
            modifier =
                Modifier
                    .size(Dimens.IconLg)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = step.number.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Spacer(modifier = Modifier.width(Dimens.SpacingMd))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
