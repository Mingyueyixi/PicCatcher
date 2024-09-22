package com.pic.catcher.base

import android.app.Activity
import android.app.Fragment
import androidx.annotation.IdRes

class FragmentNavigation(val activity: Activity, @IdRes val fragmentContainerResId: Int) {
    private val mFragmentStack = ArrayList<Fragment>()

    /**
     * 导航到指定cls
     */
    fun navigate(fragClass: Class<out Fragment>) {
        var tag = fragClass.toString()
        var frag: Fragment? = activity.fragmentManager.findFragmentByTag(tag)
        if (frag == null) {
            frag = fragClass.newInstance()
        }
        navigate(frag!!)
    }

    /**
     * 导航到指定fragment
     */
    fun navigate(fragment: Fragment) {
        val tag = fragment.javaClass.toString()
        navigateInterval(fragment, tag)
    }


    private fun navigateInterval(fragment: Fragment, tag: String) {
        if (mFragmentStack.contains(fragment)) {
            activity.fragmentManager.beginTransaction().show(fragment).commitNow()
        } else {
            activity.fragmentManager.beginTransaction().add(fragmentContainerResId, fragment).commitNow()
        }
    }


    /**
     * 退后
     */
    fun navigateBack(): Boolean {
        if (mFragmentStack.size <= 1) {
            mFragmentStack.clear()
            return false
        }
        val fragment = mFragmentStack.removeAt(mFragmentStack.lastIndex)
        navigate(fragment)
        return true
    }

}
