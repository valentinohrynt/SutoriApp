package com.inoo.sutoriapp.ui.customview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton

class CustomImageButton : AppCompatImageButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var pressedColor: Int = android.graphics.Color.WHITE

    init {
        isClickable = true

        setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    animatePress(v)
                    true
                }
                android.view.MotionEvent.ACTION_UP -> {
                    animateRelease(v)
                    v.performClick()
                    true
                }
                android.view.MotionEvent.ACTION_CANCEL -> {
                    animateRelease(v)
                    true
                }
                else -> false
            }
        }
    }

    private fun animatePress(view: View) {
        ObjectAnimator.ofFloat(view, "scaleX", 0.95f).apply {
            duration = 150
            start()
        }
        ObjectAnimator.ofFloat(view, "scaleY", 0.95f).apply {
            duration = 150
            start()
        }

        @Suppress("DEPRECATION")
        drawable?.setColorFilter(pressedColor, PorterDuff.Mode.SRC_IN)
    }

    private fun animateRelease(view: View) {
        ObjectAnimator.ofFloat(view, "scaleX", 1.0f).apply {
            duration = 150
            start()
        }
        ObjectAnimator.ofFloat(view, "scaleY", 1.0f).apply {
            duration = 150
            start()
        }

        drawable?.clearColorFilter()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}
