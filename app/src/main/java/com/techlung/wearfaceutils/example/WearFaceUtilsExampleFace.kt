package com.techlung.wearfaceutils.example

import android.graphics.Canvas
import android.graphics.Rect
import android.support.wearable.watchface.WatchFaceService
import android.view.WindowInsets
import com.techlung.wearfaceutils.WearFaceUtils
import com.techlung.wearfaceutils.example.generic.BaseWatchFaceService
import com.techlung.wearfaceutils.example.modules.face.BackgroundPainter
import com.techlung.wearfaceutils.example.modules.face.ClockworkPainter

class WearFaceUtilsExampleFace : BaseWatchFaceService() {

    override fun onCreateEngine(): Engine {
        return Engine()
    }

    inner class Engine : BaseWatchFaceService.BaseWatchFaceEngine() {

        private var clockworkPainter: ClockworkPainter = ClockworkPainter()
        private var backgroundPainter: BackgroundPainter = BackgroundPainter()

        override fun setAmbientMode(isAmbient: Boolean) {
            clockworkPainter.setAmbientMode(isAmbient)
            backgroundPainter.setAmbientMode(isAmbient)
        }

        override fun setInsets(insets: WindowInsets) {
            WearFaceUtils.init(insets)
        }

        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            when (tapType) {
                WatchFaceService.TAP_TYPE_TOUCH -> {
                }
                WatchFaceService.TAP_TYPE_TOUCH_CANCEL -> {
                }
                WatchFaceService.TAP_TYPE_TAP -> {
                    clockworkPainter.startAnimation()
                }
            }
            invalidate()
        }

        override fun onDraw(canvas: Canvas?, bounds: Rect?) {
            if (canvas == null || bounds == null) {
                return
            }

            val canvasNonNull: Canvas = canvas
            val boundsNonNull: Rect = bounds

            var continueAnimation = false

            continueAnimation = backgroundPainter.onDraw(applicationContext, canvasNonNull, boundsNonNull) || continueAnimation
            continueAnimation = clockworkPainter.onDraw(applicationContext, canvasNonNull, boundsNonNull) || continueAnimation

            if (continueAnimation) {
                invalidate()
            }
        }

    }
}
