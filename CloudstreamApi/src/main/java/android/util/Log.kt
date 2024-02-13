package android.util

class Log {
    companion object {
        @JvmStatic
        fun d(tag: String, message: String) {
            println("DEBUG $tag: $message")
        }

        @JvmStatic
        fun i(tag: String, message: String) {
            println("INFO $tag: $message")
        }

        @JvmStatic
        fun v(tag: String, message: String) {
            println("VERBOSE $tag: $message")
        }

        @JvmStatic
        fun w(tag: String, message: String) {
            println("WARNING $tag: $message")
        }
    }
}