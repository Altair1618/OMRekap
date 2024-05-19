package com.k2_9.omrekap.data.models

import org.opencv.core.Point

/**
 * Data class for corner points of a document
 * @param topLeft top left corner point
 * @param topRight top right corner point
 * @param bottomRight bottom right corner point
 * @param bottomLeft bottom left corner point
 */
data class CornerPoints(
	val topLeft: Point,
	val topRight: Point,
	val bottomRight: Point,
	val bottomLeft: Point,
)
