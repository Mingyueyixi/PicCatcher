package com.pic.catcher.ui

import android.os.Bundle
import android.webkit.WebView
import android.widget.FrameLayout
import com.lu.magic.util.log.LogUtil
import com.pic.catcher.base.BaseActivity
import com.pic.catcher.ui.wrapper.WebViewComponent

class WebViewActivity : BaseActivity() {
    val webViewComponent by lazy { WebViewComponent(this) }
    var hasLoadUrl = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentLayout = FrameLayout(this)
        setContentView(contentLayout)

        val webUrl = intent.getStringExtra("url")
        val forceHtml = intent.getBooleanExtra("forceHtml", false)
        val preTitleText = intent.getStringExtra("title")
        if (!preTitleText.isNullOrBlank()) {
            title = preTitleText
        }

        if (webUrl.isNullOrBlank()) {
            finish()
            return
        }
        LogUtil.i("onCreate")
        webViewComponent.forceHtml = forceHtml
        webViewComponent.attachView(contentLayout)
        webViewComponent.loadUrl(webUrl, object : com.pic.catcher.ui.wrapper.WebViewComponentCallBack {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                if (preTitleText.isNullOrBlank() && !title.isNullOrBlank()) {
                    setTitle(title)
                }
            }
        })
        hasLoadUrl = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (hasLoadUrl) {
            webViewComponent.destroy()
        }
    }
}