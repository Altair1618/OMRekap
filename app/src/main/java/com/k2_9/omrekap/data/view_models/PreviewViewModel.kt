package com.k2_9.omrekap.data.view_models

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k2_9.omrekap.data.models.CornerPoints
import com.k2_9.omrekap.data.models.ImageSaveData
import com.k2_9.omrekap.utils.CropHelper
import com.k2_9.omrekap.utils.PreprocessHelper
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point

class PreviewViewModel : ViewModel() {
	private val _data = MutableLiveData<ImageSaveData>()
	val data = _data as LiveData<ImageSaveData>

	fun preprocessImage(img: Bitmap) {
		viewModelScope.launch {
			val data = ImageSaveData(img, img, mapOf())
			_data.value = PreprocessHelper.preprocessImage(data)
		}
	}
}
