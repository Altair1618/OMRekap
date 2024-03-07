package com.k2_9.omrekap.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.k2_9.omrekap.R
import com.k2_9.omrekap.fragments.HomePageFragment
import com.k2_9.omrekap.fragments.ResultPageFragment

class MainActivity : AppCompatActivity() {
	companion object {
		const val EXTRA_NAME_IS_RESULT = "IS_RESULT"
		const val EXTRA_NAME_IS_FROM_CAMERA = "IS_FROM_CAMERA"
		const val EXTRA_NAME_IMAGE_URI_STRING = "IMAGE_URI_STRING"
	}

	private var isResult: Boolean = false
	private var isFromCamera: Boolean = false // must be false if isResult is false
	private var imageUriString: String? = null // can't be null if isResult is true

	private fun onGalleryButtonClick() {
		if (isResult) {
			finish()
		}

		val intent = Intent(this, MainActivity::class.java)

		intent.putExtra(EXTRA_NAME_IS_RESULT, true)
		// TODO: pass image URI from gallery
		intent.putExtra(EXTRA_NAME_IMAGE_URI_STRING, "")

		startActivity(intent)
	}

	private fun onCameraButtonClick() {
		val intent = Intent(this, CameraActivity::class.java)

		if (isResult) {
			intent.putExtra(CameraActivity.EXTRA_NAME_IMAGE_URI_STRING, imageUriString)
			intent.putExtra(CameraActivity.EXTRA_NAME_IS_FROM_CAMERA_RESULT, isFromCamera)
		}

		if (isResult) {
			finish()
		}
		startActivity(intent)
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)

		if (intent != null && (intent.getBooleanExtra(EXTRA_NAME_IS_RESULT, false) or isResult)) {
			val newIntent = Intent(this, MainActivity::class.java)
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

			finish()
			startActivity(newIntent)
		}

		isResult = false
		isFromCamera = false
		imageUriString = null
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		isResult = intent.getBooleanExtra(EXTRA_NAME_IS_RESULT, false)

		if (isResult) {
			isFromCamera = intent.getBooleanExtra(EXTRA_NAME_IS_FROM_CAMERA, false)
			imageUriString = intent.getStringExtra(EXTRA_NAME_IMAGE_URI_STRING)

			if (imageUriString == null) {
				throw IllegalArgumentException("Image URI string is null")
			}
		}

		assert(!isFromCamera || isResult)

		// fix shadow
		val galleryCardView: CardView = findViewById(R.id.gallery_card_view)
		val cameraCardView: CardView = findViewById(R.id.camera_card_view)

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
			galleryCardView.elevation = 0f
			cameraCardView.elevation = 0f
		}

		// set fragment view
		val fragmentManager = supportFragmentManager
		val fragmentTransaction = fragmentManager.beginTransaction()
		var fragment: Fragment? = null
		if (isResult) {
			// Create an instance of your fragment
			fragment = ResultPageFragment()

			val arguments =
				Bundle().apply {
					putBoolean(ResultPageFragment.ARG_NAME_IS_FROM_CAMERA, isFromCamera)
					// TODO: PASS IMAGE URI STRING
				}

			// Set the arguments for the fragment
			fragment.arguments = arguments
		} else {
			fragment = HomePageFragment()
		}

		fragmentTransaction.replace(R.id.fragment_container_view, fragment)
		fragmentTransaction.commit()

		// add button listeners
		val galleryButton: ImageButton = findViewById(R.id.gallery_button)
		val cameraButton: ImageButton = findViewById(R.id.camera_button)

		galleryButton.setOnClickListener {
			onGalleryButtonClick()
		}

		cameraButton.setOnClickListener {
			onCameraButtonClick()
		}
	}
}
