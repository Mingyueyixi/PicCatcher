package com.pic.catcher.bean

import com.pic.catcher.util.ext.optString2
import org.json.JSONObject

open class TextItem(name: CharSequence, value: CharSequence? = null) : UiItem() {
    var name: CharSequence by observableProperty(name, "name")
    var value: CharSequence? by observableProperty(value, "value")

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("name", name)
            put("value", value)
        }
    }


    companion object {
        @JvmStatic
        fun fromJson(json: JSONObject?): TextItem? {
            if (json == null) return null
            return TextItem(json.optString2("name") ?: "", json.optString2("value"))
        }
    }
}
