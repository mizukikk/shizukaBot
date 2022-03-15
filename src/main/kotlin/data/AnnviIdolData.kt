package data

import api.model.EventLog

data class AnnviIdolData(
    val name: String,
    val eventLogList: List<EventLog>
)