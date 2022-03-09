package extension

import java.text.SimpleDateFormat
import java.util.*

const val BASE_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX"

fun String.dateToMillis(pattern: String = BASE_DATE_PATTERN) =
    SimpleDateFormat(pattern)
        .parse(this)
        .time

fun Long.millisToDate(pattern: String = BASE_DATE_PATTERN) =
    SimpleDateFormat(pattern)
        .apply {
            timeZone = TimeZone.getTimeZone("GMT+9")
        }
        .format(this)

fun Long.nextUpdateMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val minute = calendar.get(Calendar.MINUTE)
    if (minute in 10..39) {
        calendar.set(Calendar.MINUTE, 40)
    } else {
        calendar.add(Calendar.HOUR, 1)
        calendar.set(Calendar.MINUTE, 10)
    }
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}
