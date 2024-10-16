package com.inoo.sutoriapp.ui.customview

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.inoo.sutoriapp.R

@SuppressLint("ClickableViewAccessibility")
class CustomButton : AppCompatButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var enabledBackground: Drawable =
        ContextCompat.getDrawable(context, R.drawable.bg_success_button_ripple) as Drawable
    private var disabledBackground: Drawable =
        ContextCompat.getDrawable(context, R.drawable.bg_success_button_disabled) as Drawable

    init {
        isClickable = true
        updateBackground()
        textSize = 12f
        gravity = Gravity.CENTER

        setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    animatePress(v)
                    false
                }
                android.view.MotionEvent.ACTION_UP -> {
                    animateRelease(v)
                    false
                }
                android.view.MotionEvent.ACTION_CANCEL -> {
                    animateRelease(v)
                    false
                }
                else -> false
            }
        }
    }

    private fun animatePress(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.95f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.95f)
        scaleX.duration = 150
        scaleY.duration = 150
        scaleX.start()
        scaleY.start()
    }

    private fun animateRelease(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f)
        scaleX.duration = 150
        scaleY.duration = 150
        scaleX.start()
        scaleY.start()
    }

    private fun updateBackground() {
        background = if (isEnabled) enabledBackground else disabledBackground
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        updateBackground()
    }
}