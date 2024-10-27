package com.pic.catcher.util.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import com.lu.magic.util.AppUtil

import com.lu.magic.util.ResUtil
import com.lu.magic.util.SizeUtil
import com.pic.catcher.util.ColorUtilX


val sizeIntCache = HashMap<String, Int>()
val sizeFloatCache = HashMap<String, Float>()


val Int.dp: Int
    get() = sizeIntCache[this.toString()].let {
        if (it == null) {
            val v = SizeUtil.dp2px(AppUtil.getContext().resources, this.toFloat()).toInt()
            sizeIntCache[this.toString()] = v
            return@let v
        }
        return it
    }

val Float.dp: Float
    get() = sizeFloatCache[this.toString()].let {
        if (it == null) {
            val v = SizeUtil.dp2px(AppUtil.getContext().resources, this.toFloat())
            sizeFloatCache[this.toString()] = v
            return@let v
        }
        return it
    }

val Double.dp: Float
    get() = this.toFloat().dp

fun CharSequence?.toIntElse(fallback: Int): Int = try {
    if (this == null) {
        fallback
    }
    Integer.parseInt(this.toString())
} catch (e: Exception) {
    fallback
}

fun TextView.setTextColorTheme(color: Int) {
    if (ResUtil.isAppNightMode(this.context)) {
        setTextColor(ColorUtilX.invertColor(color))
    } else {
        setTextColor(color)
    }
}


fun <T> T?.takeNotNull(fallback: T): T {
    if (this == null) {
        return fallback
    }
    return this
}

fun View.setPadding(all: Int) {
    this.setPadding(all, all, all, all)
}

fun View.setPadding(h: Int, v: Int) {
    this.setPadding(h, v, h, v)
}


val View.layoutInflate: LayoutInflater
    get() = LayoutInflater.from(this.context)

fun View.contains(v:View?): Boolean {
    if (v == null) {
        return false
    }
    if (this is ViewGroup) {
        for (i in 0 until this.childCount) {
            if (v == this.getChildAt(i)) {
                return true
            }
        }
    }
    return false
}
