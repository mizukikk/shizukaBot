package api.parameter

import api.enum.EventType

data class GetEventLogsParameter(
    val id: Int,
    val type: EventType,
    val ranks: String = "1,2,3,10,100,2500",
    val timeMin: String? = null
)