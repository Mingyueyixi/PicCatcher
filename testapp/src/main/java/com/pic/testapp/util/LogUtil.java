package com.pic.testapp.util;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * @author Lu
 * @date 2024/10/26 22:25
 * @description
 */
public class LogUtil {
    private static final String TAG = ">>>";

    public static void d(Object... msg) {
        printLog(Log.DEBUG, msg);
    }


    public static void e(Object... msg) {
        printLog(Log.ERROR, msg);
    }

    public static void w(Object... msg) {
        printLog(Log.WARN, msg);
    }

    public static void i(Object... msg) {
        printLog(Log.INFO, msg);
    }

    public static void v(Object... msg) {
        printLog(Log.VERBOSE, msg);
    }

    private static String buildMessage(Object... msg) {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append(Thread.currentThread().getName() + "  ");
        for (Object o : msg) {
            if (o instanceof Throwable) {
                msgBuilder.append(Log.getStackTraceString((Throwable) o));
            } else if (o == null) {
                msgBuilder.append("null");
            } else if (o.getClass().isArray()) {
                msgBuilder.append(Arrays.toString((Object[]) o));
            } else {
                msgBuilder.append(o);
            }
            msgBuilder.append("  ");
        }
        return msgBuilder.toString();
    }


    private static void printLog(int level, Object[] msg) {
        switch (level) {
            case Log.VERBOSE:
                Log.v(TAG, buildMessage(msg));
                break;
            case Log.INFO:
                Log.i(TAG, buildMessage(msg));
                break;
            case Log.WARN:
                Log.w(TAG, buildMessage(msg));
                break;
            case Log.ERROR:
                Log.e(TAG, buildMessage(msg));
                break;
            case Log.DEBUG:
            default:
                Log.d(TAG, buildMessage(msg));
                break;
        }
    }
}
