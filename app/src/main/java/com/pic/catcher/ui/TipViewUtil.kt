package com.pic.catcher.ui

import android.R
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.lu.magic.util.SizeUtil
import com.lu.magic.util.ToastUtil
import com.pic.catcher.util.ActivityUtils
import com.pic.catcher.util.BarUtils
import java.lang.ref.WeakReference

/**
 * @author Lu
 * @date 2024/11/4 1:06
 * @description
 */
class TipViewUtil {
    companion object {
        @JvmStatic
        fun showLong(context: Context?, text: CharSequence) {
            val activity = ActivityUtils.getActivityByContext(context)
            val parent = activity?.findViewById<ViewGroup>(R.id.content)
            if (parent == null) {
                ToastUtil.showLong(text.toString())
                return
            }

            val textView = TextView(parent!!.context)
            val dp16 = SizeUtil.dp2px(parent.resources, 16f).toInt()
            textView.setPadding(dp16, dp16, dp16, dp16)
            textView.setTextColor(Color.BLACK)

            textView.setText(text)
            // 创建 GradientDrawable 对象
            val gradientDrawable = GradientDrawable()
            // 设置圆角半径
            gradientDrawable.cornerRadius = dp16.toFloat()
            // 设置渐变颜色
            gradientDrawable.setColor(Color.WHITE)
            // 设置渐变方向
//        gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            textView.background = gradientDrawable
            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.setMargins(dp16, dp16 + BarUtils.getStatusBarHeight(), dp16, dp16 + BarUtils.getNavBarHeight())
            lp.gravity = Gravity.BOTTOM
            val tipContent = if (parent is FrameLayout) {
                textView
            } else {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT
                FrameLayout(parent.context).apply {
                    isClickable = false
                    addView(textView)
                }
            }
            parent.addView(tipContent, lp)
            val parentRef = WeakReference(parent)
            val contentRef = WeakReference<View>(tipContent)
            parent.postDelayed({
                val p = parentRef.get()
                val v = contentRef.get()
                if (p != null && v != null) {
                    p.removeView(v)
                }
            }, 5000)
        }
    }

}
