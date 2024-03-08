package com.k2_9.omrekap.view_models

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k2_9.omrekap.models.ImageSaveData
import kotlinx.coroutines.launch

class ImageDataViewModel : ViewModel() {
	private val _data = MutableLiveData<ImageSaveData>()
	val data = _data as LiveData<ImageSaveData>

	fun processImage(image: Uri) {
		viewModelScope.launch {
			// TODO: Process the raw image using OMRHelper
			val data = ImageSaveData(image, image, mapOf())

			_data.value = data
		}
	}
}
