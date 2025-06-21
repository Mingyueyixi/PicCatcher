package com.pic.catcher.plugin;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.lu.lposed.api2.XC_MethodHook2;
import com.lu.lposed.api2.XposedHelpers2;
import com.lu.lposed.plugin.IPlugin;
import com.lu.magic.util.AppUtil;
import com.lu.magic.util.log.LogUtil;
import com.pic.catcher.ClazzN;
import com.pic.catcher.config.ModuleConfig;
import com.pic.catcher.util.PicUtil;
import com.pic.catcher.util.Regexs;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Lu
 * @date 2024/10/26 23:16
 * @description
 */
public class OKHttpPlugin implements IPlugin {
    @Override
    public void handleHook(Context context, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        handleHookOkHttp3(context, loadPackageParam);
        handleHookAndroidOkHttp(context, loadPackageParam);
    }

    private void handleHookAndroidOkHttp(Context context, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers2.findAndHookMethod(
                ClazzN.from("com.android.okhttp.internal.http.HttpEngine"),
                "readResponse",
                new XC_MethodHook2() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (!ModuleConfig.getInstance().isCatchNetPic()) {
                            LogUtil.d("catchNetPic is false");
                            return;
                        }
                        Object response = XposedHelpers2.getObjectField(param.thisObject, "userResponse");

                        if (response == null) {
                            return;
                        }
//                        LogUtil.d("response", response);
                        String contentType = (String) XposedHelpers2.callMethod(response, "header", "Content-Type");
                        if (TextUtils.isEmpty(contentType)) {
                            LogUtil.d("content-type is empty");
//                            if (Regexs.PIC_URL.matcher(url))
                            return;
                        }
                        String guessFileEx = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType);
                        if (!PicUtil.isPicSuffix(guessFileEx)) {
                            //不是图片
                            return;
                        }
                        Object body = XposedHelpers2.callMethod(response, "body");
                        LogUtil.d("body", body);
                        if (body == null) {
                            return;
                        }
                        //com.android.okhttp.internal.http.Http1xStream$FixedLengthSource
                        Object bufferSource = XposedHelpers2.callMethod(body, "source");
                        LogUtil.d("bufferedSource", bufferSource);
                        if (bufferSource == null) {
                            return;
                        }
                        Object buffer = XposedHelpers2.getObjectField(bufferSource, "buffer");
                        //读取二进制
                        Object bytes = XposedHelpers2.callMethod(bufferSource, "readByteArray");
                        XposedHelpers2.callMethod(buffer, "write", bytes);
                        //因为读完流就废了，所以重新写回去，否则影响http实际的读写。
                        //安卓系统的buffer没有peeked方法，所以这么做;
                        //但是这样似乎会丢失原先的流读取异常。可以考虑复制继承自安卓hide api 自行实现一个PeekedSource来读取

                        if (bytes != null) {
                            LogUtil.d("readByteArray is bytes. start download");
                            PicExportManager.getInstance().exportByteArray((byte[]) bytes, guessFileEx);
                        }
                    }
                }
        );

    }

    private void handleHookOkHttp3(Context context, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        ClassLoader clazzLoader = AppUtil.getClassLoader();
        LogUtil.d("OKHttpPlugin", "handleHook", clazzLoader);
        Class<?> realCallClazz = ClazzN.from("okhttp3.RealCall", clazzLoader);
        if (realCallClazz == null) {
            LogUtil.d("can not find RealCall class");
            return;
        }
        XposedHelpers2.findAndHookMethod(
                realCallClazz,
//                "execute",
                "getResponseWithInterceptorChain",
                new XC_MethodHook2() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (!ModuleConfig.getInstance().isCatchNetPic()) {
                            LogUtil.d("catchNetPic is false");
                            return;
                        }
                        Object response = param.getResult();
                        if (response == null) {
                            return;
                        }
                        String contentType = (String) XposedHelpers2.callMethod(response, "header", "Content-Type");
                        if (TextUtils.isEmpty(contentType)) {
                            LogUtil.d("content-type is empty");
                            return;
                        }
                        String guessFileEx = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType);
                        if (!PicUtil.isPicSuffix(guessFileEx)) {
                            //不是图片
                            return;
                        }

                        // 制造一个新的body，不影响原始数据读写
                        Object response2 = XposedHelpers2.callMethod(response, "peekBody", Long.MAX_VALUE);
                        if (response2 == null) {
                            LogUtil.d("response2 is null");
                            return;
                        }
                        Object bytes = XposedHelpers2.callMethod(response2, "bytes");
                        if (bytes instanceof byte[]) {
                            LogUtil.d("readByteArray is bytes. start download");
                            PicExportManager.getInstance().exportByteArray((byte[]) bytes, guessFileEx);
                        }
                    }
                }
        );

    }
}
