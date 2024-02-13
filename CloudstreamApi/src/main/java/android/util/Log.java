package android.util;

import org.jetbrains.annotations.Nullable;

public final class Log {
    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    public static int println(String level, @Nullable String tag, String msg) {
        System.out.println(level + " " + tag + ": " + msg);
        return 1;
    }

    public static int e(@Nullable String tag, String msg) {
        return println("ERROR", tag, msg);
    }

    public static int v(@Nullable String tag, String msg) {
        return println("VERBOSE", tag, msg);
    }

    public static int d(@Nullable String tag, String msg) {
        return println("DEBUG", tag, msg);
    }

    public static int i(@Nullable String tag, String msg) {
        return println("INFO", tag, msg);
    }

    public static int w(@Nullable String tag, String msg) {
        return println("WARNING", tag, msg);
    }
}
