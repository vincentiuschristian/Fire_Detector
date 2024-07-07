package com.dev.firedetector.customview

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.dev.firedetector.R
import com.google.android.material.textfield.TextInputLayout

class CustomTextInputLayout: TextInputLayout {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }
    private fun init(){
        boxStrokeWidth = 0
        boxStrokeWidthFocused = 0
        isHintEnabled = false
        errorIconDrawable = ContextCompat.getDrawable(context, R.drawable.baseline_error)
        setErrorTextColor(ContextCompat.getColorStateList(context, R.color.black))
        setErrorIconTintList(ContextCompat.getColorStateList(context, R.color.teal_700))
    }
}