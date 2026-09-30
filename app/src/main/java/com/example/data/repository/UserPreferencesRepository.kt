package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class UserPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("learn_sql_prefs", Context.MODE_PRIVATE)

    private val _userName = MutableStateFlow(prefs.getString("user_name", "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _isOnboarded = MutableStateFlow(prefs.getBoolean("is_onboarded", false))
    val isOnboarded: StateFlow<Boolean> = _isOnboarded.asStateFlow()

    private val _experienceLevel = MutableStateFlow(prefs.getString("experience_level", "Complete Beginner") ?: "Complete Beginner")
    val experienceLevel: StateFlow<String> = _experienceLevel.asStateFlow()

    private val _databasePreference = MutableStateFlow(prefs.getString("db_pref", "General SQL") ?: "General SQL")
    val databasePreference: StateFlow<String> = _databasePreference.asStateFlow()

    private val _completedLessons = MutableStateFlow(
        prefs.getStringSet("completed_lessons", emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    )
    val completedLessons: StateFlow<Set<Int>> = _completedLessons.asStateFlow()

    private val _solvedChallenges = MutableStateFlow(
        prefs.getStringSet("solved_challenges", emptySet()) ?: emptySet()
    )
    val solvedChallenges: StateFlow<Set<String>> = _solvedChallenges.asStateFlow()

    private val _points = MutableStateFlow(prefs.getInt("points", 120))
    val points: StateFlow<Int> = _points.asStateFlow()

    private val _streak = MutableStateFlow(prefs.getInt("streak", 1))
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _themeMode = MutableStateFlow(
        try {
            AppThemeMode.valueOf(prefs.getString("theme_mode", AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name)
        } catch (_: Exception) {
            AppThemeMode.DARK
        }
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _savedQueries = MutableStateFlow(
        prefs.getStringSet("saved_queries", setOf(
            "SELECT name, gpa FROM students WHERE gpa >= 3.8 ORDER BY gpa DESC;",
            "SELECT customers.name, SUM(orders.total_amount) FROM customers JOIN orders ON customers.id = orders.customer_id GROUP BY customers.id;",
            "SELECT name, salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk FROM instructors;"
        ))?.toList() ?: emptyList()
    )
    val savedQueries: StateFlow<List<String>> = _savedQueries.asStateFlow()

    private val _queryHistory = MutableStateFlow(
        prefs.getStringSet("query_history", setOf(
            "SELECT * FROM students;",
            "SELECT name, price FROM products WHERE price > 100;",
            "SELECT * FROM orders JOIN customers ON orders.customer_id = customers.id;"
        ))?.toList() ?: emptyList()
    )
    val queryHistory: StateFlow<List<String>> = _queryHistory.asStateFlow()

    private val _certificateIssued = MutableStateFlow(prefs.getBoolean("cert_issued", false))
    val certificateIssued: StateFlow<Boolean> = _certificateIssued.asStateFlow()

    val certificateId: String
        get() {
            var cid = prefs.getString("cert_id", null)
            if (cid == null) {
                cid = "SQL-" + UUID.randomUUID().toString().take(8).uppercase(Locale.ROOT)
                prefs.edit().putString("cert_id", cid).apply()
            }
            return cid
        }

    val certificateDate: String
        get() {
            var cdate = prefs.getString("cert_date", null)
            if (cdate == null) {
                cdate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
                prefs.edit().putString("cert_date", cdate).apply()
            }
            return cdate
        }

    fun completeOnboarding(name: String, experience: String, dbPref: String) {
        val cleanName = name.trim().ifEmpty { "SQL Explorer" }
        prefs.edit()
            .putString("user_name", cleanName)
            .putString("experience_level", experience)
            .putString("db_pref", dbPref)
            .putBoolean("is_onboarded", true)
            .apply()

        _userName.value = cleanName
        _experienceLevel.value = experience
        _databasePreference.value = dbPref
        _isOnboarded.value = true
    }

    fun markLessonCompleted(lessonId: Int) {
        val updated = _completedLessons.value.toMutableSet().apply { add(lessonId) }
        prefs.edit().putStringSet("completed_lessons", updated.map { it.toString() }.toSet()).apply()
        _completedLessons.value = updated
        addPoints(25)
    }

    fun markChallengeSolved(challengeId: String, pts: Int = 50) {
        val updated = _solvedChallenges.value.toMutableSet().apply { add(challengeId) }
        prefs.edit().putStringSet("solved_challenges", updated).apply()
        _solvedChallenges.value = updated
        addPoints(pts)
    }

    fun addPoints(amount: Int) {
        val newPts = _points.value + amount
        prefs.edit().putInt("points", newPts).apply()
        _points.value = newPts
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    fun addQueryToHistory(query: String) {
        if (query.isBlank()) return
        val current = _queryHistory.value.toMutableList()
        current.remove(query)
        current.add(0, query)
        val trimmed = current.take(30)
        prefs.edit().putStringSet("query_history", trimmed.toSet()).apply()
        _queryHistory.value = trimmed
    }

    fun toggleSaveQuery(query: String) {
        val current = _savedQueries.value.toMutableList()
        if (current.contains(query)) {
            current.remove(query)
        } else {
            current.add(0, query)
        }
        prefs.edit().putStringSet("saved_queries", current.toSet()).apply()
        _savedQueries.value = current
    }

    fun issueCertificate() {
        prefs.edit().putBoolean("cert_issued", true).apply()
        _certificateIssued.value = true
    }

    fun resetProgress() {
        prefs.edit()
            .remove("completed_lessons")
            .remove("solved_challenges")
            .remove("points")
            .remove("cert_issued")
            .apply()
        _completedLessons.value = emptySet()
        _solvedChallenges.value = emptySet()
        _points.value = 0
        _certificateIssued.value = false
    }
}
