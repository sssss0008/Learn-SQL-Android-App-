package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SqlColumn
import com.example.data.model.SqlRelationship
import com.example.data.model.SqlTable
import com.example.ui.theme.BlueGlow
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
import kotlin.math.roundToInt

@Composable
fun ErDiagramView(
    tables: List<SqlTable>,
    relationships: List<SqlRelationship>,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Navy900)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.6f, 2.0f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        // Grid background Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSpacing = 30f * scale
            val w = size.width
            val h = size.height

            var x = (offsetX % gridSpacing)
            while (x < w) {
                drawLine(
                    color = Color(0xFF1E293B).copy(alpha = 0.5f),
                    start = Offset(x, 0f),
                    end = Offset(x, h),
                    strokeWidth = 1f
                )
                x += gridSpacing
            }

            var y = (offsetY % gridSpacing)
            while (y < h) {
                drawLine(
                    color = Color(0xFF1E293B).copy(alpha = 0.5f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )
                y += gridSpacing
            }
        }

        // Draw connecting relation lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            relationships.forEachIndexed { idx, rel ->
                // Draw decorative relation vectors
                val startX = (60f + (idx * 160f) % 320f) * scale + offsetX
                val startY = (80f + (idx * 90f) % 240f) * scale + offsetY
                val endX = (240f + (idx * 140f) % 300f) * scale + offsetX
                val endY = (180f + (idx * 110f) % 260f) * scale + offsetY

                val path = Path().apply {
                    moveTo(startX, startY)
                    cubicTo(
                        startX + 60f, startY,
                        endX - 60f, endY,
                        endX, endY
                    )
                }

                drawPath(
                    path = path,
                    color = CyanAccent.copy(alpha = 0.7f),
                    style = Stroke(width = 2.5f * scale, cap = StrokeCap.Round)
                )

                // Draw relationship node dot
                drawCircle(
                    color = CyanAccent,
                    radius = 4f * scale,
                    center = Offset(startX, startY)
                )
                drawCircle(
                    color = BlueGlow,
                    radius = 4f * scale,
                    center = Offset(endX, endY)
                )
            }
        }

        // Table Entity Cards
        Row(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .padding(16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            tables.take(4).forEach { table ->
                TableEntityCard(table = table)
            }
        }

        // Legend overlay
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Navy800.copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SqlWarning))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PK", fontSize = 10.sp, fontFamily = SqlMonospace, color = SqlWarning)
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyanAccent))
                Spacer(modifier = Modifier.width(4.dp))
                Text("FK", fontSize = 10.sp, fontFamily = SqlMonospace, color = CyanAccent)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Pan/Zoom active", fontSize = 10.sp, color = TextMuted)
            }
        }
    }
}

@Composable
fun TableEntityCard(
    table: SqlTable,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, BluePrimary.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Navy800)
    ) {
        Column {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BluePrimary.copy(alpha = 0.3f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TableChart,
                    contentDescription = "Table",
                    tint = CyanAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = table.name.uppercase(),
                    fontFamily = SqlMonospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }

            Divider(color = DarkSurfaceBorder)

            // Columns List
            Column(modifier = Modifier.padding(8.dp)) {
                table.columns.forEach { col ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (col.isPrimaryKey) {
                                Surface(
                                    shape = RoundedCornerShape(3.dp),
                                    color = SqlWarning.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, SqlWarning)
                                ) {
                                    Text(
                                        text = "PK",
                                        fontFamily = SqlMonospace,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SqlWarning,
                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            } else if (col.name.endsWith("_id")) {
                                Surface(
                                    shape = RoundedCornerShape(3.dp),
                                    color = CyanAccent.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, CyanAccent)
                                ) {
                                    Text(
                                        text = "FK",
                                        fontFamily = SqlMonospace,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent,
                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            Text(
                                text = col.name,
                                fontFamily = SqlMonospace,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Text(
                            text = col.type.take(4),
                            fontFamily = SqlMonospace,
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableSchemaViewer(
    table: SqlTable,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Navy900),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = "Table Schema",
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = table.name,
                        fontFamily = SqlMonospace,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Navy800
                ) {
                    Text(
                        text = "${table.columns.size} columns • ${table.rows.size} rows",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Column Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy800)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Column Name", fontSize = 11.sp, fontFamily = SqlMonospace, color = CyanAccent, fontWeight = FontWeight.Bold)
                Text("Data Type", fontSize = 11.sp, fontFamily = SqlMonospace, color = CyanAccent, fontWeight = FontWeight.Bold)
                Text("Key", fontSize = 11.sp, fontFamily = SqlMonospace, color = CyanAccent, fontWeight = FontWeight.Bold)
            }

            table.columns.forEach { col ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = col.name,
                        fontFamily = SqlMonospace,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Text(
                        text = col.type,
                        fontFamily = SqlMonospace,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Row {
                        if (col.isPrimaryKey) {
                            Text("🔑 PK", fontSize = 10.sp, color = SqlWarning, fontWeight = FontWeight.Bold)
                        } else if (col.name.endsWith("_id")) {
                            Text("🔗 FK", fontSize = 10.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                        } else {
                            Text("—", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }
                Divider(color = DarkSurfaceBorder.copy(alpha = 0.4f))
            }
        }
    }
}
