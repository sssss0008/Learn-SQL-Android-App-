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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schema
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.SqlDatabaseInfo
import com.example.data.model.SqlExecutionResult
import com.example.ui.components.DatabaseSwitcher
import com.example.ui.components.QueryFlowVisualization
import com.example.ui.components.SqlCodeEditor
import com.example.ui.components.SqlResultGrid
import com.example.ui.components.TableSchemaViewer
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SqlMonospace
import com.example.ui.theme.SqlSuccess
import com.example.ui.theme.TextMuted

@Composable
fun SqlPlaygroundScreen(
    sqlEngine: SqlExecutionEngine,
    savedQueries: List<String>,
    queryHistory: List<String>,
    onToggleSaveQuery: (String) -> Unit,
    onAddHistory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentQuery by remember { mutableStateOf("SELECT * FROM students LIMIT 10;") }
    var activeDbId by remember { mutableStateOf(sqlEngine.currentDatabaseId) }
    var dbInfo by remember { mutableStateOf(sqlEngine.getDatabaseInfo(activeDbId)) }
    var result by remember { mutableStateOf<SqlExecutionResult?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Editor & Result, 1: Schema Browser, 2: Query Plan, 3: History & Saved

    fun refreshDbInfo() {
        dbInfo = sqlEngine.getDatabaseInfo(activeDbId)
    }

    LaunchedEffect(activeDbId) {
        refreshDbInfo()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Top Toolbar: Database Switcher + Reset + Favorite
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DatabaseSwitcher(
                activeDatabaseId = activeDbId,
                onSelectDatabase = { newDb ->
                    activeDbId = newDb
                    sqlEngine.switchDatabase(newDb)
                    refreshDbInfo()
                    currentQuery = when (newDb) {
                        "school" -> "SELECT * FROM students LIMIT 10;"
                        "ecommerce" -> "SELECT * FROM products LIMIT 10;"
                        "hospital" -> "SELECT * FROM doctors LIMIT 10;"
                        "banking" -> "SELECT * FROM accounts LIMIT 10;"
                        "library" -> "SELECT * FROM books LIMIT 10;"
                        else -> "SELECT 1;"
                    }
                    result = null
                }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                val isSaved = savedQueries.contains(currentQuery)
                IconButton(
                    onClick = { onToggleSaveQuery(currentQuery) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save Query",
                        tint = if (isSaved) CyanAccent else TextMuted
                    )
                }

                IconButton(
                    onClick = {
                        sqlEngine.resetCurrentDatabase()
                        refreshDbInfo()
                        result = null
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset DB",
                        tint = TextMuted
                    )
                }
            }
        }

        // Playground Tabs: Editor, Schema, Plan, Saved/History
        val tabs = listOf("Console & Data", "Schema Browser", "Query Plan", "Saved & History")
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = CyanAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyanAccent
                )
            },
            divider = { Divider(color = DarkSurfaceBorder) },
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { idx, label ->
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

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Console & Data: SQL Code Editor + Live Result Grid
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SqlCodeEditor(
                            query = currentQuery,
                            onQueryChange = { currentQuery = it },
                            onRun = {
                                onAddHistory(currentQuery)
                                result = sqlEngine.executeQuery(currentQuery)
                            },
                            onFormat = {
                                currentQuery = sqlEngine.formatSql(currentQuery)
                            },
                            onExplain = {
                                selectedTab = 2 // Switch to query plan tab
                            },
                            onClear = { currentQuery = "" }
                        )
                    }

                    item {
                        SqlResultGrid(result = result)
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            1 -> {
                // Schema Browser: Tables, Columns, Types, PKs
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "${dbInfo.name} Tables (${dbInfo.tables.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Tap any table to generate a quick SELECT statement",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    items(dbInfo.tables) { table ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentQuery = "SELECT * FROM ${table.name} LIMIT 10;"
                                    selectedTab = 0
                                },
                            colors = CardDefaults.cardColors(containerColor = Navy800),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                        ) {
                            TableSchemaViewer(table = table)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            2 -> {
                // Query Plan & Execution Flow Simulation
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        val flow = sqlEngine.explainQueryLogically(currentQuery)
                        QueryFlowVisualization(steps = flow)
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Navy800),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "SQLite EXPLAIN QUERY PLAN",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    fontFamily = SqlMonospace
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (result != null && result!!.queryPlan.isNotEmpty()) {
                                    result!!.queryPlan.forEach { planStep ->
                                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                            Text("⚡ ", fontSize = 11.sp)
                                            Text(
                                                text = planStep,
                                                fontFamily = SqlMonospace,
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Run query to generate cost & access plan details.",
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
                // Saved & History
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Saved Queries (${savedQueries.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }

                    items(savedQueries) { q ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentQuery = q
                                    selectedTab = 0
                                },
                            colors = CardDefaults.cardColors(containerColor = Navy800),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Bookmark, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = q,
                                    fontFamily = SqlMonospace,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Recent Query History (${queryHistory.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    items(queryHistory) { h ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentQuery = h
                                    selectedTab = 0
                                },
                            colors = CardDefaults.cardColors(containerColor = Navy900),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = h,
                                    fontFamily = SqlMonospace,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
