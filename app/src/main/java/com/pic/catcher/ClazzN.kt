package com.pic.catcher

import com.lu.lposed.api2.XposedHelpers2
import com.lu.magic.util.AppUtil

interface ClazzN {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun from(clazz: String, classLoader: ClassLoader = AppUtil.getContext().classLoader): Class<*>? {
            return XposedHelpers2.findClassIfExists(clazz, classLoader)
        }
    }
}