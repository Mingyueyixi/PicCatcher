package com.pic.catcher.plugin;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lu.lposed.api2.XC_MethodHook2;
import com.lu.lposed.api2.XposedHelpers2;
import com.lu.lposed.plugin.IPlugin;
import com.lu.magic.util.log.LogUtil;
import com.pic.catcher.config.ModuleConfig;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Lu
 * @date 2024/10/13 14:48
 * @description webview 网页图片加载获取
 */
public class WebViewCatcherPlugin implements IPlugin {

    @Override
    public void handleHook(Context context, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers2.findAndHookMethod(
                WebViewClient.class,
                "onLoadResource",
                WebView.class,
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
