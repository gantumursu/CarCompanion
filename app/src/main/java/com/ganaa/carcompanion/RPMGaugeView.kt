package com.ganaa.carcompanion.ui.gauge

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.ganaa.carcompanion.R
import kotlin.math.min

class RPMGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arcRect = RectF()

    private var rpm = 0f
    private val maxRpm = 8000f

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    // Define colors
    private val backgroundColor = Color.DKGRAY
    private val gaugeColor = Color.RED
    private val textColor = Color.WHITE
    private val lowRpmColor = Color.GREEN
    private val mediumRpmColor = Color.YELLOW
    private val highRpmColor = Color.RED

    init {
        paint.isAntiAlias = true
    }

    fun setRpm(value: Float) {
        rpm = value.coerceIn(0f, maxRpm)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = w / 2f
        centerY = h / 2f
        radius = min(centerX, centerY) * 0.8f

        arcRect.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background arc
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = radius * 0.1f
        paint.color = backgroundColor
        canvas.drawArc(arcRect, 135f, 270f, false, paint)

        // Draw colored segments
        drawColoredSegments(canvas)

        // Draw RPM value arc
        val rpmAngle = (rpm / maxRpm) * 270f
        paint.color = getCurrentColor()
        canvas.drawArc(arcRect, 135f, rpmAngle, false, paint)

        // Draw text
        paint.style = Paint.Style.FILL
        paint.textSize = radius * 0.2f
        paint.color = textColor
        paint.typeface = Typeface.DEFAULT_BOLD

        val rpmText = "${rpm.toInt()} RPM"
        val textWidth = paint.measureText(rpmText)
        canvas.drawText(rpmText, centerX - textWidth / 2, centerY + radius * 0.5f, paint)

        // Draw title
        paint.textSize = radius * 0.15f
        val title = context.getString(R.string.rpm)
        val titleWidth = paint.measureText(title)
        canvas.drawText(title, centerX - titleWidth / 2, centerY - radius * 0.3f, paint)
    }

    private fun drawColoredSegments(canvas: Canvas) {
        // Draw green segment (0-5000 RPM)
        paint.color = lowRpmColor
        val greenAngle = (5000f / maxRpm) * 270f
        canvas.drawArc(arcRect, 135f, greenAngle, false, paint)

        // Draw yellow segment (5000-6500 RPM)
        paint.color = mediumRpmColor
        val yellowStart = 135f + greenAngle
        val yellowAngle = ((6500f - 5000f) / maxRpm) * 270f
        canvas.drawArc(arcRect, yellowStart, yellowAngle, false, paint)

        // Draw red segment (6500-8000 RPM)
        paint.color = highRpmColor
        val redStart = yellowStart + yellowAngle
        val redAngle = ((8000f - 6500f) / maxRpm) * 270f
        canvas.drawArc(arcRect, redStart, redAngle, false, paint)
    }

    private fun getCurrentColor(): Int {
        return when {
            rpm < 5000f -> lowRpmColor
            rpm < 6500f -> mediumRpmColor
            else -> highRpmColor
        }
    }
}