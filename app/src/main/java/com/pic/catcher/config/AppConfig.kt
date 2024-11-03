package com.pic.catcher.config

import com.pic.catcher.ui.JsonMenuManager
import org.json.JSONObject


class AppConfig(
    var mainUi: MainUi?,
    var version: Int,
) {
    constructor() : this(null, 0)

    companion object {
        fun fromJson(json: JSONObject?): AppConfig? {
            return if (json == null) null else AppConfig(
                MainUi.fromJson(json.optJSONObject("mainUi")),
                json.optInt("version")
            )
        }
    }
}

class MainUi(
    var donateCard: DonateCard?,
    var moduleCard: ModuleCard?,
    var menuList: List<MenuBean>?
) {
    companion object {
        fun fromJson(json: JSONObject?): MainUi? {
            return if (json == null) null else MainUi(
                DonateCard.fromJson(json.optJSONObject("donateCard")),
                ModuleCard.fromJson(json.optJSONObject("moduleCard")),
                MenuBean.fromJsonArray(json.optJSONArray("menuList"))
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