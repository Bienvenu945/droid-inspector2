package com.droidinspector.feature.sensors

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.TextMutedDark
import com.droidinspector.feature.inspection.InspectionViewModel
import kotlinx.coroutines.flow.catch

@Composable
fun SensorLabScreen(viewModel: InspectionViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
    val sensors = viewModel.sensors
    var selectedType by remember { mutableIntStateOf(sensors.firstOrNull()?.type ?: -1) }
    var values by remember { mutableStateOf<List<Float>>(emptyList()) }
    var accuracyLabel by remember { mutableStateOf("Sélectionne un capteur pour afficher ses mesures") }

    LaunchedEffect(selectedType) {
        values = emptyList()
        if (selectedType >= 0) {
            viewModel.sensorUpdates(selectedType).catch { accuracyLabel = "Mesure indisponible sur cet appareil" }
                .collect { values = it; accuracyLabel = "Mesure en direct · actualisation selon le capteur" }
        }
    }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Sensor Lab", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("${sensors.size} capteurs physiques détectés", color = TextMutedDark, fontSize = 12.sp)
        Spacer(Modifier.height(14.dp))
        SectionHeader("Mesure en temps réel", "Les valeurs proviennent du SensorManager Android")
        GlassCard {
            Column(Modifier.fillMaxWidth()) {
                Text(sensors.firstOrNull { it.type == selectedType }?.name ?: "Aucun capteur", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                if (values.isEmpty()) Text(accuracyLabel, color = TextMutedDark, fontSize = 12.sp)
                else values.forEachIndexed { i, v ->
                    Text("Axe ${i + 1} : ${"%.4f".format(v)}", color = Color.White, fontSize = 15.sp)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        SectionHeader("Capteurs disponibles", "Appuie sur un capteur pour l'inspecter")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(sensors, key = { "${it.type}-${it.name}" }) { sensor ->
                GlassCard(modifier = Modifier.fillMaxWidth().clickable { selectedType = sensor.type }) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Sensors, null, tint = if (sensor.type == selectedType) EmeraldPrimary else TextMutedDark)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(sensor.typeString, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(sensor.name, color = TextMutedDark, fontSize = 11.sp)
                            Text("${sensor.vendor} · ${sensor.powerMa} mA · plage ${sensor.maxRange}", color = TextMutedDark, fontSize = 10.sp)
                        }
                        if (sensor.type == selectedType) Text("ACTIF", color = EmeraldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (sensors.isEmpty()) item { Text("Aucun capteur détecté par Android.", color = TextMutedDark) }
        }
    }
}
