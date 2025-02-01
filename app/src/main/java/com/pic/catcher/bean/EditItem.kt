package com.pic.catcher.bean

import android.text.InputType
import com.pic.catcher.util.ext.optString2
import org.json.JSONObject

/**
 * @author Lu
 * @date 2025/1/5 12:27
 * @description
 */
class EditItem(name: CharSequence, value: CharSequence? = null, inputType: Int = InputType.TYPE_CLASS_TEXT) : TextItem(name, value) {
    var inputType by observableProperty(inputType, "inputType")

    companion object {
        @JvmStatic
        fun fromJson(json: JSONObject?): EditItem? {
            if (json == null) return null
            return EditItem(json.optString2("name") ?: "", json.optString2("value"))
        }
    }

}
