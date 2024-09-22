package com.pic.catcher

import android.util.Base64
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class AppBuildInfo(
    val buildTime: String,
    val branch: String,
    val commit: String,
) {
    companion object {
        fun of(): com.pic.catcher.AppBuildInfo {
            val json = JSONObject(Base64.decode(BuildConfig.buildInfoJson64, Base64.DEFAULT).toString(Charsets.UTF_8))
            return com.pic.catcher.AppBuildInfo(
                SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(json.optLong("time")),
                json.optString("branch"),
                json.optString("commit").substring(0, 11)
            )
        }
    }
}

