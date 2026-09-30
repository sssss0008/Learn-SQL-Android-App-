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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.SqlChallenge
import com.example.data.model.SqlExecutionResult
import com.example.data.repository.SqlCurriculumRepository
import com.example.ui.components.SqlCodeEditor
import com.example.ui.components.SqlResultGrid
import com.example.ui.theme.BluePrimary
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
fun PracticeScreen(
    sqlEngine: SqlExecutionEngine,
    solvedChallenges: Set<String>,
    onSolveChallenge: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Challenges, 1: Clause Ordering Puzzle, 2: Output Quiz
    var activeChallenge by remember { mutableStateOf<SqlChallenge?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Practice & Challenge Center",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Coding challenges, quizzes & query puzzles",
                    fontSize = 11.sp,
                    color = TextMuted
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
            listOf("Coding Challenges", "Clause Ordering", "Output Quiz").forEachIndexed { idx, label ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = {
                        selectedTab = idx
                        activeChallenge = null
                    },
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

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> {
                if (activeChallenge != null) {
                    ChallengeDetailView(
                        challenge = activeChallenge!!,
                        sqlEngine = sqlEngine,
                        isSolved = solvedChallenges.contains(activeChallenge!!.id),
                        onBack = { activeChallenge = null },
                        onSuccess = { onSolveChallenge(activeChallenge!!.id, activeChallenge!!.points) }
                    )
                } else {
                    ChallengeListView(
                        challenges = SqlCurriculumRepository.allChallenges,
                        solvedChallenges = solvedChallenges,
                        onSelectChallenge = { activeChallenge = it }
                    )
                }
            }
            1 -> ClauseOrderingPuzzle()
            2 -> OutputPredictionQuiz()
        }
    }
}

@Composable
fun ChallengeListView(
    challenges: List<SqlChallenge>,
    solvedChallenges: Set<String>,
    onSelectChallenge: (SqlChallenge) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(challenges) { challenge ->
            val isSolved = solvedChallenges.contains(challenge.id)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectChallenge(challenge) }
                    .border(1.dp, if (isSolved) SqlSuccess.copy(alpha = 0.5f) else Navy700, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Navy800)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isSolved) Color(0xFF064E3B) else Navy700),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSolved) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Solved", tint = SqlSuccess, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Code, contentDescription = "Code", tint = CyanAccent, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Navy900
                            ) {
                                Text(
                                    text = challenge.difficulty.uppercase(),
                                    fontSize = 9.sp,
                                    fontFamily = SqlMonospace,
                                    fontWeight = FontWeight.Bold,
                                    color = when (challenge.difficulty) {
                                        "Beginner" -> SqlSuccess
                                        "Intermediate" -> SqlWarning
                                        else -> Color(0xFFA78BFA)
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${challenge.topic}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = challenge.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Navy700
                    ) {
                        Text(
                            text = "+${challenge.points} pts",
                            fontSize = 11.sp,
                            fontFamily = SqlMonospace,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ChallengeDetailView(
    challenge: SqlChallenge,
    sqlEngine: SqlExecutionEngine,
    isSolved: Boolean,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var userSql by remember(challenge.id) { mutableStateOf(challenge.starterSql) }
    var result by remember(challenge.id) { mutableStateOf<SqlExecutionResult?>(null) }
    var hintIndex by remember { mutableIntStateOf(0) }
    var solvedState by remember { mutableStateOf(isSolved) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("← All Challenges", fontSize = 12.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                if (solvedState) {
                    Text("✅ Solved", color = SqlSuccess, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = challenge.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = challenge.prompt, fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        item {
            SqlCodeEditor(
                query = userSql,
                onQueryChange = { userSql = it },
                onRun = {
                    sqlEngine.switchDatabase(challenge.databaseId)
                    val res = sqlEngine.executeQuery(userSql)
                    result = res
                    if (res.isSuccess && res.rows.isNotEmpty()) {
                        solvedState = true
                        onSuccess()
                    }
                },
                onFormat = { userSql = sqlEngine.formatSql(userSql) },
                onExplain = {},
                onClear = { userSql = "" }
            )
        }

        item {
            SqlResultGrid(result = result)
        }

        // Progressive Hints
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Progressive Hints", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                        if (hintIndex < challenge.hints.size) {
                            Button(
                                onClick = { hintIndex++ },
                                colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Show Hint ${hintIndex + 1}", fontSize = 11.sp)
                            }
                        }
                    }

                    if (hintIndex > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        for (i in 0 until hintIndex) {
                            Text(
                                text = "Hint ${i + 1}: ${challenge.hints[i]}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ClauseOrderingPuzzle() {
    val initialClauses = listOf("WHERE", "SELECT", "ORDER BY", "FROM", "LIMIT", "GROUP BY", "HAVING")
    val correctOrder = listOf("SELECT", "FROM", "WHERE", "GROUP BY", "HAVING", "ORDER BY", "LIMIT")

    val clausesList = remember { mutableStateListOf(*initialClauses.toTypedArray()) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "SQL Clause Ordering Puzzle",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Arrange SQL clauses in their correct written syntax order using the up/down arrows.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        clausesList.forEachIndexed { index, clause ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${index + 1}. $clause",
                        fontFamily = SqlMonospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row {
                        IconButton(
                            onClick = {
                                if (index > 0) {
                                    val temp = clausesList[index]
                                    clausesList[index] = clausesList[index - 1]
                                    clausesList[index - 1] = temp
                                    isCorrect = null
                                }
                            },
                            enabled = index > 0,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up", tint = Color.White)
                        }

                        IconButton(
                            onClick = {
                                if (index < clausesList.size - 1) {
                                    val temp = clausesList[index]
                                    clausesList[index] = clausesList[index + 1]
                                    clausesList[index + 1] = temp
                                    isCorrect = null
                                }
                            },
                            enabled = index < clausesList.size - 1,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down", tint = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                isCorrect = clausesList.toList() == correctOrder
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Verify Syntax Order", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        if (isCorrect != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isCorrect == true) Color(0xFF064E3B) else Color(0xFF450A0A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isCorrect == true)
                        "🎉 Correct! The written order is SELECT → FROM → WHERE → GROUP BY → HAVING → ORDER BY → LIMIT.\nNote: Logical query processing starts with FROM, then WHERE, GROUP BY, HAVING, SELECT, and ORDER BY!"
                    else
                        "❌ Not quite right yet. Written query sequence begins with SELECT, followed by FROM, WHERE, GROUP BY, HAVING, ORDER BY, LIMIT.",
                    fontSize = 12.sp,
                    color = if (isCorrect == true) SqlSuccess else Color(0xFFFCA5A5),
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun OutputPredictionQuiz() {
    var selectedOption by remember { mutableIntStateOf(-1) }
    var submitted by remember { mutableStateOf(false) }

    val options = listOf(
        "A) 1 row (Alex Rivers, 3.85)",
        "B) 2 rows (Alex Rivers, 3.85 and Maya Lin, 3.92)",
        "C) 3 rows",
        "D) 0 rows"
    )
    val correctIndex = 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Output Prediction Challenge", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Analyze this query mentally. What will it return?", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Navy900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "SELECT name, gpa\nFROM students\nWHERE major = 'Computer Science'\n  AND gpa > 3.80;",
                        fontFamily = SqlMonospace,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        options.forEachIndexed { idx, opt ->
            val isSelected = selectedOption == idx
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (!submitted) selectedOption = idx
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (submitted && idx == correctIndex)
                        Color(0xFF064E3B)
                    else if (submitted && isSelected && idx != correctIndex)
                        Color(0xFF450A0A)
                    else if (isSelected)
                        Navy700
                    else
                        Navy900
                ),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanAccent else Navy700)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = opt, fontSize = 13.sp, color = Color.White)
                }
            }
        }

        Button(
            onClick = { submitted = true },
            enabled = selectedOption != -1 && !submitted,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Submit Prediction", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        if (submitted) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Navy800,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Explanation: In the students table, Alex Rivers has GPA 3.85 and Maya Lin has 3.92 in Computer Science. Elena Gomez has 3.75, which is not > 3.80. So exactly 2 rows match!",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
