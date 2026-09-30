package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SqlError
import com.example.ui.theme.SqlMonospace
import com.example.ui.theme.SqlSuccess
import com.example.ui.theme.TextMuted

data class ChatMessage(
    val isUser: Boolean,
    val text: String,
    val codeSnippet: String? = null
)

@Composable
fun AiTutorScreen(
    sqlEngine: SqlExecutionEngine,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: AI Tutor Chat, 1: Query Explainer, 2: Query Debugger

    var userMessage by remember { mutableStateOf("") }
    val chatHistory = remember {
        mutableStateListOf(
            ChatMessage(
                isUser = false,
                text = "Hello! I am your AI SQL Tutor. Ask me any database concept, query doubt, or request an explanation of complex topics like JOINs, CTEs, Window Functions, or Normalization."
            )
        )
    }

    // Explainer state
    var queryToExplain by remember {
        mutableStateOf("SELECT students.name, AVG(enrollments.grade) FROM students JOIN enrollments ON students.id = enrollments.student_id GROUP BY students.id;")
    }
    var explanationResult by remember { mutableStateOf<String?>(null) }

    // Debugger state
    var brokenQuery by remember {
        mutableStateOf("SELECT name, age FROM students WHERE age > 20 GROUP BY name HAVING age > 22;")
    }
    var debugResult by remember { mutableStateOf<String?>(null) }

    val quickQuestions = listOf(
        "Explain JOINs simply",
        "Why is GROUP BY failing?",
        "How do Indexes work?",
        "Explain Window Functions",
        "How to avoid slow queries?"
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
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "AI SQL Tutor & Debugger",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Instant conceptual guidance, query analysis & error fixes",
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
            listOf("Ask AI Tutor", "Query Explainer", "Query Debugger").forEachIndexed { idx, label ->
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
                // Chat Mode
                Column(modifier = Modifier.fillMaxSize()) {
                    // Quick Prompts
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        items(quickQuestions) { q ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Navy800,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                                modifier = Modifier.clickable {
                                    handleSendChat(q, chatHistory)
                                }
                            ) {
                                Text(
                                    text = q,
                                    fontSize = 11.sp,
                                    color = CyanAccent,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Chat messages list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(chatHistory) { msg ->
                            val bg = if (msg.isUser) BluePrimary.copy(alpha = 0.3f) else Navy800
                            val align = if (msg.isUser) Alignment.End else Alignment.Start
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = align
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = bg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (msg.isUser) BluePrimary else Navy700),
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = if (msg.isUser) "You" else "AI Tutor",
                                            fontSize = 10.sp,
                                            fontFamily = SqlMonospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (msg.isUser) CyanAccent else Color(0xFFA78BFA)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.text,
                                            fontSize = 13.sp,
                                            color = Color.White.copy(alpha = 0.95f),
                                            lineHeight = 18.sp
                                        )
                                        if (msg.codeSnippet != null) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Navy900,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = msg.codeSnippet,
                                                    fontFamily = SqlMonospace,
                                                    fontSize = 11.sp,
                                                    color = CyanAccent,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Input Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userMessage,
                            onValueChange = { userMessage = it },
                            placeholder = { Text("Ask about SQL concepts, syntax, or errors...", fontSize = 12.sp, color = Color.Gray) },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = Navy700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (userMessage.isNotBlank()) {
                                    handleSendChat(userMessage, chatHistory)
                                    userMessage = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BluePrimary)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }

            1 -> {
                // Query Explainer
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("Paste Any SQL Query to Explain:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = queryToExplain,
                            onValueChange = { queryToExplain = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = SqlMonospace, fontSize = 12.sp, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = Navy700)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val steps = sqlEngine.explainQueryLogically(queryToExplain)
                                explanationResult = steps.joinToString("\n\n") { "• Step ${it.stepNumber}: ${it.title}\n  ${it.description} (${it.visualDetail})" }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Explain Query Flow", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    if (explanationResult != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Navy800),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Structured Query Breakdown:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = explanationResult!!,
                                        fontSize = 12.sp,
                                        fontFamily = SqlMonospace,
                                        color = Color.White.copy(alpha = 0.9f),
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Query Debugger
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("Paste Broken or Failing SQL Query:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = brokenQuery,
                            onValueChange = { brokenQuery = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = SqlMonospace, fontSize = 12.sp, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SqlError, unfocusedBorderColor = Navy700)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val res = sqlEngine.executeQuery(brokenQuery)
                                debugResult = if (!res.isSuccess) {
                                    "Error Detected:\n${res.errorMessage}\n\nLine: ${res.errorLine}\nSuggestion: ${res.errorSuggestion}\n\nRecommended Fix:\nEnsure aggregate conditions are placed in HAVING, and individual row filters remain in WHERE. Check table schema for valid column names."
                                } else {
                                    "Query actually parsed successfully in SQLite! Returned ${res.rowsCount} rows. If you expected different data, verify the JOIN condition or WHERE predicate."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SqlError),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Diagnose & Fix Query", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    if (debugResult != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Navy800),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SqlError.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("AI Diagnostic Report:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SqlError)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = debugResult!!,
                                        fontSize = 12.sp,
                                        fontFamily = SqlMonospace,
                                        color = Color.White.copy(alpha = 0.9f),
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun handleSendChat(question: String, history: MutableList<ChatMessage>) {
    history.add(ChatMessage(isUser = true, text = question))
    val lower = question.lowercase()

    val (answer, code) = when {
        lower.contains("join") -> Pair(
            "A JOIN combines rows from two or more tables based on a related column between them. INNER JOIN retains only matching pairs. LEFT JOIN keeps all rows from the left table and fills unmatched right columns with NULL.",
            "SELECT students.name, courses.title\nFROM enrollments\nJOIN students ON enrollments.student_id = students.id\nJOIN courses ON enrollments.course_id = courses.id;"
        )
        lower.contains("group by") -> Pair(
            "GROUP BY groups rows with identical values in specified columns into summary rows. Remember the Single-Value Rule: any column in SELECT must either be in the GROUP BY clause or wrapped in an aggregate function (COUNT, SUM, AVG, MIN, MAX).",
            "SELECT major, COUNT(*) AS student_count, AVG(gpa) AS avg_gpa\nFROM students\nGROUP BY major\nHAVING student_count >= 2;"
        )
        lower.contains("index") -> Pair(
            "An index is a balanced B-Tree data structure mapping indexed values to physical row locations. It transforms full table scans (O(N)) into fast lookups (O(log N)), at the trade-off of slowing down INSERT/UPDATE writes.",
            "CREATE INDEX idx_students_major ON students(major);\nEXPLAIN QUERY PLAN SELECT * FROM students WHERE major = 'Computer Science';"
        )
        lower.contains("window") -> Pair(
            "Window functions perform calculations across a frame of rows without collapsing them into a single row like GROUP BY. Use OVER (PARTITION BY ... ORDER BY ...).",
            "SELECT name, salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rank\nFROM instructors;"
        )
        lower.contains("slow") || lower.contains("optimize") -> Pair(
            "To optimize slow queries: 1) Run EXPLAIN QUERY PLAN to check for table scans. 2) Create indexes on columns used in WHERE, JOIN, and ORDER BY. 3) Avoid SELECT *; project only needed columns. 4) Avoid functions on indexed columns in WHERE clauses.",
            "EXPLAIN QUERY PLAN SELECT name FROM customers WHERE email = 'bruce@wayne.com';"
        )
        else -> Pair(
            "Great SQL question! In relational databases, think declaratively about sets and relations. Tell the engine WHAT data you want through projections, predicates, and joins, rather than writing procedural loops.",
            "SELECT name, gpa FROM students WHERE gpa >= 3.8 ORDER BY gpa DESC;"
        )
    }

    history.add(ChatMessage(isUser = false, text = answer, codeSnippet = code))
}
