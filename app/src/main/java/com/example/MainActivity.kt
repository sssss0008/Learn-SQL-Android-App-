package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.repository.SqlCurriculumRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.ui.components.CommandPaletteDialog
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.DatabaseLabScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InterviewScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.LessonDetailScreen
import com.example.ui.screens.OnboardingFlowScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.ProfileProgressScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.ReferenceScreen
import com.example.ui.screens.SqlPlaygroundScreen
import com.example.ui.screens.VisualLabsScreen
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.LearnSqlTheme
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SqlMonospace
import com.example.ui.theme.SqlSuccess
import com.example.ui.theme.TextMuted

class MainActivity : ComponentActivity() {

    private lateinit var prefsRepo: UserPreferencesRepository
    private val sqlEngine = SqlExecutionEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        prefsRepo = UserPreferencesRepository(applicationContext)

        setContent {
            val themeMode by prefsRepo.themeMode.collectAsState()
            val isOnboarded by prefsRepo.isOnboarded.collectAsState()
            val userName by prefsRepo.userName.collectAsState()
            val completedLessons by prefsRepo.completedLessons.collectAsState()
            val solvedChallenges by prefsRepo.solvedChallenges.collectAsState()
            val streak by prefsRepo.streak.collectAsState()
            val points by prefsRepo.points.collectAsState()
            val savedQueries by prefsRepo.savedQueries.collectAsState()
            val queryHistory by prefsRepo.queryHistory.collectAsState()
            val certIssued by prefsRepo.certificateIssued.collectAsState()

            LearnSqlTheme(themeMode = themeMode) {
                if (!isOnboarded) {
                    OnboardingFlowScreen(
                        onFinishOnboarding = { name, exp, db ->
                            prefsRepo.completeOnboarding(name, exp, db)
                        }
                    )
                } else {
                    LearnSqlAppContent(
                        prefsRepo = prefsRepo,
                        sqlEngine = sqlEngine,
                        userName = userName,
                        completedLessons = completedLessons,
                        solvedChallenges = solvedChallenges,
                        streak = streak,
                        points = points,
                        savedQueries = savedQueries,
                        queryHistory = queryHistory,
                        certificateIssued = certIssued
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnSqlAppContent(
    prefsRepo: UserPreferencesRepository,
    sqlEngine: SqlExecutionEngine,
    userName: String,
    completedLessons: Set<Int>,
    solvedChallenges: Set<String>,
    streak: Int,
    points: Int,
    savedQueries: List<String>,
    queryHistory: List<String>,
    certificateIssued: Boolean
) {
    var currentScreen by remember { mutableStateOf("home") } // home, learn, playground, practice, visuallabs, databaselab, projects, interview, aitutor, reference, profile, lesson_detail
    var activeLessonId by remember { mutableIntStateOf(1) }
    var showCommandPalette by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    // Android back handling
    BackHandler(enabled = currentScreen != "home") {
        currentScreen = "home"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Navy800,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                        ) {
                            Box(modifier = Modifier.padding(6.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = "Logo",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Learn SQL",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Awiskar Acharya",
                                fontSize = 10.sp,
                                fontFamily = SqlMonospace,
                                color = CyanAccent
                            )
                        }
                    }
                },
                actions = {
                    // Command Palette Trigger Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Navy800,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                        modifier = Modifier
                            .clickable { showCommandPalette = true }
                            .testTag("command_palette_trigger")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = CyanAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("⌘K", fontSize = 11.sp, fontFamily = SqlMonospace, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // More Tools Dropdown Trigger
                    Box {
                        IconButton(
                            onClick = { showMoreMenu = true },
                            modifier = Modifier.size(36.dp).testTag("more_tools_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "More", tint = CyanAccent, modifier = Modifier.size(20.dp))
                        }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false },
                            modifier = Modifier.background(Navy800)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Database Lab", color = Color.White, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.Storage, contentDescription = null, tint = CyanAccent) },
                                onClick = {
                                    currentScreen = "databaselab"
                                    showMoreMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Real Projects", color = Color.White, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.Work, contentDescription = null, tint = SqlSuccess) },
                                onClick = {
                                    currentScreen = "projects"
                                    showMoreMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Interview Center", color = Color.White, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFFA78BFA)) },
                                onClick = {
                                    currentScreen = "interview"
                                    showMoreMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("AI SQL Tutor", color = Color.White, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanAccent) },
                                onClick = {
                                    currentScreen = "aitutor"
                                    showMoreMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("SQL Reference", color = Color.White, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.Book, contentDescription = null, tint = Color.White) },
                                onClick = {
                                    currentScreen = "reference"
                                    showMoreMenu = false
                                }
                            )
                        }
                    }

                    IconButton(
                        onClick = { currentScreen = "profile" },
                        modifier = Modifier.size(36.dp).testTag("profile_button")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BluePrimary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userName.take(1).uppercase().ifBlank { "U" },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Navy900,
                contentColor = CyanAccent,
                tonalElevation = 4.dp,
                modifier = Modifier.navigationBarsPadding().height(64.dp)
            ) {
                val navItems = listOf(
                    Triple("home", "Home", Icons.Default.Home),
                    Triple("learn", "Learn", Icons.Default.School),
                    Triple("playground", "SQL Lab", Icons.Default.Terminal),
                    Triple("practice", "Practice", Icons.Default.Code),
                    Triple("visuallabs", "Visual Labs", Icons.Default.Hub)
                )

                navItems.forEach { (route, label, icon) ->
                    val isSelected = currentScreen == route || (route == "learn" && currentScreen == "lesson_detail")
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = route },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CyanAccent,
                            indicatorColor = CyanAccent,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "home" -> HomeScreen(
                    userName = userName,
                    completedLessons = completedLessons,
                    streak = streak,
                    points = points,
                    onNavigateToLesson = { lessonId ->
                        activeLessonId = lessonId
                        currentScreen = "lesson_detail"
                    },
                    onNavigateToPlayground = { currentScreen = "playground" },
                    onNavigateToDatabaseLab = { currentScreen = "databaselab" },
                    onNavigateToPractice = { currentScreen = "practice" },
                    onNavigateToProjects = { currentScreen = "projects" },
                    onNavigateToLearn = { currentScreen = "learn" }
                )

                "learn" -> LearnScreen(
                    completedLessons = completedLessons,
                    onSelectLesson = { lessonId ->
                        activeLessonId = lessonId
                        currentScreen = "lesson_detail"
                    }
                )

                "lesson_detail" -> {
                    val lesson = SqlCurriculumRepository.allLessons.firstOrNull { it.id == activeLessonId }
                        ?: SqlCurriculumRepository.allLessons.first()
                    LessonDetailScreen(
                        lesson = lesson,
                        sqlEngine = sqlEngine,
                        isCompleted = completedLessons.contains(lesson.id),
                        onBack = { currentScreen = "learn" },
                        onMarkCompleted = { id ->
                            prefsRepo.markLessonCompleted(id)
                        }
                    )
                }

                "playground" -> SqlPlaygroundScreen(
                    sqlEngine = sqlEngine,
                    savedQueries = savedQueries,
                    queryHistory = queryHistory,
                    onToggleSaveQuery = { q -> prefsRepo.toggleSaveQuery(q) },
                    onAddHistory = { q -> prefsRepo.addQueryToHistory(q) }
                )

                "practice" -> PracticeScreen(
                    sqlEngine = sqlEngine,
                    solvedChallenges = solvedChallenges,
                    onSolveChallenge = { id, pts ->
                        prefsRepo.markChallengeSolved(id, pts)
                    }
                )

                "visuallabs" -> VisualLabsScreen(
                    sqlEngine = sqlEngine,
                    onNavigateToPlayground = { q ->
                        currentScreen = "playground"
                    }
                )

                "databaselab" -> DatabaseLabScreen(
                    sqlEngine = sqlEngine,
                    onNavigateToPlaygroundWithQuery = { q ->
                        currentScreen = "playground"
                    }
                )

                "projects" -> ProjectsScreen(
                    sqlEngine = sqlEngine,
                    onAddPoints = { pts -> prefsRepo.addPoints(pts) }
                )

                "interview" -> InterviewScreen()

                "aitutor" -> AiTutorScreen(sqlEngine = sqlEngine)

                "reference" -> ReferenceScreen()

                "profile" -> ProfileProgressScreen(
                    userName = userName,
                    completedLessons = completedLessons,
                    solvedChallenges = solvedChallenges,
                    streak = streak,
                    points = points,
                    certificateIssued = certificateIssued,
                    certificateId = prefsRepo.certificateId,
                    certificateDate = prefsRepo.certificateDate,
                    currentTheme = prefsRepo.themeMode.collectAsState().value,
                    onSetTheme = { mode -> prefsRepo.setThemeMode(mode) },
                    onIssueCertificate = { prefsRepo.issueCertificate() },
                    onResetProgress = { prefsRepo.resetProgress() }
                )
            }

            if (showCommandPalette) {
                CommandPaletteDialog(
                    onDismiss = { showCommandPalette = false },
                    onNavigate = { route ->
                        currentScreen = route
                    }
                )
            }
        }
    }
}
