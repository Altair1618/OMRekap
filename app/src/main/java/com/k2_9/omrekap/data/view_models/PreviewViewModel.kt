package com.k2_9.omrekap.data.view_models

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k2_9.omrekap.data.models.ImageSaveData
import com.k2_9.omrekap.utils.PreprocessHelper
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * ViewModel for image data processing in Preview page
 */
class PreviewViewModel : ViewModel() {
	private val _data = MutableLiveData<ImageSaveData>()
	val data = _data as LiveData<ImageSaveData>

	/**
	 * Preprocess the image data
	 * @param img image to be preprocessed
	 */
	fun preprocessImage(img: Bitmap) {
		viewModelScope.launch {
			val data = ImageSaveData(img, img, mapOf(), Instant.now())
			_data.value = PreprocessHelper.preprocessImage(data)
		}
	}
}
