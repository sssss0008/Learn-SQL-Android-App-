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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
import com.example.data.model.Lesson
import com.example.data.repository.SqlCurriculumRepository
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SqlMonospace
import com.example.ui.theme.SqlSuccess
import com.example.ui.theme.TextMuted

@Composable
fun LearnScreen(
    completedLessons: Set<Int>,
    onSelectLesson: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedModuleFilter by remember { mutableStateOf("All") }

    val modules = listOf(
        "All",
        "Database Fundamentals",
        "Database Architecture",
        "Core SQL",
        "Data Manipulation (DML)",
        "Data Analysis & Aggregation",
        "Multi-Table Queries",
        "Advanced Querying",
        "Built-in Functions",
        "Logic & Expressions",
        "Performance & Architecture",
        "Advanced SQL",
        "Architecture & Engineering",
        "Mastery"
    )

    val filteredLessons = SqlCurriculumRepository.allLessons.filter { lesson ->
        val matchesSearch = lesson.title.contains(searchQuery, ignoreCase = true) ||
                lesson.subtitle.contains(searchQuery, ignoreCase = true) ||
                lesson.whatIsIt.contains(searchQuery, ignoreCase = true)
        val matchesModule = selectedModuleFilter == "All" || lesson.moduleName.equals(selectedModuleFilter, ignoreCase = true)
        matchesSearch && matchesModule
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Title & Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "39 Levels Roadmap",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${completedLessons.size} of ${SqlCurriculumRepository.allLessons.size} completed",
                    fontSize = 12.sp,
                    color = CyanAccent,
                    fontFamily = SqlMonospace
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search topics, clauses, concepts...", color = Color.Gray, fontSize = 13.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("search_lessons_input"),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyanAccent,
                unfocusedBorderColor = Navy700,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Module Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(modules) { mod ->
                val isSelected = selectedModuleFilter == mod
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) CyanAccent else Navy800,
                    modifier = Modifier.clickable { selectedModuleFilter = mod }
                ) {
                    Text(
                        text = mod,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Lessons List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredLessons) { lesson ->
                val isCompleted = completedLessons.contains(lesson.id)
                LessonRoadmapCard(
                    lesson = lesson,
                    isCompleted = isCompleted,
                    onClick = { onSelectLesson(lesson.id) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun LessonRoadmapCard(
    lesson: Lesson,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(
                1.dp,
                if (isCompleted) SqlSuccess.copy(alpha = 0.5f) else Navy700,
                RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Navy800)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) Color(0xFF064E3B) else Navy700)
                    .border(1.dp, if (isCompleted) SqlSuccess else CyanAccent.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = SqlSuccess,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = "${lesson.levelNumber}",
                        fontFamily = SqlMonospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LEVEL ${lesson.levelNumber} • ${lesson.moduleName.uppercase()}",
                    fontSize = 10.sp,
                    fontFamily = SqlMonospace,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCompleted) SqlSuccess else CyanAccent
                )

                Text(
                    text = lesson.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = lesson.subtitle,
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open Lesson",
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
