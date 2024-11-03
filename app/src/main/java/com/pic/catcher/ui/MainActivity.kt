package com.pic.catcher.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.pic.catcher.base.BaseActivity
import com.pic.catcher.base.FragmentNavigation
import com.pic.catcher.base.ViewModelProviders
import com.pic.catcher.databinding.LayoutMainBinding
import com.pic.catcher.route.AppRouter
import com.pic.catcher.ui.vm.AppUpdateViewModel


class MainActivity : BaseActivity() {
    private lateinit var fragmentNavigation: FragmentNavigation
    private lateinit var binding: LayoutMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentNavigation = FragmentNavigation(this, binding.mainContainer.id)
        fragmentNavigation.navigate(MainFragment::class.java)

        ViewModelProviders.from(this).get(AppUpdateViewModel::class.java).checkOnEnter(this)
        handleDeeplinkRoute(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeeplinkRoute(intent)
    }

    private fun handleDeeplinkRoute(intent: Intent?) {
        if (intent == null) return
        val from = intent.getStringExtra("from")
        if (DeepLinkActivity::class.java.name != from) {
            return
        }
        intent.data?.let {
            binding.root.post {
                AppRouter.route(this, it.toString())
            }
        }
    }


    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (!fragmentNavigation.navigateBack()) {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu)
        JsonMenuManager.inflate(this, menu)
        return true
    }


}