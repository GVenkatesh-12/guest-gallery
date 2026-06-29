package com.guestgallery.security.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.guestgallery.core.theme.Dimens
import kotlinx.coroutines.delay

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

/** Staggered delay (ms) between step reveal animations. */
private const val STEP_STAGGER_DELAY = 250L

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
    // Tracks how many steps have been revealed so far.
    var revealedSteps by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        for (i in 1..guideSteps.size) {
            delay(STEP_STAGGER_DELAY)
            revealedSteps = i
        }
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
                    AnimatedVisibility(
                        visible = index < revealedSteps,
                        enter =
                            expandVertically(
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                    ) {
                        StepRow(step = step)
                    }
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
