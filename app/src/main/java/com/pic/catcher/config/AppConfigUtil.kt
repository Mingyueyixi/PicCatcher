package com.pic.catcher.config

import android.net.Uri
import com.lu.magic.util.AppUtil
import com.lu.magic.util.log.LogUtil
import com.pic.catcher.R
import com.pic.catcher.util.LanguageUtil
import com.pic.catcher.util.TimeExpiredCalculator
import com.pic.catcher.util.http.HttpConnectUtil
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream

class AppConfigUtil {
    companion object {
        private val configFilePath_zh = "app/src/main/res/raw/app_config.json"
        private val configFilePath_en = "app/src/main/res/raw/app_config_en.json"
            get() {
                LogUtil.d("configFilePath: $field isEnglish", LanguageUtil.isEnglish())
                if (LanguageUtil.isEnglish()) {
                    return configFilePath_en
                }
                return configFilePath_zh

            }
        private  var configFilePath = configFilePath_zh
        val githubMainUrl = "https://raw.githubusercontent.com/Mingyueyixi/PicCatcher/refs/heads/main"

        //@main分支 或者@v1.6， commit id之类的，直接在写/main有时候不行
        //不指定版本，则取最后一个https://www.jsdelivr.com/?docs=gh
        val cdnMainUrl = "https://cdn.jsdelivr.net/gh/Mingyueyixi/PicCatcher@main"
        var config: AppConfig = AppConfig()

        //5分钟过期时间
        val releaseNoteExpiredSetting by lazy { TimeExpiredCalculator(5 * 60 * 1000L) }

        fun init() {
            load()
            checkRemoteUpdate()
        }
        private fun openAppConfigFromRawResource(): InputStream {
            if (LanguageUtil.isEnglish()) {
                return AppUtil.getContext().resources.openRawResource(R.raw.app_config_en)
            }
            return AppUtil.getContext().resources.openRawResource(R.raw.app_config)
        }

        fun load() {
            val file = getLocalFile(configFilePath)
            var rawBin: ByteArray
            val rawConfig: JSONObject = openAppConfigFromRawResource().use {
                rawBin = it.readBytes()
                JSONObject(rawBin.toString(Charsets.UTF_8))
            }
            val localConfig: JSONObject? = try {
                if (file.exists()) JSONObject(file.readText(Charsets.UTF_8)) else null
            } catch (e: Exception) {
                LogUtil.e("load local config fail, del it", e)
                file.delete()
                null
            }
            val finalJson = localConfig ?: rawConfig
            if (localConfig == null) {
                saveLocalFile(file, rawBin)
            } else {
                if (rawConfig.optInt("version", 0) > localConfig.optInt("version", 0)) {
                    saveLocalFile(file, rawBin)
                }
            }
            config = AppConfig.fromJson(finalJson) ?: config
        }


        //  不通过构造函数创建对象
        // UnsafeAllocator.INSTANCE.newInstance(AppConfig::class.java)
        fun checkRemoteUpdate(callBack: ((config: AppConfig, fromRemote: Boolean) -> Unit)? = null) {
            val rawUrl = "$githubMainUrl/$configFilePath"
            //例如分支v1.12, 写法url编码，且前缀加@v：@vv1.12%2Fdev
            val cdnUrl = "$cdnMainUrl/$configFilePath"
            val file = getLocalFile(configFilePath)

            HttpConnectUtil.get(rawUrl, HttpConnectUtil.noCacheHttpHeader) { raw ->
                if (raw.error != null || raw.code != 200) {
                    LogUtil.d("request raw fail, $rawUrl", raw)
                    HttpConnectUtil.get(cdnUrl, HttpConnectUtil.noCacheHttpHeader) { cdn ->
                        if (cdn.error == null && cdn.code == 200) {
                            saveLocalFile(file, cdn.body)
                            callBack?.invoke(config, true)
                        } else {
                            LogUtil.d("request cdn fail, $cdnUrl", cdn)
                            callBack?.invoke(config, false)
                        }
                    }
                } else {
                    saveLocalFile(file, raw.body)
                    callBack?.invoke(config, true)
                }
            }
        }


        private fun saveLocalFile(file: File, data: ByteArray) {
            file.outputStream().use {
                it.write(data)
            }
        }


        fun getReleaseNoteWebUrl(): String {
            val filePath = "app/src/main/res/raw/releases_note.html"
            val cdnUrl = "$cdnMainUrl/$filePath"
            val githubUrl = "$githubMainUrl/$filePath"

            val file = getLocalFile(filePath)
            if (!file.exists()) {
                try {
                    file.parentFile?.mkdirs()
                    AppUtil.getContext().resources.openRawResource(R.raw.releases_note).use { inStream ->
                        saveLocalFile(file, inStream.readBytes())
                    }
                } catch (e: Exception) {
                }
                releaseNoteExpiredSetting.updateLastTime(0)
            }
            if (releaseNoteExpiredSetting.isExpired()) {
                HttpConnectUtil.get(githubUrl) { github ->
                    if (github.error == null && github.body.isNotEmpty()) {
                        saveLocalFile(file, github.body)
                        releaseNoteExpiredSetting.updateLastTime()
                    } else {
                        LogUtil.i("get fail: ", githubUrl, github)
                        HttpConnectUtil.get(cdnUrl) { cdn ->
                            if (cdn.error == null && cdn.body.isNotEmpty()) {
                                saveLocalFile(file, cdn.body)
                                releaseNoteExpiredSetting.updateLastTime()
                            } else {
                                LogUtil.i("get fail: ", cdnUrl, cdn)
                            }
                        }
                    }
                }
            }
            return Uri.fromFile(file).toString()
        }

        private fun getLocalFile(relativePath: String): File {
            return File(AppUtil.getContext().filesDir, relativePath).also {
                it.parentFile?.mkdirs()
            }
        }
    }
}


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
