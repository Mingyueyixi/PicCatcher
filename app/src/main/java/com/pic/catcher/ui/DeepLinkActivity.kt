package com.pic.catcher.ui

import android.content.Intent
import android.os.Bundle
import com.pic.catcher.base.BaseActivity

class DeepLinkActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.putExtra("from", DeepLinkActivity::class.java.name)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

//        intent.data?.let {
//            if (AppRouter.isPageGroup(it)) {
//                AppRouter.route(this, it.toString())
//                finish()
//                return
//            }
//        }
        intent.setClass(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}