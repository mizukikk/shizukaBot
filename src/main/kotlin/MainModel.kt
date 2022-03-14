import api.ResponseCallBack
import api.enum.EventType
import api.model.EventInfo
import api.model.EventLog
import api.parameter.GetAnivIdolEventLogsParameter
import api.parameter.GetEventLogsParameter
import bot.DiscordBot
import extension.nextUpdateMillis
import net.dv8tion.jda.api.EmbedBuilder
import repository.MLTDRepository
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

    private fun getEventLogs() {
        if (currentEventInfo != null) {
            val para = GetEventLogsParameter(currentEventInfo!!.id, EventType.EVENT_POINT)
            repository.getEventLogs(para, object : ResponseCallBack<List<EventLog>> {
                override fun success(response: List<EventLog>) {
                    try {

                    } catch (e: NoSuchElementException) {
                    }
                    if (currentEventInfo!!.isAnniversaryEvent) {
                        idolId = 1
                        getAnnivLogs()
                    }
                }

                override fun fail() {}
            })
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