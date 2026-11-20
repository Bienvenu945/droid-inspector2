package com.droidinspector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.droidinspector.core.navigation.AdaptiveNavigationRail
import com.droidinspector.core.navigation.BottomNavigationBar
import com.droidinspector.core.navigation.NavGraph
import com.droidinspector.core.theme.DroidInspectorTheme
import dagger.hilt.android.AndroidEntryPoint

/** Main Compose host. Navigation adapts to compact and expanded window widths. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DroidInspectorTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent() {
    val navController = rememberNavController()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Material window breakpoints: compact phones use bottom navigation;
        // tablets, foldables and landscape phones use a navigation rail.
        val expanded = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (expanded) {
                AdaptiveNavigationRail(navController = navController)
            }

            Scaffold(
                modifier = Modifier.weight(1f).fillMaxSize(),
                bottomBar = {
                    if (!expanded) BottomNavigationBar(navController = navController)
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.widthIn(max = 1180.dp).fillMaxSize()
                    )
                }
            }
        }
    }
}
