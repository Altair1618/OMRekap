package com.k2_9.omrekap.data.models

import com.k2_9.omrekap.data.configs.omr.ContourOMRHelperConfig
import com.k2_9.omrekap.data.configs.omr.OMRSection
import com.k2_9.omrekap.data.configs.omr.TemplateMatchingOMRHelperConfig

/**
 * Scanned image's OMR detection template
 */
data class OMRBaseConfiguration(
	val omrConfigs: Map<String, OMRConfigurationParameter>,
)

data class OMRConfigurationParameter(
	val contents: Map<OMRSection, String>,
	val contourOMRHelperConfig: ContourOMRHelperConfig,
	val templateMatchingOMRHelperConfig: TemplateMatchingOMRHelperConfig,
)
