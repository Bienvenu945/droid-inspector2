package com.droidinspector.feature.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.DeviceThermostat
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidinspector.core.theme.BackgroundDark
import com.droidinspector.core.theme.EmeraldDark
import com.droidinspector.core.theme.EmeraldLight
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.SurfaceCard
import com.droidinspector.core.theme.SurfaceDark
import com.droidinspector.core.theme.SurfaceGlass
import com.droidinspector.core.theme.SurfaceGlassBorder
import com.droidinspector.core.theme.TextMutedDark
import com.droidinspector.core.theme.TextPrimaryDark
import com.droidinspector.core.theme.TextSecondaryDark

/**
 * Écran Dashboard principal de Droid Inspector X.
 * Affiche en temps réel :
 * - Appareil : Marque & Modèle
 * - Android : Version & Niveau API
 * - RAM : Utilisée / Totale
 * - Stockage interne : Utilisé / Total
 * - Batterie : Niveau, État de charge, Température
 * Rafraîchissement automatique toutes les 2 secondes.
 *
 * Signature : @Bienvenu
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    // Récupération de l'état avec collectAsState() selon contraintes
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // En-tête : Titre & Signature officielle @Bienvenu
        DashboardHeader()

        // 1. Carte Appareil & Plateforme Android
        DeviceIdentityCard(state = state)

        // 2. Section Métriques en Temps Réel (RAM & Stockage avec jauges)
        Text(
            text = "MÉMOIRE & STOCKAGE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = EmeraldLight,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Carte RAM
            ResourceGaugeCard(
                title = "RAM",
                icon = Icons.Rounded.Memory,
                usedFormatted = state.ramUsedFormatted,
                totalFormatted = state.ramTotalFormatted,
                percentage = state.ramUsagePercentage,
                modifier = Modifier.weight(1f)
            )

            // Carte Stockage interne
            ResourceGaugeCard(
                title = "Stockage",
                icon = Icons.Rounded.Storage,
                usedFormatted = state.storageUsedFormatted,
                totalFormatted = state.storageTotalFormatted,
                percentage = state.storageUsagePercentage,
                modifier = Modifier.weight(1f)
            )
        }

        // 3. Section Batterie (Niveau, État de charge, Température)
        Text(
            text = "ÉNERGIE & ACCUMULATEUR",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = EmeraldLight,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        BatteryMonitorCard(state = state)

        // Pied de page : Signature et horloge de polling 2s
        DashboardFooter()

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * En-tête de l'application avec signature @Bienvenu et badge de rafraîchissement 2s.
 */
@Composable
private fun DashboardHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "DROID INSPECTOR X",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp,
                color = TextPrimaryDark
            )
            Text(
                text = "Diagnostic Système par @Bienvenu",
                fontSize = 12.sp,
                color = EmeraldLight,
                fontWeight = FontWeight.Medium
            )
        }

        // Indicateur vert glacé de rafraîchissement 2s
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceCard)
                .border(1.dp, SurfaceGlassBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "2s LIVE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = EmeraldLight
            )
        }
    }
}

/**
 * Carte affichant Marque, Modèle, Version Android et Niveau d'API.
 */
@Composable
private fun DeviceIdentityCard(state: DashboardUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        SurfaceCard,
                        SurfaceGlass
                    )
                )
            )
            .border(1.dp, SurfaceGlassBorder, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldDark.copy(alpha = 0.35f))
                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Smartphone,
                            contentDescription = null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (state.isLoaded) "${state.manufacturer} ${state.model}" else "Chargement...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Matériel détecté",
                            fontSize = 11.sp,
                            color = TextMutedDark
                        )
                    }
                }

                // Badge niveau API
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "API ${state.apiLevel}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = EmeraldLight
                    )
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "VERSION ANDROID", fontSize = 10.sp, color = TextMutedDark, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (state.isLoaded) "Android ${state.androidVersion}" else "--",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "CONSTRUCTEUR", fontSize = 10.sp, color = TextMutedDark, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (state.isLoaded) state.manufacturer else "--",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                }
            }
        }
    }
}

/**
 * Carte affichant une jauge circulaire animée pour la RAM ou le stockage.
 */
@Composable
private fun ResourceGaugeCard(
    title: String,
    icon: ImageVector,
    usedFormatted: String,
    totalFormatted: String,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceGlass)
            .border(1.dp, SurfaceGlassBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = EmeraldLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                }
            }

            // Jauge circulaire animée
            CircularProgressGauge(
                percentage = percentage,
                size = 90.dp,
                strokeWidth = 8.dp
            )

            // Détails utilisés / total
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$usedFormatted / $totalFormatted",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryDark,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Utilisé",
                    fontSize = 10.sp,
                    color = TextMutedDark
                )
            }
        }
    }
}

/**
 * Jauge circulaire animée vert glacé futuriste.
 */
@Composable
private fun CircularProgressGauge(
    percentage: Float,
    size: Dp,
    strokeWidth: Dp
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(percentage) {
        animatedProgress.animateTo(
            targetValue = (percentage / 100f).coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            // Cercle d'arrière-plan
            drawCircle(
                color = SurfaceCard,
                style = stroke
            )
            // Arc de progression vert glacé
            drawArc(
                color = EmeraldPrimary,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress.value,
                useCenter = false,
                style = stroke
            )
        }

        Text(
            text = "${(animatedProgress.value * 100).toInt()}%",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = TextPrimaryDark
        )
    }
}

/**
 * Carte de surveillance de la batterie (Niveau, État de charge, Température).
 */
@Composable
private fun BatteryMonitorCard(state: DashboardUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceGlass)
            .border(1.dp, SurfaceGlassBorder, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldDark.copy(alpha = 0.35f))
                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BatteryChargingFull,
                            contentDescription = null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Batterie",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = state.batteryStatusText,
                            fontSize = 11.sp,
                            color = if (state.isCharging) EmeraldLight else TextSecondaryDark
                        )
                    }
                }

                // Pourcentage principal
                Text(
                    text = "${state.batteryLevel}%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = EmeraldLight
                )
            }

            // Barre de niveau de batterie animée
            LinearProgressIndicator(
                progress = { (state.batteryLevel / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = EmeraldPrimary,
                trackColor = SurfaceCard,
            )

            // Sous-métriques : État de charge et Température en °C
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.DeviceThermostat,
                        contentDescription = null,
                        tint = EmeraldLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Température :",
                        fontSize = 12.sp,
                        color = TextMutedDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.batteryTemperature} °C",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimaryDark
                    )
                }

                Text(
                    text = if (state.isCharging) "Charge active" else "Sur batterie",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (state.isCharging) EmeraldLight else TextMutedDark
                )
            }
        }
    }
}

/**
 * Pied de page avec signature officielle @Bienvenu.
 */
@Composable
private fun DashboardFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "DROID INSPECTOR X — @Bienvenu",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = TextMutedDark,
            letterSpacing = 0.5.sp
        )
    }
}
