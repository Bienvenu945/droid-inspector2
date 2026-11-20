package com.droidinspector.feature.files

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.documentfile.provider.DocumentFile
import com.droidinspector.core.theme.EmeraldPrimary
import com.droidinspector.core.theme.SurfaceCard
import com.droidinspector.core.theme.SurfaceDark
import com.droidinspector.core.theme.TextMutedDark
import java.text.DateFormat
import java.util.Date
import java.util.Locale

private enum class FileSort { NAME, SIZE, DATE, TYPE }

/** SAF-based file browser. Access is limited to the directory tree explicitly selected by the user. */
@Composable
fun FileExplorerScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var treeUriText by rememberSaveable { mutableStateOf("") }
    var directoryStack by remember { mutableStateOf<List<DocumentFile>>(emptyList()) }
    var refreshKey by remember { mutableStateOf(false) }
    var search by rememberSaveable { mutableStateOf("") }
    var showHidden by rememberSaveable { mutableStateOf(false) }
    var sort by rememberSaveable { mutableStateOf(FileSort.NAME) }
    var sortMenu by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<DocumentFile?>(null) }
    var renameTarget by remember { mutableStateOf<DocumentFile?>(null) }
    var renameText by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<DocumentFile?>(null) }
    var showCreateFolder by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (_: SecurityException) { /* Some providers grant only transient access. */ }
            treeUriText = uri.toString()
            DocumentFile.fromTreeUri(context, uri)?.let { directoryStack = listOf(it) }
            search = ""
            refreshKey = !refreshKey
        }
    }

    val root = remember(treeUriText, refreshKey) {
        treeUriText.takeIf { it.isNotBlank() }?.let { DocumentFile.fromTreeUri(context, Uri.parse(it)) }
    }
    val current = directoryStack.lastOrNull() ?: root
    val entries = remember(current, refreshKey, search, showHidden, sort) {
        runCatching {
            current?.listFiles()?.filter { file ->
                (showHidden || !file.name.orEmpty().startsWith('.')) &&
                    file.name.orEmpty().contains(search, ignoreCase = true)
            }?.sortedWith(when (sort) {
                FileSort.NAME -> compareBy<DocumentFile> { !it.isDirectory }.thenBy { it.name?.lowercase(Locale.ROOT) }
                FileSort.SIZE -> compareBy<DocumentFile> { !it.isDirectory }.thenByDescending { it.length() }
                FileSort.DATE -> compareBy<DocumentFile> { !it.isDirectory }.thenByDescending { it.lastModified() }
                FileSort.TYPE -> compareBy<DocumentFile> { !it.isDirectory }.thenBy { it.type.orEmpty() }
            }).orEmpty()
        }.getOrElse { emptyList() }
    }

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("File Explorer", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Exploration technique du stockage", color = TextMutedDark, fontSize = 12.sp)
            }
            IconButton(onClick = { showHidden = !showHidden }) {
                Icon(Icons.Rounded.MoreVert, contentDescription = "Options", tint = EmeraldPrimary)
            }
            Box {
                IconButton(onClick = { sortMenu = true }) {
                    Icon(Icons.Rounded.Sort, contentDescription = "Trier", tint = EmeraldPrimary)
                }
                DropdownMenu(expanded = sortMenu, onDismissRequest = { sortMenu = false }) {
                    FileSort.entries.forEach { option ->
                        DropdownMenuItem(text = { Text("Trier : ${option.label()}") }, onClick = {
                            sort = option; sortMenu = false
                        })
                    }
                    DropdownMenuItem(text = { Text(if (showHidden) "Masquer les fichiers cachés" else "Afficher les fichiers cachés") }, onClick = {
                        showHidden = !showHidden; sortMenu = false
                    })
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(),
            singleLine = true, placeholder = { Text("Rechercher dans ce dossier…") },
            leadingIcon = { Icon(Icons.Rounded.Search, null, tint = EmeraldPrimary) },
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceCard), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.FolderOpen, null, tint = EmeraldPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text(if (current == null) "Aucun emplacement sélectionné" else current.name ?: "Stockage sélectionné", color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${entries.size} éléments", color = TextMutedDark, fontSize = 11.sp)
                }
                if (current != null) {
                    Text(current.uri.toString(), color = TextMutedDark, fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
                    Button(onClick = { folderPicker.launch(null) }, colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)) {
                        Icon(Icons.Rounded.Folder, null); Spacer(Modifier.width(6.dp)); Text("Choisir un dossier")
                    }
                    TextButton(onClick = { showCreateFolder = true; newFolderName = "" }, enabled = current?.canWrite() == true) { Text("Nouveau dossier") }
                    TextButton(onClick = {
                        if (directoryStack.size > 1) directoryStack = directoryStack.dropLast(1)
                    }, enabled = directoryStack.size > 1) { Text("Retour") }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        if (current == null) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.FolderOpen, null, tint = EmeraldPrimary, modifier = Modifier.size(56.dp))
                    Text("Sélectionne un dossier pour commencer", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Android ouvrira son sélecteur officiel. Tu gardes le contrôle de l'accès.", color = TextMutedDark, fontSize = 12.sp)
                    Button(onClick = { folderPicker.launch(null) }, modifier = Modifier.padding(top = 12.dp), colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)) { Text("Parcourir le stockage") }
                }
            }
        } else if (entries.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(if (search.isBlank()) "Ce dossier est vide" else "Aucun résultat", color = TextMutedDark)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.weight(1f)) {
                items(entries, key = { it.uri.toString() }) { file ->
                    FileEntryRow(file = file, onClick = {
                        if (file.isDirectory) directoryStack = directoryStack + file else selected = file
                    })
                }
            }
        }
        message?.let { Text(it, color = EmeraldPrimary, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp)) }
    }

    selected?.let { file ->
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(file.name ?: "Fichier") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    DetailLine("Type MIME", file.type ?: "Inconnu")
                    DetailLine("Taille", formatBytes(file.length()))
                    DetailLine("Modifié", if (file.lastModified() > 0) DateFormat.getDateTimeInstance().format(Date(file.lastModified())) else "Indisponible")
                    DetailLine("URI", file.uri.toString())
                    DetailLine("Lecture", if (file.canRead()) "Autorisée" else "Non autorisée")
                    DetailLine("Écriture", if (file.canWrite()) "Autorisée" else "Non autorisée")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val mime = file.type ?: "*/*"
                    val intent = Intent(Intent.ACTION_VIEW).setDataAndType(file.uri, mime).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    runCatching { context.startActivity(intent) }.onFailure { message = "Aucune application compatible trouvée" }
                    selected = null
                }) { Text("Ouvrir") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { renameTarget = file; renameText = file.name.orEmpty(); selected = null }) { Text("Renommer") }
                    TextButton(onClick = { deleteTarget = file; selected = null }) { Text("Supprimer") }
                    TextButton(onClick = { selected = null }) { Text("Fermer") }
                }
            }
        )
    }
    renameTarget?.let { file ->
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            title = { Text("Renommer") },
            text = { OutlinedTextField(value = renameText, onValueChange = { renameText = it }, singleLine = true, label = { Text("Nouveau nom") }) },
            confirmButton = { TextButton(onClick = {
                val ok = renameText.isNotBlank() && file.renameTo(renameText.trim())
                message = if (ok) "Élément renommé" else "Renommage impossible avec ce fournisseur"
                renameTarget = null; refreshKey = !refreshKey
            }) { Text("Valider") } },
            dismissButton = { TextButton(onClick = { renameTarget = null }) { Text("Annuler") } }
        )
    }
    if (showCreateFolder) {
        AlertDialog(
            onDismissRequest = { showCreateFolder = false },
            title = { Text("Créer un dossier") },
            text = { OutlinedTextField(value = newFolderName, onValueChange = { newFolderName = it }, singleLine = true, label = { Text("Nom du dossier") }) },
            confirmButton = { TextButton(onClick = {
                val created = newFolderName.isNotBlank() && current?.createDirectory(newFolderName.trim()) != null
                message = if (created) "Dossier créé" else "Création impossible dans cet emplacement"
                showCreateFolder = false; refreshKey = !refreshKey
            }) { Text("Créer") } },
            dismissButton = { TextButton(onClick = { showCreateFolder = false }) { Text("Annuler") } }
        )
    }

    deleteTarget?.let { file ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Supprimer cet élément ?") },
            text = { Text("${file.name}\nCette opération peut être définitive selon le fournisseur de documents.") },
            confirmButton = { TextButton(onClick = {
                val ok = file.delete()
                message = if (ok) "Élément supprimé" else "Suppression refusée ou impossible"
                deleteTarget = null; refreshKey = !refreshKey
            }) { Text("Supprimer") } },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Annuler") } }
        )
    }
}

@Composable
private fun FileEntryRow(file: DocumentFile, onClick: () -> Unit) {
    val icon = fileIcon(file)
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (file.isDirectory) EmeraldPrimary else Color(0xFF8BA99B), modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(file.name ?: "Sans nom", color = Color.White, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(if (file.isDirectory) "Dossier" else "${file.type ?: extensionOf(file.name)} • ${formatBytes(file.length())}", color = TextMutedDark, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(if (file.isDirectory) "›" else "⋮", color = EmeraldPrimary, fontSize = 22.sp)
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Column {
        Text(label, color = TextMutedDark, fontSize = 10.sp)
        Text(value, color = Color.White, fontSize = 12.sp)
    }
}

private fun fileIcon(file: DocumentFile): ImageVector {
    if (file.isDirectory) return Icons.Rounded.Folder
    return when {
        file.type?.startsWith("image/") == true -> Icons.Rounded.Image
        file.type?.startsWith("video/") == true -> Icons.Rounded.VideoFile
        file.type?.startsWith("audio/") == true -> Icons.Rounded.AudioFile
        file.type == "application/pdf" -> Icons.Rounded.PictureAsPdf
        file.type?.contains("zip") == true || file.type?.contains("compressed") == true -> Icons.Rounded.Archive
        file.type?.startsWith("text/") == true -> Icons.Rounded.Description
        else -> Icons.Rounded.InsertDriveFile
    }
}

private fun extensionOf(name: String?): String = name?.substringAfterLast('.', "Fichier")?.uppercase(Locale.ROOT) ?: "Fichier"
private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes o"
    val units = listOf("Ko", "Mo", "Go", "To")
    var value = bytes.toDouble(); var index = -1
    do { value /= 1024.0; index++ } while (value >= 1024 && index < units.lastIndex)
    return String.format(Locale.getDefault(), "%.1f %s", value, units[index])
}
private fun FileSort.label(): String = when (this) {
    FileSort.NAME -> "Nom"; FileSort.SIZE -> "Taille"; FileSort.DATE -> "Date"; FileSort.TYPE -> "Type"
}
