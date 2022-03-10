package api.parameter

import api.enum.EventType

data class GetEventLogsParameter(
    val id: Int,
    val type: EventType,
    val ranks: String,
    val timeMin: String? = null
)