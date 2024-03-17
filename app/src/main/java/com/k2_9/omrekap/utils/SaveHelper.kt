package com.k2_9.omrekap.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.k2_9.omrekap.models.ImageSaveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveHelper {
	suspend fun save(
		context: Context,
		data: ImageSaveData,
	) {
		val folderName: String = generateFolderName()

		if (data.rawImage == null) {
			throw RuntimeException("The raw image bitmap is null")
		}

		if (data.annotatedImage == null || data.data == null) {
			throw RuntimeException("Image has not been processed yet")
		}

		if (data.rawImage!!.width <= 0 || data.rawImage!!.height <= 0) {
			throw RuntimeException("The raw image bitmap is empty")
		}

		if(data.annotatedImage!!.width <= 0 || data.rawImage!!.height<=0){
			throw RuntimeException("The annotated image bitmap is empty")
		}

		withContext(Dispatchers.IO) {
			saveImage(context, data.rawImage, folderName, "raw_image.jpg")
			saveImage(context, data.annotatedImage, folderName, "annotated_image.jpg")
			saveJSON(context, data.data!!, folderName, "data.json")
		}
	}

	fun uriToBitmap(
		context: Context,
		selectedFileUri: Uri,
	): Bitmap {
		val parcelFileDescriptor = context.contentResolver.openFileDescriptor(selectedFileUri, "r")
		val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
		val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
		parcelFileDescriptor.close()
		return image
	}

	private fun generateFolderName(): String {
		val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
		return sdf.format(Date())
	}

	private fun saveImage(
		context: Context,
		image: Bitmap,
		folderName: String,
		fileName: String,
	) {
		// Save the image to the Documents/OMRekap/folderName directory

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
			saveImageBeforeAndroidQ(image, folderName, fileName)
		} else {
			saveImageAndroidQandAbove(context, image, folderName, fileName)
		}
	}

	private fun saveJSON(
		context: Context,
		data: Map<String, Int>,
		folderName: String,
		fileName: String,
	) {
		// Save the JSON to the Documents/OMRekap/folderName directory

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
			saveJSONBeforeAndroidQ(data, folderName, fileName)
		} else {
			saveJSONAndroidQandAbove(context, data, folderName, fileName)
		}
	}

	private fun saveImageBeforeAndroidQ(
		image: Bitmap,
		folderName: String,
		fileName: String,
	) {
		// Save the image using the deprecated environment.getExternalStoragePublicDirectory() method

		val documentDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
		val appDir = File(documentDir, "OMRekap")
		val fileDir = File(appDir, folderName)

		if (!fileDir.exists()) {
			if (!fileDir.mkdirs()) {
				throw RuntimeException("Failed to create directory: $fileDir")
			}
		}

		val imageFile = File(fileDir, fileName)
		val fileOutputStream = FileOutputStream(imageFile)
		image.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
		fileOutputStream.flush()
		fileOutputStream.close()
	}

	@RequiresApi(Build.VERSION_CODES.Q)
	private fun saveImageAndroidQandAbove(
		context: Context,
		image: Bitmap,
		folderName: String,
		fileName: String,
	) {
		// Save the image using the new MediaStore API

		val contentValues =
			ContentValues().apply {
				put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
				put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
				put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + File.separator + "OMRekap" + File.separator + folderName)
			}

		val uri = context.contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues)

		if (uri != null) {
			val stream = context.contentResolver.openOutputStream(uri)
			if (stream != null) {
				image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
				stream.close()
			} else {
				// TODO: Handle the case where the stream is null
			}
		} else {
			// TODO: Handle the case where the uri is null
		}
	}

	private fun saveJSONBeforeAndroidQ(
		data: Map<String, Int>,
		folderName: String,
		fileName: String,
	) {
		// Save the JSON using the deprecated environment.getExternalStoragePublicDirectory() method

		val documentDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
		val appDir = File(documentDir, "OMRekap")
		val fileDir = File(appDir, folderName)

		if (!fileDir.exists()) {
			if (!fileDir.mkdirs()) {
				throw RuntimeException("Failed to create directory: $fileDir")
			}
		}

		val jsonFile = File(fileDir, fileName)
		val fileOutputStream = FileOutputStream(jsonFile)
		fileOutputStream.write(data.toString().toByteArray())
		fileOutputStream.flush()
		fileOutputStream.close()
	}

	@RequiresApi(Build.VERSION_CODES.Q)
	private fun saveJSONAndroidQandAbove(
		context: Context,
		data: Map<String, Int>,
		folderName: String,
		fileName: String,
	) {
		// Save the JSON using the new MediaStore API

		val contentValues =
			ContentValues().apply {
				put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
				put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
				put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + File.separator + "OMRekap" + File.separator + folderName)
			}

		val uri = context.contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues)

		if (uri != null) {
			val stream = context.contentResolver.openOutputStream(uri)
			if (stream != null) {
				stream.write(data.toString().toByteArray())
				stream.close()
			} else {
				// TODO: Handle the case where the stream is null
			}
		} else {
			// TODO: Handle the case where the uri is null
		}
	}
}
