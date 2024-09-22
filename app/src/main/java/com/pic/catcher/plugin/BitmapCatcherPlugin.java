package com.pic.catcher.plugin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.TypedValue;

import com.lu.lposed.api2.XC_MethodHook2;
import com.lu.lposed.api2.XposedHelpers2;
import com.lu.lposed.plugin.IPlugin;

import java.io.File;
import java.io.InputStream;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Mingyueyixi
 * @date 2024/9/22 0:22
 * @description bitmap hook
 */
public class BitmapCatcherPlugin implements IPlugin {
    @Override
    public void handleHook(Context context, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers2.findAndHookMethod(
                BitmapFactory.class,
                "decodeFile",
                String.class,
                BitmapFactory.Options.class,
                new XC_MethodHook2() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String filePath = (String) param.args[0];
                        PicExportManager.getInstance().exportBitmapFile(new File(filePath));
                    }
                });

        XposedHelpers2.findAndHookMethod(
                BitmapFactory.class,
                "decodeStream",
                InputStream.class,
                Rect.class,
                BitmapFactory.Options.class,
                new XC_MethodHook2() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Bitmap bitmap = (Bitmap) param.getResult();
                        PicExportManager.getInstance().exportBitmap(bitmap);
                    }
                }
        );

        XposedHelpers2.findAndHookMethod(
                BitmapFactory.class,
                "decodeResource",
                Resources.class,
                int.class,
                BitmapFactory.Options.class,
                new XC_MethodHook2() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Bitmap bitmap = (Bitmap) param.getResult();
                        PicExportManager.getInstance().exportBitmap(bitmap);
                    }
                }
        );
        XposedHelpers2.findAndHookMethod(
                BitmapFactory.class,
                "decodeResourceStream",
                Resources.class,
                TypedValue.class,
                InputStream.class,
                Rect.class,
                BitmapFactory.Options.class,
                new XC_MethodHook2() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Bitmap bitmap = (Bitmap) param.getResult();
                        PicExportManager.getInstance().exportBitmap(bitmap);
                    }
                }
        );
        XposedHelpers2.findAndHookMethod(
                BitmapFactory.class,
                "decodeFileDescriptor",
                java.io.FileDescriptor.class,
                Rect.class,
                BitmapFactory.Options.class,
                new XC_MethodHook2() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Bitmap bitmap = (Bitmap) param.getResult();
                        PicExportManager.getInstance().exportBitmap(bitmap);
                    }
                }
        );
        XposedHelpers2.findAndHookMethod(
                BitmapFactory.class,
                "decodeByteArray",
                byte[].class,
                int.class,
                int.class,
                BitmapFactory.Options.class,
                new XC_MethodHook2() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Bitmap bitmap = (Bitmap) param.getResult();
                        PicExportManager.getInstance().exportBitmap(bitmap);
                    }
                }
        );
    }


}
