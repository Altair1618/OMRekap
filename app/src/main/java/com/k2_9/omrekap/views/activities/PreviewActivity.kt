package com.k2_9.omrekap.views.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView
import com.k2_9.omrekap.R

class PreviewActivity : AppCompatActivity() {
	companion object {
		const val EXTRA_NAME_IMAGE_URI_STRING = ResultActivity.EXTRA_NAME_IMAGE_URI_STRING
		const val EXTRA_NAME_IS_RESET = ResultActivity.EXTRA_NAME_IS_RESET
		const val EXTRA_NAME_IS_FROM_CAMERA = "IS_FROM_CAMERA"
	}

	private var imageUriString: String? = null
	private var isFromCamera: Boolean = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_preview)

		// display photo
		val photoView: PhotoView = findViewById(R.id.preview_content)

		imageUriString = intent.getStringExtra(EXTRA_NAME_IMAGE_URI_STRING)
		isFromCamera = intent.getBooleanExtra(EXTRA_NAME_IS_FROM_CAMERA, false)

		if (imageUriString == null) {
			throw IllegalArgumentException("Image URI string is null")
		}

		photoView.setImageURI(Uri.parse(imageUriString))

		// set buttons action
		val acceptButton = findViewById<ImageButton>(R.id.accept_preview_button)
		val rejectButton = findViewById<ImageButton>(R.id.reject_preview_button)

		acceptButton.setOnClickListener {
			val newIntentClass = if (isFromCamera) ResultFromCameraActivity::class.java else ResultFromGalleryActivity::class.java
			val newIntent = Intent(this, newIntentClass)

			intent.extras?.let { extras ->
				newIntent.putExtras(extras)
			}

			newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
			finish()

			Log.d("FROMCAMERAGA", isFromCamera.toString())
			startActivity(newIntent)
		}

		rejectButton.setOnClickListener {
			finish()
		}
	}
}
