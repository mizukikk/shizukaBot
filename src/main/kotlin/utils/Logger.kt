package utils

object Logger {
    fun log(tag: String, message: String?) {
        println("$tag : $message")
    }

    fun log(message: String?) {
        println("aaa3 : $message")
    }
}