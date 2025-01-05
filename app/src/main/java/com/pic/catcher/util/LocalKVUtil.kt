package com.pic.catcher.util

import android.content.Context
import android.content.SharedPreferences
import com.lu.magic.util.AppUtil
import com.pic.catcher.BuildConfig

class LocalKVUtil {
    companion object {
        const val defaultTableName = "app"

        @JvmStatic
        fun getTable(name: String): SharedPreferences {
            if (BuildConfig.APPLICATION_ID == AppUtil.getContext().packageName) {
                return try {
                    AppUtil.getContext().getSharedPreferences(name, Context.MODE_WORLD_READABLE)
                } catch (e: Exception) {
                    AppUtil.getContext().getSharedPreferences(name, Context.MODE_PRIVATE)
                }
            }
            // xposed hook
            return LspUtil.getTable(name)
        }

        @JvmStatic
        fun getDefaultTable(): SharedPreferences {
            return getTable(defaultTableName)
        }
    }
}