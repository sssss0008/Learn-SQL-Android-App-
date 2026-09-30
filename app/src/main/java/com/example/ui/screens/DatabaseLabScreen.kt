package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
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
import com.example.data.model.SqlColumn
import com.example.data.model.SqlDatabaseInfo
import com.example.data.model.SqlTable
import com.example.ui.components.DatabaseSwitcher
import com.example.ui.components.ErDiagramView
import com.example.ui.components.TableSchemaViewer
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
fun DatabaseLabScreen(
    sqlEngine: SqlExecutionEngine,
    onNavigateToPlaygroundWithQuery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeDbId by remember { mutableStateOf(sqlEngine.currentDatabaseId) }
    var dbInfo by remember { mutableStateOf(sqlEngine.getDatabaseInfo(activeDbId)) }
    var selectedTable by remember { mutableStateOf<SqlTable?>(dbInfo.tables.firstOrNull()) }
    var viewMode by remember { mutableIntStateOf(0) } // 0: Data Rows, 1: Schema View, 2: ER Diagram, 3: Table Builder

    // New Table Builder state
    var newTableName by remember { mutableStateOf("") }
    var newCol1Name by remember { mutableStateOf("id") }
    var newCol1Type by remember { mutableStateOf("INTEGER PRIMARY KEY") }
    var newCol2Name by remember { mutableStateOf("title") }
    var newCol2Type by remember { mutableStateOf("TEXT NOT NULL") }
    var newCol3Name by remember { mutableStateOf("created_at") }
    var newCol3Type by remember { mutableStateOf("TEXT") }
    var tableCreationStatus by remember { mutableStateOf<String?>(null) }

    fun refreshDb() {
        dbInfo = sqlEngine.getDatabaseInfo(activeDbId)
        selectedTable = dbInfo.tables.firstOrNull { it.name == selectedTable?.name } ?: dbInfo.tables.firstOrNull()
    }

    LaunchedEffect(activeDbId) {
        refreshDb()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Database Switcher Bar
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
                    refreshDb()
                }
            )

            Button(
                onClick = {
                    onNavigateToPlaygroundWithQuery("SELECT * FROM ${selectedTable?.name ?: "students"} LIMIT 20;")
                },
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Query Table", fontSize = 12.sp, color = Color.White)
            }
        }

        // Table Selection Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            items(dbInfo.tables) { table ->
                val isSelected = selectedTable?.name == table.name
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) CyanAccent else Navy800,
                    modifier = Modifier.clickable { selectedTable = table }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = null,
                            tint = if (isSelected) Color.Black else CyanAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = table.name,
                            fontFamily = SqlMonospace,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lab View Modes: Data, Schema, ER Diagram, Table Builder
        TabRow(
            selectedTabIndex = viewMode,
            containerColor = Color.Transparent,
            contentColor = CyanAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[viewMode]),
                    color = CyanAccent
                )
            },
            divider = { Divider(color = DarkSurfaceBorder) }
        ) {
            listOf("Data Rows", "Schema View", "ER Diagram", "Table Builder").forEachIndexed { idx, label ->
                Tab(
                    selected = viewMode == idx,
                    onClick = { viewMode = idx },
                    text = {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (viewMode == idx) FontWeight.Bold else FontWeight.Normal,
                            color = if (viewMode == idx) CyanAccent else TextMuted
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (viewMode) {
            0 -> {
                // Interactive Data Rows View
                val curTable = selectedTable
                if (curTable != null) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${curTable.name} (${curTable.rows.size} rows)",
                                fontSize = 13.sp,
                                fontFamily = SqlMonospace,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Simulated Live Table Grid",
                                fontSize = 11.sp,
                                color = CyanAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Navy900),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Navy700)
                        ) {
                            val horizScroll = rememberScrollState()
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .horizontalScroll(horizScroll)
                            ) {
                                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                    // Header
                                    Row(modifier = Modifier.background(Navy800).padding(vertical = 6.dp)) {
                                        Box(modifier = Modifier.width(40.dp).padding(horizontal = 4.dp)) {
                                            Text("#", fontFamily = SqlMonospace, fontSize = 11.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                                        }
                                        curTable.columns.forEach { col ->
                                            Box(modifier = Modifier.width(130.dp).padding(horizontal = 6.dp)) {
                                                Text(
                                                    text = col.name,
                                                    fontFamily = SqlMonospace,
                                                    fontSize = 11.sp,
                                                    color = CyanAccent,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    // Data
                                    curTable.rows.forEachIndexed { idx, rowMap ->
                                        val bg = if (idx % 2 == 0) Navy900 else Navy800.copy(alpha = 0.5f)
                                        Row(modifier = Modifier.background(bg).padding(vertical = 6.dp)) {
                                            Box(modifier = Modifier.width(40.dp).padding(horizontal = 4.dp)) {
                                                Text("${idx + 1}", fontFamily = SqlMonospace, fontSize = 11.sp, color = TextMuted)
                                            }
                                            curTable.columns.forEach { col ->
                                                val cellVal = rowMap[col.name]?.toString() ?: "NULL"
                                                Box(modifier = Modifier.width(130.dp).padding(horizontal = 6.dp)) {
                                                    Text(
                                                        text = cellVal,
                                                        fontFamily = SqlMonospace,
                                                        fontSize = 11.sp,
                                                        color = if (cellVal == "NULL") Color.Gray else Color.White.copy(alpha = 0.9f),
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            1 -> {
                // Table Schema Inspector
                val curTable = selectedTable
                if (curTable != null) {
                    TableSchemaViewer(table = curTable)
                }
            }

            2 -> {
                // Interactive ER Diagram Canvas
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Entity-Relationship (ER) Architecture",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Interactive visual entity relationship graph",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ErDiagramView(
                        tables = dbInfo.tables,
                        relationships = dbInfo.relationships
                    )
                }
            }

            3 -> {
                // Table Builder
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Interactive Table Builder (DDL)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Create custom tables directly in the active database",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = newTableName,
                            onValueChange = { newTableName = it },
                            label = { Text("Table Name (e.g. projects, tasks)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("new_table_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = Navy700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newCol1Name,
                                onValueChange = { newCol1Name = it },
                                label = { Text("Col 1 Name") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = Navy700, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            OutlinedTextField(
                                value = newCol1Type,
                                onValueChange = { newCol1Type = it },
                                label = { Text("Col 1 Type & PK") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = Navy700, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newCol2Name,
                                onValueChange = { newCol2Name = it },
                                label = { Text("Col 2 Name") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = Navy700, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            OutlinedTextField(
                                value = newCol2Type,
                                onValueChange = { newCol2Type = it },
                                label = { Text("Col 2 Type") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = Navy700, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                if (newTableName.isNotBlank()) {
                                    val sql = """
                                        CREATE TABLE IF NOT EXISTS ${newTableName.trim()} (
                                            $newCol1Name $newCol1Type,
                                            $newCol2Name $newCol2Type
                                        );
                                    """.trimIndent()
                                    val res = sqlEngine.executeQuery(sql)
                                    if (res.isSuccess) {
                                        tableCreationStatus = "Table '${newTableName.trim()}' created successfully!"
                                        refreshDb()
                                    } else {
                                        tableCreationStatus = "Error: ${res.errorMessage}"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SqlSuccess),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Create Table in Database", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (tableCreationStatus != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Navy800,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = tableCreationStatus!!,
                                    fontSize = 12.sp,
                                    color = if (tableCreationStatus!!.startsWith("Error")) Color(0xFFFCA5A5) else SqlSuccess,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
