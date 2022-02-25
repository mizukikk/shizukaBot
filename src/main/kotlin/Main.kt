import bot.DiscordBot
import repository.MLTDRepository

private val repository = MLTDRepository.getInstance()

fun main(args: Array<String>) {
    val bot= DiscordBot.getInstance().connect()
    bot.getServiceList()
}