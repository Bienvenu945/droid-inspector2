package com.droidinspector.feature.applications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
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
import com.droidinspector.domain.model.AppPackageInfo
import com.droidinspector.feature.inspection.InspectionViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun AppManagerScreen(viewModel: InspectionViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
    val apps by viewModel.apps.collectAsState()
    val includeSystem by viewModel.includeSystem.collectAsState()
    var query by remember { mutableStateOf("") }
    var selectedApp by remember { mutableStateOf<AppPackageInfo?>(null) }
    val filtered = remember(apps, query) {
        apps.filter { it.appName.contains(query, true) || it.packageName.contains(query, true) }
            .sortedBy { it.appName.lowercase() }
    }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("Gestionnaire d'applications", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Text("Inventaire local · ${apps.size} applications chargées", color = TextMutedDark, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Nom ou nom de package…", color = TextMutedDark) },
            leadingIcon = { Icon(Icons.Rounded.Search, null, tint = EmeraldPrimary) }
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = includeSystem, onCheckedChange = { viewModel.loadApps(it) }, colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary))
            Text("Inclure les applications système", color = Color.White, fontSize = 12.sp)
            Spacer(Modifier.weight(1f))
            Text("${filtered.size} résultat(s)", color = TextMutedDark, fontSize = 11.sp)
        }
        SectionHeader("Applications installées", "Version, cible Android et dates d'installation")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            items(filtered, key = { it.packageName }) { app ->
                GlassCard(modifier = Modifier.fillMaxWidth().clickable { selectedApp = app }) {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(app.appName, color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            Text(if (app.isSystemApp) "SYSTÈME" else "UTILISATEUR", color = EmeraldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(app.packageName, color = TextMutedDark, fontSize = 10.sp)
                        Text("Version ${app.versionName} (${app.versionCode}) · Target SDK ${app.targetSdkVersion}", color = Color.White.copy(alpha = .85f), fontSize = 11.sp)
                        Text("Installée : ${DateFormat.getDateInstance().format(Date(app.firstInstallTime))} · Mise à jour : ${DateFormat.getDateInstance().format(Date(app.lastUpdateTime))}", color = TextMutedDark, fontSize = 10.sp)
                        Text("${app.requestedPermissions.size} permission(s) déclarée(s) · Toucher pour les détails", color = EmeraldPrimary, fontSize = 10.sp)
                    }
                }
            }
            if (filtered.isEmpty()) item { Text("Aucune application ne correspond à cette recherche.", color = TextMutedDark) }
        }
    }

    selectedApp?.let { app ->
        AlertDialog(
            onDismissRequest = { selectedApp = null },
            title = { Text(app.appName) },
            text = {
                Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                    Text(app.packageName, color = EmeraldPrimary, fontSize = 11.sp)
                    Text("Version ${app.versionName} · code ${app.versionCode}", fontSize = 12.sp)
                    Text("Target SDK ${app.targetSdkVersion}", fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Permissions déclarées (${app.requestedPermissions.size})", fontWeight = FontWeight.Bold)
                    app.requestedPermissions.forEach { permission -> Text("• $permission", fontSize = 10.sp) }
                    if (app.requestedPermissions.isEmpty()) Text("Aucune permission déclarée.", fontSize = 11.sp)
                }
            },
            confirmButton = { TextButton(onClick = { selectedApp = null }) { Text("Fermer") } }
        )
    }
}
