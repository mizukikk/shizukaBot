import api.ResponseCallBack
import api.enum.EventType
import api.model.EventInfo
import api.model.EventLog
import api.model.Score
import api.parameter.GetAnivIdolEventLogsParameter
import api.parameter.GetEventLogsParameter
import bot.DiscordBot
import extension.dateToMillis
import extension.millisToDate
import extension.nextUpdateMillis
import net.dv8tion.jda.api.EmbedBuilder
import repository.MLTDRepository
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainModel {
    private val repository = MLTDRepository.getInstance()
    private val bot: DiscordBot = DiscordBot.getInstance()
    private var currentEventInfo: EventInfo? = null
    private val timer = Timer()
    private var idolId = 1

    fun connectBot() {
        bot.connect()
    }

    fun scheduleTimerTask() {
        val currentMillis = System.currentTimeMillis()
        val nextUpdateMillis = System.currentTimeMillis().nextUpdateMillis()
        val delay = nextUpdateMillis - currentMillis
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (isEventEffective()) {
                    getEventLogs()
                } else {
                    getEventInfo()
                }
            }
        }, delay, TimeUnit.MINUTES.toMillis(30))
    }

    private fun isEventEffective(): Boolean {
        val effectiveMillis = currentEventInfo!!.schedule.endDateMillis + TimeUnit.HOURS.toMillis(1)
        return currentEventInfo != null && effectiveMillis >= System.currentTimeMillis()
    }

    fun getEventInfo() {
        repository.getEventInfoList(object : ResponseCallBack<List<EventInfo>> {
            override fun success(response: List<EventInfo>) {
                currentEventInfo = try {
                    response.last()
                } catch (e: NoSuchElementException) {
                    null
                }
                if (isEventEffective())
                    getEventLogs()
            }

            override fun fail() {
                currentEventInfo = null
            }
        })
    }

    fun getEventLogs() {
        if (currentEventInfo != null) {
            val para = GetEventLogsParameter(currentEventInfo!!.id, EventType.EVENT_POINT)
            repository.getEventLogs(para, object : ResponseCallBack<List<EventLog>> {
                override fun success(response: List<EventLog>) {
                    val embedBuilder = getBaseLogEmbedBuilder()
                    setEventLogsMessage(response, embedBuilder)
                    //todo aaa3 送出訊息到指定的聊天室

                    if (currentEventInfo!!.isAnniversaryEvent) {
                        idolId = 1
                        getAnnivLogs()
                    }
                }

                override fun fail() {}
            })
        }
    }

    private fun getBaseLogEmbedBuilder() = EmbedBuilder()
        .setTitle(currentEventInfo?.name)
        .setDescription(currentEventInfo?.eventDate)
        .setFooter("資料更新時間 ${System.currentTimeMillis().millisToDate("yyyy-MM-dd HH:mm XXX")}")

    private fun setEventLogsMessage(
        eventLogList: List<EventLog>,
        embedBuilder: EmbedBuilder
    ) {
        eventLogList.forEach { log ->
            val scoreList = log.data.reversed()
            //取得當前分數
            val currentScore = try {
                scoreList.first()
            } catch (e: NoSuchElementException) {
                null
            }

            if (currentScore == null)
                return@forEach

            var halfHourScore: Score? = null
            var oneHourScore: Score? = null
            var oneDayScore: Score? = null

            val currentScoreMillis = currentScore.summaryTime.dateToMillis()

            for (score: Score in scoreList) {
                val diffMillis = currentScoreMillis - score.summaryTime.dateToMillis()

                if (diffMillis >= TimeUnit.MINUTES.toMillis(30) && halfHourScore == null)
                    halfHourScore = score

                if (diffMillis >= TimeUnit.HOURS.toMillis(1) && oneHourScore == null)
                    oneHourScore = score

                if (diffMillis >= TimeUnit.DAYS.toMillis(1) && oneDayScore == null)
                    oneDayScore = score

                if (halfHourScore != null && oneHourScore != null && oneDayScore != null)
                    break
            }


            val scoreSb = StringBuilder()
            currentScore.let {
                scoreSb.append(NumberFormat.getInstance().format(it.score.toInt()))
            }
            halfHourScore?.let {
                val score = currentScore.score.toInt() - it.score.toInt()
                scoreSb.append("\n+${NumberFormat.getInstance().format(score)} (30min)")
            }
            oneHourScore?.let {
                val score = currentScore.score.toInt() - it.score.toInt()
                scoreSb.append("\n+${NumberFormat.getInstance().format(score)} (1hr)")
            }
            oneDayScore?.let {
                val score = currentScore.score.toInt() - it.score.toInt()
                scoreSb.append("\n+${NumberFormat.getInstance().format(score)} (24hr)")
            }
            if (scoreSb.isNotEmpty())
                embedBuilder
                    .addField(
                        "${log.rank}",
                        scoreSb.toString(),
                        true
                    )
        }
    }

    private fun getAnnivLogs() {
        if (currentEventInfo != null) {
            repository.getAnivIdolEventLogs(GetAnivIdolEventLogsParameter(currentEventInfo!!.id, idolId),
                object : ResponseCallBack<List<EventLog>> {
                    override fun success(response: List<EventLog>) {

                    }

                    override fun fail() {}
                })
        }
    }
}