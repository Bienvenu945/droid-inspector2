package com.droidinspector.feature.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.SettingsSuggest
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droidinspector.core.navigation.Screen
import com.droidinspector.core.theme.BackgroundDark
import com.droidinspector.core.theme.EmeraldDark
import com.droidinspector.core.theme.EmeraldLight
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.SurfaceCard
import com.droidinspector.core.theme.SurfaceDark
import com.droidinspector.core.theme.SurfaceGlassBorder
import com.droidinspector.core.theme.TextMutedDark
import com.droidinspector.core.theme.TextPrimaryDark
import com.droidinspector.core.theme.TextSecondaryDark

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        val wide = maxWidth >= 720.dp
        val horizontalPadding = when {
            maxWidth >= 1000.dp -> 36.dp
            maxWidth >= 600.dp -> 28.dp
            else -> 18.dp
        }

        Column(
            modifier = Modifier
                .widthIn(max = 1180.dp)
                .align(Alignment.TopCenter)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            DashboardTopBar()
            DeviceHeroCard(state)

            SectionHeading(
                title = "ÉTAT DU TERMINAL",
                subtitle = "Ressources système actualisées automatiquement"
            )

            if (wide) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Mémoire vive",
                        icon = Icons.Rounded.Memory,
                        value = state.ramUsagePercentage,
                        primary = "${state.ramUsagePercentage.toInt()}%",
                        detail = "${state.ramUsedFormatted} utilisés",
                        footer = "sur ${state.ramTotalFormatted}"
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Stockage interne",
                        icon = Icons.Rounded.Storage,
                        value = state.storageUsagePercentage,
                        primary = "${state.storageUsagePercentage.toInt()}%",
                        detail = "${state.storageUsedFormatted} utilisés",
                        footer = "sur ${state.storageTotalFormatted}"
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Batterie",
                        icon = Icons.Rounded.BatteryChargingFull,
                        value = state.batteryLevel.toFloat(),
                        primary = "${state.batteryLevel}%",
                        detail = state.batteryStatusText.ifBlank { "État indisponible" },
                        footer = if (state.batteryTemperature > 0f) "${state.batteryTemperature} °C" else "Température indisponible"
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Mémoire vive",
                        icon = Icons.Rounded.Memory,
                        value = state.ramUsagePercentage,
                        primary = "${state.ramUsagePercentage.toInt()}%",
                        detail = "${state.ramUsedFormatted} utilisés",
                        footer = "sur ${state.ramTotalFormatted}"
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Stockage",
                        icon = Icons.Rounded.Storage,
                        value = state.storageUsagePercentage,
                        primary = "${state.storageUsagePercentage.toInt()}%",
                        detail = "${state.storageUsedFormatted} utilisés",
                        footer = "sur ${state.storageTotalFormatted}"
                    )
                }
                BatteryWideCard(state)
            }

            SectionHeading(
                title = "OUTILS DE DIAGNOSTIC",
                subtitle = "Accès direct aux modules d'inspection"
            )

            val shortcuts = listOf(
                DiagnosticShortcut("Hardware Explorer", "Processeur, RAM et écran", Icons.Rounded.Memory, Screen.Hardware.route),
                DiagnosticShortcut("Laboratoire capteurs", "Mesures et capteurs détectés", Icons.Rounded.Sensors, Screen.Sensors.route),
                DiagnosticShortcut("Gestionnaire d'apps", "Applications installées", Icons.Rounded.Apps, Screen.Applications.route),
                DiagnosticShortcut("Analyse système", "Version Android et sécurité", Icons.Rounded.SettingsSuggest, Screen.System.route),
                DiagnosticShortcut("Analyse stockage", "Volumes et espace disponible", Icons.Rounded.Storage, Screen.Storage.route),
                DiagnosticShortcut("File Explorer", "Parcourir et gérer les fichiers", Icons.Rounded.FolderOpen, Screen.FileExplorer.route),
                DiagnosticShortcut("Rapports PDF", "Exporter un diagnostic local", Icons.Rounded.Description, Screen.Reports.route),
                DiagnosticShortcut("À propos & auteur", "Confidentialité et informations", Icons.Rounded.VerifiedUser, Screen.About.route)
            )

            if (wide) {
                shortcuts.chunked(3).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowItems.forEach { item ->
                            ShortcutCard(item, Modifier.weight(1f)) {
                                navController.navigate(item.route) { launchSingleTop = true }
                            }
                        }
                        repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            } else {
                shortcuts.chunked(2).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowItems.forEach { item ->
                            ShortcutCard(item, Modifier.weight(1f)) {
                                navController.navigate(item.route) { launchSingleTop = true }
                            }
                        }
                        if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            DashboardFooter()
        }
    }
}

@Composable
private fun DashboardTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "DROID INSPECTOR X",
                color = TextPrimaryDark,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.7.sp
            )
            Text(
                text = "Android System Intelligence & Diagnostics",
                color = TextSecondaryDark,
                fontSize = 11.sp
            )
        }
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(EmeraldPrimary.copy(alpha = 0.12f))
                .border(1.dp, EmeraldPrimary.copy(alpha = 0.28f), CircleShape)
                .padding(horizontal = 11.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(EmeraldLight))
            Spacer(Modifier.width(7.dp))
            Text("LIVE · 2 S", color = EmeraldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun DeviceHeroCard(state: DashboardUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.24f))
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(Color(0xFF123D2D), Color(0xFF0D2119), Color(0xFF0A1410))))
                .padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Smartphone, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(26.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("APPAREIL ANALYSÉ", color = EmeraldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
                        Text(
                            text = if (state.isLoaded) "${state.manufacturer} ${state.model}" else "Identification du terminal…",
                            color = TextPrimaryDark,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.16f))
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        Text("API ${state.apiLevel.takeIf { it > 0 } ?: "--"}", color = EmeraldLight, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.10f)))
                Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                    DeviceInfoItem("PLATEFORME", if (state.isLoaded) "Android ${state.androidVersion}" else "--", Modifier.weight(1f))
                    DeviceInfoItem("FABRICANT", if (state.isLoaded) state.manufacturer else "--", Modifier.weight(1f))
                    DeviceInfoItem("SIGNATURE", "@Bienvenu", Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DeviceInfoItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, color = TextMutedDark, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.7.sp)
        Text(value, color = TextPrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 2)
    }
}

@Composable
private fun SectionHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, color = EmeraldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.15.sp)
        Text(subtitle, color = TextMutedDark, fontSize = 11.sp)
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    value: Float,
    primary: String,
    detail: String,
    footer: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF12251C), SurfaceDark)))
            .border(1.dp, SurfaceGlassBorder, RoundedCornerShape(20.dp))
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(17.dp))
            Spacer(Modifier.width(7.dp))
            Text(title, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
        }
        MetricRing(value = value, primary = primary, size = 82.dp)
        Text(detail, color = TextPrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        Text(footer, color = TextMutedDark, fontSize = 10.sp, maxLines = 1)
    }
}

@Composable
private fun MetricRing(value: Float, primary: String, size: androidx.compose.ui.unit.Dp) {
    val progress by animateFloatAsState(
        targetValue = (value / 100f).coerceIn(0f, 1f),
        animationSpec = tween(650),
        label = "metric-ring"
    )
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawArc(
                color = Color(0xFF20372C),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(EmeraldDark, EmeraldLight, EmeraldPrimary)),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(primary, color = TextPrimaryDark, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun BatteryWideCard(state: DashboardUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceGlassBorder, RoundedCornerShape(20.dp))
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.BatteryChargingFull, contentDescription = null, tint = EmeraldLight)
            Spacer(Modifier.width(9.dp))
            Text("Batterie", color = TextPrimaryDark, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("${state.batteryLevel}%", color = EmeraldLight, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 19.sp)
        }
        LinearProgressIndicator(
            progress = { (state.batteryLevel / 100f).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = EmeraldPrimary,
            trackColor = SurfaceDark
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(state.batteryStatusText.ifBlank { "État indisponible" }, color = TextSecondaryDark, fontSize = 11.sp)
            Text(if (state.batteryTemperature > 0f) "${state.batteryTemperature} °C" else "Température indisponible", color = TextSecondaryDark, fontSize = 11.sp)
        }
    }
}

private data class DiagnosticShortcut(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

@Composable
private fun ShortcutCard(item: DiagnosticShortcut, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(17.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceGlassBorder, RoundedCornerShape(17.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(EmeraldPrimary.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(item.icon, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(item.title, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, maxLines = 2)
            Text(item.description, color = TextMutedDark, fontSize = 9.sp, maxLines = 2, lineHeight = 12.sp)
        }
    }
}

@Composable
private fun DashboardFooter() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 3.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Refresh, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(6.dp))
        Text("Données locales · Actualisation toutes les 2 secondes · @Bienvenu", color = TextMutedDark, fontSize = 9.sp)
    }
}
