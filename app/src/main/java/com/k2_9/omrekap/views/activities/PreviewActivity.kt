package com.k2_9.omrekap.views.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import com.github.chrisbanes.photoview.PhotoView
import com.k2_9.omrekap.R
import com.k2_9.omrekap.data.models.ImageSaveData
import com.k2_9.omrekap.data.view_models.PreviewViewModel
import com.k2_9.omrekap.utils.CropHelper
import com.k2_9.omrekap.utils.ImageSaveDataHolder
import org.opencv.android.OpenCVLoader

class PreviewActivity : AppCompatActivity() {
	companion object {
		const val EXTRA_NAME_IMAGE_URI_STRING = "IMAGE_URI_STRING"
		const val EXTRA_NAME_IS_RESET = ResultActivity.EXTRA_NAME_IS_RESET
		const val EXTRA_NAME_IS_FROM_CAMERA = "IS_FROM_CAMERA"
	}

	private val viewModel: PreviewViewModel by viewModels()

	private var imageUriString: String? = null
	private var isFromCamera: Boolean = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_preview)

		OpenCVLoader.initLocal()

		// display photo
		val photoView: PhotoView = findViewById(R.id.preview_content)

		imageUriString = intent.getStringExtra(EXTRA_NAME_IMAGE_URI_STRING)
		isFromCamera = intent.getBooleanExtra(EXTRA_NAME_IS_FROM_CAMERA, false)

		if (imageUriString == null) {
			throw IllegalArgumentException("Image URI string is null")
		}

		val bitmapOptions = BitmapFactory.Options()
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
		val cornerPatternBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.raw.corner_pattern, bitmapOptions)

		CropHelper.loadPattern(cornerPatternBitmap)

		photoView.setImageURI(Uri.parse(imageUriString))

		viewModel.preprocessImage(photoView.drawable.toBitmap())

		// Observe Data
		val preprocessImageObserver =
			Observer<ImageSaveData> { newValue ->
				photoView.setImageBitmap(newValue.annotatedImage)
				ImageSaveDataHolder.save(newValue)
			}

		viewModel.data.observe(this, preprocessImageObserver)

		// set buttons action
		val acceptButton = findViewById<ImageButton>(R.id.accept_preview_button)
		val rejectButton = findViewById<ImageButton>(R.id.reject_preview_button)

		acceptButton.setOnClickListener {
			val newIntentClass = if (isFromCamera) ResultFromCameraActivity::class.java else ResultFromGalleryActivity::class.java
			val newIntent = Intent(this, newIntentClass)

			intent.extras?.let { extras ->
				newIntent.putExtras(extras)
			}

			newIntent.putExtra(
				EXTRA_NAME_IS_RESET,
				intent.extras?.getBoolean(EXTRA_NAME_IS_RESET, false) ?: false
			)

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
