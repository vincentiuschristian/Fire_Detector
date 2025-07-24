package com.dev.firedetector.data.response

data class HistoryResponse(
	val message: String,
	val data: List<SensorDataResponse>
)

