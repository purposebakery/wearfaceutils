package com.techlung.wearfaceutils.sample.modules.face

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.techlung.example.R
import com.techlung.wearfaceutils.WearFaceUtils
import com.techlung.wearfaceutils.sample.animation.DrawAnimation
import com.techlung.wearfaceutils.sample.generic.GenericPainter
import com.techlung.wearfaceutils.sample.utils.CircleUtil
import com.techlung.wearfaceutils.sample.utils.UiUtils

class ClockworkPainter : GenericPainter {
    val NUMBER_MARGIN_DP = 14
    val HOUR_NEEDLE_MARGIN_DP = 70
    val MINUTE_NEEDLE_MARGIN_DP = 40

    val ANIMATION_DURATION_NORMAL = 1000
    val ANIMATION_DURATION_VERY_LONG = 4000

    private var radiusTotal: Int = 0
    private var marginNumbers: Int = 0
    private var marginHourNeedle: Int = 0
    private var marginMinuteNeedle: Int = 0
    private var initialized: Boolean = false
    private var center: Point = Point()
    private var bounds: Rect = Rect()
    private var numbersLargePaint: Paint = Paint()
    private var numbersNormalPaint: Paint = Paint()
    private var dotsPaint: Paint = Paint()
    private var hourNeedlePaint: Paint = Paint()
    private var minuteNeedlePaint: Paint = Paint()

    private var debugPaint: Paint = Paint()

    private var numberAppearanceAnimation: DrawAnimation = DrawAnimation(AccelerateDecelerateInterpolator(), ANIMATION_DURATION_NORMAL)
    private var needleRotationAppearanceAnimation: DrawAnimation = DrawAnimation(DecelerateInterpolator(), ANIMATION_DURATION_VERY_LONG)
    private var needleAlphaAppearanceAnimation: DrawAnimation = DrawAnimation(LinearInterpolator(), ANIMATION_DURATION_VERY_LONG)

    private var secondsTimeDifference: Int = 0
    private var drawCounter = 0

    private var context: Context? = null

    fun initialize(context: Context, bounds: Rect) {
        initialized = true
        radiusTotal = bounds.width() / 2
        marginNumbers = UiUtils.dpToPx(context, NUMBER_MARGIN_DP)
        marginHourNeedle = UiUtils.dpToPx(context, HOUR_NEEDLE_MARGIN_DP)
        marginMinuteNeedle = UiUtils.dpToPx(context, MINUTE_NEEDLE_MARGIN_DP)

        center = Point(bounds.width() / 2, bounds.height() / 2)
        this.bounds = bounds
        this.context = context

        numbersLargePaint.color = ContextCompat.getColor(context, R.color.white)
        numbersLargePaint.textSize = context.resources.getDimension(R.dimen.face_numbers_large_size)
        numbersLargePaint.textAlign = Paint.Align.CENTER
        numbersLargePaint.isAntiAlias = true

        numbersNormalPaint.color = ContextCompat.getColor(context, R.color.white)
        numbersNormalPaint.textSize = context.resources.getDimension(R.dimen.face_numbers_normal_size)
        numbersNormalPaint.textAlign = Paint.Align.CENTER
        numbersNormalPaint.isAntiAlias = true

        hourNeedlePaint.color = ContextCompat.getColor(context, R.color.white)
        hourNeedlePaint.strokeWidth = UiUtils.dpToPx(context, 7).toFloat()
        hourNeedlePaint.strokeCap = Paint.Cap.ROUND
        hourNeedlePaint.isAntiAlias = true

        minuteNeedlePaint.color = ContextCompat.getColor(context, R.color.white)
        minuteNeedlePaint.strokeWidth = UiUtils.dpToPx(context, 4).toFloat()
        minuteNeedlePaint.strokeCap = Paint.Cap.ROUND
        minuteNeedlePaint.isAntiAlias = true

        dotsPaint.color = ContextCompat.getColor(context, R.color.white)
        dotsPaint.isAntiAlias = true

        debugPaint.color = ContextCompat.getColor(context, android.R.color.holo_red_dark)
        debugPaint.textSize = UiUtils.dpToPx(context, 20).toFloat()
        debugPaint.textAlign = Paint.Align.CENTER
        debugPaint.isAntiAlias = true

    }

    override fun setAmbientMode(isAmbient: Boolean) {
        val isAntiAlias = !isAmbient

        numbersNormalPaint.isAntiAlias = isAntiAlias
        numbersLargePaint.isAntiAlias = isAntiAlias
        hourNeedlePaint.isAntiAlias = isAntiAlias
        minuteNeedlePaint.isAntiAlias = isAntiAlias
        dotsPaint.isAntiAlias = isAntiAlias
    }

    fun startAnimation() {
        numberAppearanceAnimation.start()
        needleRotationAppearanceAnimation.start()
        needleAlphaAppearanceAnimation.start()

        drawCounter = 0
    }

    fun onDraw(context: Context, canvas: Canvas, bounds: Rect): Boolean {
        if (!initialized) {
            initialize(context, bounds)
        }

        var continueAnimation = false
        continueAnimation = adjustTimesPaint() || continueAnimation
        continueAnimation = adjustNeedlesPaint() || continueAnimation

        drawTimes(canvas)

        drawCounter++

        return continueAnimation
    }

    fun adjustTimesPaint(): Boolean {
        numbersNormalPaint.alpha = (255f * numberAppearanceAnimation.getValue()).toInt()
        numbersLargePaint.alpha = (255f * numberAppearanceAnimation.getValue()).toInt()
        dotsPaint.alpha = (255f * numberAppearanceAnimation.getValue()).toInt()

        return numberAppearanceAnimation.isAnimationRunning()
    }

    fun adjustNeedlesPaint(): Boolean {
        minuteNeedlePaint.alpha = (255f * needleAlphaAppearanceAnimation.getValue()).toInt()
        hourNeedlePaint.alpha = (255f * needleAlphaAppearanceAnimation.getValue()).toInt()

        secondsTimeDifference = ((CircleUtil.SECONDS_IN_1_HOUR.toFloat() * 2f) * (1f - needleRotationAppearanceAnimation.getValue())).toInt()
        return needleRotationAppearanceAnimation.isAnimationRunning()
    }

    fun drawTimes(canvas: Canvas) {
        drawTime(3, canvas, numbersLargePaint)
        drawTime(6, canvas, numbersLargePaint)
        drawTime(9, canvas, numbersLargePaint)
        drawTime(12, canvas, numbersLargePaint)

        drawTime(1, canvas, numbersNormalPaint)
        drawTime(2, canvas, numbersNormalPaint)
        drawTime(4, canvas, numbersNormalPaint)
        drawTime(5, canvas, numbersNormalPaint)
        drawTime(7, canvas, numbersNormalPaint)
        drawTime(8, canvas, numbersNormalPaint)
        drawTime(10, canvas, numbersNormalPaint)
        drawTime(11, canvas, numbersNormalPaint)

        drawNeedles(canvas)

        drawDots(canvas)
    }

    fun drawTime(number: Int, canvas: Canvas, paint: Paint) {
        drawText(number.toString(), canvas, paint, WearFaceUtils.pointOnFace(marginNumbers, CircleUtil.secondsToClockRadians(CircleUtil.SECONDS_IN_1_HOUR * number), bounds))
    }

    fun drawText(value: String, canvas: Canvas, paint: Paint, point: Point) {
        val r = Rect()
        paint.getTextBounds(value, 0, value.length, r)
        val yD = Math.abs(r.height()) / 2

        canvas.drawText(value, point.x.toFloat(), point.y.toFloat() + yD, paint)
    }

    fun drawNeedles(canvas: Canvas) {
        val secondsOfDay: Long = ((System.currentTimeMillis() - secondsTimeDifference * 1000) % (CircleUtil.SECONDS_IN_12_HOURS.toLong() * 1000.toLong())) / 1000.toLong() //((System.currentTimeMillis() / 1000f) - secondsTimeDifference.toFloat()) % (WatchFaceUtil.SECONDS_IN_12_HOURS.toFloat())
        val hourOfDay: Float = secondsOfDay / (60f * 60f)
        val secondsOfHour: Float = (secondsOfDay % CircleUtil.SECONDS_IN_1_HOUR.toFloat()) / CircleUtil.SECONDS_IN_1_HOUR.toFloat()

        val hoursAngle = CircleUtil.radiansToClockRadians(Math.PI * 2f * (hourOfDay / 12f))
        val minutesAngle = CircleUtil.radiansToClockRadians(Math.PI * 2f * secondsOfHour)
        val hoursOuterPoint = WearFaceUtils.pointOnFace(marginHourNeedle, hoursAngle, bounds)
        val minutesOuterPoint = WearFaceUtils.pointOnFace(marginMinuteNeedle, minutesAngle, bounds)

        canvas.drawLine(center.x.toFloat(), center.y.toFloat(), hoursOuterPoint.x.toFloat(), hoursOuterPoint.y.toFloat(), hourNeedlePaint)
        canvas.drawLine(center.x.toFloat(), center.y.toFloat(), minutesOuterPoint.x.toFloat(), minutesOuterPoint.y.toFloat(), minuteNeedlePaint)

        //canvas.drawText(drawCounter.toString() + " " + secondsTimeDifference.toString(), center.x.toFloat(), center.y.toFloat(), debugPaint)
    }

    fun drawDots(canvas: Canvas) {
        for (i in 0..60) {
            if (i % 5 == 0) {
                continue
            }
            val angle = CircleUtil.secondsToClockRadians(60 * 12 * i)
            val point = WearFaceUtils.pointOnFace(marginNumbers, angle, bounds)
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), UiUtils.dpToPx(this.context!!, 1).toFloat(), dotsPaint)
        }
    }
}
