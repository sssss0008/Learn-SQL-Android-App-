package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InterviewItem
import com.example.data.repository.SqlCurriculumRepository
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SqlMonospace
import com.example.ui.theme.SqlSuccess
import com.example.ui.theme.SqlWarning
import com.example.ui.theme.TextMuted

@Composable
fun InterviewScreen(
    modifier: Modifier = Modifier
) {
    var selectedQuestion by remember { mutableStateOf<InterviewItem?>(null) }
    val questions = SqlCurriculumRepository.allInterviews

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
            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "SQL Technical Interview Center",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "High-frequency technical questions, edge cases & query solutions",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedQuestion != null) {
            InterviewDetailView(
                item = selectedQuestion!!,
                onBack = { selectedQuestion = null }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(questions) { q ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedQuestion = q }
                            .border(1.dp, Navy700, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Navy800)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Navy700),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Q",
                                    fontFamily = SqlMonospace,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Navy900
                                    ) {
                                        Text(
                                            text = q.difficulty.uppercase(),
                                            fontSize = 9.sp,
                                            fontFamily = SqlMonospace,
                                            fontWeight = FontWeight.Bold,
                                            color = when (q.difficulty) {
                                                "Beginner" -> SqlSuccess
                                                "Intermediate" -> SqlWarning
                                                else -> Color(0xFFA78BFA)
                                            },
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "• ${q.topic}", fontSize = 11.sp, color = TextMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = q.question,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun InterviewDetailView(
    item: InterviewItem,
    onBack: () -> Unit
) {
    var showHint by remember { mutableStateOf(false) }
    var showAnswer by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("← All Questions", fontSize = 12.sp, color = Color.White)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Navy900
                    ) {
                        Text(
                            text = "${item.difficulty.uppercase()} • ${item.topic.uppercase()}",
                            fontSize = 10.sp,
                            fontFamily = SqlMonospace,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.question,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp)) {
                            Text("💭 ", fontSize = 12.sp)
                            Text(
                                text = "Think Prompt: ${item.thinkPrompt}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Hints
        item {
            Button(
                onClick = { showHint = !showHint },
                colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (showHint) "Hide Interview Hints" else "Show Interview Hints", fontSize = 12.sp)
            }

            AnimatedVisibility(visible = showHint) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy900),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        item.hints.forEach { h ->
                            Text(text = "💡 $h", fontSize = 12.sp, color = CyanAccent, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }

        // Answer & SQL Example
        item {
            Button(
                onClick = { showAnswer = !showAnswer },
                colors = ButtonDefaults.buttonColors(containerColor = if (showAnswer) Navy700 else BluePrimary),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (showAnswer) "Hide Full Solution" else "Reveal Complete Answer & Code", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            AnimatedVisibility(visible = showAnswer) {
                Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Navy800),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Verified Model Answer", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SqlSuccess)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = item.answer, fontSize = 13.sp, color = Color.White.copy(alpha = 0.95f), lineHeight = 18.sp)
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Navy900),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("SQL Implementation:", fontSize = 11.sp, fontFamily = SqlMonospace, color = CyanAccent)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.sqlExample, fontFamily = SqlMonospace, fontSize = 12.sp, color = Color.White)
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Navy800),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Interviewer Follow-Up:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SqlWarning)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.followUp, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                        }
                    }
                }
            }
        }
    }
}
