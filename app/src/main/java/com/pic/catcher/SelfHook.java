package com.pic.catcher;

import android.content.Context;

import androidx.annotation.Keep;

import com.lu.lposed.plugin.IPlugin;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@Keep
public class SelfHook implements IPlugin {


    private final static class Holder {
        private static final SelfHook INSTANCE = new SelfHook();
    }

    public static SelfHook getInstance() {
        return Holder.INSTANCE;
    }

    //自己hook自己，改变其值，说明模块有效
    public boolean isModuleEnable() {
        return false;
    }

    @Override
    public void handleHook(Context context, XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(
                SelfHook.class.getName(),
                lpparam.classLoader,
                "isModuleEnable",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                    }
                }
        );

    }

}