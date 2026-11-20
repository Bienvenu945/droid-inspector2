package com.droidinspector.feature.hardware

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.InfoRow
import com.droidinspector.core.components.LoadingPane
import com.droidinspector.core.components.ScreenHeader
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.BackgroundDark
import com.droidinspector.core.theme.TextMutedDark
import com.droidinspector.core.utils.formatBytes
import com.droidinspector.domain.model.CpuCore
import com.droidinspector.domain.model.HardwareInfo
import java.util.Locale

/**
 * Hardware Explorer: every value is read from the device.
 * When Android does not expose a value, the screen says so instead of guessing.
 * Signature : @Bienvenu
 */
@Composable
fun HardwareScreen(
    viewModel: HardwareViewModel,
    modifier: Modifier = Modifier
) {
    val info = viewModel.uiState.collectAsState().value

    Box(modifier = modifier.fillMaxSize().background(BackgroundDark)) {
        if (info == null) {
            LoadingPane()
        } else {
            HardwareContent(info = info)
        }
    }
}

@Composable
private fun HardwareContent(info: HardwareInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenHeader(
            title = "Hardware Explorer",
            subtitle = "Valeurs réelles lues sur cet appareil"
        )

        SectionHeader("Processeur", "Plateforme, architecture et jeux d'instructions")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow("SoC / Plateforme", info.socName)
                InfoRow("Architecture", info.cpuArchitecture)
                InfoRow("Cœurs", info.coreCount.toString())
                InfoRow("Jeux d'instructions (ABI)", info.abiList.joinToString(", "))
            }
        }

        SectionHeader("Fréquences par cœur", "Minimum et maximum déclarés par le noyau")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val readable = info.cores.filter { it.maxFrequencyMhz != null }
                if (readable.isEmpty()) {
                    Text(
                        text = "Fréquences non exposées par ce téléphone",
                        color = TextMutedDark,
                        fontSize = 13.sp
                    )
                } else {
                    info.cores.forEach { core ->
                        InfoRow("Cœur ${core.index}", formatFrequencyRange(core))
                    }
                }
            }
        }

        SectionHeader("Mémoire", "RAM et mémoire d'échange")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow("RAM totale", formatBytes(info.ramTotalBytes))
                InfoRow("RAM disponible", formatBytes(info.ramAvailableBytes))
                InfoRow("Swap / zram", formatSwap(info.swapTotalBytes, info.swapFreeBytes))
            }
        }

        SectionHeader("Affichage", "Dalle réelle de l'appareil")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow("Résolution", info.screenResolution + " px")
                InfoRow("Densité", info.screenDensityDpi.toString() + " dpi")
                InfoRow("Fréquence de rafraîchissement", formatRefreshRate(info.screenRefreshRateHz))
            }
        }

        SectionHeader("Graphismes", "API de rendu")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow("OpenGL ES", info.openGlEsVersion)
            }
        }

        Text(
            text = "Le modèle exact du GPU n'est pas affiché : Android ne l'expose " +
                "qu'à travers un contexte OpenGL, ce qui sera ajouté plus tard.",
            color = TextMutedDark,
            fontSize = 11.sp
        )
    }
}

private fun formatFrequencyRange(core: CpuCore): String {
    val min = core.minFrequencyMhz
    val max = core.maxFrequencyMhz
    return when {
        min != null && max != null -> "$min – $max MHz"
        max != null -> "max $max MHz"
        else -> "Non disponible"
    }
}

private fun formatSwap(total: Long?, free: Long?): String {
    if (total == null || free == null || total <= 0L) return "Aucun ou non exposé"
    return formatBytes(total - free) + " / " + formatBytes(total)
}

private fun formatRefreshRate(hertz: Float): String {
    return if (hertz > 0f) String.format(Locale.US, "%.0f Hz", hertz) else "Non disponible"
}
