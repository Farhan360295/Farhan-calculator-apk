package com.example.ui.calculator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Architecture
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.calculator.HistoryEntry
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.LocalCalculatorPalette
import com.example.ui.theme.SpaceGroteskFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
    history: List<HistoryEntry>,
    onSelectResult: (String) -> Unit,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = LocalCalculatorPalette.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.calculatorSurface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.history_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                if (history.isNotEmpty()) {
                    TextButton(
                        onClick = onClearHistory,
                        modifier = Modifier.testTag("btn_clear_history"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = stringResource(R.string.history_clear))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (history.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = palette.displayWell,
                    border = BorderStroke(1.dp, palette.hairline),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = null,
                            tint = palette.brandAccentStart,
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.history_empty_title),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.history_empty_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.statusMuted,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(history, key = { it.id }) { entry ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectResult(entry.result)
                                    onDismiss()
                                }
                                .testTag("history_item_${entry.id}"),
                            shape = RoundedCornerShape(14.dp),
                            color = palette.displayWell,
                            border = BorderStroke(1.dp, palette.hairline),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalAlignment = Alignment.End,
                            ) {
                                Text(
                                    text = entry.expression,
                                    fontFamily = JetBrainsMonoFamily,
                                    fontSize = 13.sp,
                                    color = palette.statusMuted,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "= ${entry.result}",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp,
                                    color = palette.brandAccentStart,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoAndFeaturesBottomSheet(
    onDismiss: () -> Unit,
) {
    val palette = LocalCalculatorPalette.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.calculatorSurface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = palette.keyScientific,
                border = BorderStroke(1.dp, palette.hairline),
            ) {
                Text(
                    text = stringResource(R.string.badge_first_project),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.brandAccentStart,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.brand_full_name),
                style = TextStyle(
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(palette.brandAccentStart, palette.brandAccentEnd),
                    ),
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.hero_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = stringResource(R.string.section_features),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = palette.hairline)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.footer_credit),
                style = MaterialTheme.typography.bodyMedium,
                color = palette.statusMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.footer_copyright),
                style = MaterialTheme.typography.labelSmall,
                color = palette.statusMuted.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun FeatureHighlightCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalCalculatorPalette.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = palette.displayWell,
        border = BorderStroke(1.dp, palette.hairline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = palette.keyScientific,
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = palette.brandAccentStart,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.brandAccentStart,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
