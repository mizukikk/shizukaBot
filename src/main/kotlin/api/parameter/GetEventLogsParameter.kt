package api.parameter

import api.enum.EventType

data class GetEventLogsParameter(
    val id: Int,
    val type: EventType,
    val ranks: String = "100,2500,5000,10000,25000,50000",
    val timeMin: String? = null
)