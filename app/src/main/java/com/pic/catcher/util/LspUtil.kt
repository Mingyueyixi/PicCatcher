package com.pic.catcher.util

import android.content.SharedPreferences
import com.pic.catcher.BuildConfig

/**
 * xposed api
 */
class LspUtil {

    companion object {
        /**
         * 需要独立，避免未注入的app，例如宿主app运行在不分虚拟框架下，找不到相关类，无法完成功能
         */
        @JvmStatic
        fun getTable(table: String): SharedPreferences {
            return de.robv.android.xposed.XSharedPreferences(BuildConfig.APPLICATION_ID, table)
        }
    }
}