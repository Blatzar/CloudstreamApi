package android.util

object Log {
    fun d(tag: String, message: String) {
        println("DEBUG $tag: $message")
    }

    fun i(tag: String, message: String) {
        println("INFO $tag: $message")
    }

    fun v(tag: String, message: String) {
        println("VERBOSE $tag: $message")
    }
}