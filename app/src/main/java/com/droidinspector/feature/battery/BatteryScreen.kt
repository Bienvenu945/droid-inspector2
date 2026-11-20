package com.droidinspector.feature.battery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
import com.droidinspector.domain.model.BatteryInfo
import java.util.Locale

/**
 * Battery Monitor: live readings from BatteryManager.
 * Values the phone does not expose are shown as such, never invented.
 * Signature : @Bienvenu
 */
@Composable
fun BatteryScreen(
    viewModel: BatteryViewModel,
    modifier: Modifier = Modifier
) {
    val info = viewModel.uiState.collectAsState().value

    Box(modifier = modifier.fillMaxSize().background(BackgroundDark)) {
        if (info == null) {
            LoadingPane()
        } else {
            BatteryContent(info = info)
        }
    }
}

@Composable
private fun BatteryContent(info: BatteryInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenHeader(
            title = "Battery Monitor",
            subtitle = "Mesures en direct issues de BatteryManager"
        )

        GlassCard(contentPadding = 24.dp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MetricGauge(
                    value = info.percentage.toFloat(),
                    size = 140.dp,
                    strokeWidth = 12.dp,
                    label = "Niveau"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (info.isCharging) "En charge · " + info.chargingSource else "Sur batterie",
                    fontSize = 13.sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        SectionHeader("État de l'accumulateur", "Rafraîchi automatiquement")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoRow("Santé", info.health)
                InfoRow("Technologie", info.technology)
                InfoRow("Température", String.format(Locale.US, "%.1f °C", info.temperatureCelsius))
                InfoRow("Tension", String.format(Locale.US, "%.2f V", info.voltageMilliVolts / 1000f))
                InfoRow("Source d'alimentation", info.chargingSource)
            }
        }

        SectionHeader("Courant et capacité", "Selon ce que le fabricant expose")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val current = info.currentNowMicroAmps
                val counter = info.chargeCounterMicroAmpHours
                val estimated = estimateCapacityMah(info)

                InfoRow(
                    "Courant instantané",
                    if (current != null) (current / 1000L).toString() + " mA" else "Non exposé"
                )
                InfoRow(
                    "Charge restante",
                    if (counter != null) (counter / 1000L).toString() + " mAh" else "Non exposée"
                )
                InfoRow(
                    "Capacité estimée",
                    if (estimated != null) "≈ " + estimated.toString() + " mAh" else "Non calculable"
                )
                InfoRow(
                    "Cycles de charge",
                    if (info.cycleCount != null) info.cycleCount.toString() else "Non exposés"
                )
            }
        }

        Text(
            text = "La capacité estimée est un calcul approximatif (charge restante ÷ niveau). " +
                "Android n'expose pas la capacité d'origine de la batterie.",
            color = TextMutedDark,
            fontSize = 11.sp
        )
    }
}

/** Rough full capacity in mAh, only when the charge counter is available and the level is not too low. */
private fun estimateCapacityMah(info: BatteryInfo): Long? {
    val counter = info.chargeCounterMicroAmpHours ?: return null
    if (info.percentage < 10) return null
    return (counter / 1000L) * 100L / info.percentage
}
