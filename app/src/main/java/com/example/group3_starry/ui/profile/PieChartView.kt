package com.example.group3_starry.ui.profile

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFB6C1")
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private var planetPositions = mutableListOf<PlanetPosition>()
    private val bounds = RectF()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 30f
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
    }

    private val housePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFE4E1")
        strokeWidth = 5f
        style = Paint.Style.FILL_AND_STROKE
    }

    private val zodiacPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E0FFFF")
        strokeWidth = 5f
        style = Paint.Style.FILL_AND_STROKE
    }

    private val aspectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    data class PlanetPosition(
        val symbol: String,
        val degree: Float,
        val color: Int
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = 50f
        val diameter = minOf(w, h).toFloat() - (padding * 2)
        bounds.set(padding, padding, diameter + padding, diameter + padding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw zodiac background (outer circle)
        for (i in 0..11) {
            val startAngle = i * 30f
            val sweepAngle = 30f
            canvas.drawArc(bounds, startAngle, sweepAngle, true, zodiacPaint)
        }

        // Draw houses background (inner circle)
        val houseBounds = RectF(
            bounds.left + bounds.width() * 0.1f,
            bounds.top + bounds.height() * 0.1f,
            bounds.right - bounds.width() * 0.1f,
            bounds.bottom - bounds.height() * 0.1f
        )
        for (i in 0..11) {
            val startAngle = i * 30f
            val sweepAngle = 30f
            canvas.drawArc(houseBounds, startAngle, sweepAngle, true, housePaint)
        }

        // Draw house lines
        for (i in 0..11) {
            val angle = i * 30f
            val radians = Math.toRadians(angle.toDouble())
            val startX = bounds.centerX()
            val startY = bounds.centerY()
            val endX = bounds.centerX() + cos(radians).toFloat() * bounds.width() / 2
            val endY = bounds.centerY() + sin(radians).toFloat() * bounds.height() / 2
            canvas.drawLine(startX, startY, endX, endY, paint)
        }

        // Draw zodiac symbols
        val zodiacSymbols = listOf(
            "♈", "♉", "♊", "♋", "♌", "♍",
            "♎", "♏", "♐", "♑", "♒", "♓"
        )
        for (i in zodiacSymbols.indices) {
            val angle = i * 30f + 15f
            val radians = Math.toRadians(angle.toDouble())
            val radius = bounds.width() / 2 * 0.9f
            val x = bounds.centerX() + cos(radians).toFloat() * radius
            val y = bounds.centerY() + sin(radians).toFloat() * radius
            canvas.drawText(zodiacSymbols[i], x, y, textPaint)
        }

        // Draw planets
        planetPositions.forEach { planet ->
            val radians = Math.toRadians(planet.degree.toDouble())
            val radius = bounds.width() / 2 * 0.7f // 70% of radius
            val x = bounds.centerX() + cos(radians).toFloat() * radius
            val y = bounds.centerY() + sin(radians).toFloat() * radius

            textPaint.color = planet.color
            canvas.drawCircle(x, y, 15f, textPaint)
            canvas.drawText(planet.symbol, x, y - 20f, textPaint)
        }

        // Draw aspects between planets
        for (i in planetPositions.indices) {
            for (j in i + 1 until planetPositions.size) {
                val planet1 = planetPositions[i]
                val planet2 = planetPositions[j]
                val angleDifference = Math.abs(planet1.degree - planet2.degree)

                if (isMajorAspect(angleDifference)) {
                    aspectPaint.color = getAspectColor(angleDifference)
                    val radians1 = Math.toRadians(planet1.degree.toDouble())
                    val x1 = bounds.centerX() + cos(radians1).toFloat() * bounds.width() / 2 * 0.7f
                    val y1 = bounds.centerY() + sin(radians1).toFloat() * bounds.height() / 2 * 0.7f

                    val radians2 = Math.toRadians(planet2.degree.toDouble())
                    val x2 = bounds.centerX() + cos(radians2).toFloat() * bounds.width() / 2 * 0.7f
                    val y2 = bounds.centerY() + sin(radians2).toFloat() * bounds.height() / 2 * 0.7f

                    canvas.drawLine(x1, y1, x2, y2, aspectPaint)
                }
            }
        }
    }

    private fun isMajorAspect(angle: Float): Boolean {
        val majorAspects = listOf(0f, 60f, 90f, 120f, 180f)
        return majorAspects.any { aspect -> Math.abs(angle - aspect) < 5 }
    }

    private fun getAspectColor(angle: Float): Int {
        return when {
            Math.abs(angle - 0f) < 5 -> Color.RED // Conjunction
            Math.abs(angle - 60f) < 5 -> Color.GREEN // Sextile
            Math.abs(angle - 90f) < 5 -> Color.BLUE // Square
            Math.abs(angle - 120f) < 5 -> Color.MAGENTA // Trine
            Math.abs(angle - 180f) < 5 -> Color.YELLOW // Opposition
            else -> Color.GRAY
        }
    }

    fun setPlanetPositions(positions: List<PlanetPosition>) {
        planetPositions.clear()
        planetPositions.addAll(positions)
        invalidate()
    }
}
