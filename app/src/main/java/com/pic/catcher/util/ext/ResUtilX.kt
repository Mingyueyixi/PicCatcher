package com.pic.catcher.util.ext

import com.lu.magic.util.AppUtil
import com.lu.magic.util.ResUtil

@JvmOverloads
fun ResUtil.getViewId(idName: String, packageName: String = AppUtil.getPackageName()): Int {
    val k = "@id/$idName"
    return AppUtil.getContext().resources.getIdentifier(idName, "id", packageName)
}


