package com.inoo.sutoriapp.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.inoo.sutoriapp.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var clearButtonImage: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable

    init {
        setPaddingRelative(40, 20, 60, 20)

        addTextChangedListener { text ->
            if (text.toString().isNotEmpty()) {
                showClearButton()
                validateEmail(text.toString())
            } else {
                hideClearButton()
            }
        }
        setOnTouchListener(this)
        setupEditTextStyle()
    }

    private fun validateEmail(email: String) {
        error = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            context.getString(R.string.email_error)
        } else {
            null
        }
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(startOfTheText: Drawable? = null, topOfTheText: Drawable? = null, endOfTheText: Drawable? = null, bottomOfTheText: Drawable? = null) {
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                if (event.x < clearButtonEnd) isClearButtonClicked = true
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                if (event.x > clearButtonStart) isClearButtonClicked = true
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                        text?.clear()
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            }
            return false
        }
        return false
    }

    private fun setupEditTextStyle() {
        background = ContextCompat.getDrawable(context, R.drawable.custom_edit_text_bg)
        textSize = 14f
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}
