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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.data.engine.SqlExecutionEngine
import com.example.data.model.ProjectItem
import com.example.data.model.SqlExecutionResult
import com.example.data.repository.SqlCurriculumRepository
import com.example.ui.components.SqlCodeEditor
import com.example.ui.components.SqlResultGrid
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
fun ProjectsScreen(
    sqlEngine: SqlExecutionEngine,
    onAddPoints: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeProject by remember { mutableStateOf<ProjectItem?>(null) }

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
            Column {
                Text(
                    text = "Real-World Projects Laboratory",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Production-grade database design and multi-table analytics",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (activeProject != null) {
            ProjectWorkspaceView(
                project = activeProject!!,
                sqlEngine = sqlEngine,
                onBack = { activeProject = null },
                onAddPoints = onAddPoints
            )
        } else {
            ProjectListView(
                projects = SqlCurriculumRepository.allProjects,
                onSelectProject = { activeProject = it }
            )
        }
    }
}

@Composable
fun ProjectListView(
    projects: List<ProjectItem>,
    onSelectProject: (ProjectItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(projects) { proj ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectProject(proj) }
                    .border(1.dp, Navy700, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Navy800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Navy900
                        ) {
                            Text(
                                text = proj.industry.uppercase(),
                                fontSize = 10.sp,
                                fontFamily = SqlMonospace,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "${proj.tasks.size} Query Tasks",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = proj.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = proj.description,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tables: ${proj.tablesRequired.joinToString(", ")}",
                            fontSize = 11.sp,
                            fontFamily = SqlMonospace,
                            color = TextMuted
                        )

                        Button(
                            onClick = { onSelectProject(proj) },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Open Project", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
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
fun ProjectWorkspaceView(
    project: ProjectItem,
    sqlEngine: SqlExecutionEngine,
    onBack: () -> Unit,
    onAddPoints: (Int) -> Unit
) {
    var currentTaskIndex by remember { mutableStateOf(0) }
    val currentTask = project.tasks.getOrNull(currentTaskIndex) ?: project.tasks.first()
    var userSql by remember(currentTask.id) { mutableStateOf(currentTask.starterSql) }
    var result by remember(currentTask.id) { mutableStateOf<SqlExecutionResult?>(null) }
    var isPassed by remember(currentTask.id) { mutableStateOf(false) }

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
                Text("← All Projects", fontSize = 12.sp, color = Color.White)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = project.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Task ${currentTaskIndex + 1} of ${project.tasks.size}: ${currentTask.title}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CyanAccent)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = currentTask.prompt, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        item {
            SqlCodeEditor(
                query = userSql,
                onQueryChange = { userSql = it },
                onRun = {
                    val dbToUse = if (project.id == "p2") "ecommerce" else if (project.id == "p3") "banking" else "school"
                    sqlEngine.switchDatabase(dbToUse)
                    val res = sqlEngine.executeQuery(userSql)
                    result = res
                    if (res.isSuccess && res.rows.isNotEmpty()) {
                        isPassed = true
                        onAddPoints(30)
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

        if (isPassed && currentTaskIndex < project.tasks.size - 1) {
            item {
                Button(
                    onClick = {
                        currentTaskIndex++
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SqlSuccess),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Proceed to Next Task →", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
