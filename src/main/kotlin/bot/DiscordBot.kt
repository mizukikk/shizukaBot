package bot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import utils.Logger
import java.io.File

class DiscordBot private constructor() {
    companion object {
        private var instance: DiscordBot? = null

        @Synchronized
        fun getInstance(): DiscordBot {
            if (instance == null) {
                instance = DiscordBot()
            }
            return instance!!
        }
    }

    private val token by lazy {
        File("botConfig.txt")
            .readText()
    }

    private var jda: JDA? = null

    fun connect(): DiscordBot {
        if (jda?.status == JDA.Status.CONNECTED)
            return this
        Logger.log("bot connect")
        Logger.log(jda?.status?.name)
        jda = JDABuilder.createDefault(token)
            .addEventListeners(object : ListenerAdapter() {
                //取得訊息
                override fun onMessageReceived(event: MessageReceivedEvent) {
                    Logger.log(event.message.contentRaw)
                }
            })
            .build()
            .awaitReady()
        Logger.log("bot start")
        return this
    }

    fun getServiceList() = jda?.guilds

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
}