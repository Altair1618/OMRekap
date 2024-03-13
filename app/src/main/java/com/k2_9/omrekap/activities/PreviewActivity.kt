package com.k2_9.omrekap.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView
import com.k2_9.omrekap.R

class PreviewActivity : AppCompatActivity() {
	private var imageUriString: String? = null
	private var isFromCamera: Boolean = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_preview)

		// display photo
		val photoView: PhotoView = findViewById(R.id.preview_content)

		imageUriString = intent.getStringExtra(MainActivity.EXTRA_NAME_IMAGE_URI_STRING)
		isFromCamera = intent.getBooleanExtra(MainActivity.EXTRA_NAME_IS_FROM_CAMERA, false)

		if (imageUriString == null) {
			throw IllegalArgumentException("Image URI string is null")
		}

		photoView.setImageURI(Uri.parse(imageUriString))

		// set buttons action
		val acceptButton = findViewById<ImageButton>(R.id.accept_preview_button)
		val rejectButton = findViewById<ImageButton>(R.id.reject_preview_button)

		acceptButton.setOnClickListener {
			val newIntent = Intent(this, MainActivity::class.java)

			intent.extras?.let { extras ->
				newIntent.putExtras(extras)
			}

			if (isFromCamera) {
				newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
			} else {
				newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
			}

			finish()
			startActivity(newIntent)
		}

		rejectButton.setOnClickListener {
			finish()
		}
	}
}
