package com.k2_9.omrekap.activities

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaActionSound
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraEffect
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraX
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.k2_9.omrekap.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.Executors

class CameraActivity: AppCompatActivity() {
	private lateinit var previewView: PreviewView
	private lateinit var captureButton: ImageButton
	private lateinit var imageCapture: ImageCapture
	private lateinit var cameraController: CameraController
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_camera)
		previewView = findViewById(R.id.preview_view)
		captureButton = findViewById(R.id.take_photo_button)
		captureButton.setOnClickListener {
			takePhoto()
		}
	}

	override fun onStart() {
		super.onStart()
		requirePermission(Manifest.permission.CAMERA) {
			imageCapture = ImageCapture.Builder()
				.setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
				.setFlashMode(ImageCapture.FLASH_MODE_ON)
				.build()

			cameraController = LifecycleCameraController(this)
			(cameraController as LifecycleCameraController).bindToLifecycle(this)
			cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
			previewView.controller = cameraController
		}
	}

	private fun requirePermission(permission: String, verbose: Boolean = true, operation: () -> Unit) {
		if (ContextCompat.checkSelfPermission(
			this, permission) == PackageManager.PERMISSION_GRANTED) {
			operation()
		}
		else {
			val requestPermissionLauncher =
				registerForActivityResult(RequestPermission()) {
				isGranted: Boolean ->
				if (isGranted) {
					operation()
				}
				else {
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
		var dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

		// delete previous file from cache
		var outputDir = this@CameraActivity.cacheDir
		outputDir.listFiles()?.forEach { file: File? ->
			if ((file != null) && file.exists() && file.isFile) {
				val sign = file.name.slice(IntRange(0, 9))
				if (sign == "temp-image") {
					file.delete()
				}
			}
		}

		// save temp file on cache
		var outputFile = File.createTempFile("temp-image-${dateString}", ".png", outputDir)
		outputFile.outputStream().use {
			image.toBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
		}

		// Notify user
		var URI = outputFile.toURI()

		// send URI to MainActivity
		startActivity(
			Intent(this, MainActivity::class.java)
				.putExtra("imageURI", URI.toString())
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
		cameraController.takePicture(cameraExecutor, object:
			ImageCapture.OnImageCapturedCallback() {
			override fun onError(exception: ImageCaptureException) {
				// TODO
			}

			override fun onCaptureSuccess(image: ImageProxy) {
				super.onCaptureSuccess(image)
//				Toast.makeText(this@CameraActivity, "hi", Toast.LENGTH_SHORT)
				playShutterSound() // TODO Move to shutterCallback
				GlobalScope.launch {
					saveImageOnCache(image)
				}
			}
		})
	}
}
