package com.pic.catcher.util

import org.json.JSONObject

/**
 * @author Lu
 * @date 2025/1/5 12:59
 * @description
 */
class JSONX {
    companion object {
        @JvmStatic
        fun toLong(value: Any): Long? {
            when (value) {
                is Long -> {
                    return value
                }

                is Number -> {
                    return value.toLong()
                }

                is String -> {
                    try {
                        return value.toLong()
                    } catch (ignored: NumberFormatException) {
                    }
                }
            }

            return null
        }

        @JvmStatic
        fun getLong(json: JSONObject, name: String): Long {
            val value = json.get(name)
            val result: Long = toLong(value) ?: json.optLong(name)
            return result
        }

        @JvmStatic
        fun optLong(json: JSONObject?, name: String, fallback: Long = 0): Long {
            if (json == null || json.isNull(name)) {
                return fallback;
            }
            return json.opt(name)?.let { toLong(it) } ?: fallback
        }

        @JvmStatic
        fun optBoolean(source: JSONObject?, name: String, fallback: Boolean = false): Boolean {
            if (source == null) {
                return fallback
            }
            return source.optBoolean(name, fallback)
        }

        @JvmStatic
        fun toJSONObject(s: String?): JSONObject? {
            if (s == null) {
                return null
            }
            return try {
                JSONObject(s)
            } catch (e: Exception) {
                JSONObject()
            }
        }

        @JvmStatic
        fun optString(json: JSONObject?, name: String, fallback: String? = null): String? {
            if (json == null) {
                return null
            }
            if (json.isNull(name)) {
                return null
            }
            return try {
                json.optString(name, fallback)
            } catch (e: Exception) {
                fallback
            }
        }
    }
}
