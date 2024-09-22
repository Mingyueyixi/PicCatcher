package com.pic.catcher.route

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.lu.magic.util.AppUtil
import com.lu.magic.util.ToastUtil
import com.lu.magic.util.log.LogUtil
import com.pic.catcher.App
import com.pic.catcher.base.ViewModelProvider
import com.pic.catcher.base.ViewModelProviders
import com.pic.catcher.config.AppConfigUtil
import com.pic.catcher.ui.MainActivity
import com.pic.catcher.ui.WebViewActivity
import com.pic.catcher.ui.vm.AppUpdateViewModel

/**
 * app内部跳转协议实现，如：
 * piccatcher://com.pic.catcher/feat/checkAppUpdate
 * piccatcher://com.pic.catcher/page/main
 * piccatcher://com.pic.catcher/page/webView?forceHtml=false&isDialog=true&url=https://www.baidu.com
 * piccatcher://com.pic.catcher/page/releasesNote?isDialog=true&title=更新日记
 */
class AppRouter {
    companion object {
        val vailScheme = "piccatcher"
        val vailHost = "com.pic.catcher"
        private val appUpdateViewModel = ViewModelProviders.from(App.instance).get(AppUpdateViewModel::class.java)
        fun routeCheckAppUpdateFeat(activity: Activity) {
            route(activity, "piccatcher://com.pic.catcher/feat/checkAppUpdate")
        }

        fun routeDonateFeat(activity: Activity) {
            route(activity, "piccatcher://com.pic.catcher/feat/donate")
        }

        fun routeWebViewPage(
            activity: Context,
            webUrl: String,
            title: String,
            isDialogUI: Boolean = false,
            forceHtml: Boolean = false
        ) {
            val uri = Uri.parse("piccatcher://com.pic.catcher/page/webView")
                .buildUpon()
                .appendQueryParameter("forceHtml", forceHtml.toString())
                .appendQueryParameter("isDialog", isDialogUI.toString())
                .appendQueryParameter("url", webUrl)
                .appendQueryParameter("title", title)
                .build()
            route(activity, uri.toString())
        }

        fun routeReleasesNotePage(activity: Activity, title: String, isDialogWebUI: Boolean = false) {
            val uri = Uri.parse("piccatcher://com.pic.catcher/page/releasesNote")
                .buildUpon()
                .appendQueryParameter("isDialog", isDialogWebUI.toString())
                .appendQueryParameter("title", title)
                .build()
            route(activity, uri.toString())
        }

        fun isAppLink(uri: Uri?): Boolean {
            if (uri == null) {
                return false
            }
            return vailScheme == uri.scheme && vailHost == uri.host
        }
        fun isPageGroup(uri: Uri): Boolean {
            val segments = uri.pathSegments
            if (segments.size >= 1) {
                return segments[0] == "page"
            }
            return false
        }

        @JvmOverloads
        fun route(context: Context = AppUtil.getContext(), url: String?, onFail: ((e: Throwable) -> Unit)? = null) {
            try {
                LogUtil.i("App Route", url)
                val uri = Uri.parse(url)
                if (isAppLink(uri)) {
                    val pathSegments = uri.pathSegments
                    if (pathSegments.size == 2) {
                        val group = pathSegments[0] ?: ""
                        val name = pathSegments[1] ?: ""
                        routeAppLink(context, uri, group, name)
                    } else {
                        LogUtil.w("is App link ,but pathSegments‘s size is not match. Jump to main Page")
                        jumpMainPage(context, uri)
                    }
                } else {
                    val intent = Intent.parseUri(url, Intent.URI_ALLOW_UNSAFE)
                    //                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            } catch (e: Throwable) {
                LogUtil.w(e)
                onFail?.invoke(e)
            }
        }

//        private fun routeOtherAppLink(context: Context, uri: Uri) {
//            val intent = Intent()
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.action = Intent.ACTION_VIEW
//            intent.data = uri
//            context.startActivity(intent)
//        }

        private fun routeAppLink(context: Context, uri: Uri, group: String, name: String) {
            when (group) {
                "feat" -> routeFeatGroup(context, uri, name)
                "page" -> routePageGroup(context, uri, name)
                else -> {
                    LogUtil.w(group, "for link 's group not impl")
                }
            }
        }

        private fun routeFeatGroup(context: Context, uri: Uri, name: String) {
            when (name) {
                "checkAppUpdate" -> appUpdateViewModel.checkOnce(context)
                "donate" -> {
                    ToastUtil.show("Good good study, day day up.")
                }
                else -> LogUtil.w(name, "for link featGroup not impl")
            }

        }

        private fun routePageGroup(context: Context, uri: Uri, name: String) {
            when (name) {
                "webView" -> {
                    val isDialog = uri.getQueryParameter("isDialog").toBoolean()
                    if (isDialog) {
                        com.pic.catcher.ui.WebViewDialog(
                            context,
                            uri.getQueryParameter("url") ?: "about:blank",
                            uri.getQueryParameter("title"),
                            uri.getQueryParameter("forceHtml").toBoolean()
                        ).show()
                    } else {
                        val intent = Intent(context, WebViewActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("url", uri.getQueryParameter("url"))
                        intent.putExtra("title", uri.getQueryParameter("title"))
                        intent.putExtra("forceHtml", uri.getQueryParameter("forceHtml").toBoolean())
                        context.startActivity(intent)
                    }
                }

                "releasesNote" -> {
                    val isDialog = uri.getQueryParameter("isDialog").toBoolean()
                    val webUrl = AppConfigUtil.getReleaseNoteWebUrl()
                    val forceHtml = Regex("https?://.+\\.html", RegexOption.IGNORE_CASE).matches(webUrl)
                    if (isDialog) {
                        com.pic.catcher.ui.WebViewDialog(
                            context,
                            webUrl,
                            uri.getQueryParameter("title") ?: context.applicationInfo.name,
                            forceHtml
                        ).show()
                    } else {
                        val intent = Intent(context, WebViewActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("url", webUrl)
                        intent.putExtra("title", uri.getQueryParameter("title"))
                        intent.putExtra("forceHtml", forceHtml)
                        context.startActivity(intent)
                    }
                }

                "main" -> jumpMainPage(context, uri)

                else -> LogUtil.w(name, "for link pageGroup not impl")
            }
        }

        fun routeMainPage(context: Context, intentData: Uri? = null) {
            val routeUri = Uri.parse("piccatcher://com.pic.catcher/page/main").buildUpon()
                .appendQueryParameter("data", intentData.toString())
                .build()
                .toString()
            route(context, routeUri)
        }

        fun jumpMainPage(context: Context, uri: Uri) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            uri.getQueryParameter("data")?.let {
                intent.data = Uri.parse(it)
            }
            context.startActivity(intent)
        }
    }
}