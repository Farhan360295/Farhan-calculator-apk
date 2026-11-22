package com.example.ui.calculator

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.calculator.ScientificOperation
import com.example.ui.theme.LocalCalculatorPalette
import com.example.ui.theme.SpaceGroteskFamily

enum class KeyTier {
    STANDARD,
    OPERATOR,
    SCIENTIFIC,
    CLEAR,
    EQUALS,
}

@Composable
fun CalculatorKeyButton(
    label: String,
    testTag: String,
    tier: KeyTier,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accessibilityLabel: String? = null,
    fontSize: TextUnit = 19.sp,
    cornerRadius: Dp = 14.dp,
    minHeight: Dp = 48.dp,
) {
    val palette = LocalCalculatorPalette.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = "keyScale",
    )

    val shape = RoundedCornerShape(cornerRadius)
    val contentColor = when (tier) {
        KeyTier.STANDARD -> palette.keyStandardInk
        KeyTier.OPERATOR -> palette.keyOperatorInk
        KeyTier.SCIENTIFIC -> palette.keyScientificInk
        KeyTier.CLEAR -> palette.keyClearInk
        KeyTier.EQUALS -> Color.White
    }

    val backgroundModifier = if (tier == KeyTier.EQUALS) {
        Modifier.background(
            brush = Brush.linearGradient(
                colors = listOf(palette.equalsGradientStart, palette.equalsGradientEnd),
            ),
            shape = shape,
        )
    } else {
        val bgColor = when (tier) {
            KeyTier.STANDARD -> palette.keyStandard
            KeyTier.OPERATOR -> palette.keyOperator
            KeyTier.SCIENTIFIC -> palette.keyScientific
            KeyTier.CLEAR -> palette.keyClear
            KeyTier.EQUALS -> Color.Transparent
        }
        Modifier.background(color = bgColor, shape = shape)
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .minimumInteractiveComponentSize()
            .defaultMinSize(minWidth = 48.dp, minHeight = minHeight)
            .clip(shape)
            .then(backgroundModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = contentColor),
                role = Role.Button,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
            )
            .semantics {
                if (accessibilityLabel != null) {
                    contentDescription = accessibilityLabel
                }
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = accessibilityLabel,
                tint = contentColor,
                modifier = Modifier.size(21.dp),
            )
        } else {
            Text(
                text = label,
                fontFamily = SpaceGroteskFamily,
                fontWeight = if (tier == KeyTier.EQUALS || tier == KeyTier.OPERATOR) {
                    FontWeight.Bold
                } else {
                    FontWeight.SemiBold
                },
                fontSize = fontSize,
                color = contentColor,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun ScientificKeyPanel(
    onScientificClick: (ScientificOperation) -> Unit,
    onTokenClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    compactMode: Boolean = false,
) {
    val palette = LocalCalculatorPalette.current
    val keySpacing = 8.dp

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = palette.calculatorSurface,
        border = BorderStroke(1.dp, palette.hairline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Text(
                text = stringResource(R.string.section_scientific),
                style = MaterialTheme.typography.labelSmall,
                color = palette.statusMuted,
            )
            Spacer(modifier = Modifier.height(8.dp))

            val rowHeight = if (compactMode) 48.dp else 50.dp
            val rowMod = Modifier
                .fillMaxWidth()
                .height(rowHeight)

            Column(verticalArrangement = Arrangement.spacedBy(keySpacing)) {
                Row(
                    modifier = rowMod,
                    horizontalArrangement = Arrangement.spacedBy(keySpacing),
                ) {
                    CalculatorKeyButton(
                        label = "sin",
                        testTag = "btn_sin",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onScientificClick(ScientificOperation.SIN) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 14.sp,
                        cornerRadius = 11.dp,
                    )
                    CalculatorKeyButton(
                        label = "cos",
                        testTag = "btn_cos",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onScientificClick(ScientificOperation.COS) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 14.sp,
                        cornerRadius = 11.dp,
                    )
                    CalculatorKeyButton(
                        label = "tan",
                        testTag = "btn_tan",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onScientificClick(ScientificOperation.TAN) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 14.sp,
                        cornerRadius = 11.dp,
                    )
                    CalculatorKeyButton(
                        label = "√",
                        testTag = "btn_sqrt",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onScientificClick(ScientificOperation.SQRT) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 16.sp,
                        cornerRadius = 11.dp,
                        accessibilityLabel = "Square root",
                    )
                }

                Row(
                    modifier = rowMod,
                    horizontalArrangement = Arrangement.spacedBy(keySpacing),
                ) {
                    CalculatorKeyButton(
                        label = "ln",
                        testTag = "btn_ln",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onScientificClick(ScientificOperation.LN) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 14.sp,
                        cornerRadius = 11.dp,
                    )
                    CalculatorKeyButton(
                        label = "log",
                        testTag = "btn_log",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onScientificClick(ScientificOperation.LOG) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 14.sp,
                        cornerRadius = 11.dp,
                    )
                    CalculatorKeyButton(
                        label = "n!",
                        testTag = "btn_factorial",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onScientificClick(ScientificOperation.FACTORIAL) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 14.sp,
                        cornerRadius = 11.dp,
                        accessibilityLabel = "Factorial",
                    )
                    CalculatorKeyButton(
                        label = "xʸ",
                        testTag = "btn_pow",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onTokenClick("^") },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 14.sp,
                        cornerRadius = 11.dp,
                        accessibilityLabel = "Power",
                    )
                }

                Row(
                    modifier = rowMod,
                    horizontalArrangement = Arrangement.spacedBy(keySpacing),
                ) {
                    CalculatorKeyButton(
                        label = "x²",
                        testTag = "btn_square",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onScientificClick(ScientificOperation.SQUARE) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 14.sp,
                        cornerRadius = 11.dp,
                        accessibilityLabel = "Square",
                    )
                    CalculatorKeyButton(
                        label = "±",
                        testTag = "btn_sign",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onScientificClick(ScientificOperation.SIGN) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 16.sp,
                        cornerRadius = 11.dp,
                        accessibilityLabel = "Toggle sign",
                    )
                    CalculatorKeyButton(
                        label = "π",
                        testTag = "btn_pi",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onTokenClick("π") },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 16.sp,
                        cornerRadius = 11.dp,
                        accessibilityLabel = "Pi",
                    )
                    CalculatorKeyButton(
                        label = "e",
                        testTag = "btn_e",
                        tier = KeyTier.SCIENTIFIC,
                        onClick = { onTokenClick("e") },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        fontSize = 15.sp,
                        cornerRadius = 11.dp,
                        accessibilityLabel = "Euler number e",
                    )
                }
            }
        }
    }
}

@Composable
fun StandardKeyGrid(
    onTokenClick: (String) -> Unit,
    onClearAll: () -> Unit,
    onDeleteLast: () -> Unit,
    onCalculate: () -> Unit,
    modifier: Modifier = Modifier,
    fillHeight: Boolean = true,
) {
    val keySpacing = 10.dp
    val clearLabel = stringResource(R.string.cd_clear_all)
    val delLabel = stringResource(R.string.cd_backspace)
    val eqLabel = stringResource(R.string.cd_equals)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(keySpacing),
    ) {
        val rowModifier = if (fillHeight) {
            Modifier.fillMaxWidth().weight(1f)
        } else {
            Modifier.fillMaxWidth().height(58.dp)
        }

        // Row 1: AC, DEL, (, )
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(keySpacing)) {
            CalculatorKeyButton(
                label = "AC",
                testTag = "btn_ac",
                tier = KeyTier.CLEAR,
                onClick = onClearAll,
                modifier = Modifier.weight(1f).fillMaxSize(),
                accessibilityLabel = clearLabel,
                fontSize = 16.sp,
            )
            CalculatorKeyButton(
                label = "DEL",
                testTag = "btn_del",
                tier = KeyTier.STANDARD,
                onClick = onDeleteLast,
                icon = Icons.AutoMirrored.Rounded.Backspace,
                accessibilityLabel = delLabel,
                modifier = Modifier.weight(1f).fillMaxSize(),
            )
            CalculatorKeyButton(
                label = "(",
                testTag = "btn_lparen",
                tier = KeyTier.OPERATOR,
                onClick = { onTokenClick("(") },
                modifier = Modifier.weight(1f).fillMaxSize(),
            )
            CalculatorKeyButton(
                label = ")",
                testTag = "btn_rparen",
                tier = KeyTier.OPERATOR,
                onClick = { onTokenClick(")") },
                modifier = Modifier.weight(1f).fillMaxSize(),
            )
        }

        // Row 2: 7, 8, 9, ÷
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(keySpacing)) {
            CalculatorKeyButton("7", "btn_7", KeyTier.STANDARD, { onTokenClick("7") }, Modifier.weight(1f).fillMaxSize())
            CalculatorKeyButton("8", "btn_8", KeyTier.STANDARD, { onTokenClick("8") }, Modifier.weight(1f).fillMaxSize())
            CalculatorKeyButton("9", "btn_9", KeyTier.STANDARD, { onTokenClick("9") }, Modifier.weight(1f).fillMaxSize())
            CalculatorKeyButton("÷", "btn_div", KeyTier.OPERATOR, { onTokenClick("÷") }, Modifier.weight(1f).fillMaxSize(), fontSize = 22.sp, accessibilityLabel = "Divide")
        }

        // Row 3: 4, 5, 6, ×
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(keySpacing)) {
            CalculatorKeyButton("4", "btn_4", KeyTier.STANDARD, { onTokenClick("4") }, Modifier.weight(1f).fillMaxSize())
            CalculatorKeyButton("5", "btn_5", KeyTier.STANDARD, { onTokenClick("5") }, Modifier.weight(1f).fillMaxSize())
            CalculatorKeyButton("6", "btn_6", KeyTier.STANDARD, { onTokenClick("6") }, Modifier.weight(1f).fillMaxSize())
            CalculatorKeyButton("×", "btn_mul", KeyTier.OPERATOR, { onTokenClick("×") }, Modifier.weight(1f).fillMaxSize(), fontSize = 22.sp, accessibilityLabel = "Multiply")
        }

        // Row 4: 1, 2, 3, −
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(keySpacing)) {
            CalculatorKeyButton("1", "btn_1", KeyTier.STANDARD, { onTokenClick("1") }, Modifier.weight(1f).fillMaxSize())
            CalculatorKeyButton("2", "btn_2", KeyTier.STANDARD, { onTokenClick("2") }, Modifier.weight(1f).fillMaxSize())
            CalculatorKeyButton("3", "btn_3", KeyTier.STANDARD, { onTokenClick("3") }, Modifier.weight(1f).fillMaxSize())
            CalculatorKeyButton("−", "btn_sub", KeyTier.OPERATOR, { onTokenClick("−") }, Modifier.weight(1f).fillMaxSize(), fontSize = 22.sp, accessibilityLabel = "Subtract")
        }

        // Row 5: 0, ., +, =
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(keySpacing)) {
            CalculatorKeyButton("0", "btn_0", KeyTier.STANDARD, { onTokenClick("0") }, Modifier.weight(1f).fillMaxSize())
            CalculatorKeyButton(".", "btn_dot", KeyTier.STANDARD, { onTokenClick(".") }, Modifier.weight(1f).fillMaxSize(), accessibilityLabel = "Decimal point")
            CalculatorKeyButton("+", "btn_add", KeyTier.OPERATOR, { onTokenClick("+") }, Modifier.weight(1f).fillMaxSize(), fontSize = 22.sp, accessibilityLabel = "Add")
            CalculatorKeyButton("=", "btn_eq", KeyTier.EQUALS, onCalculate, Modifier.weight(1f).fillMaxSize(), fontSize = 24.sp, accessibilityLabel = eqLabel)
        }
    }
}
