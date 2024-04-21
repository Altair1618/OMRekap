package com.k2_9.omrekap.data.configs.omr

class ContourOMRHelperConfig(
	omrCropper: OMRCropper,
	minRadius: Int,
	maxRadius: Int,
	minAspectRatio: Float,
	maxAspectRatio: Float,
	darkPercentageThreshold: Float,
	darkIntensityThreshold: Int,
) : OMRHelperConfig(omrCropper) {
	var minRadius: Int
		private set
	var maxRadius: Int
		private set
	var minAspectRatio: Float
		private set
	var maxAspectRatio: Float
		private set
	var darkPercentageThreshold: Float
		private set
	var darkIntensityThreshold: Int
		private set

	init {
		require(minRadius >= 0) { "minRadius must be non-negative" }
		require(maxRadius >= minRadius) { "maxRadius must be greater than or equal to minRadius" }
		require(minAspectRatio >= 0.0f) { "minAspectRatio must be non-negative" }
		require(maxAspectRatio >= minAspectRatio) { "maxAspectRatio must be greater than or equal to minAspectRatio" }
		require(darkPercentageThreshold in 0.0f..1.0f) { "darkPercentageThreshold must be between 0 and 1" }
		require(darkIntensityThreshold >= 0) { "darkIntensityThreshold must be non-negative" }

		this.minRadius = minRadius
		this.maxRadius = maxRadius
		this.minAspectRatio = minAspectRatio
		this.maxAspectRatio = maxAspectRatio
		this.darkPercentageThreshold = darkPercentageThreshold
		this.darkIntensityThreshold = darkIntensityThreshold
	}
}
