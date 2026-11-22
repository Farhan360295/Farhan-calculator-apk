package com.example.ui.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.calculator.CalculatorErrorType
import com.example.calculator.StatusKind
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalCalculatorPalette

@Composable
fun CalculatorDisplay(
    displayText: String,
    livePreview: String?,
    statusKind: StatusKind,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalCalculatorPalette.current
    val scrollState = rememberScrollState()

    LaunchedEffect(displayText) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val statusText = when (statusKind) {
        StatusKind.Ready -> stringResource(R.string.status_ready)
        StatusKind.Calculating -> stringResource(R.string.status_calculating)
        StatusKind.Success -> stringResource(R.string.status_success)
        is StatusKind.ScientificSuccess ->
            stringResource(R.string.status_op_calculated, statusKind.operationLabel)
        is StatusKind.Error -> when (statusKind.errorType) {
            CalculatorErrorType.INVALID_EXPRESSION -> stringResource(R.string.err_invalid_expression)
            CalculatorErrorType.DIVISION_BY_ZERO -> stringResource(R.string.err_division_zero)
            CalculatorErrorType.INVALID_RESULT -> stringResource(R.string.err_invalid_result)
            CalculatorErrorType.ENTER_NUMBER_FIRST -> stringResource(R.string.err_enter_number)
            CalculatorErrorType.TANGENT_UNDEFINED -> stringResource(R.string.err_tan_undefined)
            CalculatorErrorType.INVALID_SQRT -> stringResource(R.string.err_invalid_sqrt)
            CalculatorErrorType.POSITIVE_NUMBER_REQUIRED -> stringResource(R.string.err_positive_number)
            CalculatorErrorType.FACTORIAL_RANGE -> stringResource(R.string.err_factorial_range)
        }
    }

    val statusColor by animateColorAsState(
        targetValue = when (statusKind) {
            is StatusKind.Error -> palette.statusError
            StatusKind.Success, is StatusKind.ScientificSuccess -> palette.statusSuccess
            else -> palette.statusMuted
        },
        label = "statusColor",
    )

    val mainFontSize = when {
        displayText.length <= 10 -> 38.sp
        displayText.length <= 15 -> 30.sp
        displayText.length <= 22 -> 24.sp
        else -> 20.sp
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = palette.displayWell,
        border = BorderStroke(1.dp, palette.hairline),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                palette.brandAccentStart.copy(alpha = if (palette.isDark) 0.14f else 0.08f),
                                Color.Transparent,
                            ),
                            center = Offset(size.width * 0.92f, size.height * 0.12f),
                            radius = size.width * 0.65f,
                        ),
                        center = Offset(size.width * 0.92f, size.height * 0.12f),
                        radius = size.width * 0.65f,
                    )
                }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = palette.keyScientific,
                        ) {
                            Text(
                                text = stringResource(R.string.badge_deg),
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.brandAccentStart,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }

                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag("display_status"),
                        )
                    }

                    IconButton(
                        onClick = onCopyClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(48.dp)
                            .testTag("btn_copy"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = stringResource(R.string.cd_copy_result),
                            tint = palette.statusMuted,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Text(
                        text = displayText,
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = mainFontSize,
                        lineHeight = (mainFontSize.value + 6f).sp,
                        color = if (statusKind is StatusKind.Error && displayText == "Error") {
                            palette.statusError
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        modifier = Modifier.testTag("display_result"),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !livePreview.isNullOrEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Text(
                            text = "= ${livePreview.orEmpty()}",
                            fontFamily = JetBrainsMonoFamily,
                            fontSize = 15.sp,
                            color = palette.brandAccentStart.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag("display_preview"),
                        )
                    }
                }
            }
        }
    }
}
