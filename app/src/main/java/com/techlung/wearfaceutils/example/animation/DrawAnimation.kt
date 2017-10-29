package com.techlung.wearfaceutils.example.animation

import android.view.animation.Interpolator

class DrawAnimation(val interpolator: Interpolator, val durationMillis: Int) {
    var startTime: Long = 0
    var animationRunning: Boolean = false

    fun start() {
        startTime = System.currentTimeMillis()
        animationRunning = true
    }

    fun getValue(): Float {
        val diff: Long = System.currentTimeMillis() - startTime
        if (diff > durationMillis) {
            animationRunning = false;
            return interpolator.getInterpolation(1f)
        } else {
            animationRunning = true;
            return interpolator.getInterpolation(diff.toFloat() / durationMillis.toFloat())
        }
    }

    fun isAnimationRunning(): Boolean {
        return animationRunning
    }
}