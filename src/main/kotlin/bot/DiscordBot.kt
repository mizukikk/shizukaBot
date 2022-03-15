package bot

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import utils.ChannelUtil
import utils.Logger
import java.io.File

class DiscordBot private constructor() {
    companion object {
        private var instance: DiscordBot? = null
        private const val BOT_COMMAND = "~shizuka"
        private const val ACTION_INFO = "info"
        private const val ACTION_ENABLE = "enable"
        private const val ACTION_DISABLE = "disable"
        private const val ACTION_EVENT = "event"
        private const val ACTION_DEBUG = "debug"
        private const val ACTION_UPDATE_NOW = "update_now"

        @Synchronized
        fun getInstance(): DiscordBot {
            if (instance == null) {
                instance = DiscordBot()
            }
            return instance!!
        }
    }

    private var listener: DCBotListener? = null

    private val token by lazy {
        File("botConfig.txt")
            .readText()
    }

    private var jda: JDA? = null

    fun connect() {
        if (jda?.status == JDA.Status.CONNECTED)
            return
        Logger.log("bot connect")
        jda = JDABuilder.createDefault(token)
            .addEventListeners(commandAdapter)
            .build()
            .awaitReady()
        Logger.log("bot start")
    }

    fun getServiceList() = jda?.guilds

    fun getChannelById(channelId: String) = jda?.getGuildChannelById(channelId)

    fun sendMessage(channelId: String, message: String) {
        jda?.getTextChannelById(channelId)
            ?.sendMessage(message)
            ?.queue()
    }

    fun sendMessage(channelId: String, message: MessageEmbed) {
        jda?.getTextChannelById(channelId)
            ?.sendMessageEmbeds(message)
            ?.queue()
    }

    private val commandAdapter = object : ListenerAdapter() {
        //取得訊息
        override fun onMessageReceived(event: MessageReceivedEvent) {
            val splitList = event.message.contentRaw.split(" ")
            if (splitList.size != 2)
                return

            val command = splitList[0]
            if (command != BOT_COMMAND)
                return

            val action = splitList[1]
            when (action) {
                ACTION_DEBUG -> {
                    listener?.showDebugMessage(event.channel.id)
                }
                ACTION_UPDATE_NOW->
                    listener?.updateNow()
                ACTION_ENABLE -> {
                    val channelList = ChannelUtil.getChannelList()
                    if (channelList.contains(event.channel.id)) {
                        getInstance().sendMessage(event.channel.id, "此頻道已經設定過了")
                    } else {
                        ChannelUtil.addChannel(event.channel.id)
                        getInstance().sendMessage(event.channel.id, "頻道設定成功")
                    }
                }
                ACTION_DISABLE -> {
                    ChannelUtil.deleteChannel(event.channel.id)
                    getInstance().sendMessage(event.channel.id, "頻道移除成功")
                }
                ACTION_EVENT -> {
                    listener?.showEventMessage(event.channel.id)
                }
                ACTION_INFO -> {
                    val embedBuilder = EmbedBuilder()
                    embedBuilder.setTitle("指令")
                    embedBuilder.addField(ACTION_ENABLE, "設定紀錄活動資訊頻道", false)
                    embedBuilder.addField(ACTION_DISABLE, "移除紀錄活動資訊頻道", false)
                    embedBuilder.addField(ACTION_EVENT, "查看當前活動資訊", false)
                    getInstance().sendMessage(event.channel.id, embedBuilder.build())
                }
            }
        }
    }

    fun setListener(listener: DCBotListener) {
        this.listener = listener
    }

    interface DCBotListener {
        fun showEventMessage(channelId: String)
        fun showDebugMessage(channelId: String)
        fun updateNow()
    }
}