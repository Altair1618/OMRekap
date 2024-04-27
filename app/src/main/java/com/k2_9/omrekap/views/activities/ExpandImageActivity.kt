package com.k2_9.omrekap.views.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView
import com.k2_9.omrekap.R

class ExpandImageActivity : AppCompatActivity() {
	companion object {
		const val EXTRA_NAME_DRAWABLE_RESOURCE = "DRAWABLE_RESOURCE"
		const val EXTRA_NAME_IMAGE_RESOURCE = "IMAGE_RESOURCE"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_expand_image)

		val photoView: PhotoView = findViewById(R.id.fullscreen_content)

		val imageResource = intent.getStringExtra(EXTRA_NAME_IMAGE_RESOURCE)

		if (imageResource != null) {
			val stream = openFileInput(imageResource)
			val bitmap = BitmapFactory.decodeStream(stream)
			stream.close()

			photoView.setImageBitmap(bitmap)

			deleteFile(imageResource)
		} else {
			// Retrieve the image resource ID from the intent
			val drawableResource = intent.getIntExtra(EXTRA_NAME_DRAWABLE_RESOURCE, 0)
			// Set the image resource to PhotoView
			if (drawableResource != 0) {
				photoView.setImageResource(drawableResource)
			} else {
				photoView.setImageResource(R.drawable.ic_image)
			}
		}
	}
}
