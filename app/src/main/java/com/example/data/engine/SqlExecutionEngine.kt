package com.example.data.engine

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.example.data.model.QueryFlowStep
import com.example.data.model.SqlColumn
import com.example.data.model.SqlDatabaseInfo
import com.example.data.model.SqlExecutionResult
import com.example.data.model.SqlRelationship
import com.example.data.model.SqlTable
import java.util.Locale

class SqlExecutionEngine {

    private var activeDb: SQLiteDatabase? = null
    var currentDatabaseId: String = "school"
        private set

    init {
        switchDatabase("school")
    }

    @Synchronized
    fun switchDatabase(databaseId: String) {
        try {
            activeDb?.close()
        } catch (_: Exception) {}

        currentDatabaseId = databaseId
        activeDb = SQLiteDatabase.create(null)
        initializeDatabase(databaseId)
    }

    fun resetCurrentDatabase() {
        switchDatabase(currentDatabaseId)
    }

    private fun initializeDatabase(databaseId: String) {
        val db = activeDb ?: return
        when (databaseId) {
            "school" -> seedSchoolDatabase(db)
            "ecommerce" -> seedEcommerceDatabase(db)
            "hospital" -> seedHospitalDatabase(db)
            "banking" -> seedBankingDatabase(db)
            "library" -> seedLibraryDatabase(db)
            else -> seedSchoolDatabase(db)
        }
    }

    private fun seedSchoolDatabase(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE departments (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                building TEXT,
                budget REAL
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO departments VALUES
            (1, 'Computer Science', 'Turing Hall', 120000.0),
            (2, 'Mathematics', 'Euler Hall', 85000.0),
            (3, 'Physics', 'Newton Hall', 95000.0),
            (4, 'Business', 'Smith Hall', 110000.0);
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE instructors (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                department_id INTEGER,
                salary REAL,
                hire_year INTEGER
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO instructors VALUES
            (1, 'Dr. Alan Turing', 1, 95000.0, 2018),
            (2, 'Dr. Ada Lovelace', 1, 98000.0, 2016),
            (3, 'Dr. Carl Gauss', 2, 88000.0, 2019),
            (4, 'Dr. Marie Curie', 3, 92000.0, 2017),
            (5, 'Prof. John Nash', 4, 85000.0, 2021);
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE students (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                age INTEGER,
                gpa REAL,
                major TEXT,
                email TEXT,
                city TEXT
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO students VALUES
            (1, 'Alex Rivers', 21, 3.85, 'Computer Science', 'alex@univ.edu', 'Boston'),
            (2, 'Maya Lin', 20, 3.92, 'Computer Science', 'maya@univ.edu', 'Seattle'),
            (3, 'James Chen', 22, 3.40, 'Mathematics', 'james@univ.edu', 'Chicago'),
            (4, 'Sofia Rossi', 21, 3.65, 'Physics', 'sofia@univ.edu', 'New York'),
            (5, 'Liam Patel', 23, 2.95, 'Business', 'liam@univ.edu', 'Austin'),
            (6, 'Elena Gomez', 19, 3.75, 'Computer Science', 'elena@univ.edu', 'Boston'),
            (7, 'David Kim', 22, 3.10, 'Business', 'david@univ.edu', 'Denver'),
            (8, 'Chloe Becker', 20, 3.88, 'Mathematics', 'chloe@univ.edu', 'Chicago');
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE courses (
                id INTEGER PRIMARY KEY,
                title TEXT NOT NULL,
                department_id INTEGER,
                credits INTEGER
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO courses VALUES
            (101, 'Intro to Database Systems', 1, 4),
            (102, 'Data Structures & Algorithms', 1, 4),
            (103, 'Linear Algebra', 2, 3),
            (104, 'Quantum Physics', 3, 4),
            (105, 'Corporate Finance', 4, 3),
            (106, 'Advanced Machine Learning', 1, 4);
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE enrollments (
                id INTEGER PRIMARY KEY,
                student_id INTEGER,
                course_id INTEGER,
                grade TEXT,
                semester TEXT
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO enrollments VALUES
            (1, 1, 101, 'A', 'Fall 2025'),
            (2, 1, 102, 'A-', 'Spring 2026'),
            (3, 2, 101, 'A+', 'Fall 2025'),
            (4, 2, 106, 'A', 'Spring 2026'),
            (5, 3, 103, 'B+', 'Fall 2025'),
            (6, 4, 104, 'A', 'Fall 2025'),
            (7, 5, 105, 'B', 'Spring 2026'),
            (8, 6, 101, 'A-', 'Fall 2025'),
            (9, 7, 105, 'C+', 'Spring 2026'),
            (10, 8, 103, 'A', 'Fall 2025');
        """.trimIndent())
    }

    private fun seedEcommerceDatabase(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE categories (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                description TEXT
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO categories VALUES
            (1, 'Electronics', 'Phones, Laptops, Gadgets'),
            (2, 'Home & Living', 'Furniture and Kitchenware'),
            (3, 'Fashion', 'Apparel and Accessories'),
            (4, 'Books', 'Fiction and Tech Books');
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE customers (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT UNIQUE,
                city TEXT,
                registered_date TEXT
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO customers VALUES
            (1, 'Sarah Connor', 'sarah@skynet.com', 'Los Angeles', '2024-01-15'),
            (2, 'Bruce Wayne', 'bruce@wayne.com', 'Gotham', '2024-02-10'),
            (3, 'Peter Parker', 'peter@dailybugle.com', 'New York', '2024-03-22'),
            (4, 'Diana Prince', 'diana@themyscira.gov', 'Washington', '2024-04-05'),
            (5, 'Clark Kent', 'clark@planet.com', 'Metropolis', '2024-05-18'),
            (6, 'Tony Stark', 'tony@stark.com', 'New York', '2024-06-30');
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE products (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                category_id INTEGER,
                price REAL,
                stock INTEGER
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO products VALUES
            (1, 'Quantum Pro Laptop', 1, 1299.99, 45),
            (2, 'Ergo Comfort Chair', 2, 249.50, 80),
            (3, 'Wireless Noise-Canceling Buds', 1, 149.00, 120),
            (4, 'Mechanical Gaming Keyboard', 1, 89.99, 60),
            (5, 'SQL Masterclass Book', 4, 39.99, 200),
            (6, 'Minimalist Leather Jacket', 3, 180.00, 30);
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE orders (
                id INTEGER PRIMARY KEY,
                customer_id INTEGER,
                order_date TEXT,
                status TEXT,
                total_amount REAL
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO orders VALUES
            (1001, 1, '2025-01-10', 'Delivered', 1448.99),
            (1002, 2, '2025-01-12', 'Delivered', 2599.98),
            (1003, 3, '2025-01-15', 'Shipped', 129.98),
            (1004, 1, '2025-02-01', 'Delivered', 249.50),
            (1005, 5, '2025-02-10', 'Processing', 149.00),
            (1006, 6, '2025-02-14', 'Delivered', 3899.97);
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE order_items (
                id INTEGER PRIMARY KEY,
                order_id INTEGER,
                product_id INTEGER,
                quantity INTEGER,
                unit_price REAL
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO order_items VALUES
            (1, 1001, 1, 1, 1299.99),
            (2, 1001, 3, 1, 149.00),
            (3, 1002, 1, 2, 1299.99),
            (4, 1003, 4, 1, 89.99),
            (5, 1003, 5, 1, 39.99),
            (6, 1004, 2, 1, 249.50),
            (7, 1005, 3, 1, 149.00),
            (8, 1006, 1, 3, 1299.99);
        """.trimIndent())
    }

    private fun seedHospitalDatabase(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE departments (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                floor INTEGER,
                head_doctor TEXT
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO departments VALUES
            (1, 'Cardiology', 3, 'Dr. Gregory House'),
            (2, 'Neurology', 4, 'Dr. Meredith Grey'),
            (3, 'Pediatrics', 2, 'Dr. John Watson'),
            (4, 'Orthopedics', 1, 'Dr. Shaun Murphy');
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE doctors (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                department_id INTEGER,
                specialty TEXT,
                experience_years INTEGER
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO doctors VALUES
            (1, 'Dr. Lisa Cuddy', 1, 'Cardiovascular Surgery', 15),
            (2, 'Dr. Derek Shepherd', 2, 'Neurosurgery', 18),
            (3, 'Dr. James Wilson', 3, 'General Pediatrics', 12),
            (4, 'Dr. Allison Cameron', 1, 'Cardiology', 8),
            (5, 'Dr. Robert Chase', 4, 'Sports Orthopedics', 10);
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE patients (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                age INTEGER,
                gender TEXT,
                blood_type TEXT,
                city TEXT
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO patients VALUES
            (1, 'Arthur Pendelton', 54, 'M', 'O+', 'Chicago'),
            (2, 'Clara Oswald', 29, 'F', 'A-', 'London'),
            (3, 'Rose Tyler', 32, 'F', 'B+', 'London'),
            (4, 'Jack Harkness', 42, 'M', 'AB+', 'Cardiff'),
            (5, 'Martha Jones', 35, 'F', 'O-', 'London'),
            (6, 'Rory Williams', 30, 'M', 'A+', 'Leadworth');
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE appointments (
                id INTEGER PRIMARY KEY,
                patient_id INTEGER,
                doctor_id INTEGER,
                appointment_date TEXT,
                status TEXT,
                fee REAL
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO appointments VALUES
            (101, 1, 1, '2025-03-01', 'Completed', 250.0),
            (102, 2, 2, '2025-03-02', 'Completed', 300.0),
            (103, 3, 3, '2025-03-03', 'Completed', 180.0),
            (104, 4, 5, '2025-03-05', 'Scheduled', 220.0),
            (105, 5, 4, '2025-03-06', 'Scheduled', 250.0),
            (106, 6, 2, '2025-03-08', 'Completed', 300.0);
        """.trimIndent())
    }

    private fun seedBankingDatabase(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE customers (
                id INTEGER PRIMARY KEY,
                full_name TEXT NOT NULL,
                phone TEXT,
                credit_score INTEGER,
                joined_date TEXT
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO customers VALUES
            (1, 'Robert Langdon', '555-0101', 785, '2022-04-12'),
            (2, 'Sophie Neveu', '555-0102', 810, '2021-08-19'),
            (3, 'Dan Brown', '555-0103', 720, '2023-01-05'),
            (4, 'Vittoria Vetra', '555-0104', 760, '2023-09-14');
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE accounts (
                id INTEGER PRIMARY KEY,
                customer_id INTEGER,
                account_type TEXT,
                balance REAL,
                status TEXT
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO accounts VALUES
            (1001, 1, 'Checking', 14500.50, 'Active'),
            (1002, 1, 'Savings', 58200.00, 'Active'),
            (1003, 2, 'Checking', 8900.25, 'Active'),
            (1004, 2, 'Investment', 125000.00, 'Active'),
            (1005, 3, 'Checking', 3200.00, 'Active'),
            (1006, 4, 'Savings', 42000.00, 'Active');
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE transactions (
                id INTEGER PRIMARY KEY,
                account_id INTEGER,
                txn_type TEXT,
                amount REAL,
                txn_date TEXT,
                description TEXT
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO transactions VALUES
            (1, 1001, 'Deposit', 3500.0, '2025-02-01', 'Monthly Salary'),
            (2, 1001, 'Withdrawal', 120.0, '2025-02-02', 'Grocery Store'),
            (3, 1002, 'Deposit', 2000.0, '2025-02-05', 'Transfer from Checking'),
            (4, 1003, 'Withdrawal', 450.0, '2025-02-07', 'Utility Payment'),
            (5, 1004, 'Deposit', 5000.0, '2025-02-10', 'Dividend Payout');
        """.trimIndent())
    }

    private fun seedLibraryDatabase(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE authors (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                nationality TEXT,
                birth_year INTEGER
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO authors VALUES
            (1, 'George Orwell', 'British', 1903),
            (2, 'J.K. Rowling', 'British', 1965),
            (3, 'Isaac Asimov', 'American', 1920),
            (4, 'Harper Lee', 'American', 1926);
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE books (
                id INTEGER PRIMARY KEY,
                title TEXT NOT NULL,
                author_id INTEGER,
                genre TEXT,
                publication_year INTEGER,
                copies INTEGER
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO books VALUES
            (1, '1984', 1, 'Dystopian', 1949, 8),
            (2, 'Animal Farm', 1, 'Political Satire', 1945, 5),
            (3, 'Harry Potter and the Sorcerer Stone', 2, 'Fantasy', 1997, 12),
            (4, 'Foundation', 3, 'Sci-Fi', 1951, 6),
            (5, 'I, Robot', 3, 'Sci-Fi', 1950, 4),
            (6, 'To Kill a Mockingbird', 4, 'Classic', 1960, 7);
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE members (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT,
                join_date TEXT,
                active INTEGER
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO members VALUES
            (1, 'Alice Smith', 'alice@read.org', '2023-01-10', 1),
            (2, 'Bob Jones', 'bob@read.org', '2023-03-15', 1),
            (3, 'Charlie Brown', 'charlie@read.org', '2023-06-20', 0);
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE loans (
                id INTEGER PRIMARY KEY,
                book_id INTEGER,
                member_id INTEGER,
                loan_date TEXT,
                return_date TEXT
            );
        """.trimIndent())

        db.execSQL("""
            INSERT INTO loans VALUES
            (1, 1, 1, '2025-01-10', '2025-01-24'),
            (2, 3, 2, '2025-01-15', NULL),
            (3, 4, 1, '2025-02-01', NULL),
            (4, 2, 3, '2025-02-05', '2025-02-18');
        """.trimIndent())
    }

    fun executeQuery(sql: String): SqlExecutionResult {
        val trimmed = sql.trim()
        if (trimmed.isEmpty()) {
            return SqlExecutionResult(
                isSuccess = false,
                query = sql,
                errorMessage = "Query is empty. Please enter an SQL statement."
            )
        }

        val db = activeDb ?: return SqlExecutionResult(
            isSuccess = false,
            query = sql,
            errorMessage = "Database not initialized."
        )

        val startTime = System.currentTimeMillis()
        val upper = trimmed.uppercase(Locale.ROOT)

        val isSelect = upper.startsWith("SELECT") ||
                upper.startsWith("WITH") ||
                upper.startsWith("PRAGMA") ||
                upper.startsWith("EXPLAIN")

        return try {
            if (isSelect) {
                // Execute SELECT query
                val cursor: Cursor = db.rawQuery(trimmed, null)
                val duration = System.currentTimeMillis() - startTime
                val colCount = cursor.columnCount
                val columnNames = (0 until colCount).map { cursor.getColumnName(it) }
                val rows = mutableListOf<List<String>>()

                cursor.use { c ->
                    while (c.moveToNext() && rows.size < 500) {
                        val row = (0 until colCount).map { i ->
                            if (c.isNull(i)) "NULL" else c.getString(i) ?: "NULL"
                        }
                        rows.add(row)
                    }
                }

                // Retrieve real execution plan
                val planSteps = mutableListOf<String>()
                try {
                    val planCursor = db.rawQuery("EXPLAIN QUERY PLAN $trimmed", null)
                    planCursor.use { pc ->
                        while (pc.moveToNext()) {
                            val detail = pc.getString(pc.getColumnIndexOrThrow("detail"))
                            planSteps.add(detail)
                        }
                    }
                } catch (_: Exception) {
                    planSteps.add("Direct Table Scan & Evaluation")
                }

                SqlExecutionResult(
                    isSuccess = true,
                    query = trimmed,
                    columns = columnNames,
                    rows = rows,
                    rowsCount = rows.size,
                    executionTimeMs = duration,
                    queryPlan = planSteps
                )
            } else {
                // Execute DML/DDL (INSERT, UPDATE, DELETE, CREATE, DROP, etc.)
                db.execSQL(trimmed)
                val duration = System.currentTimeMillis() - startTime

                // Check changes
                var changes = 0
                try {
                    val c = db.rawQuery("SELECT changes()", null)
                    c.use {
                        if (it.moveToNext()) {
                            changes = it.getInt(0)
                        }
                    }
                } catch (_: Exception) {}

                SqlExecutionResult(
                    isSuccess = true,
                    query = trimmed,
                    affectedRows = changes,
                    executionTimeMs = duration,
                    queryPlan = listOf("DML/DDL Statement executed successfully. Rows affected: $changes")
                )
            }
        } catch (e: SQLiteException) {
            val message = e.message ?: "Unknown SQL error"
            val errorAnalysis = diagnoseError(trimmed, message)
            SqlExecutionResult(
                isSuccess = false,
                query = trimmed,
                errorMessage = message,
                errorLine = errorAnalysis.line,
                errorSuggestion = errorAnalysis.suggestion
            )
        } catch (e: Exception) {
            SqlExecutionResult(
                isSuccess = false,
                query = trimmed,
                errorMessage = e.localizedMessage ?: "Execution failed"
            )
        }
    }

    private data class ErrorAnalysis(val line: Int, val suggestion: String)

    private fun diagnoseError(query: String, sqliteError: String): ErrorAnalysis {
        val lines = query.lines()
        var lineNum = 1

        val lowerErr = sqliteError.lowercase(Locale.ROOT)
        val suggestion = when {
            lowerErr.contains("no such table") -> {
                val table = sqliteError.substringAfter("no such table:").trim()
                "The table '$table' doesn't exist in the currently active database. Check the Schema Sidebar for valid tables."
            }
            lowerErr.contains("no such column") -> {
                val col = sqliteError.substringAfter("no such column:").trim()
                "The column '$col' is not found. Check table explorer to verify column spelling."
            }
            lowerErr.contains("syntax error") -> {
                "Syntax Error: Check for missing commas between SELECT columns, unmatched quotes, or improper clause order (SELECT → FROM → WHERE → GROUP BY → HAVING → ORDER BY → LIMIT)."
            }
            lowerErr.contains("misuse of aggregate") -> {
                "Aggregates like COUNT, SUM, AVG cannot be used directly in WHERE. Use HAVING clause for filtering aggregated results."
            }
            lowerErr.contains("ambiguous column name") -> {
                "Multiple joined tables share this column name. Prefix it with the table name (e.g., users.id instead of just id)."
            }
            lowerErr.contains("unique constraint failed") -> {
                "A row with this primary or unique key value already exists in the table."
            }
            else -> {
                "Verify SQL syntax and database schema alignment."
            }
        }

        return ErrorAnalysis(lineNum, suggestion)
    }

    fun getDatabaseInfo(databaseId: String): SqlDatabaseInfo {
        val db = activeDb ?: SQLiteDatabase.create(null)
        val tables = mutableListOf<SqlTable>()

        val tableCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'android_%' AND name NOT LIKE 'sqlite_%'", null)
        tableCursor.use { tc ->
            while (tc.moveToNext()) {
                val tableName = tc.getString(0)
                val columns = mutableListOf<SqlColumn>()

                val colCursor = db.rawQuery("PRAGMA table_info($tableName)", null)
                colCursor.use { cc ->
                    while (cc.moveToNext()) {
                        val colName = cc.getString(cc.getColumnIndexOrThrow("name"))
                        val colType = cc.getString(cc.getColumnIndexOrThrow("type"))
                        val notNull = cc.getInt(cc.getColumnIndexOrThrow("notnull")) == 1
                        val pk = cc.getInt(cc.getColumnIndexOrThrow("pk")) > 0

                        columns.add(
                            SqlColumn(
                                name = colName,
                                type = colType.ifEmpty { "TEXT" },
                                isPrimaryKey = pk,
                                isNullable = !notNull
                            )
                        )
                    }
                }

                // Sample rows
                val rowList = mutableListOf<Map<String, Any?>>()
                val dataCursor = db.rawQuery("SELECT * FROM $tableName LIMIT 50", null)
                dataCursor.use { dc ->
                    val names = dc.columnNames
                    while (dc.moveToNext()) {
                        val rowMap = mutableMapOf<String, Any?>()
                        for (i in names.indices) {
                            rowMap[names[i]] = if (dc.isNull(i)) "NULL" else dc.getString(i)
                        }
                        rowList.add(rowMap)
                    }
                }

                tables.add(
                    SqlTable(
                        name = tableName,
                        columns = columns,
                        rows = rowList,
                        description = "Relational entity with ${columns.size} columns and ${rowList.size} records"
                    )
                )
            }
        }

        val relationships = when (databaseId) {
            "school" -> listOf(
                SqlRelationship("instructors", "department_id", "departments", "id", "1:N"),
                SqlRelationship("students", "major", "departments", "name", "1:N"),
                SqlRelationship("courses", "department_id", "departments", "id", "1:N"),
                SqlRelationship("enrollments", "student_id", "students", "id", "1:N"),
                SqlRelationship("enrollments", "course_id", "courses", "id", "1:N")
            )
            "ecommerce" -> listOf(
                SqlRelationship("products", "category_id", "categories", "id", "1:N"),
                SqlRelationship("orders", "customer_id", "customers", "id", "1:N"),
                SqlRelationship("order_items", "order_id", "orders", "id", "1:N"),
                SqlRelationship("order_items", "product_id", "products", "id", "1:N")
            )
            "hospital" -> listOf(
                SqlRelationship("doctors", "department_id", "departments", "id", "1:N"),
                SqlRelationship("appointments", "patient_id", "patients", "id", "1:N"),
                SqlRelationship("appointments", "doctor_id", "doctors", "id", "1:N")
            )
            "banking" -> listOf(
                SqlRelationship("accounts", "customer_id", "customers", "id", "1:N"),
                SqlRelationship("transactions", "account_id", "accounts", "id", "1:N")
            )
            "library" -> listOf(
                SqlRelationship("books", "author_id", "authors", "id", "1:N"),
                SqlRelationship("loans", "book_id", "books", "id", "1:N"),
                SqlRelationship("loans", "member_id", "members", "id", "1:N")
            )
            else -> emptyList()
        }

        val (dbName, dbDesc, dbIcon) = when (databaseId) {
            "school" -> Triple("School & University", "Students, courses, instructors, departments & enrollments", "school")
            "ecommerce" -> Triple("E-Commerce Store", "Customers, products, categories, orders & line items", "shopping_cart")
            "hospital" -> Triple("Hospital System", "Patients, doctors, clinical departments & medical appointments", "local_hospital")
            "banking" -> Triple("Banking & Finance", "Customers, bank accounts, balances & fund transactions", "account_balance")
            "library" -> Triple("Library Catalog", "Books, authors, registered members & book loans", "library_books")
            else -> Triple("Database", "Simulated SQL database", "storage")
        }

        return SqlDatabaseInfo(
            id = databaseId,
            name = dbName,
            description = dbDesc,
            iconName = dbIcon,
            tables = tables,
            relationships = relationships
        )
    }

    fun formatSql(rawSql: String): String {
        var formatted = rawSql.trim()
        val keywords = listOf(
            "SELECT", "FROM", "WHERE", "INNER JOIN", "LEFT JOIN", "RIGHT JOIN",
            "FULL JOIN", "JOIN", "GROUP BY", "HAVING", "ORDER BY", "LIMIT",
            "INSERT INTO", "VALUES", "UPDATE", "SET", "DELETE FROM", "WITH"
        )
        for (kw in keywords) {
            val regex = Regex("(?i)\\b$kw\\b")
            formatted = formatted.replace(regex, "\n$kw")
        }
        return formatted.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString("\n")
    }

    fun explainQueryLogically(sql: String): List<QueryFlowStep> {
        val steps = mutableListOf<QueryFlowStep>()
        val upper = sql.uppercase(Locale.ROOT)

        steps.add(
            QueryFlowStep(
                stepNumber = 1,
                title = "SQL Parser & AST",
                description = "Validates syntax, tokenizes keywords and builds logical AST.",
                icon = "code",
                visualDetail = "Parsed ${sql.split(" ", "\n").filter { it.isNotBlank() }.size} tokens."
            )
        )

        val fromMatch = Regex("(?i)FROM\\s+([a-zA-Z0-9_]+)").find(sql)
        val fromTable = fromMatch?.groupValues?.get(1) ?: "target table"
        steps.add(
            QueryFlowStep(
                stepNumber = 2,
                title = "Table Access ($fromTable)",
                description = "Loads catalog and reads table data into memory buffer.",
                icon = "table_chart",
                visualDetail = "Scanning rows from $fromTable"
            )
        )

        if (upper.contains("JOIN")) {
            steps.add(
                QueryFlowStep(
                    stepNumber = 3,
                    title = "JOIN Resolution",
                    description = "Matches primary and foreign keys between relational tables.",
                    icon = "hub",
                    visualDetail = "Cross-referencing matching keys across tables."
                )
            )
        }

        if (upper.contains("WHERE")) {
            val whereClause = sql.substringAfter("WHERE", "").substringBefore("GROUP").substringBefore("ORDER").substringBefore("LIMIT").trim()
            steps.add(
                QueryFlowStep(
                    stepNumber = 4,
                    title = "Filter Rows (WHERE)",
                    description = "Evaluates boolean predicate on each row; discards non-matching rows.",
                    icon = "filter_alt",
                    visualDetail = "Condition: $whereClause"
                )
            )
        }

        if (upper.contains("GROUP BY")) {
            steps.add(
                QueryFlowStep(
                    stepNumber = 5,
                    title = "Grouping & Aggregates",
                    description = "Partitions rows by key and evaluates COUNT, SUM, AVG, MIN, MAX.",
                    icon = "pie_chart",
                    visualDetail = "Gathering records into distinct group buckets."
                )
            )
        }

        if (upper.contains("HAVING")) {
            steps.add(
                QueryFlowStep(
                    stepNumber = 6,
                    title = "Filter Groups (HAVING)",
                    description = "Filters aggregated groups after GROUP BY calculation.",
                    icon = "tune",
                    visualDetail = "Removing groups that do not meet aggregate condition."
                )
            )
        }

        if (upper.contains("ORDER BY")) {
            steps.add(
                QueryFlowStep(
                    stepNumber = 7,
                    title = "Sort (ORDER BY)",
                    description = "Orders remaining records in ascending or descending sequence.",
                    icon = "sort",
                    visualDetail = "Applying sort buffer."
                )
            )
        }

        val selectColumns = sql.substringAfter("SELECT", "").substringBefore("FROM").trim()
        steps.add(
            QueryFlowStep(
                stepNumber = steps.size + 1,
                title = "Project Columns (SELECT)",
                description = "Extracts requested columns and formats result grid.",
                icon = "view_column",
                visualDetail = "Selected: $selectColumns"
            )
        )

        if (upper.contains("LIMIT")) {
            val limitNum = sql.substringAfter("LIMIT").trim().split(" ").firstOrNull() ?: ""
            steps.add(
                QueryFlowStep(
                    stepNumber = steps.size + 1,
                    title = "Pagination (LIMIT)",
                    description = "Caps output to first $limitNum rows.",
                    icon = "vertical_align_bottom",
                    visualDetail = "Truncating stream to $limitNum rows"
                )
            )
        }

        return steps
    }
}
