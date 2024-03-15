package com.k2_9.omrekap.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.k2_9.omrekap.R

abstract class MainActivity : AppCompatActivity() {
	protected abstract fun getGalleryPreviewIntent(imageUri: Uri): Intent

	protected abstract fun getCameraIntent(): Intent

	protected abstract fun getFragment(): Fragment

	protected abstract fun handleBackNavigation()

	private fun onGalleryButtonClick() {
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "image/*"
		pickImage.launch(intent)
	}

	private val pickImage =
		registerForActivityResult(
			ActivityResultContracts.StartActivityForResult(),
		) { result: ActivityResult ->
			if (result.resultCode == RESULT_OK) {
				val data: Intent? = result.data
				val imageUri: Uri? = data?.data

				if (imageUri != null) {
					val intent = getGalleryPreviewIntent(imageUri)
					startActivity(intent)
				}
			}
		}

	private fun onCameraButtonClick() {
		val intent = getCameraIntent()
		startActivity(intent)
	}

	protected fun setFragment() {
		// set fragment view
		val fragmentManager = supportFragmentManager
		val fragmentTransaction = fragmentManager.beginTransaction()

		// Create an instance of your fragment
		val fragment = getFragment()

		fragmentTransaction.replace(R.id.fragment_container_view, fragment)
		fragmentTransaction.commit()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		// fix shadow
		val galleryCardView: CardView = findViewById(R.id.gallery_card_view)
		val cameraCardView: CardView = findViewById(R.id.camera_card_view)

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
			galleryCardView.elevation = 0f
			cameraCardView.elevation = 0f
		}

		setFragment()

		// add button listeners
		val galleryButton: ImageButton = findViewById(R.id.gallery_button)
		val cameraButton: ImageButton = findViewById(R.id.camera_button)

		galleryButton.setOnClickListener {
			onGalleryButtonClick()
		}

		cameraButton.setOnClickListener {
			onCameraButtonClick()
		}

		onBackPressedDispatcher.addCallback(
			this,
			object : OnBackPressedCallback(true) {
				override fun handleOnBackPressed() {
					handleBackNavigation()
				}
			},
		)
	}
}
