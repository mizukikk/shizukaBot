package utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object ChannelUtil {

    private const val CHANNELS = "channels.txt"
    private val gson by lazy {
        Gson()
    }

    fun getChannelList(): MutableList<String> {
        val json = try {
            File(CHANNELS)
                .readText()
        } catch (e: Exception) {
            ""
        }
        if (json.isEmpty())
            return mutableListOf()
        else
            return Gson().fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
    }

    fun addChannel(channelId: String) {
        val channelList = getChannelList()
        channelList.add(channelId)
        val file = File(CHANNELS)
        val json = gson.toJson(channelList)

        file.writeText(json)
    }

    fun deleteChannel(channelId: String) {
        val channelList = getChannelList()
        channelList.remove(channelId)
        val file = File(CHANNELS)
        if (file.exists().not()) {
            file.mkdir()
        }

        val json = gson.toJson(channelList)

        file.writeText(json)
    }
}