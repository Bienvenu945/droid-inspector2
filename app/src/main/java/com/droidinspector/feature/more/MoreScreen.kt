package com.droidinspector.feature.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.navigation.Screen
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.TextMutedDark

private data class MoreEntry(val title: String, val subtitle: String, val icon: ImageVector, val route: String)

@Composable
fun MoreScreen(navController: NavController, modifier: Modifier = Modifier) {
    val entries = listOf(
        MoreEntry("Capteurs", "Inventaire des capteurs et mesures en direct", Icons.Rounded.Sensors, Screen.Sensors.route),
        MoreEntry("Applications", "Inventaire, versions et permissions déclarées", Icons.Rounded.Apps, Screen.Applications.route),
        MoreEntry("Système", "Build Android, sécurité et identité", Icons.Rounded.Settings, Screen.System.route),
        MoreEntry("Rapports", "Générer et exporter un rapport PDF", Icons.Rounded.Description, Screen.Reports.route),
        MoreEntry("À propos", "Version, architecture et confidentialité", Icons.Rounded.Info, Screen.About.route)
    )
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Explorer", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Outils complémentaires de Droid Inspector X", color = TextMutedDark, fontSize = 12.sp)
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(entries) { entry ->
                GlassCard(modifier = Modifier.clickable { navController.navigate(entry.route) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(entry.icon, null, tint = EmeraldPrimary, modifier = Modifier.size(26.dp))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(entry.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(entry.subtitle, color = TextMutedDark, fontSize = 11.sp)
                        }
                        Text("›", color = EmeraldPrimary, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
