package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QueryFlowStep
import com.example.data.model.SqlColumn
import com.example.data.model.SqlExecutionResult
import com.example.data.model.SqlTable
import com.example.ui.theme.BlueGlow
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SqlError
import com.example.ui.theme.SqlFunction
import com.example.ui.theme.SqlKeyword
import com.example.ui.theme.SqlMonospace
import com.example.ui.theme.SqlNumber
import com.example.ui.theme.SqlString
import com.example.ui.theme.SqlSuccess
import com.example.ui.theme.TextMuted
import java.util.Locale

@Composable
fun SqlCodeEditor(
    query: String,
    onQueryChange: (String) -> Unit,
    onRun: () -> Unit,
    onFormat: () -> Unit,
    onExplain: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    val quickTokens = listOf(
        "SELECT", "FROM", "WHERE", "JOIN", "ON",
        "GROUP BY", "HAVING", "ORDER BY", "LIMIT",
        "*", "COUNT(*)", "SUM()", "AVG()", "AND", "OR", "DESC"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Navy900)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Editor Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy800)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "SQL Editor",
                        fontFamily = SqlMonospace,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onFormat,
                        modifier = Modifier.size(32.dp).testTag("format_sql_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Format SQL",
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onExplain,
                        modifier = Modifier.size(32.dp).testTag("explain_sql_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Explain Query",
                            tint = BlueGlow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.size(32.dp).testTag("clear_sql_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Quick keywords chip bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy800.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(quickTokens) { token ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Navy700,
                        modifier = Modifier
                            .clickable {
                                val space = if (query.isNotEmpty() && !query.endsWith(" ") && !query.endsWith("\n")) " " else ""
                                onQueryChange(query + space + token)
                            }
                    ) {
                        Text(
                            text = token,
                            fontFamily = SqlMonospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (token in listOf("SELECT", "FROM", "WHERE", "JOIN", "GROUP BY", "ORDER BY")) CyanAccent else Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Code Input Area with Line Numbers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 110.dp, max = 220.dp)
                    .padding(8.dp)
            ) {
                // Line numbers
                val linesCount = maxOf(1, query.lines().size)
                val lineNumbersText = (1..linesCount).joinToString("\n")
                Text(
                    text = lineNumbersText,
                    fontFamily = SqlMonospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = Color.Gray.copy(alpha = 0.6f),
                    modifier = Modifier
                        .width(28.dp)
                        .padding(end = 6.dp)
                )

                // Editable SQL Area
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    readOnly = readOnly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .testTag("sql_editor_input"),
                    textStyle = TextStyle(
                        fontFamily = SqlMonospace,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = Color.White
                    ),
                    cursorBrush = SolidColor(CyanAccent),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                text = "Enter SQL statement (e.g. SELECT * FROM students;)",
                                fontFamily = SqlMonospace,
                                fontSize = 13.sp,
                                color = Color.Gray.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )
            }

            // Action Run Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy800)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${query.lines().size} lines • ${query.length} chars",
                    fontFamily = SqlMonospace,
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Button(
                    onClick = onRun,
                    colors = ButtonDefaults.buttonColors(containerColor = SqlSuccess),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("run_query_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Run Query",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Run Query",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SqlResultGrid(
    result: SqlExecutionResult?,
    modifier: Modifier = Modifier
) {
    if (result == null) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Navy800.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = "No Results",
                        tint = TextMuted,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No query executed yet",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Run a query above to see live results",
                        color = TextMuted.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }
        }
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (result.isSuccess) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) else SqlError.copy(alpha = 0.6f),
                RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Navy900)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Result Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (result.isSuccess) Navy800 else Color(0xFF3B1219))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (result.isSuccess) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                        contentDescription = "Status",
                        tint = if (result.isSuccess) SqlSuccess else SqlError,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (result.isSuccess) "Query Succeeded" else "Query Failed",
                        fontFamily = SqlMonospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (result.isSuccess) SqlSuccess else SqlError
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (result.isSuccess) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Navy700
                        ) {
                            Text(
                                text = "${result.rowsCount} rows • ${result.executionTimeMs}ms",
                                fontFamily = SqlMonospace,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            if (!result.isSuccess) {
                // Error Details Panel
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = result.errorMessage ?: "Unknown error occurred.",
                        fontFamily = SqlMonospace,
                        color = Color(0xFFFCA5A5),
                        fontSize = 12.sp
                    )
                    if (!result.errorSuggestion.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Navy800,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "💡 Fix Suggestion: ",
                                    color = CyanAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = result.errorSuggestion,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            } else if (result.columns.isEmpty() && result.affectedRows > 0) {
                // DML statement result
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Statement executed successfully. Affected rows: ${result.affectedRows}",
                        fontFamily = SqlMonospace,
                        color = SqlSuccess,
                        fontSize = 13.sp
                    )
                }
            } else if (result.rows.isEmpty()) {
                // Empty SELECT
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "0 rows returned (Empty result set)",
                        fontFamily = SqlMonospace,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            } else {
                // Data Table with Horizontal & Vertical Scrolling
                val horizontalScroll = rememberScrollState()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .horizontalScroll(horizontalScroll)
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .background(Navy800)
                                .border(0.5.dp, DarkSurfaceBorder)
                        ) {
                            // Row Number Column
                            TableCell(text = "#", isHeader = true, width = 45.dp)
                            result.columns.forEach { colName ->
                                TableCell(text = colName, isHeader = true, width = 130.dp)
                            }
                        }

                        // Data Rows
                        result.rows.forEachIndexed { index, rowValues ->
                            val rowBg = if (index % 2 == 0) Navy900 else Navy800.copy(alpha = 0.5f)
                            Row(
                                modifier = Modifier
                                    .background(rowBg)
                                    .border(0.5.dp, DarkSurfaceBorder.copy(alpha = 0.5f))
                            ) {
                                TableCell(text = "${index + 1}", isHeader = false, width = 45.dp, isRowIndex = true)
                                rowValues.forEach { cellValue ->
                                    TableCell(text = cellValue, isHeader = false, width = 130.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    isHeader: Boolean,
    width: androidx.compose.ui.unit.Dp,
    isRowIndex: Boolean = false
) {
    val isNull = text == "NULL"
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = if (isRowIndex) Alignment.Center else Alignment.CenterStart
    ) {
        if (isNull) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF334155)
            ) {
                Text(
                    text = "NULL",
                    fontFamily = SqlMonospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        } else {
            Text(
                text = text,
                fontFamily = SqlMonospace,
                fontSize = if (isHeader) 11.sp else 12.sp,
                fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                color = if (isHeader) CyanAccent else if (isRowIndex) TextMuted else Color.White.copy(alpha = 0.9f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun QueryFlowVisualization(
    steps: List<QueryFlowStep>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Navy900)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Query Flow",
                    tint = CyanAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Query Execution Flow Simulation",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Educational simulation of relational processing",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            steps.forEachIndexed { idx, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Navy700)
                            .border(1.dp, CyanAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepNumber}",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = step.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = step.description,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Navy800
                    ) {
                        Text(
                            text = step.visualDetail,
                            fontFamily = SqlMonospace,
                            fontSize = 10.sp,
                            color = BlueGlow,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (idx < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .width(2.dp)
                            .height(10.dp)
                            .background(CyanAccent.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}

@Composable
fun DatabaseSwitcher(
    activeDatabaseId: String,
    onSelectDatabase: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val dbs = listOf(
        "school" to "School & University",
        "ecommerce" to "E-Commerce Store",
        "hospital" to "Hospital System",
        "banking" to "Banking & Finance",
        "library" to "Library Catalog"
    )
    val activeTitle = dbs.firstOrNull { it.first == activeDatabaseId }?.second ?: "Database"

    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Navy800,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier
                .clickable { expanded = true }
                .testTag("database_switcher_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = "Database",
                    tint = CyanAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = activeTitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Expand",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Navy800)
        ) {
            dbs.forEach { (id, label) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            color = if (id == activeDatabaseId) CyanAccent else Color.White,
                            fontWeight = if (id == activeDatabaseId) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    onClick = {
                        onSelectDatabase(id)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = label,
                            tint = if (id == activeDatabaseId) CyanAccent else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}
