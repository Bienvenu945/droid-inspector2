package com.droidinspector.feature.reports

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.TextMutedDark
import com.droidinspector.feature.inspection.InspectionViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun ReportsScreen(viewModel: InspectionViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val device = viewModel.device
    val apps by viewModel.apps.collectAsState()
    val sensors = viewModel.sensors
    val reportLines = remember(device, apps, sensors) { listOf(
        "DROID INSPECTOR X — RAPPORT DE DIAGNOSTIC",
        "Généré le ${DateFormat.getDateTimeInstance().format(Date())}", "",
        "APPAREIL", "Fabricant : ${device.manufacturer}", "Modèle : ${device.model}", "Board : ${device.board}",
        "ANDROID", "Version : ${device.androidVersion} (API ${device.apiLevel})", "Correctif : ${device.securityPatch}", "Build : ${device.buildNumber}",
        "MATÉRIEL", "Bootloader : ${device.bootloader}", "Radio : ${device.radioVersion}",
        "CAPTEURS", "Nombre détecté : ${sensors.size}", *sensors.map { "${it.typeString} — ${it.name} (${it.vendor})" }.toTypedArray(),
        "APPLICATIONS", "Nombre inventorié : ${apps.size}", *apps.map { "${it.appName} — ${it.packageName} — ${it.versionName}" }.toTypedArray(),
        "", "Note : les valeurs correspondent aux informations accessibles à l'application via les API Android.", "DROID INSPECTOR X · @Bienvenu"
    ) }
    val createPdf = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null) {
            val pdf = PdfDocument()
            try {
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 10f; color = android.graphics.Color.DKGRAY }
                val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 14f; color = android.graphics.Color.rgb(0, 120, 90); isFakeBoldText = true }
                val pageLines = reportLines.chunked(46)
                pageLines.forEachIndexed { index, lines ->
                    val info = PdfDocument.PageInfo.Builder(595, 842, index + 1).create()
                    val page = pdf.startPage(info)
                    page.canvas.drawText("DROID INSPECTOR X", 40f, 34f, titlePaint)
                    var y = 58f
                    lines.forEach { line ->
                        val safeLine = if (line.length > 95) line.take(92) + "…" else line
                        val p = if (line in listOf("APPAREIL", "ANDROID", "MATÉRIEL", "CAPTEURS", "APPLICATIONS", "DROID INSPECTOR X — RAPPORT DE DIAGNOSTIC")) titlePaint else paint
                        page.canvas.drawText(safeLine, 40f, y, p)
                        y += 16f
                    }
                    page.canvas.drawText("Page ${index + 1}/${pageLines.size}", 500f, 820f, paint)
                    pdf.finishPage(page)
                }
                context.contentResolver.openOutputStream(uri)?.use { pdf.writeTo(it) }
            } finally { pdf.close() }
        }
    }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Centre de rapports", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Export local des informations réellement collectées", color = TextMutedDark, fontSize = 12.sp)
        Spacer(Modifier.height(16.dp)); SectionHeader("Rapport de diagnostic", "Contenu préparé à partir du terminal")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("DROID INSPECTOR X", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("${device.manufacturer} ${device.model}", color = Color.White, fontWeight = FontWeight.SemiBold)
                Text("Android ${device.androidVersion} · API ${device.apiLevel}", color = TextMutedDark, fontSize = 12.sp)
                Divider(color = Color.White.copy(alpha = .12f))
                Text("• Identité et informations système", color = Color.White, fontSize = 12.sp)
                Text("• ${sensors.size} capteurs recensés", color = Color.White, fontSize = 12.sp)
                Text("• ${apps.size} applications inventoriées", color = Color.White, fontSize = 12.sp)
                Text("• Version du build, correctif et informations matérielles", color = Color.White, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
        Button(onClick = { createPdf.launch("DroidInspectorX-rapport.pdf") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black)) {
            Icon(Icons.Rounded.Download, null); Spacer(Modifier.width(8.dp)); Text("Exporter le rapport PDF", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text("Le PDF est généré sur l'appareil et enregistré à l'emplacement choisi. Aucun envoi réseau.", color = TextMutedDark, fontSize = 11.sp)
    }
}
