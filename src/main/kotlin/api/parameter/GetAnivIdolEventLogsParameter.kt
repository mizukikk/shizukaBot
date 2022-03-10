package api.parameter

data class GetAnivIdolEventLogsParameter(
    val id: Int,
    val idolId: Int,
    val timeMin: String? = null,
)