package com.example.ui.calculator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Architecture
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.calculator.CalculatorUiState
import com.example.calculator.ScientificOperation
import com.example.ui.theme.LocalCalculatorPalette
import com.example.ui.theme.SpaceGroteskFamily
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(
    uiState: CalculatorUiState,
    onTokenClick: (String) -> Unit,
    onScientificClick: (ScientificOperation) -> Unit,
    onClearAll: () -> Unit,
    onDeleteLast: () -> Unit,
    onCalculate: () -> Unit,
    onToggleTheme: () -> Unit,
    onSelectHistoryResult: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalCalculatorPalette.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val copiedMessage = stringResource(R.string.copied_to_clipboard)

    var showHistorySheet by rememberSaveable { mutableStateOf(false) }
    var showInfoSheet by rememberSaveable { mutableStateOf(false) }

    val handleCopy = {
        clipboardManager.setText(AnnotatedString(uiState.displayText))
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(copiedMessage)
        }
        Unit
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = palette.canvas,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.canvas)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                palette.brandAccentStart.copy(alpha = if (palette.isDark) 0.14f else 0.08f),
                                Color.Transparent,
                            ),
                            center = Offset(size.width * 0.2f, 0f),
                            radius = size.maxDimension * 0.65f,
                        ),
                        center = Offset(size.width * 0.2f, 0f),
                        radius = size.maxDimension * 0.65f,
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                palette.brandAccentEnd.copy(alpha = if (palette.isDark) 0.10f else 0.06f),
                                Color.Transparent,
                            ),
                            center = Offset(size.width * 0.9f, size.height * 0.95f),
                            radius = size.maxDimension * 0.55f,
                        ),
                        center = Offset(size.width * 0.9f, size.height * 0.95f),
                        radius = size.maxDimension * 0.55f,
                    )
                }
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                FarhanTopBar(
                    isDarkTheme = uiState.isDarkTheme,
                    historyCount = uiState.history.size,
                    onHistoryClick = { showHistorySheet = true },
                    onThemeToggleClick = onToggleTheme,
                    onInfoClick = { showInfoSheet = true },
                )

                Spacer(modifier = Modifier.height(8.dp))

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    val isWideScreen = maxWidth >= 600.dp
                    val hasTallViewport = maxHeight >= 600.dp

                    if (isWideScreen) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(0.95f)
                                    .fillMaxHeight()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                CalculatorDisplay(
                                    displayText = uiState.displayText,
                                    livePreview = uiState.livePreview,
                                    statusKind = uiState.statusKind,
                                    onCopyClick = handleCopy,
                                )

                                FeatureHighlightCard(
                                    icon = Icons.Rounded.Calculate,
                                    title = stringResource(R.string.feature_basic_title),
                                    description = stringResource(R.string.feature_basic_desc),
                                )
                                FeatureHighlightCard(
                                    icon = Icons.Rounded.Architecture,
                                    title = stringResource(R.string.feature_trig_title),
                                    description = stringResource(R.string.feature_trig_desc),
                                )
                                FeatureHighlightCard(
                                    icon = Icons.Rounded.Functions,
                                    title = stringResource(R.string.feature_adv_title),
                                    description = stringResource(R.string.feature_adv_desc),
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1.05f)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(24.dp),
                                color = palette.calculatorSurface.copy(alpha = 0.72f),
                                border = BorderStroke(1.dp, palette.hairline),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    ScientificKeyPanel(
                                        onScientificClick = onScientificClick,
                                        onTokenClick = onTokenClick,
                                        compactMode = true,
                                    )
                                    StandardKeyGrid(
                                        onTokenClick = onTokenClick,
                                        onClearAll = onClearAll,
                                        onDeleteLast = onDeleteLast,
                                        onCalculate = onCalculate,
                                        fillHeight = true,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }
                    } else if (hasTallViewport) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = 520.dp)
                                .fillMaxSize()
                                .padding(bottom = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            CalculatorDisplay(
                                displayText = uiState.displayText,
                                livePreview = uiState.livePreview,
                                statusKind = uiState.statusKind,
                                onCopyClick = handleCopy,
                            )

                            ScientificKeyPanel(
                                onScientificClick = onScientificClick,
                                onTokenClick = onTokenClick,
                                compactMode = true,
                            )

                            StandardKeyGrid(
                                onTokenClick = onTokenClick,
                                onClearAll = onClearAll,
                                onDeleteLast = onDeleteLast,
                                onCalculate = onCalculate,
                                fillHeight = true,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .widthIn(max = 520.dp)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            CalculatorDisplay(
                                displayText = uiState.displayText,
                                livePreview = uiState.livePreview,
                                statusKind = uiState.statusKind,
                                onCopyClick = handleCopy,
                            )

                            ScientificKeyPanel(
                                onScientificClick = onScientificClick,
                                onTokenClick = onTokenClick,
                                compactMode = true,
                            )

                            StandardKeyGrid(
                                onTokenClick = onTokenClick,
                                onClearAll = onClearAll,
                                onDeleteLast = onDeleteLast,
                                onCalculate = onCalculate,
                                fillHeight = false,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showHistorySheet) {
        HistoryBottomSheet(
            history = uiState.history,
            onSelectResult = onSelectHistoryResult,
            onClearHistory = onClearHistory,
            onDismiss = { showHistorySheet = false },
        )
    }

    if (showInfoSheet) {
        InfoAndFeaturesBottomSheet(
            onDismiss = { showInfoSheet = false },
        )
    }
}

@Composable
private fun FarhanTopBar(
    isDarkTheme: Boolean,
    historyCount: Int,
    onHistoryClick: () -> Unit,
    onThemeToggleClick: () -> Unit,
    onInfoClick: () -> Unit,
) {
    val palette = LocalCalculatorPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = palette.brandAccentStart)) {
                        append(stringResource(R.string.brand_name))
                    }
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                        append(".")
                    }
                },
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 23.sp,
                letterSpacing = 0.5.sp,
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = palette.keyScientific,
                border = BorderStroke(1.dp, palette.hairline),
            ) {
                Text(
                    text = stringResource(R.string.badge_scientific),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.brandAccentEnd,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            IconButton(
                onClick = onHistoryClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(48.dp)
                    .testTag("btn_history"),
            ) {
                BadgedBox(
                    badge = {
                        if (historyCount > 0) {
                            Badge(
                                containerColor = palette.brandAccentStart,
                                contentColor = Color.White,
                            ) {
                                Text(text = if (historyCount > 9) "9+" else historyCount.toString())
                            }
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = stringResource(R.string.cd_history),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            IconButton(
                onClick = onThemeToggleClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(48.dp)
                    .testTag("btn_theme"),
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                    contentDescription = stringResource(R.string.cd_toggle_theme),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(
                onClick = onInfoClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(48.dp)
                    .testTag("btn_info"),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = stringResource(R.string.cd_info),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
