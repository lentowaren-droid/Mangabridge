package app.mangabridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF0B0D12)
private val Panel = Color(0xFF151923)
private val Panel2 = Color(0xFF202735)
private val Accent = Color(0xFFB7F36B)
private val Muted = Color(0xFF9AA4B2)
private val Paper = Color(0xFFF1F3F5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MangaBridgeApp() }
    }
}

data class Manga(val title: String, val chapter: String, val progress: Float, val tag: String, val art: String)

private val library = listOf(
    Manga("The Beginning After the End", "Chapter 198", .72f, "FANTASY", "TB"),
    Manga("Solo Leveling", "Chapter 179", .42f, "ACTION", "SL"),
    Manga("The Greatest Estate Developer", "Chapter 162", .18f, "COMEDY", "GE"),
    Manga("Omniscient Reader", "Chapter 231", .86f, "FANTASY", "OR")
)

@Composable
fun MangaBridgeApp() {
    var tab by remember { mutableStateOf("Home") }
    var selected by remember { mutableStateOf<Manga?>(null) }
    var query by remember { mutableStateOf("") }
    MaterialTheme(colorScheme = darkColorScheme(
        background = Ink, surface = Panel, primary = Accent,
        onPrimary = Ink, onBackground = Paper, onSurface = Paper,
        secondary = Muted
    )) {
        Surface(Modifier.fillMaxSize(), color = Ink) {
            if (selected != null) {
                ReaderScreen(manga = selected!!, onBack = { selected = null })
            } else {
                Column(Modifier.fillMaxSize()) {
                    Box(Modifier.weight(1f)) {
                        when (tab) {
                            "Home" -> HomeScreen(onOpen = { selected = it })
                            "Discover" -> DiscoverScreen(query, { query = it }, onOpen = { selected = it })
                            "Cloud" -> CloudScreen()
                            else -> SettingsScreen()
                        }
                    }
                    NavigationBar(containerColor = Panel, contentColor = Paper) {
                        val items = listOf("Home", "Discover", "Cloud", "Settings")
                        val icons = listOf(Icons.Filled.Home, Icons.Filled.Search, Icons.Filled.Cloud, Icons.Filled.Settings)
                        items.forEachIndexed { index, label ->
                            NavigationBarItem(
                                selected = tab == label,
                                onClick = { tab = label },
                                icon = { Icon(icons[index], contentDescription = label) },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Accent, selectedTextColor = Accent,
                                    indicatorColor = Panel2, unselectedIconColor = Muted,
                                    unselectedTextColor = Muted
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onOpen: (Manga) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("MANGABRIDGE", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("Your reading space.", color = Paper, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                }
                Box(Modifier.size(42.dp).clip(RoundedCornerShape(15.dp)).background(Panel2), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Person, null, tint = Accent)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("IN LIBRARY", "24", Modifier.weight(1f))
                StatCard("READING", "03", Modifier.weight(1f))
                StatCard("PROVIDERS", "01", Modifier.weight(1f))
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionTitle("PICK UP WHERE YOU LEFT OFF", "VIEW ALL")
                FeaturedCard(library.first(), onClick = { onOpen(library.first()) })
            }
        }
        item { SectionTitle("YOUR LIBRARY", "SEE ALL") }
        items(library) { manga -> MangaRow(manga, onClick = { onOpen(manga) }) }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(16.dp)).background(Panel).padding(12.dp)) {
        Text(label, color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        Text(value, color = Paper, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionTitle(title: String, action: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), color = Paper, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text(action, color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Cover(manga: Manga, modifier: Modifier = Modifier, large: Boolean = false) {
    Box(modifier.clip(RoundedCornerShape(12.dp)).background(
        when (manga.art) { "TB" -> Color(0xFF42566B); "SL" -> Color(0xFF473B58); "GE" -> Color(0xFF75634A); else -> Color(0xFF345B59) }
    ), contentAlignment = Alignment.Center) {
        Text(manga.art, color = Color.White.copy(alpha = .88f), fontSize = if (large) 32.sp else 20.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun FeaturedCard(manga: Manga, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Panel).clickable { onClick() }.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Cover(manga, Modifier.width(104.dp).height(142.dp), true)
        Column(Modifier.weight(1f).align(Alignment.CenterVertically), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(manga.tag, color = Accent, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text(manga.title, color = Paper, fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 21.sp)
            Text(manga.chapter, color = Muted, fontSize = 12.sp)
            LinearProgressIndicator(progress = { manga.progress }, modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(5.dp)), color = Accent, trackColor = Panel2)
            Text("${(manga.progress * 100).toInt()}% completed", color = Muted, fontSize = 10.sp)
            Text("CONTINUE READING  →", color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MangaRow(manga: Manga, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(Panel).clickable { onClick() }.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Cover(manga, Modifier.size(width = 58.dp, height = 76.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(manga.title, color = Paper, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(manga.chapter, color = Muted, fontSize = 11.sp)
            LinearProgressIndicator(progress = { manga.progress }, modifier = Modifier.fillMaxWidth().height(3.dp), color = Accent, trackColor = Panel2)
        }
        Icon(Icons.Filled.ChevronRight, null, tint = Muted)
    }
}

@Composable
fun DiscoverScreen(query: String, onQuery: (String) -> Unit, onOpen: (Manga) -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("Discover", color = Paper, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = query, onValueChange = onQuery, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search manga, manhwa, manhua…", color = Muted) },
            leadingIcon = { Icon(Icons.Filled.Search, null, tint = Muted) },
            singleLine = true, shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent, unfocusedBorderColor = Panel2,
                focusedTextColor = Paper, unfocusedTextColor = Paper,
                cursorColor = Accent
            )
        )
        Text("TRENDING IN YOUR SOURCES", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        library.filter { query.isBlank() || it.title.contains(query, true) }.forEach { MangaRow(it, onClick = { onOpen(it) }) }
        if (query.isNotBlank() && library.none { it.title.contains(query, true) }) {
            Text("No local demo results. Source search will be connected in the next build.", color = Muted, fontSize = 13.sp)
        }
    }
}

@Composable
fun CloudScreen() {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Cloud providers", color = Paper, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text("Connect your personal cloud account to browse supported files.", color = Muted, fontSize = 13.sp)
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Panel).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(Panel2), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Cloud, null, tint = Accent)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Premiumize", color = Paper, fontWeight = FontWeight.Bold)
                Text("Personal cloud provider", color = Muted, fontSize = 11.sp)
            }
            Surface(color = Color(0xFF303B2A), shape = RoundedCornerShape(20.dp)) {
                Text("NOT CONNECTED", Modifier.padding(horizontal = 9.dp, vertical = 6.dp), color = Accent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text("Provider connection will be enabled after secure credential storage and API testing are implemented.", color = Muted, fontSize = 12.sp)
        OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) { Text("Connect Premiumize · Coming next") }
    }
}

@Composable
fun SettingsScreen() {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Settings", color = Paper, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        SettingRow(Icons.Filled.Palette, "Appearance", "Dark theme")
        SettingRow(Icons.Filled.Download, "Downloads", "On-device storage")
        SettingRow(Icons.Filled.Sync, "Reading sync", "Local for now")
        SettingRow(Icons.Filled.Security, "Privacy", "Personal-use mode")
        SettingRow(Icons.Filled.Info, "About MangaBridge", "Version 0.1.0 · Preview")
    }
}

@Composable
fun SettingRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(Panel).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Accent)
        Spacer(Modifier.width(13.dp))
        Column {
            Text(title, color = Paper, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
fun ReaderScreen(manga: Manga, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFF050608))) {
        Row(Modifier.fillMaxWidth().background(Panel).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.ArrowBack, "Back", tint = Paper, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(manga.title, color = Paper, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                Text(manga.chapter, color = Muted, fontSize = 10.sp)
            }
            Icon(Icons.Filled.Tune, "Reader settings", tint = Muted)
        }
        Box(Modifier.weight(1f).fillMaxWidth().padding(18.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF171D27)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Filled.MenuBook, null, tint = Accent, modifier = Modifier.size(48.dp))
                Text("READER PREVIEW", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text("Chapter pages will appear here once a source or cloud archive is connected.", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }
        Row(Modifier.fillMaxWidth().background(Panel).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("‹ PREVIOUS", color = Muted, fontSize = 11.sp)
            Text("PAGE 1 / —", color = Paper, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("NEXT ›", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
