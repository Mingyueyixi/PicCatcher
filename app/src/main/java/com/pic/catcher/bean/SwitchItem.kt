package com.pic.catcher.bean

import org.json.JSONObject

/**
 * @author Lu
 * @date 2025/1/5 12:27
 * @description
 */
class SwitchItem(title: CharSequence, checked: Boolean = false) : UiItem() {
    var title: CharSequence by observableProperty(title, "title")
    var checked: Boolean by observableProperty(checked, "checked")


    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("title", title)
            put("checked", checked)
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: JSONObject?): SwitchItem? {
            if (json == null) return null
            return SwitchItem(json.optString("title") ?: "", json.optBoolean("checked") ?: false)
        }
    }


}
