package com.pic.catcher.config

import com.lu.magic.util.log.LogUtil
import com.pic.catcher.util.JSONX
import com.pic.catcher.util.LocalKVUtil
import com.pic.catcher.util.ext.toJSONObject
import org.json.JSONObject

/**
 * @author Lu
 * @date 2025/1/5 20:02
 * @description
 */
class ModuleConfig(var source: JSONObject) {
    var isCatchGlidePic: Boolean = true
        get() = JSONX.optBoolean(source, "isCatchGlidePic", true)
        set(value) {
            field = value
            source.put("isCatchGlidePic", value)
        }

    var isCatchWebViewPic: Boolean = true
        get() = JSONX.optBoolean(source, "isCatchWebViewPic", true)
        set(value) {
            field = value
            source.put("isCatchWebViewPic", value)
        }

    var isCatchNetPic: Boolean = true
        get() = JSONX.optBoolean(source, "isCatchNetPic", true)
        set(value) {
            field = value
            source.put("isCatchNetPic", value)
        }

    var minSpaceSize = 0
        get() = JSONX.optLong(source, "minSpaceSize", 0).toInt()
        set(value) {
            field = value
            source.put("minSpaceSize", value)
        }

    fun toJson(): String {
        return source.toString()
    }

    fun save() {
        save(this)
    }

    companion object {
        @JvmStatic
        val instance: ModuleConfig by lazy {
            load()
        }

        @JvmStatic
        fun load(): ModuleConfig {
            val moduleConfig = LocalKVUtil.getTable("module").getString("module_config", "{}")
            LogUtil.d("moduleConfig", moduleConfig)
            val json = moduleConfig.toJSONObject()
            return ModuleConfig(json ?: JSONObject())
        }

        @JvmStatic
        fun save(moduleConfig: ModuleConfig) {
            val json = moduleConfig.toJson()
            LocalKVUtil.getTable("module").edit().putString("module_config", json).apply()
        }

        @JvmStatic
        fun isLessThanMinSize(length: Long): Boolean {
            return length < instance.minSpaceSize * 1024L
        }

    }
}
