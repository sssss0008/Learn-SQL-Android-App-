package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SqlCurriculumRepository
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SqlMonospace
import com.example.ui.theme.SqlSuccess
import com.example.ui.theme.SqlWarning
import com.example.ui.theme.TextMuted

@Composable
fun ProfileProgressScreen(
    userName: String,
    completedLessons: Set<Int>,
    solvedChallenges: Set<String>,
    streak: Int,
    points: Int,
    certificateIssued: Boolean,
    certificateId: String,
    certificateDate: String,
    currentTheme: AppThemeMode,
    onSetTheme: (AppThemeMode) -> Unit,
    onIssueCertificate: () -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Analytics & Skills, 1: Certificate, 2: Creator & Settings

    val totalLessons = SqlCurriculumRepository.allLessons.size
    val totalChallenges = SqlCurriculumRepository.allChallenges.size
    val completionPercent = if (totalLessons > 0) (completedLessons.size.toFloat() / totalLessons * 100).toInt() else 0

    val skillCategories = listOf(
        "Fundamentals & Schema" to minOf(1.0f, completedLessons.count { it in 1..7 } / 7f),
        "Core SELECT & Filter" to minOf(1.0f, completedLessons.count { it in 8..12 } / 5f),
        "Data Mutations (DML)" to minOf(1.0f, completedLessons.count { it in 13..15 } / 3f),
        "Aggregation & GROUP BY" to minOf(1.0f, completedLessons.count { it in 16..18 } / 3f),
        "Relational JOINs" to minOf(1.0f, completedLessons.count { it in 19..19 } / 1f),
        "Subqueries & Functions" to minOf(1.0f, completedLessons.count { it in 20..25 } / 6f),
        "Constraints & Indexes" to minOf(1.0f, completedLessons.count { it in 26..28 } / 3f),
        "Transactions & Windows" to minOf(1.0f, completedLessons.count { it in 29..31 } / 3f),
        "Database Architecture" to minOf(1.0f, completedLessons.count { it in 32..39 } / 8f)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = BluePrimary,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = userName.take(1).uppercase().ifBlank { "S" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = userName.ifBlank { "SQL Developer" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "$completionPercent% Course Completion • $points pts earned",
                    fontSize = 11.sp,
                    color = CyanAccent,
                    fontFamily = SqlMonospace
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = CyanAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyanAccent
                )
            },
            divider = { Divider(color = DarkSurfaceBorder) }
        ) {
            listOf("Skill Analytics", "Certificate", "Creator & Settings").forEachIndexed { idx, label ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == idx) CyanAccent else TextMuted
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            0 -> {
                // Analytics & Skills Radar
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Stat badges
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Navy800),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${completedLessons.size}/$totalLessons", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CyanAccent, fontFamily = SqlMonospace)
                                    Text("Lessons", fontSize = 11.sp, color = TextMuted)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Navy800),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${solvedChallenges.size}/$totalChallenges", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SqlWarning, fontFamily = SqlMonospace)
                                    Text("Challenges", fontSize = 11.sp, color = TextMuted)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Navy800),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("$streak d", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SqlSuccess, fontFamily = SqlMonospace)
                                    Text("Streak", fontSize = 11.sp, color = TextMuted)
                                }
                            }
                        }
                    }

                    item {
                        Text("SQL Skill Mastery Breakdown", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    items(skillCategories) { (skillName, progressVal) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Navy900),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = skillName, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                    Text(text = "${(progressVal * 100).toInt()}%", fontSize = 11.sp, fontFamily = SqlMonospace, color = CyanAccent)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { progressVal },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = if (progressVal >= 1f) SqlSuccess else CyanAccent,
                                    trackColor = Navy700
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            1 -> {
                // Official Certificate
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, CyanAccent.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF071120))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Certificate",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(44.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "CERTIFICATE OF ACHIEVEMENT",
                                    fontSize = 12.sp,
                                    fontFamily = SqlMonospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "This certifies that",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )

                                Text(
                                    text = userName.ifBlank { "SQL Developer" },
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Text(
                                    text = "has successfully mastered the complete curriculum of",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Learn SQL",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Text(
                                    text = "“Query. Analyze. Build. Master SQL.”",
                                    fontSize = 11.sp,
                                    fontFamily = SqlMonospace,
                                    color = Color(0xFFA78BFA)
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                Divider(color = DarkSurfaceBorder)
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("COURSE CREATOR", fontSize = 9.sp, fontFamily = SqlMonospace, color = TextMuted)
                                        Text("Awiskar Acharya", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("ISSUE DATE", fontSize = 9.sp, fontFamily = SqlMonospace, color = TextMuted)
                                        Text(certificateDate, fontSize = 12.sp, fontFamily = SqlMonospace, color = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Verification ID: $certificateId",
                                    fontSize = 10.sp,
                                    fontFamily = SqlMonospace,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = onIssueCertificate,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (certificateIssued) "Certificate Verified & Saved" else "Issue & Validate Certificate",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/awiskaracharya/"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B5)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Share Certificate with Awiskar Acharya", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            2 -> {
                // Creator Profile & Settings
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Creator Bio Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Navy800),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = BluePrimary,
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("AA", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Awiskar Acharya", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Creator of Learn SQL", fontSize = 12.sp, color = CyanAccent, fontFamily = SqlMonospace)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "“Don't just read SQL. Build databases, write queries, visualize how queries execute, inspect tables, understand relationships, debug errors, optimize queries, and solve real database problems.”",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    lineHeight = 17.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Everything in this application is 100% free forever for students, developers, and data engineers worldwide. No subscriptions or hidden fees.",
                                    fontSize = 12.sp,
                                    color = SqlSuccess,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/awiskaracharya/"))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B5)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("linkedin_profile_cta")
                                ) {
                                    Text("Connect on LinkedIn", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    // Theme selector
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Navy800),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Display Theme Mode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AppThemeMode.entries.forEach { mode ->
                                        val isSel = currentTheme == mode
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSel) CyanAccent else Navy700,
                                            modifier = Modifier.clickable { onSetTheme(mode) }
                                        ) {
                                            Text(
                                                text = mode.name.replace("_", " "),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) Color.Black else Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Reset Progress
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Navy900),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Data Management", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = onResetProgress,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF450A0A)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Restore, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reset Learning Progress", color = Color(0xFFFCA5A5), fontSize = 12.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
