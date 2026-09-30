package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.SqlExecutionEngine
import com.example.data.model.SqlExecutionResult
import com.example.ui.components.SqlResultGrid
import com.example.ui.theme.BlueGlow
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
fun VisualLabsScreen(
    sqlEngine: SqlExecutionEngine,
    onNavigateToPlayground: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeLabTab by remember { mutableIntStateOf(0) } // 0: JOIN Visualizer, 1: Window Functions, 2: Group By & Aggregate, 3: Transactions & ACID, 4: Index & Performance, 5: Visual Query Builder

    val labNames = listOf(
        "JOIN Lab",
        "Window Lab",
        "Group By & Aggregates",
        "ACID Transactions",
        "Index & Performance",
        "Query Builder"
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
            Icon(Icons.Default.Hub, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Specialized Visual Laboratories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Simulations of relational query mechanics",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        // Labs Tabs
        ScrollableTabRow(
            selectedTabIndex = activeLabTab,
            containerColor = Color.Transparent,
            contentColor = CyanAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeLabTab]),
                    color = CyanAccent
                )
            },
            divider = { Divider(color = DarkSurfaceBorder) },
            edgePadding = 0.dp
        ) {
            labNames.forEachIndexed { idx, title ->
                Tab(
                    selected = activeLabTab == idx,
                    onClick = { activeLabTab = idx },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (activeLabTab == idx) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeLabTab == idx) CyanAccent else TextMuted
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (activeLabTab) {
            0 -> JoinVisualizerLab(sqlEngine, onNavigateToPlayground)
            1 -> WindowFunctionsLab(sqlEngine, onNavigateToPlayground)
            2 -> GroupByAggregateLab(sqlEngine, onNavigateToPlayground)
            3 -> TransactionsAcidLab(sqlEngine)
            4 -> IndexPerformanceLab(sqlEngine)
            5 -> VisualQueryBuilderLab(sqlEngine, onNavigateToPlayground)
        }
    }
}

@Composable
fun JoinVisualizerLab(
    sqlEngine: SqlExecutionEngine,
    onOpenInPlayground: (String) -> Unit
) {
    var joinType by remember { mutableStateOf("INNER JOIN") }
    var result by remember { mutableStateOf<SqlExecutionResult?>(null) }

    val joinOptions = listOf("INNER JOIN", "LEFT JOIN", "CROSS JOIN")

    val currentQuery = when (joinType) {
        "INNER JOIN" -> """
            SELECT students.name, courses.title, enrollments.grade
            FROM enrollments
            INNER JOIN students ON enrollments.student_id = students.id
            INNER JOIN courses ON enrollments.course_id = courses.id
            LIMIT 10;
        """.trimIndent()
        "LEFT JOIN" -> """
            SELECT students.name, enrollments.course_id, enrollments.grade
            FROM students
            LEFT JOIN enrollments ON students.id = enrollments.student_id
            LIMIT 10;
        """.trimIndent()
        "CROSS JOIN" -> """
            SELECT students.name, departments.name AS department
            FROM students
            CROSS JOIN departments
            LIMIT 10;
        """.trimIndent()
        else -> "SELECT 1;"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Select JOIN Type to Visualize", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        joinOptions.forEach { opt ->
                            val isSel = joinType == opt
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) CyanAccent else Navy700,
                                modifier = Modifier.clickable {
                                    joinType = opt
                                    sqlEngine.switchDatabase("school")
                                    result = sqlEngine.executeQuery(currentQuery)
                                }
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val explanation = when (joinType) {
                        "INNER JOIN" -> "Returns only rows where matching keys exist in both students and enrollments."
                        "LEFT JOIN" -> "Returns all students from left table; unmatched enrollment rows are filled with NULL."
                        "CROSS JOIN" -> "Cartesian Product: pairs every student with every department (N x M rows)."
                        else -> ""
                    }
                    Text(text = explanation, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Generated SQL Query", fontSize = 11.sp, fontFamily = SqlMonospace, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = currentQuery, fontSize = 12.sp, fontFamily = SqlMonospace, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = {
                                sqlEngine.switchDatabase("school")
                                result = sqlEngine.executeQuery(currentQuery)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SqlSuccess),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Simulate & Run", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Text("Execution Output", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            SqlResultGrid(result = result)
        }
    }
}

@Composable
fun WindowFunctionsLab(
    sqlEngine: SqlExecutionEngine,
    onOpenInPlayground: (String) -> Unit
) {
    var mode by remember { mutableStateOf("Ranking") } // Ranking vs Running Total
    var result by remember { mutableStateOf<SqlExecutionResult?>(null) }

    val query = if (mode == "Ranking") {
        """
            SELECT name, department_id, salary,
                   ROW_NUMBER() OVER (PARTITION BY department_id ORDER BY salary DESC) AS row_num,
                   RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) AS rank,
                   DENSE_RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) AS dense_rank
            FROM instructors;
        """.trimIndent()
    } else {
        """
            SELECT id, order_date, total_amount,
                   SUM(total_amount) OVER (ORDER BY order_date) AS running_total,
                   AVG(total_amount) OVER (ORDER BY order_date) AS moving_avg
            FROM orders;
        """.trimIndent()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Window Analytical Functions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    Text("Calculations across partition frames without collapsing rows", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Ranking", "Running Total").forEach { opt ->
                            val isSel = mode == opt
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) CyanAccent else Navy700,
                                modifier = Modifier.clickable {
                                    mode = opt
                                    sqlEngine.switchDatabase(if (opt == "Ranking") "school" else "ecommerce")
                                    result = sqlEngine.executeQuery(query)
                                }
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = query, fontSize = 12.sp, fontFamily = SqlMonospace, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            sqlEngine.switchDatabase(if (mode == "Ranking") "school" else "ecommerce")
                            result = sqlEngine.executeQuery(query)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Calculate Window Function", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            SqlResultGrid(result = result)
        }
    }
}

@Composable
fun GroupByAggregateLab(
    sqlEngine: SqlExecutionEngine,
    onOpenInPlayground: (String) -> Unit
) {
    var selectedAgg by remember { mutableStateOf("AVG") }
    var result by remember { mutableStateOf<SqlExecutionResult?>(null) }

    val query = """
        SELECT major,
               COUNT(*) AS student_count,
               AVG(gpa) AS avg_gpa,
               MIN(gpa) AS min_gpa,
               MAX(gpa) AS max_gpa
        FROM students
        GROUP BY major
        HAVING student_count >= 2;
    """.trimIndent()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("GROUP BY & HAVING Pipeline", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1. Gathers rows by 'major' into distinct buckets.\n2. Computes COUNT, AVG, MIN, MAX.\n3. HAVING discards groups with fewer than 2 students.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            sqlEngine.switchDatabase("school")
                            result = sqlEngine.executeQuery(query)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SqlSuccess),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Run Group By Simulation", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            SqlResultGrid(result = result)
        }
    }
}

@Composable
fun TransactionsAcidLab(
    sqlEngine: SqlExecutionEngine
) {
    var account1Balance by remember { mutableStateOf(14500.50) }
    var account2Balance by remember { mutableStateOf(58200.00) }
    var transactionState by remember { mutableStateOf("IDLE") } // IDLE, IN_TRANSACTION, COMMITTED, ROLLED_BACK
    var logMessages by remember { mutableStateOf(listOf("Ready. Press BEGIN TRANSACTION to initiate fund transfer.")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("ACID Transaction Simulator", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                Text("Demonstrates Atomicity: All operations commit or roll back with zero partial writes.", fontSize = 11.sp, color = TextMuted)

                Spacer(modifier = Modifier.height(12.dp))

                // Accounts display
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Account #1001", fontSize = 11.sp, color = TextMuted)
                            Text("$${String.format("%.2f", account1Balance)}", fontSize = 16.sp, fontFamily = SqlMonospace, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Account #1002", fontSize = 11.sp, color = TextMuted)
                            Text("$${String.format("%.2f", account2Balance)}", fontSize = 16.sp, fontFamily = SqlMonospace, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    transactionState = "IN_TRANSACTION"
                    account1Balance -= 500.0
                    account2Balance += 500.0
                    logMessages = listOf(
                        "BEGIN TRANSACTION;",
                        "UPDATE accounts SET balance = balance - 500 WHERE id = 1001; [UNCOMMITTED]",
                        "UPDATE accounts SET balance = balance + 500 WHERE id = 1002; [UNCOMMITTED]",
                        "Pending COMMIT or ROLLBACK..."
                    )
                },
                enabled = transactionState == "IDLE",
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Transfer $500", fontSize = 12.sp)
            }

            Button(
                onClick = {
                    transactionState = "COMMITTED"
                    logMessages = logMessages + "COMMIT; -> Transaction written to disk page & WAL. Permanent."
                },
                enabled = transactionState == "IN_TRANSACTION",
                colors = ButtonDefaults.buttonColors(containerColor = SqlSuccess),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("COMMIT", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    transactionState = "ROLLED_BACK"
                    account1Balance += 500.0
                    account2Balance -= 500.0
                    logMessages = logMessages + "ROLLBACK; -> Aborted! Balances restored to initial state."
                },
                enabled = transactionState == "IN_TRANSACTION",
                colors = ButtonDefaults.buttonColors(containerColor = SqlError),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("ROLLBACK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Reset Button
        if (transactionState != "IDLE") {
            Button(
                onClick = {
                    account1Balance = 14500.50
                    account2Balance = 58200.00
                    transactionState = "IDLE"
                    logMessages = listOf("Reset complete. Press Transfer $500 to begin.")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset Simulator", fontSize = 12.sp)
            }
        }

        // Transaction Log
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Navy900),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Transaction Audit Log", fontSize = 11.sp, fontFamily = SqlMonospace, color = CyanAccent, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                logMessages.forEach { msg ->
                    Text(text = "• $msg", fontFamily = SqlMonospace, fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }
    }
}

@Composable
fun IndexPerformanceLab(
    sqlEngine: SqlExecutionEngine
) {
    var searchEmail by remember { mutableStateOf("bruce@wayne.com") }
    var useIndex by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Index vs Full Table Scan Visualizer", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                Text("Educational comparison of conceptual I/O work and time complexity", fontSize = 11.sp, color = TextMuted)

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (!useIndex) CyanAccent else Navy700,
                        modifier = Modifier.clickable { useIndex = false }
                    ) {
                        Text(
                            text = "Full Table Scan (No Index)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!useIndex) Color.Black else Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (useIndex) CyanAccent else Navy700,
                        modifier = Modifier.clickable { useIndex = true }
                    ) {
                        Text(
                            text = "B-Tree Index Lookup",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (useIndex) Color.Black else Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Navy900),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (useIndex) "INDEX SCAN: idx_customers_email" else "TABLE SCAN: customers",
                    fontSize = 13.sp,
                    fontFamily = SqlMonospace,
                    fontWeight = FontWeight.Bold,
                    color = if (useIndex) SqlSuccess else SqlWarning
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (useIndex)
                        "Complexity: O(log N). Traverses 3 B-Tree depth levels directly to row pointer. Rows examined: 1."
                    else
                        "Complexity: O(N). Scans every data page sequentially from start to finish. Rows examined: 1,000,000.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Navy800,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (useIndex)
                            "EXPLAIN: SEARCH customers USING INDEX idx_customers_email (email=?)"
                        else
                            "EXPLAIN: SCAN customers",
                        fontFamily = SqlMonospace,
                        fontSize = 11.sp,
                        color = CyanAccent,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VisualQueryBuilderLab(
    sqlEngine: SqlExecutionEngine,
    onOpenInPlayground: (String) -> Unit
) {
    var selectedTable by remember { mutableStateOf("students") }
    var selectedCols by remember { mutableStateOf("name, major, gpa") }
    var whereCond by remember { mutableStateOf("gpa > 3.5") }
    var orderByCol by remember { mutableStateOf("gpa DESC") }
    var limitVal by remember { mutableStateOf("5") }
    var builtResult by remember { mutableStateOf<SqlExecutionResult?>(null) }

    val builtSql = """
        SELECT $selectedCols
        FROM $selectedTable
        WHERE $whereCond
        ORDER BY $orderByCol
        LIMIT $limitVal;
    """.trimIndent()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Visual Query Builder", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    Text("Construct structured queries visually step by step", fontSize = 11.sp, color = TextMuted)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Generated SQL:", fontSize = 11.sp, fontFamily = SqlMonospace, color = CyanAccent)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(builtSql, fontSize = 12.sp, fontFamily = SqlMonospace, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            sqlEngine.switchDatabase("school")
                            builtResult = sqlEngine.executeQuery(builtSql)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SqlSuccess),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Execute Built Query", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            SqlResultGrid(result = builtResult)
        }
    }
}
