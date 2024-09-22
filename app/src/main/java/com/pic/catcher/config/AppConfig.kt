package com.pic.catcher.config

import androidx.annotation.Keep
import org.json.JSONObject


class AppConfig(
    var mainUi: MainUi?
) {
    constructor() : this(null)

    companion object {
        fun fromJson(json: JSONObject?): AppConfig? {
            return if (json == null) null else AppConfig(
                MainUi.fromJson(json.optJSONObject("mainUi"))
            )
        }
    }
}

class MainUi(
    var donateCard: DonateCard?,
    var moduleCard: ModuleCard?
) {
    companion object {
        fun fromJson(json: JSONObject?): MainUi? {
            return if (json == null) null else MainUi(
                DonateCard.fromJson(json.optJSONObject("donateCard")),
                ModuleCard.fromJson(json.optJSONObject("moduleCard"))
            )
        }
    }
}

class DonateCard(
    var des: String?,
    var show: Boolean = false,
    var title: String?
) {
    companion object {
        fun fromJson(json: JSONObject?): DonateCard? {
            return if (json == null) null else DonateCard(
                json.optString("des"),
                json.optBoolean("show", false),
                json.optString("title")
            )
        }
    }
}

class ModuleCard(var link: String?) {
    companion object {
        fun fromJson(json: JSONObject?): ModuleCard? {
            return if (json == null) null else ModuleCard(json.optString("link"))
        }
    }
}