package com.pic.catcher.plugin;


import android.content.Context;
import android.webkit.MimeTypeMap;

import com.lu.lposed.api2.XC_MethodHook2;
import com.lu.lposed.api2.XposedHelpers2;
import com.lu.lposed.plugin.IPlugin;
import com.lu.magic.util.IOUtil;
import com.lu.magic.util.log.LogUtil;
import com.pic.catcher.ClazzN;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Lu
 * @date 2024/10/13 15:18
 * @description
 */
public class GlideCatcherPlugin implements IPlugin {
    @Override 
    public void handleHook(Context context, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        LogUtil.d("GlideCatcherPlugin", "handleHook", ClazzN.from("com.bumptech.glide.load.resource.gif.StreamGifDecoder"));
        XposedHelpers2.findAndHookMethod(ClazzN.from("com.bumptech.glide.load.resource.gif.StreamGifDecoder"), "inputStreamToBytes", InputStream.class, new XC_MethodHook2() { // from class: com.pic.catcher.plugin.GlideCatcherPlugin.1
            @Override // com.lu.lposed.api2.XC_MethodHook2
            public void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                Object result = param.getResult();
                LogUtil.d("GlideCatcherPlugin", "inputStreamToBytes", result);
                if (result != null) {
                    PicExportManager.getInstance().exportByteArray((byte[]) result, ".gif");
                }
            }
        });
        XposedHelpers2.hookAllMethods(ClazzN.from("com.bumptech.glide.load.model.FileLoader"), "buildLoadData", new XC_MethodHook2() { // from class: com.pic.catcher.plugin.GlideCatcherPlugin.2
            @Override // com.lu.lposed.api2.XC_MethodHook2
            public void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
                Object arg = param.args[0];
                if (arg instanceof File) {
                    PicExportManager.getInstance().exportBitmapFile((File) arg);
                }
            }
        });
        XposedHelpers2.hookAllMethods(ClazzN.from("com.bumptech.glide.load.data.HttpUrlFetcher"), "loadData", new XC_MethodHook2() { // from class: com.pic.catcher.plugin.GlideCatcherPlugin.3
            @Override // com.lu.lposed.api2.XC_MethodHook2
            public void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
                Object glideUrl;
                Object callMethod;
                Object callback = param.args[1];
                if (Proxy.isProxyClass(callback.getClass())) {
                    return;
                }
                glideUrl = XposedHelpers2.getObjectField(param.thisObject, "glideUrl");
                String lastName = null;
                if (glideUrl != null) {
                    callMethod = XposedHelpers2.callMethod(glideUrl, "toStringUrl", new Object[0]);
                    String url = (String) callMethod;
                    lastName = MimeTypeMap.getFileExtensionFromUrl(url);
                }
                final String url2 = lastName;
                Object callback2 = Proxy.newProxyInstance(callback.getClass().getClassLoader(), callback.getClass().getInterfaces(), new InvocationHandler() { // from class: com.pic.catcher.plugin.GlideCatcherPlugin.3.1
                    @Override // java.lang.reflect.InvocationHandler
                    public Object invoke(Object o, Method method, Object[] objects) throws InvocationTargetException, IllegalAccessException {
                        if ("onDataReady".equals(method.getName())) {
                            Object iStream = objects[0];
                            if (iStream instanceof InputStream) {
                                byte[] data = IOUtil.readToBytes((InputStream) iStream);
                                PicExportManager.getInstance().exportByteArray(data, url2);
                                ByteArrayInputStream iStream2 = new ByteArrayInputStream(data);
                                objects[0] = iStream2;
                            }
                        }
                        return method.invoke(o, objects);
                    }
                });
                param.args[1] = callback2;
            }
        });
    }
}