package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SqlMonospace
import com.example.ui.theme.TextMuted

data class CommandAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val action: () -> Unit
)

@Composable
fun CommandPaletteDialog(
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }

    val allCommands = listOf(
        CommandAction("SQL Playground IDE", "Execute live SQL queries on in-memory database", Icons.Default.Terminal) {
            onNavigate("playground")
            onDismiss()
        },
        CommandAction("Explore Database Lab", "Inspect tables, data rows & schemas", Icons.Default.Storage) {
            onNavigate("databaselab")
            onDismiss()
        },
        CommandAction("JOIN Visualizer Lab", "Visualize INNER, LEFT, CROSS, & SELF joins", Icons.Default.Hub) {
            onNavigate("visuallabs")
            onDismiss()
        },
        CommandAction("39 Levels Roadmap", "Full SQL curriculum from beginner to architect", Icons.Default.School) {
            onNavigate("learn")
            onDismiss()
        },
        CommandAction("Coding Challenges", "Solve problems and earn points", Icons.Default.Code) {
            onNavigate("practice")
            onDismiss()
        },
        CommandAction("Interview Questions", "FAANG & top tech SQL questions & answers", Icons.Default.Psychology) {
            onNavigate("interview")
            onDismiss()
        },
        CommandAction("Ask AI SQL Tutor", "Query explanations and error debugging", Icons.Default.AutoAwesome) {
            onNavigate("aitutor")
            onDismiss()
        },
        CommandAction("View Certificate", "Certificate of SQL achievement", Icons.Default.CardMembership) {
            onNavigate("profile")
            onDismiss()
        },
        CommandAction("Connect with Awiskar Acharya", "Message the creator on LinkedIn", Icons.Default.Person) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/awiskaracharya/"))
            context.startActivity(intent)
            onDismiss()
        }
    )

    val filtered = allCommands.filter {
        it.title.contains(query, ignoreCase = true) || it.subtitle.contains(query, ignoreCase = true)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Navy900),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search command, table, or lab...", fontSize = 12.sp, color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyanAccent) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = Navy700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered) { cmd ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Navy800)
                                .clickable { cmd.action() }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(cmd.icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(cmd.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(cmd.subtitle, fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}
