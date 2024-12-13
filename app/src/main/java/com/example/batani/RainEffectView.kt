package com.example.batani

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class RainEffectView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val rainPaint = Paint().apply {
        color = 0xAAFFFFFF.toInt() // Warna hujan (semi-transparan putih)
        strokeWidth = 5f
    }
    private val drops = mutableListOf<RainDrop>()

    init {
        generateRainDrops()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.let { super.onDraw(it) }

        // Gambar setiap tetesan hujan
        for (drop in drops) {
            canvas.drawLine(drop.x, drop.y, drop.x, drop.y + drop.length, rainPaint)
            drop.y += drop.speed

            // Reset tetesan hujan jika melewati batas layar
            if (drop.y > height) {
                drop.y = -drop.length
            }
        }

        // Mengulang animasi
        invalidate()
    }

    private fun generateRainDrops() {
        // Buat tetesan hujan secara acak
        for (i in 0..100) {
            drops.add(
                RainDrop(
                    x = Random.nextInt(0, width.coerceAtLeast(1)).toFloat(),
                    y = Random.nextInt(0, height.coerceAtLeast(1)).toFloat(),
                    length = Random.nextInt(10, 50).toFloat(),
                    speed = Random.nextInt(5, 15).toFloat()
                )
            )
        }
    }

    data class RainDrop(
        var x: Float,
        var y: Float,
        var length: Float,
        var speed: Float
    )
}
