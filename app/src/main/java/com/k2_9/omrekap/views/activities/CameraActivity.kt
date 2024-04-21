package com.k2_9.omrekap.views.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.AudioManager
import android.media.MediaActionSound
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.lifecycle.lifecycleScope
import com.k2_9.omrekap.R
import com.k2_9.omrekap.utils.PermissionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
	companion object {
		const val EXTRA_NAME_IMAGE_URI_STRING = "IMAGE_URI_STRING"
		const val EXTRA_NAME_IS_FROM_CAMERA_RESULT = "IS_FROM_CAMERA_RESULT"
	}

	private var imageUriString: String? = null
	private var isFromCameraResult: Boolean = false

	private lateinit var previewView: PreviewView
	private lateinit var captureButton: ImageButton
	private lateinit var cameraController: CameraController

	private fun onBackHome() {
		val intent = Intent(this, HomeActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
		startActivity(intent)
	}

	private fun onBackResult() {
		val newIntentClass = if (isFromCameraResult) ResultFromCameraActivity::class.java else ResultFromGalleryActivity::class.java

		val intent = Intent(this, newIntentClass)

		intent.putExtra(ResultActivity.EXTRA_NAME_IMAGE_URI_STRING, imageUriString)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
		startActivity(intent)
	}

	private fun handleBackNavigation() {
		if (imageUriString == null) {
			onBackHome()
		} else {
			onBackResult()
		}
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)

		if (intent != null) {
			imageUriString = intent.getStringExtra(EXTRA_NAME_IMAGE_URI_STRING)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_camera)

		imageUriString = intent.getStringExtra(EXTRA_NAME_IMAGE_URI_STRING)
		isFromCameraResult = intent.getBooleanExtra(EXTRA_NAME_IS_FROM_CAMERA_RESULT, false)

		// back navigation

		onBackPressedDispatcher.addCallback(
			this,
			object : OnBackPressedCallback(true) {
				override fun handleOnBackPressed() {
					handleBackNavigation()
				}
			},
		)
	}

	override fun onStart() {
		super.onStart()
		previewView = findViewById(R.id.preview_view)
		captureButton = findViewById(R.id.take_photo_button)
		captureButton.setOnClickListener {
			takePhoto()
		}
		captureButton.isEnabled = true

		PermissionHelper.requirePermission(this, Manifest.permission.CAMERA, true) {
			cameraController = LifecycleCameraController(this)
			(cameraController as LifecycleCameraController).bindToLifecycle(this)
			cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
			previewView.controller = cameraController
		}
	}

	fun saveImageOnCache(image: ImageProxy) {
		// get current day as sign
		val dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

		// delete previous file from cache
		val outputDir = this@CameraActivity.cacheDir
		outputDir.listFiles()?.forEach { file: File? ->
			if ((file != null) && file.exists() && file.isFile) {
				val sign = file.name.slice(IntRange(0, 9))
				if (sign == "temp-image") {
					file.delete()
				}
			}
		}

		// save temp file on cache
		val outputFile = File.createTempFile("temp-image-$dateString", ".png", outputDir)
		outputFile.outputStream().use {
			image.toBitmap()
				.rotate(image.imageInfo.rotationDegrees.toFloat())
				.compress(Bitmap.CompressFormat.PNG, 100, it)
		}

		// Notify user
		val uri = outputFile.toURI()

		// send URI to MainActivity
		startActivity(
			Intent(this, PreviewActivity::class.java)
				.putExtra(PreviewActivity.EXTRA_NAME_IMAGE_URI_STRING, uri.toString())
				.putExtra(PreviewActivity.EXTRA_NAME_IS_FROM_CAMERA, true)
				.putExtra(PreviewActivity.EXTRA_NAME_IS_RESET, true),
		)
	}

	private fun Bitmap.rotate(degrees: Float): Bitmap =
		Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(degrees) }, true)

	private fun playShutterSound() {
		val audio: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
		when (audio.ringerMode) {
			AudioManager.RINGER_MODE_NORMAL -> {
				val sound = MediaActionSound()
				sound.play(MediaActionSound.SHUTTER_CLICK)
			}
			else -> {
				// do nothing
			}
		}
	}

	private fun freezeImage(image: ImageProxy) {
		runOnUiThread {
			previewView.controller = null
			captureButton.isEnabled = false
		}
	}

	private fun takePhoto() {
		val cameraExecutor = Executors.newSingleThreadExecutor()
		cameraController.takePicture(
			cameraExecutor,
			object :
				ImageCapture.OnImageCapturedCallback() {
				override fun onError(exception: ImageCaptureException) {
					// TODO
				}

				override fun onCaptureSuccess(image: ImageProxy) {
					super.onCaptureSuccess(image)
					playShutterSound()
					freezeImage(image)
					runOnUiThread {
						Toast.makeText(this@CameraActivity, "Photo taken", Toast.LENGTH_SHORT).show()
					}
					lifecycleScope.launch {
						withContext(Dispatchers.IO) {
							saveImageOnCache(image)
						}
					}
				}
			},
		)
	}
}
