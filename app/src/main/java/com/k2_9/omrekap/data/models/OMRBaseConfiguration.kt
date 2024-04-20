package com.k2_9.omrekap.data.models

import com.k2_9.omrekap.data.configs.omr.TemplateMatchingOMRHelperConfig

/**
 * Scanned image's OMR detection template
 */
data class OMRBaseConfiguration(
	val configs: Map<String, TemplateMatchingOMRHelperConfig>
)
