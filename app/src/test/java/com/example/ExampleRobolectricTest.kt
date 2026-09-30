package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.engine.SqlExecutionEngine
import com.example.data.repository.SqlCurriculumRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Learn SQL", appName)
  }

  @Test
  fun `sql execution engine executes queries on in-memory database`() {
    val engine = SqlExecutionEngine()
    val result = engine.executeQuery("SELECT name, gpa FROM students WHERE gpa >= 3.8 ORDER BY gpa DESC;")
    assertTrue(result.isSuccess)
    assertTrue(result.rows.isNotEmpty())
    assertEquals(2, result.columns.size)
  }

  @Test
  fun `curriculum repository contains 39 levels`() {
    assertEquals(39, SqlCurriculumRepository.allLessons.size)
    assertEquals(1, SqlCurriculumRepository.allLessons.first().levelNumber)
    assertEquals(39, SqlCurriculumRepository.allLessons.last().levelNumber)
  }
}
