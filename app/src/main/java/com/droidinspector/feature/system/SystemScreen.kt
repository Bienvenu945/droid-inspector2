package com.droidinspector.feature.system

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun SystemScreen(viewModel: InspectionViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
    val d = viewModel.device
    val context = LocalContext.current
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Informations système", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Données réelles exposées par Android", color = TextMutedDark, fontSize = 12.sp)
        Spacer(Modifier.height(16.dp))
        SectionHeader("Identité de l'appareil", "Build et fabricant")
        GlassCard { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SystemInfoRow("Fabricant", d.manufacturer); SystemInfoRow("Modèle", d.model)
            SystemInfoRow("Nom", d.deviceName); SystemInfoRow("Carte", d.board)
            SystemInfoRow("Bootloader", d.bootloader); SystemInfoRow("Radio", d.radioVersion)
        } }
        Spacer(Modifier.height(14.dp)); SectionHeader("Android", "Version, API et sécurité")
        GlassCard { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SystemInfoRow("Version Android", "${d.androidVersion} (API ${d.apiLevel})")
            SystemInfoRow("Correctif sécurité", d.securityPatch); SystemInfoRow("Build", d.buildNumber)
            SystemInfoRow("Niveau SDK", Build.VERSION.SDK_INT.toString())
            SystemInfoRow("Empreinte", Build.FINGERPRINT)
        } }
        Spacer(Modifier.height(14.dp)); SectionHeader("Sécurité", "État observé sans privilèges root")
        GlassCard { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SystemInfoRow("SELinux", readSelinux())
            SystemInfoRow("Débogage USB", if (android.provider.Settings.Global.getInt(context.contentResolver, android.provider.Settings.Global.ADB_ENABLED, 0) == 1) "Activé" else "Désactivé")
            SystemInfoRow("Root", if (hasSuBinary()) "Binaire su détecté (à vérifier)" else "Aucun binaire su courant détecté")
            Text("Ces contrôles sont indicatifs : Android peut masquer certaines informations aux applications ordinaires.", color = TextMutedDark, fontSize = 10.sp)
        } }
    }
}

private fun readSelinux(): String = try { if (java.io.File("/sys/fs/selinux/enforce").readText().trim() == "1") "Enforcing" else "Permissive" } catch (_: Exception) { "Non accessible" }
private fun hasSuBinary(): Boolean = listOf("/system/bin/su", "/system/xbin/su", "/sbin/su").any { java.io.File(it).exists() }

@Composable
fun SystemInfoRow(label: String, value: String) {
    Column {
        Text(label, color = TextMutedDark, fontSize = 11.sp)
        Text(value.ifBlank { "Non communiqué" }, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
