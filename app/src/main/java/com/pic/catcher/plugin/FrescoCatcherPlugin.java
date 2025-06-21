package com.pic.catcher.plugin;

import android.content.ContentProvider;
import android.content.Context;
import android.net.Uri;

import com.lu.lposed.api2.XC_MethodHook2;
import com.lu.lposed.api2.XposedHelpers2;
import com.lu.lposed.plugin.IPlugin;
import com.lu.magic.util.log.LogUtil;
import com.pic.catcher.ClazzN;

import java.lang.reflect.Field;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Lu
 * @date 2025/4/26 15:20
 * @description
 */
public class FrescoCatcherPlugin implements IPlugin {
    @Override
    public void handleHook(Context context, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Class<?> ImageRequestClazz = ClazzN.from("com.facebook.imagepipeline.request.ImageRequest");
        if (ImageRequestClazz == null) {
            LogUtil.d("can not find ImageRequest class");
            return;
        }
        Class<?> ImageRequestBuilderClazz = ClazzN.from("com.facebook.imagepipeline.request.ImageRequestBuilder");
        if (ImageRequestBuilderClazz == null) {
            LogUtil.d("can not find ImageRequestBuilder class");
            return;
        }
        XposedHelpers2.findAndHookConstructor(
                ImageRequestClazz,
                ImageRequestBuilderClazz,
                new XC_MethodHook2() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Field[] uriField = XposedHelpers2.findFieldsByExactPredicate(param.thisObject.getClass(), field -> {
                            return field.getType().equals(Uri.class);
                        });
                        if (uriField.length == 0) {
                            return;
                        }
                        Uri uri = XposedHelpers2.getObjectField(param.thisObject, uriField[0].getName());
                        // 读取uri对应的字节数组
                        if (uri == null) {
                            return;
                        }
                        PicExportManager.getInstance().exportUrlIfNeed(uri.toString());
                    }
                });
    }
}
