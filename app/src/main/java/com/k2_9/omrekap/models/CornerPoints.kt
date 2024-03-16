package com.k2_9.omrekap.models

import org.opencv.core.Point

data class CornerPoints(
	val topLeft: Point,
	val topRight: Point,
	val bottomRight: Point,
	val bottomLeft: Point,
)
