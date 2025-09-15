package com.dev.firedetector.core.data.source.remote.response

data class HistoryResponse(
	val message: String,
	val data: List<SensorDataResponse>
)

