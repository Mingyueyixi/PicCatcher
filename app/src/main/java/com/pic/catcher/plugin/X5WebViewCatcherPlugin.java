package com.pic.catcher.plugin;

import android.content.Context;
import android.webkit.WebView;

import com.lu.lposed.api2.XC_MethodHook2;
import com.lu.lposed.api2.XposedHelpers2;
import com.lu.lposed.plugin.IPlugin;
import com.lu.magic.util.log.LogUtil;
import com.pic.catcher.ClazzN;
import com.pic.catcher.config.ModuleConfig;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Lu
 * @date 2024/10/13 14:48
 * @description x5webview 网页图片加载获取
 */
public class X5WebViewCatcherPlugin implements IPlugin {

    @Override
    public void handleHook(Context context, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Class<?> webViewClientClazz = ClazzN.from("com.tencent.smtt.sdk.WebViewClient");
        Class<?> webViewClazz = ClazzN.from("com.tencent.smtt.sdk.WebView");
        if (webViewClientClazz == null || webViewClazz == null) {
            return;
        }
        XposedHelpers2.findAndHookMethod(
                webViewClientClazz,
                "onLoadResource",
                webViewClazz,
                String.class,
                new XC_MethodHook2() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (!ModuleConfig.getInstance().isCatchWebViewPic()) {
                            LogUtil.d("catchWebViewPic is false");
                            return;
                        }
                        WebView webView = (WebView) param.args[0];
                        String url = (String) param.args[1];
                        LogUtil.d("WebViewClient.onLoadResource", "url=" + url);
                        PicExportManager.getInstance().exportUrlIfNeed(url);
                    }
                }
        );

    }
}
