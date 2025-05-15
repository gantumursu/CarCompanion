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

class SpeedometerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arcRect = RectF()

    private var speed = 0f
    private val maxSpeed = 220f  // Maximum speed in km/h

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    // Define colors
    private val backgroundColor = Color.DKGRAY
    private val gaugeColor = Color.BLUE
    private val textColor = Color.WHITE

    init {
        paint.isAntiAlias = true
    }

    fun setSpeed(value: Float) {
        speed = value.coerceIn(0f, maxSpeed)
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

        // Draw speed value arc
        val speedAngle = (speed / maxSpeed) * 270f
        paint.color = gaugeColor
        canvas.drawArc(arcRect, 135f, speedAngle, false, paint)

        // Draw text
        paint.style = Paint.Style.FILL
        paint.textSize = radius * 0.2f
        paint.color = textColor
        paint.typeface = Typeface.DEFAULT_BOLD

        val speedText = "${speed.toInt()} km/h"
        val textWidth = paint.measureText(speedText)
        canvas.drawText(speedText, centerX - textWidth / 2, centerY + radius * 0.5f, paint)

        // Draw title
        paint.textSize = radius * 0.15f
        val title = context.getString(R.string.speed)
        val titleWidth = paint.measureText(title)
        canvas.drawText(title, centerX - titleWidth / 2, centerY - radius * 0.3f, paint)
    }
}