/*
 * Copyright 2021 Vitaliy Sychov
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.javavirys.sectorprogressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.min

@Suppress("unused")
class SectorProgressBar
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    private val coroutineScope = CoroutineScope(coroutineContext)

    private val outerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val radialPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val outerRadialPaint: Paint

    private val centerBackgroundImage: Drawable?

    private var centerForegroundImage: Drawable?

    private var rcpFromProgress: Int = 0

    private var rcpProgress: Int

    private var progressAnimationFlag: Boolean = false

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.SectorProgressBar,
            defStyleAttr,
            defStyleRes
        )

        centerBackgroundImage =
            typedArray.getDrawable(R.styleable.SectorProgressBar_spbCenterBackgroundImage)

        centerForegroundImage =
            typedArray.getDrawable(R.styleable.SectorProgressBar_spbCenterForegroundImage)

        val outerCircleColor =
            typedArray.getColor(R.styleable.SectorProgressBar_spbOuterCircleColor, 0x33FFFFFF)

        val outerCircleWidth =
            typedArray.getDimension(R.styleable.SectorProgressBar_spbOuterCircleWidth, 6f)

        val rcpBackgroundRadialColor = typedArray.getColor(
            R.styleable.SectorProgressBar_spbBackgroundRadialColor,
            0x33FFFFFF
        )

        val rcpForegroundRadialColor = typedArray.getColor(
            R.styleable.SectorProgressBar_spbForegroundRadialColor,
            Color.WHITE
        )

        rcpProgress = typedArray.getInt(R.styleable.SectorProgressBar_spbProgress, 0)

        outerCirclePaint.style = Paint.Style.STROKE
        outerCirclePaint.strokeWidth = outerCircleWidth
        outerCirclePaint.color = outerCircleColor

        radialPaint.style = Paint.Style.FILL
        radialPaint.strokeWidth = 4f
        radialPaint.color = rcpBackgroundRadialColor

        outerRadialPaint = Paint(radialPaint)
        outerRadialPaint.color = rcpForegroundRadialColor

        typedArray.recycle()

        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        job.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancel()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCenterImages(canvas)
        drawRadials(canvas)
        drawOuterCircle(canvas)
    }

    private fun drawRadials(canvas: Canvas) {
        val size = min(width, height)
        val startPos = (size * 0.01 * 7).toInt()
        val endPos = (size * 0.01 * 14).toInt()

        for (i in 1..360) {
            if ((i / 2) % 2 == 0) continue
            canvas.save()
            canvas.translate(width / 2f, height / 2f)
            canvas.rotate(i.toFloat())
            canvas.drawLine(0f, -height / 2f + startPos, 0f, -height / 2f + endPos, radialPaint)
            canvas.restore()
        }

        drawProgress(canvas, startPos, endPos)
    }

    private fun drawProgress(canvas: Canvas, startPos: Int, endPos: Int) {
        val fromValue = (360 * rcpFromProgress) / 100
        val toValue = (360 * rcpProgress) / 100

        for (i in fromValue..toValue) {
            if ((i / 2) % 2 == 0) continue
            canvas.save()
            canvas.translate(width / 2f, height / 2f)
            canvas.rotate(i.toFloat())
            canvas.drawLine(
                0f,
                -height / 2f + startPos,
                0f,
                -height / 2f + endPos,
                outerRadialPaint
            )
            canvas.restore()
        }
    }

    private fun drawCenterImages(canvas: Canvas) {
        centerBackgroundImage?.let {
            val size = min(width, height)
            val startPos = (size * 0.01 * 19.44).toInt()
            it.setBounds(startPos, startPos, size - startPos, size - startPos)
            it.draw(canvas)
        }

        centerForegroundImage?.let {
            canvas.save()
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            canvas.translate((width - it.intrinsicWidth) / 2f, (height - it.intrinsicHeight) / 2f)
            it.draw(canvas)
            canvas.restore()
        }
    }

    private fun drawOuterCircle(canvas: Canvas) {
        val radius = min(width, height) / 2f - outerCirclePaint.strokeWidth
        canvas.drawCircle(width / 2f, height / 2f, radius, outerCirclePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val defaultWidth = 190 + paddingLeft + paddingRight
        val defaultHeight = 190 + paddingTop + paddingBottom
        val measuredWidth = reconcileSize(defaultWidth, widthMeasureSpec)
        val measuredHeight = reconcileSize(defaultHeight, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    private fun reconcileSize(size: Int, measureSpec: Int): Int {
        val measureSpecMode = MeasureSpec.getMode(measureSpec)
        val measureSpecSize = MeasureSpec.getSize(measureSpec)
        return when (measureSpecMode) {
            MeasureSpec.EXACTLY -> measureSpecSize
            MeasureSpec.UNSPECIFIED -> size
            MeasureSpec.AT_MOST -> if (measureSpecSize < size) measureSpecSize else size
            else -> size
        }
    }

    fun getProgress() = rcpProgress

    fun setProgress(progress: Int) {
        if (progress == INFINITE) {
            startAnimationProgress()
            return
        } else {
            stopAnimationProgress()
        }

        if (progress != rcpProgress) {
            rcpProgress = when {
                progress > 100 -> 100
                progress < 0 -> 0
                else -> progress
            }
            invalidate()
        }
    }

    private fun startAnimationProgress() {
        if (progressAnimationFlag) return
        progressAnimationFlag = true
        coroutineScope.launch(Dispatchers.IO) {
            while (progressAnimationFlag) {
                rcpFromProgress = 0
                incrementValueWithDelay { rcpProgress = it }
                incrementValueWithDelay { rcpFromProgress = it }
            }
            rcpProgress = 0
            rcpFromProgress = 0
        }
    }

    private suspend fun incrementValueWithDelay(
        range: IntRange = 0..100,
        callback: (value: Int) -> Unit
    ) {
        for (i in range) {
            if (progressAnimationFlag.not()) return
            callback(i)
            invalidateWithDelay(50)
        }
    }

    private suspend fun invalidateWithDelay(delayInMs: Long) {
        withContext(Dispatchers.Main) { invalidate() }
        delay(delayInMs)
    }

    private fun stopAnimationProgress() {
        progressAnimationFlag = false
    }

    fun setCenterForegroundImage(@DrawableRes drawableId: Int) {
        centerForegroundImage = AppCompatResources.getDrawable(context, drawableId)
        invalidate()
    }

    companion object {
        const val INFINITE = Int.MIN_VALUE
    }
}