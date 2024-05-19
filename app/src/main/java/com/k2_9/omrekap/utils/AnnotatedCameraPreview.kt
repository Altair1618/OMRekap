package com.k2_9.omrekap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class AnnotatedCameraPreview(context: Context, attrs: AttributeSet) : View(context, attrs) {
	private var bitmap: Bitmap? = null

	fun updateBitmap(bitmap: Bitmap) {
		this.bitmap = bitmap
		invalidate()
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)

		bitmap?.let {
			canvas.drawBitmap(it, 0f, 0f, null)
		}
	}
}
