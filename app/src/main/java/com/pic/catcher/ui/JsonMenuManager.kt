package com.pic.catcher.ui

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import com.lu.magic.util.AppUtil
import com.lu.magic.util.log.LogUtil
import com.pic.catcher.config.AppConfigUtil
import com.pic.catcher.route.AppRouter

class JsonMenuManager {

    companion object {

        fun inflate(context: Context, menu: Menu) {
            val menuList = AppConfigUtil.config.mainUi?.menuList ?: return
            LogUtil.d("read menu list", menuList)
            for (menuBean in menuList) {
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
                                failCount++
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

    }

}