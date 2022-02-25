package api.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EventInfo(
    @Expose
    @SerializedName("appealType")
    val appealType: Int,
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
        const val april_fool = 7 //: エイプリルフール
        const val COLLECTION_BOX = 9 //: ミリコレ！（ボックスガシャ）
        const val TWIN = 10// : ツインステージ
        const val TUNE = 11// : プラチナスターチューン
        const val TWIN_2 = 12// : ツインステージ 2
        const val tail = 13// : プラチナスターテール
        const val TALK = 14// : THEATER TALK PARTY☆
        const val treasure = 16// : プラチナスタートレジャー
    }

    val isAnniversaryEvent get() = type == ANNIVERSARY
}