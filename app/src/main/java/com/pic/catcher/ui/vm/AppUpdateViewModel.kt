package com.pic.catcher.ui.vm

import android.app.AlertDialog
import android.content.Context
import com.lu.magic.util.AppUtil
import com.lu.magic.util.ToastUtil
import com.lu.magic.util.log.LogUtil
import com.pic.catcher.R
import com.pic.catcher.base.BaseViewModel
import com.pic.catcher.route.AppRouter
import com.pic.catcher.util.AppUpdateCheckUtil

class AppUpdateViewModel : BaseViewModel() {
    private var hasOnCheckAction = false
    fun checkOnEnter(context: Context) {
        if (hasOnCheckAction) {
            return
        }
        if (!AppUpdateCheckUtil.hasCheckFlagOnEnter()) {
            return
        }
        hasOnCheckAction = true

        AppUpdateCheckUtil.checkUpdate { url, name, err ->
            if (url.isBlank() || name.isBlank() || err != null) {
                hasOnCheckAction = false
                return@checkUpdate
            }
            AlertDialog.Builder(context)
                .setTitle(R.string.app_update_dialog_title)
                .setMessage(context.getString(R.string.app_update_confirm_tip_format, name))
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(android.R.string.ok) { _, _ ->
                    openBrowserDownloadUrl(context, url)
                }
                .setPositiveButton(R.string.app_update_not_tip) { _, _ ->
                    AppUpdateCheckUtil.setCheckFlagOnEnter(false)
                }
                .setOnDismissListener {
                    hasOnCheckAction = false
                }
                .show()
        }
    }

    fun checkOnce(context: Context, fallBackText: String = AppUtil.getContext().getString(R.string.app_update_not_found)) {
        if (hasOnCheckAction) {
            return
        }
        hasOnCheckAction = true
        AppUpdateCheckUtil.checkUpdate { url, name, err ->
            if (url.isBlank() || name.isBlank()) {
                hasOnCheckAction = false
                ToastUtil.show(fallBackText)
                return@checkUpdate
            }
            AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.app_update_confirm_tip_format, name))
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton(android.R.string.ok) { _, _ ->
                    openBrowserDownloadUrl(context, url)
                }
                .setOnDismissListener {
                    hasOnCheckAction = false
                }
                .show()
        }
    }

    private fun openBrowserDownloadUrl(context: Context, url: String) {
        try {
            AppRouter.route(context, url)
        } catch (e: Exception) {
            ToastUtil.show(context.getString(R.string.app_toast_open_download_url_fail))
            LogUtil.w(e)
        }
    }

}