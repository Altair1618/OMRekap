package com.k2_9.omrekap.activities

import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView
import com.k2_9.omrekap.R

class ExpandImageActivity : AppCompatActivity() {
	companion object {
		const val EXTRA_NAME_DRAWABLE_RESOURCE = "DRAWABLE_RESOURCE"
		const val EXTRA_NAME_IMAGE_RESOURCE = "IMAGE_RESOURRE"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_expand_image)

		val photoView: PhotoView = findViewById(R.id.fullscreen_content)

		val imageResource =
			if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				intent.getParcelableExtra("DATA", Uri::class.java)
			} else {
				intent.getParcelableExtra<Uri>("DATA")
			}

		if (imageResource != null) {
			photoView.setImageURI(imageResource)
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
