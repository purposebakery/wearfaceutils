package com.techlung.wearfaceutils.sample.generic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.view.SurfaceHolder
import android.view.WindowInsets
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit

abstract class BaseWatchFaceService : CanvasWatchFaceService() {

    private class EngineHandler internal constructor(reference: BaseWatchFaceEngine) : Handler() {
        private val mWeakReference: WeakReference<BaseWatchFaceEngine>

        init {
            mWeakReference = WeakReference(reference)
        }

        override fun handleMessage(msg: Message) {
            val engine = mWeakReference.get()
            if (engine != null) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
                }
            }
        }
    }

    abstract inner class BaseWatchFaceEngine : CanvasWatchFaceService.Engine() {

        private var timeZoneReceiverRegistered = false
        private val updateTimeHandler = EngineHandler(this)
        private var lowBitAmbient: Boolean = false
        private var ambient: Boolean = false
        protected var calendar: Calendar? = null
            private set

        val timeZoneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                calendar!!.timeZone = TimeZone.getDefault()
                invalidate()
            }
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }


        override fun onDraw(canvas: Canvas?, bounds: Rect?) {
            val now = System.currentTimeMillis()
            calendar!!.timeInMillis = now
        }

        override fun onCreate(holder: SurfaceHolder?) {
            super.onCreate(holder)


            setWatchFaceStyle(WatchFaceStyle.Builder(this@BaseWatchFaceService)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build())

            calendar = Calendar.getInstance()
        }


        override fun onDestroy() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            super.onDestroy()
        }

        override fun onPropertiesChanged(properties: Bundle?) {
            super.onPropertiesChanged(properties)
            lowBitAmbient = properties!!.getBoolean(WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false)
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            if (ambient != inAmbientMode) {
                ambient = inAmbientMode
                if (lowBitAmbient) {
                    setAmbientMode(inAmbientMode)
                }
                invalidate()
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer()
        }

        fun updateTimer() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            if (shouldTimerBeRunning()) {
                updateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }


        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                registerReceiver()

                // Update time zone in case it changed while we weren't visible.
                calendar!!.timeZone = TimeZone.getDefault()
                invalidate()
            } else {
                unregisterReceiver()
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer()
        }

        private fun registerReceiver() {
            if (timeZoneReceiverRegistered) {
                return
            }
            timeZoneReceiverRegistered = true
            val filter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
            this@BaseWatchFaceService.registerReceiver(timeZoneReceiver, filter)
        }

        private fun unregisterReceiver() {
            if (!timeZoneReceiverRegistered) {
                return
            }
            timeZoneReceiverRegistered = false
            this@BaseWatchFaceService.unregisterReceiver(timeZoneReceiver)
        }

        /**
         * Returns whether the [.updateTimeHandler] timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private fun shouldTimerBeRunning(): Boolean {
            return isVisible && !isInAmbientMode
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        fun handleUpdateTimeMessage() {
            invalidate()
            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
                updateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }

        override fun onApplyWindowInsets(insets: WindowInsets) {
            super.onApplyWindowInsets(insets)
            setInsets(insets)
        }

        internal abstract fun setAmbientMode(isAmbient: Boolean)
        internal abstract fun setInsets(insets: WindowInsets)
    }

    companion object {

        /**
         * Update rate in milliseconds for interactive mode. We update once a second since seconds are
         * displayed in interactive mode.
         */
        private val INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1)

        private val MSG_UPDATE_TIME = 0
    }

}
