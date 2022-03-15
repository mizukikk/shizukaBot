import api.ResponseCallBack
import api.enum.EventType
import api.model.EventInfo
import api.model.EventLog
import api.model.Score
import api.parameter.GetAnivIdolEventLogsParameter
import api.parameter.GetEventLogsParameter
import bot.DiscordBot
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import data.AnnviIdolData
import data.Idol
import extension.dateToMillis
import extension.millisToDate
import extension.nextUpdateMillis
import net.dv8tion.jda.api.EmbedBuilder
import repository.MLTDRepository
import utils.ChannelUtil
import java.awt.Color
import java.io.File
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainModel {
    private val repository = MLTDRepository.getInstance()
    private val bot: DiscordBot = DiscordBot.getInstance()
    private var annviIdolLogMap = hashMapOf<Int, MutableList<AnnviIdolData>>()
    private val idolMap by lazy {
        val json = File("idloList.txt")
            .readText()
        Gson().fromJson<Map<Int, Idol>>(json, object : TypeToken<Map<Int, Idol>>() {}.type)
    }
    private var currentEventInfo: EventInfo? = null
    private val timer = Timer()
    private var idolId = 1

    fun connectBot() {
        bot.connect()
        bot.setListener(object : DiscordBot.DCBotListener {
            override fun showEventMessage(channelId: String) {
                val embedBuilder = getBaseLogEmbedBuilder()
                if (embedBuilder.isEmpty.not())
                    bot.sendMessage(channelId, embedBuilder.build())
            }

            override fun showDebugMessage(channelId: String) {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val embedBuilder = EmbedBuilder()
                    .setTitle("測試訊息")
                val channelList = ChannelUtil.getChannelList()
                    .map { id ->
                        bot.getChannelById(id)?.name
                    }
                val channel = gson.toJson(channelList)

                embedBuilder.addField("使用中的頻道", channel, false)

                currentEventInfo?.let {
                    val event = gson.toJson(currentEventInfo)
                    embedBuilder.addField("最新活動資訊", event, false)
                }
                bot.sendMessage(channelId, embedBuilder.build())
            }

            override fun updateNow() {
                getEventInfo()
            }
        })
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
                    sendMessage(embedBuilder)
                    checkAnnviLog()
                }

                override fun fail() {}
            })
        }
    }

    private fun checkAnnviLog() {
        if (currentEventInfo!!.isAnniversaryEvent) {
            idolId = 1
            annviIdolLogMap.clear()
            getAnnivLogs()
        }
    }

    private fun getBaseLogEmbedBuilder() = EmbedBuilder()
        .setTitle(currentEventInfo?.name)
        .setDescription(currentEventInfo?.eventDate)

    private fun setEventLogsMessage(
        eventLogList: List<EventLog>,
        embedBuilder: EmbedBuilder,
        name: String? = null,
    ) {
        eventLogList.forEach { log ->

            val scoreList = log.data.reversed()
            //取得當前分數
            val currentScore = try {
                scoreList.first()
            } catch (e: NoSuchElementException) {
                return@forEach
            }

            val scoreSb = StringBuilder()
            scoreSb.append(NumberFormat.getInstance().format(currentScore.score.toInt()))

            val updateTime = currentScore.summaryTime
                .dateToMillis()
                .millisToDate("yyyy-MM-dd HH:mm XXX")
            embedBuilder.setFooter("資料更新時間 $updateTime")

            setScoreRecordMessage(currentScore, scoreList, scoreSb)

            if (name != null) {
                embedBuilder
                    .addField(
                        name,
                        scoreSb.toString(),
                        true
                    )
            } else {
                embedBuilder
                    .addField(
                        "${log.rank}",
                        scoreSb.toString(),
                        true
                    )
            }
        }
    }

    private fun setScoreRecordMessage(
        currentScore: Score,
        scoreList: List<Score>,
        scoreSb: StringBuilder,
    ) {
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
    }

    private fun getAnnivLogs() {
        if (currentEventInfo != null) {
            repository.getAnivIdolEventLogs(GetAnivIdolEventLogsParameter(currentEventInfo!!.id, idolId),
                object : ResponseCallBack<List<EventLog>> {
                    override fun success(response: List<EventLog>) {
                        saveAnnviIdolData(response)
                        loadNextAnnivLogs()
                    }

                    override fun fail() {
                        loadNextAnnivLogs()
                    }
                })
        }
    }

    private fun saveAnnviIdolData(response: List<EventLog>) {
        val idol = idolMap[idolId]
        idol?.let {
            val data = AnnviIdolData(it.name, response.reversed())
            if (annviIdolLogMap[it.idolType] == null) {
                annviIdolLogMap[it.idolType] = mutableListOf()
            }
            annviIdolLogMap[it.idolType]?.add(data)
        }
    }

    private fun loadNextAnnivLogs() {
        idolId++
        if (idolId <= 52) {
            Thread.sleep(1000)
            getAnnivLogs()
        } else {
            annviIdolLogMap.forEach { idolType, annivIdolDataList ->
                val embedBuilder = getBaseLogEmbedBuilder()

                val messageColor = when (idolType) {
                    Idol.IdolType.AS -> Color.WHITE
                    Idol.IdolType.PRINCESS -> Color(235, 64, 131)
                    Idol.IdolType.ANGEL -> Color(215, 164, 57)
                    Idol.IdolType.FAIRY -> Color(25, 87, 213)
                    else -> Color.WHITE
                }
                embedBuilder.setColor(messageColor)

                annivIdolDataList.forEach { annviIdolData ->
                    val eventLogList = annviIdolData.eventLogList
                    setEventLogsMessage(eventLogList, embedBuilder, annviIdolData.name)

                }

                sendMessage(embedBuilder)
            }
        }
    }

    private fun sendMessage(embedBuilder: EmbedBuilder) {
        ChannelUtil.getChannelList().forEach { channelId ->
            bot.sendMessage(channelId, embedBuilder.build())
        }
    }
}