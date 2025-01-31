package com.pic.catcher.util.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lu.magic.util.AppUtil
import com.lu.magic.util.ResUtil
import com.lu.magic.util.SizeUtil
import com.pic.catcher.util.ColorUtilX
import com.pic.catcher.util.JSONX
import org.json.JSONObject


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

fun View.contains(v: View?): Boolean {
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

/**
 * android的org.JSONObject.optString方法，存在奇怪的特性，当json为 {“key”: null}，
 * optString 方法会返回 "null"，而不是 null
 */
fun JSONObject.optString2(name: String, fallback: String? = null): String? {
    return JSONX.optString(this, name, fallback)
}

fun JSONObject.getLong2(name: String): Long {
    return JSONX.getLong(this, name)
}

/**
 * 获取JSONObject中的Long，对android的org.JSONObject.optLong进行修正。
 * 原先的实现方法，对于value为string的值，通过转为Double，再转为Long，存在精度丢失的问题。
 */
fun JSONObject.optLong2(name: String, fallback: Long = 0): Long {
    return JSONX.optLong(this, name, fallback)
}

fun String?.toJSONObject(): JSONObject? {
    return JSONX.toJSONObject(this)
}