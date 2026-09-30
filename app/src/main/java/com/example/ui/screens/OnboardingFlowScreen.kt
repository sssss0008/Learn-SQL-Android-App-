package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueGlow
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SqlMonospace
import com.example.ui.theme.SqlSuccess
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingFlowScreen(
    onFinishOnboarding: (name: String, experience: String, dbPref: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var selectedExperience by remember { mutableStateOf("Complete Beginner") }
    val selectedGoals = remember { mutableStateOf(setOf("Learn SQL from zero", "Data analysis", "Interview preparation")) }
    var selectedDb by remember { mutableStateOf("General SQL") }

    val experiences = listOf(
        "Complete Beginner" to "I've never used a database before.",
        "Beginner" to "I know basic SELECT and WHERE queries.",
        "Intermediate" to "I work with JOINs and simple aggregations.",
        "Advanced" to "I want mastery of CTEs, Window Functions, and Query Tuning."
    )

    val goalOptions = listOf(
        "Learn SQL from zero", "College studies (CS/BCA)", "Web development",
        "Backend development", "Data analysis", "Data engineering",
        "Database administration", "Interview preparation", "Build real projects"
    )

    val dbOptions = listOf(
        "General SQL" to "Standard ANSI SQL (Universal)",
        "PostgreSQL" to "Advanced enterprise & analytic features",
        "MySQL" to "Popular web & LAMP stack standard",
        "SQLite" to "Ultra-light embedded database engine",
        "SQL Server" to "Microsoft enterprise T-SQL dialect"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF090E17),
                        Color(0xFF0F172A),
                        Color(0xFF070D18)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Progress dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { idx ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (idx == step) 22.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (idx == step) CyanAccent else Navy700)
                    )
                }
            }

            // Step Content
            AnimatedContent(targetState = step, label = "OnboardingStep") { currentStep ->
                when (currentStep) {
                    0 -> {
                        // Step 0: Welcome & Hero
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Navy800,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storage,
                                        contentDescription = "Logo",
                                        tint = CyanAccent,
                                        modifier = Modifier.size(54.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Learn SQL",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Text(
                                text = "Query. Analyze. Build. Master SQL.",
                                fontSize = 14.sp,
                                fontFamily = SqlMonospace,
                                color = CyanAccent,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Master databases visually. Write real SQL queries, inspect tables, visualize query execution, and design schemas through interactive laboratories.",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF064E3B).copy(alpha = 0.4f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SqlSuccess.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Free",
                                        tint = SqlSuccess,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "100% Free • No paywalls • No subscriptions",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SqlSuccess
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Created by Awiskar Acharya",
                                fontSize = 12.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    1 -> {
                        // Step 1: Name Input
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "What should we call you?",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Your name will appear on your SQL progress and certificate of completion.",
                                fontSize = 13.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                placeholder = { Text("Enter your name", color = Color.Gray) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("name_input_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = Navy700,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (name.isNotBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Navy800
                                ) {
                                    Text(
                                        text = "Welcome, ${name.trim()}! Ready to think in data.",
                                        fontSize = 13.sp,
                                        color = CyanAccent,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // Step 2: Experience Level
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "What's your SQL experience?",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "We'll tailor your interactive learning path accordingly.",
                                fontSize = 13.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            experiences.forEach { (exp, desc) ->
                                val isSelected = selectedExperience == exp
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { selectedExperience = exp }
                                        .border(
                                            1.dp,
                                            if (isSelected) CyanAccent else Navy700,
                                            RoundedCornerShape(12.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Navy800 else Navy900
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) CyanAccent else Color.Transparent)
                                                .border(2.dp, if (isSelected) CyanAccent else Color.Gray, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(Navy900)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = exp,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
                                            )
                                            Text(
                                                text = desc,
                                                fontSize = 12.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // Step 3: Goals & Database Preference
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Select your learning goals",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Select all that apply to you.",
                                fontSize = 13.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                goalOptions.forEach { goal ->
                                    val isSelected = selectedGoals.value.contains(goal)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) BluePrimary else Navy800,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) CyanAccent else Navy700
                                        ),
                                        modifier = Modifier.clickable {
                                            val current = selectedGoals.value.toMutableSet()
                                            if (isSelected) current.remove(goal) else current.add(goal)
                                            selectedGoals.value = current
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = goal,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Database technology interest",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            dbOptions.take(3).forEach { (db, desc) ->
                                val isSelected = selectedDb == db
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { selectedDb = db },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Navy800 else Navy900),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanAccent else Navy700)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = db,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) CyanAccent else Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "• $desc",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }

                    4 -> {
                        // Step 4: Personalized Roadmap Preview
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Your Personalized SQL Roadmap",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "From Fundamentals to Database Architecture & Interview Mastery",
                                fontSize = 13.sp,
                                color = CyanAccent,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            val roadmapStages = listOf(
                                "Database Fundamentals & Schema Architecture",
                                "Core SELECT, Filtering & Sorting",
                                "Data Aggregation, GROUP BY & HAVING",
                                "Relational JOINs & Subqueries",
                                "Window Functions & Common Table Expressions",
                                "Query Optimization & ER Diagram Design",
                                "Real-World Projects & Interview Mastery"
                            )

                            roadmapStages.forEachIndexed { idx, stage ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Navy800)
                                            .border(1.dp, CyanAccent, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${idx + 1}",
                                            fontSize = 11.sp,
                                            fontFamily = SqlMonospace,
                                            color = CyanAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = stage,
                                        fontSize = 13.sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Button
            Button(
                onClick = {
                    if (step < 4) {
                        step++
                    } else {
                        onFinishOnboarding(
                            name.ifBlank { "SQL Developer" },
                            selectedExperience,
                            selectedDb
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_continue_button"),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (step == 0) "Start Learning" else if (step == 4) "Start My Journey" else "Continue",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
