package com.techlung.wearfaceutils

import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import android.view.WindowInsets

object WearFaceUtils {

    var initalized = false
    var insets : WindowInsets? = null

    fun init(insets: WindowInsets) {
        this.insets = insets
        this.initalized = true;
    }

    fun pointOnCircleFace(margin: Int, angle : Double, bounds : Rect) : Point {
        val center = Point(bounds.width() / 2, bounds.height() / 2)
        val totalRadius = bounds.height() / 2
        val radius = totalRadius - margin
        return pointOnCircle(radius, angle, center)
    }

    fun pointOnChinFace(margin: Int, angle : Double, bounds : Rect, chinInset : Int) : Point {
        val center = Point(bounds.width() / 2, bounds.height() / 2)
        val totalRadius = bounds.height() / 2
        val radius = totalRadius - margin
        return pointOnChinFace(radius, totalRadius, chinInset, angle, center)
    }

    fun pointOnRectFace(margin: Int, angle : Double, bounds : Rect) : Point {
        return pointOnRect(margin, angle, bounds)
    }

    fun pointOnFace(margin: Int, angle : Double, bounds : Rect) : Point {
        if (!initalized) {
            Log.e(WearFaceUtils.javaClass.name, "WearFaceUtils not initialized")
            return Point(0,0)
        } else {
            if (insets != null && insets!!.isRound) {
                val center = Point(bounds.width() / 2, bounds.height() / 2)
                val totalRadius = bounds.height() / 2
                val radius = totalRadius - margin
                if (insets!!.systemWindowInsetBottom > 0) {
                    return pointOnChinFace(radius, totalRadius, insets!!.systemWindowInsetBottom, angle, center)
                } else {
                    return pointOnCircle(radius, angle, center)
                }
            } else {
                return pointOnRect(margin, angle, bounds)
            }
        }
    }

    private fun pointOnRect(margin: Int, angle : Double, bounds : Rect) : Point {
        val height = bounds.height() - margin * 2
        val width = bounds.width() - margin * 2

        val twoPI = (Math.PI*2).toFloat()
        var theta = twoPI - angle

        while (theta < -Math.PI) {
            theta += twoPI
        }

        while (theta > Math.PI) {
            theta -= twoPI
        }

        val rectAtan = Math.atan2(height.toDouble(), width.toDouble())
        val tanTheta = Math.tan(theta)
        val region : Int

        if ((theta > -rectAtan) && (theta <= rectAtan)) {
            region = 1
        } else if ((theta > rectAtan) && (theta <= (Math.PI - rectAtan))) {
            region = 2
        } else if ((theta > (Math.PI - rectAtan)) || (theta <= -(Math.PI - rectAtan))) {
            region = 3
        } else {
            region = 4
        }

        val edgePoint = Point(width/2, height/2)
        var xFactor = 1
        var yFactor = 1

        when (region) {
            1 -> yFactor = -1
            2 -> yFactor = -1
            3 -> xFactor = -1
            4 -> xFactor = -1
        }

        if ((region == 1) || (region == 3)) {
            edgePoint.x += (xFactor * (width / 2.0)).toInt()                        // "Z0"
            edgePoint.y += (yFactor * (width / 2.0) * tanTheta).toInt()
        } else {
            edgePoint.x += (xFactor * (height / (2.0 * tanTheta))).toInt()                        // "Z1"
            edgePoint.y += (yFactor * (height /  2.0)).toInt()
        }

        edgePoint.x += margin
        edgePoint.y += margin

        return edgePoint;
    }

    private fun pointOnChinFace(radius: Int, totalRadius: Int, chinInset: Int, angleInRadians: Double, center: Point) : Point {
        val chinInsetScaled: Int = (((radius.toFloat() * 2)  / (totalRadius.toFloat() * 2)) * chinInset).toInt()
        if (isInChin(angleInRadians, radius, chinInsetScaled)) {
            return pointOnChin(radius, chinInsetScaled, angleInRadians, center)
        } else {
            return pointOnCircle(radius, angleInRadians, center)
        }
    }

    private fun isInChin(angleInRadians: Double, radius: Int, chinInset: Int) : Boolean {
        val angleLowerBound : Float = (Math.PI * 0.5f - Math.acos(1 - chinInset.toDouble() / radius.toDouble())).toFloat()
        val angleUpperBound : Float = (Math.PI * 0.5f + Math.acos(1 - chinInset.toDouble() / radius.toDouble())).toFloat()
        return angleInRadians > angleLowerBound && angleInRadians < angleUpperBound
    }

    private fun pointOnCircle(radius: Int, angleInRadians: Double, center: Point): Point {
        val x = (radius * Math.cos(angleInRadians)).toInt() + center.x
        val y = (radius * Math.sin(angleInRadians)).toInt() + center.y
        return Point(x, y)
    }

    private fun pointOnChin(radius: Int, chinInset: Int, angleInRadians: Double, center: Point): Point {
        val angleTranslation = (Math.PI * 0.5f + Math.acos(1 - chinInset.toDouble() / radius.toDouble()))
        val x = ((radius - chinInset) * Math.tan(Math.PI - Math.acos(1 - chinInset.toDouble() / radius.toDouble()) - angleInRadians + angleTranslation)).toInt() + center.x
        val y = (radius - chinInset) + center.y
        return Point(x, y)
    }
}
