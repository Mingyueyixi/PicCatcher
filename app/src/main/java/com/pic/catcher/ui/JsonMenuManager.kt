package com.pic.catcher.ui

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.Keep
import com.lu.magic.util.AppUtil
import com.lu.magic.util.log.LogUtil
import com.pic.catcher.R
import com.pic.catcher.config.AppConfigUtil
import com.pic.catcher.route.AppRouter
import com.pic.catcher.util.ext.takeNotNull
import com.pic.catcher.util.http.HttpConnectUtil
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset

class JsonMenuManager {

    class MenuBean(
        var groupId: Int = 0,
        var itemId: Int = 0,
        var order: Int = 0,
        var title: String? = "",
        var link: String? = "",
        var appLink: AppLink? = null,
        /**最低支持app版本，小于则不显示*/
        var since: Int = 0
    ) {
        companion object {
            fun fromJson(json: JSONObject?): MenuBean {
                return if (json == null) MenuBean() else MenuBean(
                    json.optInt("groupId"),
                    json.optInt("itemId"),
                    json.optInt("order"),
                    json.optString("title"),
                    json.optString("link"),
                    json.optJSONObject("appLink")?.let { AppLink.fromJson(it) },
                    json.optInt("since")
                )
            }

            fun fromJsonArray(jsonArray: JSONArray?): ArrayList<MenuBean>? {
                if (jsonArray == null) {
                    return null
                }
                val result = ArrayList<MenuBean>()
                for (i in 0 until jsonArray.length()) {
                    result.add(MenuBean.fromJson(jsonArray.optJSONObject(i)))
                }
                return result
            }
        }
    }


    class AppLink(var links: Array<String?>? = null, var priority: Int = 0) {
        companion object {
            fun fromJson(json: JSONObject?): AppLink? {
                return if (json == null) null else AppLink(
                    json.optJSONArray("links")?.let {
                        val result = ArrayList<String?>()
                        for (i in 0 until it.length()) {
                            result.add(it.optString(i))
                        }
                        result.toTypedArray()
                    },
                    json.optInt("priority")
                )
            }
        }
    }

    companion object {
        private var isRemoteUpdating: Boolean = false
        private var lastUpdateSuccessMills = 0L
        private val menuFilePath = "res/raw/menu_ui.json"

        fun inflate(context: Context, menu: Menu) {
            for (menuBean in readMenuList(context)) {
                if (AppUtil.getVersionCode() < menuBean.since) {
                    //不支持的版本，忽略
                    continue
                }
                val menuItem = menu.add(menuBean.groupId, menuBean.itemId, menuBean.order, menuBean.title)
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                menuItem.setOnMenuItemClickListener {
                    val appLink = menuBean.appLink
                    val clickLinkPriority = 0
                    val appLinks = appLink?.links
                    if (appLink == null || clickLinkPriority > appLink.priority || appLinks.isNullOrEmpty()) {
                        try {
                            openLinkWith(context, menuBean.link)
                        } catch (e: Throwable) {
                            LogUtil.w("open link error", e)
                        }
                    } else {
                        var failCount = 0
                        for (link in appLinks) {
                            try {
                                openLinkWith(context, link)
                            } catch (e: Throwable) {
                                failCount++;
                                LogUtil.w("open link faild", e)
                            }
                            if (failCount == 0) {
                                //成功，跳出循环
                                break
                            }
                        }
                        if (failCount == appLinks.size) {
                            LogUtil.w("open appLink with all error", it)
                            try {
                                openLinkWith(context, menuBean.link)
                            } catch (e: Throwable) {
                                LogUtil.w("try open link also error", e)
                            }
                        }


                    }
                    return@setOnMenuItemClickListener true
                }
            }

        }

        private fun openLinkWith(context: Context, link: String?) {
            if (link == null) {
                throw IllegalArgumentException("link is null")
            }
            AppRouter.route(context, link) {
                throw it
            }
        }

        private fun readMenuList(context: Context): ArrayList<MenuBean> {
            val file = File(context.filesDir, menuFilePath)
            val ret: ArrayList<MenuBean> = try {
                val result = ArrayList<MenuBean>()
                if (file.exists()) {
                    MenuBean.fromJsonArray(JSONArray(file.readText()))
                } else {
                    file.parentFile.mkdirs()
                    val text = readMenuJsonTextFromRaw(context)
                    file.writeText(text)
                    MenuBean.fromJsonArray(JSONArray(text))
                }
                result
            } catch (e: Exception) {
                val text = readMenuJsonTextFromRaw(context)
                MenuBean.fromJsonArray(JSONArray(text)).takeNotNull(ArrayList<MenuBean>())
            }
            return ret
        }

        private fun readMenuJsonTextFromRaw(context: Context): String {
            return context.resources.openRawResource(R.raw.menu_ui).use {
                it.readBytes().toString(Charset.forName("UTF-8"))
            }
        }

        fun updateMenuListFromRemoteIfNeed(ctx: Context) {
            // 2 hour
            if (!isRemoteUpdating && System.currentTimeMillis() - lastUpdateSuccessMills > 1000 * 60 * 60 * 2) {
                updateMenuListFromRemote(ctx)
            }
        }

        fun updateMenuListFromRemote(ctx: Context) {
            val context = ctx.applicationContext

            val rawJsonMenuUrl = "${AppConfigUtil.githubMainUrl}/$menuFilePath"
            isRemoteUpdating = true
            HttpConnectUtil.getWithRetry(rawJsonMenuUrl, HttpConnectUtil.noCacheHttpHeader, 1, { retryCount, res ->
                LogUtil.i("onFetch retry:$retryCount", rawJsonMenuUrl)
            }, {
                if (it.error == null && it.code == 200 && it.body.isNotEmpty()) {
                    writeRemoteToLocal(context, it.body)
                    isRemoteUpdating = false
                } else {
                    LogUtil.w("request raw remote menu fail", it)

                    val cdnRawPath = "${AppConfigUtil.cdnMainUrl}/$menuFilePath"
                    LogUtil.i("request $cdnRawPath")
                    HttpConnectUtil.get(cdnRawPath, HttpConnectUtil.noCacheHttpHeader) { cdnRes ->
                        if (cdnRes.error == null && cdnRes.code == 200 && cdnRes.body.isNotEmpty()) {
                            writeRemoteToLocal(context, cdnRes.body)
                        } else {
                            LogUtil.i("request jscdn remote menu fail", cdnRes)
                        }
                        isRemoteUpdating = false
                    }

                }

            })
        }

        private fun writeRemoteToLocal(context: Context, body: ByteArray) {
            File(context.filesDir, menuFilePath).outputStream().use { out ->
                out.write(body)
            }
            lastUpdateSuccessMills = System.currentTimeMillis()
        }

    }

}