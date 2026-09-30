package com.example.data.model

data class SqlColumn(
    val name: String,
    val type: String,
    val isPrimaryKey: Boolean = false,
    val isForeignKey: Boolean = false,
    val isNullable: Boolean = true,
    val referencesTable: String? = null,
    val referencesColumn: String? = null
)

data class SqlTable(
    val name: String,
    val columns: List<SqlColumn>,
    val rows: List<Map<String, Any?>> = emptyList(),
    val description: String = ""
)

data class SqlRelationship(
    val fromTable: String,
    val fromColumn: String,
    val toTable: String,
    val toColumn: String,
    val type: String // "1:1", "1:N", "N:M"
)

data class SqlDatabaseInfo(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val tables: List<SqlTable>,
    val relationships: List<SqlRelationship>
)

data class SqlExecutionResult(
    val isSuccess: Boolean,
    val query: String,
    val columns: List<String> = emptyList(),
    val rows: List<List<String>> = emptyList(),
    val rowsCount: Int = 0,
    val executionTimeMs: Long = 0,
    val affectedRows: Int = 0,
    val errorMessage: String? = null,
    val errorLine: Int = 0,
    val errorSuggestion: String? = null,
    val queryPlan: List<String> = emptyList(),
    val isSimulatedPlan: Boolean = false
)

data class QueryFlowStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val icon: String,
    val visualDetail: String
)

data class Lesson(
    val id: Int,
    val levelNumber: Int,
    val title: String,
    val moduleName: String,
    val subtitle: String,
    val whatIsIt: String,
    val whyUseful: String,
    val realWorldExample: String,
    val defaultDatabase: String,
    val defaultQuery: String,
    val visualExplanation: String,
    val executionSteps: List<String>,
    val commonMistakes: List<String>,
    val practiceQuestion: String,
    val practiceSolution: String,
    val interviewQuestion: String,
    val interviewAnswer: String
)

data class SqlChallenge(
    val id: String,
    val title: String,
    val difficulty: String, // "Beginner", "Intermediate", "Advanced", "Interview"
    val topic: String,
    val databaseId: String,
    val prompt: String,
    val starterSql: String,
    val solutionSql: String,
    val hints: List<String>,
    val points: Int = 50
)

data class InterviewItem(
    val id: String,
    val question: String,
    val difficulty: String,
    val topic: String,
    val thinkPrompt: String,
    val hints: List<String>,
    val answer: String,
    val sqlExample: String,
    val followUp: String
)

data class ProjectItem(
    val id: String,
    val title: String,
    val difficulty: String,
    val description: String,
    val industry: String,
    val tablesRequired: List<String>,
    val tasks: List<ProjectTask>
)

data class ProjectTask(
    val id: Int,
    val title: String,
    val prompt: String,
    val starterSql: String,
    val solutionSql: String,
    val testQuery: String
)

data class CheatSheetItem(
    val clause: String,
    val syntax: String,
    val purpose: String,
    val example: String
)

data class GlossaryItem(
    val term: String,
    val category: String,
    val definition: String,
    val example: String
)
