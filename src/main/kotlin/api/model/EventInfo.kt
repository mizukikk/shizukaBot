package api.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import extension.dateToMillis
import extension.millisToDate
import java.lang.StringBuilder

data class EventInfo(
    @Expose
    @SerializedName("appealType")
    val appealType: Int?,
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("schedule")
    val schedule: Schedule,
    @Expose
    @SerializedName("type")
    val type: Int
) {
    companion object {
        const val SHOW_TIME = 1 //: THEATER SHOW TIME☆
        const val MILLI_COLLECTION = 2 //: ミリコレ！
        const val THEATER_TRUST = 3 //: プラチナスターシアター・トラスト
        const val TOUR = 4 //: プラチナスターツアー
        const val ANNIVERSARY = 5 //: 周年記念イベント
        const val WORKING = 6 //: MILLION LIVE WORKING☆
        const val APRIL_FOOL = 7 //: エイプリルフール
        const val COLLECTION_BOX = 9 //: ミリコレ！（ボックスガシャ）
        const val TWIN = 10// : ツインステージ
        const val TUNE = 11// : プラチナスターチューン
        const val TWIN_2 = 12// : ツインステージ 2
        const val TAIL = 13// : プラチナスターテール
        const val TALK = 14// : THEATER TALK PARTY☆
        const val TREASURE = 16// : プラチナスタートレジャー
    }

    val isAnniversaryEvent get() = type == ANNIVERSARY
    val eventDate
        get() :String {
            val sb = StringBuilder()
            sb.append("活動時間:")
            sb.append("\n開始：")
            sb.append(schedule.beginDate.dateToMillis().millisToDate("yyyy-MM-dd HH:mm XXX"))
            schedule.boostBeginDate?.let { beginDate ->
                sb.append("\n後半：")
                sb.append(beginDate.dateToMillis().millisToDate("yyyy-MM-dd HH:mm XXX"))
            }
            sb.append("\n結束：")
            sb.append(schedule.endDate.dateToMillis().millisToDate("yyyy-MM-dd HH:mm XXX"))
            return sb.toString()
        }
}