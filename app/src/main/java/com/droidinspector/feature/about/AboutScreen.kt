package com.droidinspector.feature.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.droidinspector.core.components.GlassCard
import com.droidinspector.core.components.SectionHeader
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.TextMutedDark

@Composable
fun AboutScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Retour", tint = EmeraldPrimary)
            }
            Text(
                text = "À propos de l'application",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Official Signature Box
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "DROID INSPECTOR X",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Android System Intelligence & Diagnostics",
                    fontSize = 13.sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Developed by @Bienvenu",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Version 0.4.0 · Phase 4 — Feature completion",
                    fontSize = 11.sp,
                    color = TextMutedDark
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionHeader("Engagement Confidentialité & Éthique", "Architecture respectueuse de votre vie privée")

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Security, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("100% Hors-Ligne & Local", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Text(
                    text = "Toutes les analyses et diagnostics matériels sont exécutés directement sur le processeur de votre appareil Android. Aucune donnée, identifiant matériel ou télémétrie n'est transmise vers des serveurs distants.",
                    color = TextMutedDark,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionHeader("Spécifications Techniques du Projet", "Technologies de pointe Android")

        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AboutSpecRow("Langage", "Kotlin 2.0.21")
                AboutSpecRow("Interface", "Jetpack Compose + Material 3")
                AboutSpecRow("Architecture", "MVVM / Clean Architecture Modulaire")
                AboutSpecRow("Design System", "Emerald Frosted Glass (Glassmorphism)")
                AboutSpecRow("Injection", "Dagger Hilt")
                AboutSpecRow("Diagnostics", "Capteurs live · Inventaire apps · Système")
                AboutSpecRow("Rapports", "Export PDF local via stockage Android")
                AboutSpecRow("Automatisation CI", "GitHub Actions (.github/workflows)")
                AboutSpecRow("Auteur Officiel", "@Bienvenu")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AboutSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMutedDark, fontSize = 12.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
