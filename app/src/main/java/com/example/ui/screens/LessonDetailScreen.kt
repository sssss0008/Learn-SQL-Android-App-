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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.engine.SqlExecutionEngine
import com.example.data.model.Lesson
import com.example.data.model.SqlExecutionResult
import com.example.ui.components.QueryFlowVisualization
import com.example.ui.components.SqlCodeEditor
import com.example.ui.components.SqlResultGrid
import com.example.ui.theme.BlueGlow
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SqlError
import com.example.ui.theme.SqlMonospace
import com.example.ui.theme.SqlSuccess
import com.example.ui.theme.SqlWarning
import com.example.ui.theme.TextMuted

@Composable
fun LessonDetailScreen(
    lesson: Lesson,
    sqlEngine: SqlExecutionEngine,
    isCompleted: Boolean,
    onBack: () -> Unit,
    onMarkCompleted: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var queryText by remember(lesson.id) { mutableStateOf(lesson.defaultQuery) }
    var result by remember(lesson.id) { mutableStateOf<SqlExecutionResult?>(null) }
    var showPracticeSolution by remember { mutableStateOf(false) }
    var showInterviewAnswer by remember { mutableStateOf(false) }

    LaunchedEffect(lesson.id) {
        // Switch to appropriate lesson database & execute default query once
        sqlEngine.switchDatabase(lesson.defaultDatabase)
        result = sqlEngine.executeQuery(lesson.defaultQuery)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp).testTag("lesson_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LEVEL ${lesson.levelNumber} • ${lesson.moduleName.uppercase()}",
                    fontSize = 10.sp,
                    fontFamily = SqlMonospace,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
                Text(
                    text = lesson.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if (isCompleted) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF064E3B)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Done",
                            tint = SqlSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Done", fontSize = 11.sp, color = SqlSuccess, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Definition ("What is it?")
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Navy800),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "What is it?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lesson.whatIsIt,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // 2. Real-World Importance ("Why is it useful?")
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Navy900),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SqlWarning,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Why is it useful?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SqlWarning
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lesson.whyUseful,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Real-World: ${lesson.realWorldExample}",
                            fontSize = 12.sp,
                            color = TextMuted,
                            fontFamily = SqlMonospace
                        )
                    }
                }
            }

            // 3. Visual Representation
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Navy800),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BlueGlow.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Visual Architecture",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlueGlow
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Navy900,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = lesson.visualExplanation,
                                fontFamily = SqlMonospace,
                                fontSize = 12.sp,
                                color = CyanAccent,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            // 4. Interactive SQL Playground Runner
            item {
                Text(
                    text = "Interactive Query Execution",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                SqlCodeEditor(
                    query = queryText,
                    onQueryChange = { queryText = it },
                    onRun = {
                        result = sqlEngine.executeQuery(queryText)
                    },
                    onFormat = {
                        queryText = sqlEngine.formatSql(queryText)
                    },
                    onExplain = {
                        val plan = sqlEngine.explainQueryLogically(queryText)
                        result = result?.copy(queryPlan = plan.map { "${it.title}: ${it.description}" })
                    },
                    onClear = { queryText = "" }
                )
            }

            // 5. Result Table
            item {
                Text(
                    text = "Live Execution Result",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                SqlResultGrid(result = result)
            }

            // 6. Query Execution Flow Simulation
            item {
                val flowSteps = sqlEngine.explainQueryLogically(queryText)
                QueryFlowVisualization(steps = flowSteps)
            }

            // 7. Common Mistakes
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF261014)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SqlError.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Mistakes",
                                tint = SqlError,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Common Pitfalls & Mistakes",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SqlError
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        lesson.commonMistakes.forEach { mistake ->
                            Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                Text("⚠️ ", fontSize = 11.sp)
                                Text(
                                    text = mistake,
                                    fontSize = 12.sp,
                                    color = Color(0xFFFCA5A5),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // 8. Practice Challenge
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Navy800),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hands-on Practice Challenge",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lesson.practiceQuestion,
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showPracticeSolution = !showPracticeSolution },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (showPracticeSolution) "Hide Solution" else "Reveal Solution",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                        AnimatedVisibility(visible = showPracticeSolution) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Navy900,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Text(
                                    text = lesson.practiceSolution,
                                    fontFamily = SqlMonospace,
                                    fontSize = 12.sp,
                                    color = SqlSuccess,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 9. Interview Question & Answer
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Navy900),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SQL Interview Question",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA78BFA)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lesson.interviewQuestion,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showInterviewAnswer = !showInterviewAnswer },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (showInterviewAnswer) "Hide Interview Answer" else "Show Interview Answer",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                        AnimatedVisibility(visible = showInterviewAnswer) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Navy800,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Text(
                                    text = lesson.interviewAnswer,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 10. Complete Lesson CTA Button
            item {
                Button(
                    onClick = { onMarkCompleted(lesson.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("complete_lesson_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) SqlSuccess else CyanAccent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCompleted) "Completed (+25 pts earned)" else "Mark Lesson as Completed (+25 pts)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
