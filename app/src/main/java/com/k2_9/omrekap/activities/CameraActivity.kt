package com.k2_9.omrekap.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaActionSound
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.k2_9.omrekap.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
	private lateinit var imageCapture: ImageCapture
	private lateinit var cameraController: CameraController

	private fun onBackHome() {
		val intent = Intent(this, MainActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
		startActivity(intent)
	}

	private fun onBackResult() {
		val intent = Intent(this, MainActivity::class.java)
		intent.putExtra(MainActivity.EXTRA_NAME_IS_RESULT, true)
		intent.putExtra(MainActivity.EXTRA_NAME_IMAGE_URI_STRING, imageUriString)
		intent.putExtra(MainActivity.EXTRA_NAME_IS_FROM_CAMERA, isFromCameraResult)
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
		previewView = findViewById(R.id.preview_view)
		captureButton = findViewById(R.id.take_photo_button)
		captureButton.setOnClickListener {
			takePhoto()
		}

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

		requirePermission(Manifest.permission.CAMERA) {
			if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.Q) {
				requirePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, false) {}
			}

			imageCapture =
				ImageCapture.Builder()
					.setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
					.setFlashMode(ImageCapture.FLASH_MODE_ON)
					.build()

			cameraController = LifecycleCameraController(this)
			(cameraController as LifecycleCameraController).bindToLifecycle(this)
			cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
			previewView.controller = cameraController
		}
	}

	private fun requirePermission(
		permission: String,
		verbose: Boolean = true,
		operation: () -> Unit,
	) {
		if (ContextCompat.checkSelfPermission(
				this,
				permission,
			) == PackageManager.PERMISSION_GRANTED
		) {
			operation()
		} else {
			val requestPermissionLauncher =
				registerForActivityResult(RequestPermission()) {
						isGranted: Boolean ->
					if (isGranted) {
						operation()
					} else {
						if (verbose) {
							Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
						}
					}
				}
			requestPermissionLauncher.launch(permission)
		}
	}

	suspend fun saveImageOnCache(image: ImageProxy) {
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
			image.toBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
		}

		// Notify user
		val uri = outputFile.toURI()

		// send URI to MainActivity
		startActivity(
			Intent(this, MainActivity::class.java)
				.putExtra(MainActivity.EXTRA_NAME_IMAGE_URI_STRING, uri.toString())
				.putExtra(MainActivity.EXTRA_NAME_IS_RESULT, true)
				.putExtra(MainActivity.EXTRA_NAME_IS_FROM_CAMERA, true),
		)
	}

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
// 				Toast.makeText(this@CameraActivity, "hi", Toast.LENGTH_SHORT)
					playShutterSound() // TODO Move to shutterCallback
					GlobalScope.launch {
						saveImageOnCache(image)
					}
				}
			},
		)
	}
}
