package com.droidinspector.feature.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.InfoRow
import com.droidinspector.core.components.LoadingPane
import com.droidinspector.core.components.MetricGauge
import com.droidinspector.core.components.ScreenHeader
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.BackgroundDark
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.TextMutedDark
import com.droidinspector.core.utils.formatBytes
import com.droidinspector.core.utils.percentOf
import com.droidinspector.domain.model.StorageInfo

/**
 * Storage Analyzer: real totals per volume, read with StatFs.
 * Signature : @Bienvenu
 */
@Composable
fun StorageScreen(
    viewModel: StorageViewModel,
    modifier: Modifier = Modifier
) {
    val info = viewModel.uiState.collectAsState().value

    Box(modifier = modifier.fillMaxSize().background(BackgroundDark)) {
        if (info == null) {
            LoadingPane()
        } else {
            StorageContent(info = info, onRefresh = { viewModel.refresh() })
        }
    }
}

@Composable
private fun StorageContent(info: StorageInfo, onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScreenHeader(
                title = "Storage Analyzer",
                subtitle = "Espace réel par volume",
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRefresh) {
                Text(text = "Actualiser", color = EmeraldPrimary, fontSize = 13.sp)
            }
        }

        GlassCard(contentPadding = 24.dp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MetricGauge(
                    value = percentOf(info.usedBytes, info.totalBytes),
                    size = 140.dp,
                    strokeWidth = 12.dp,
                    label = "Occupé"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = formatBytes(info.usedBytes) + " utilisés sur " + formatBytes(info.totalBytes) +
                        " (" + formatBytes(info.availableBytes) + " libres)",
                    fontSize = 13.sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        SectionHeader("Volumes", "Lus avec StatFs, sans aucune permission")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                info.volumes.forEach { volume ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        InfoRow(
                            volume.label,
                            formatBytes(volume.usedBytes) + " / " + formatBytes(volume.totalBytes)
                        )
                        Text(
                            text = volume.path + " · " + formatBytes(volume.availableBytes) + " libres",
                            color = TextMutedDark,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Text(
            text = "La répartition par catégorie (applications, photos…) demande une autorisation " +
                "d'accès aux statistiques d'usage. Elle sera proposée dans une phase ultérieure, " +
                "uniquement avec ton accord.",
            color = TextMutedDark,
            fontSize = 11.sp
        )
    }
}
