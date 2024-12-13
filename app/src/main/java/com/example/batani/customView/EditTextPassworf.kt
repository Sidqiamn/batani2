package com.example.submisiawal_intermediete.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class EditTextPassworf  @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val errorPaint: Paint = Paint().apply {
        color = Color.RED
        textSize = 40f
    }
    private var errorMessage: String? = null

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length < 8) {
                    errorMessage = "Password harus lebih dari 8 karakter"
                    error = errorMessage
                } else {
                    errorMessage = null
                    error = null
                }
                invalidate()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        errorMessage?.let {
            canvas.drawText(it, paddingLeft.toFloat(), (height + paddingTop).toFloat(), errorPaint)
        }
    }
}