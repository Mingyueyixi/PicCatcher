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
import com.pic.catcher.util.Regexs;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

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
//        handleHookAndroidOkHttp(context, loadPackageParam);
    }

    private void handleHookAndroidOkHttp(Context context, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers2.findAndHookMethod(
                ClazzN.from("com.android.okhttp.internal.http.HttpEngine"),
                "readResponse",
                new XC_MethodHook2() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object response = XposedHelpers2.getObjectField(param.thisObject, "userResponse");

                        LogUtil.d("response", response);

                        String contentType = (String) XposedHelpers2.callMethod(response, "header", "Content-Type");
                        if (TextUtils.isEmpty(contentType)) {
                            LogUtil.d("content-type is empty");
                            return;
                        }
                        String guessFileEx = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType);
                        if (TextUtils.isEmpty(guessFileEx) || !Regexs.PIC_EXT.matcher(guessFileEx).find()) {
                            //不是图片
                            return;
                        }
                        Object body = XposedHelpers2.callMethod(response, "body");
                        LogUtil.d("body", body);
                        if (body == null) {
                            return;
                        }
                        //com.android.okhttp.internal.http.Http1xStream$FixedLengthSource
                        Object source = XposedHelpers2.callMethod(body, "source");
                        LogUtil.d("bufferedSource", source);
                        if (source == null) {
                            return;
                        }
                        //读取二进制
                        Object bytes = XposedHelpers2.callMethod(source, "readByteArray");
//                        Object buffer = XposedHelpers2.getObjectField(source, "buffer");
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        outputStream.write((byte[]) bytes);
                        Object sink = XposedHelpers2.callStaticMethod(ClazzN.from("com.android.okhttp.okio.Okio"), "sink", outputStream);
                        Object buffer = XposedHelpers2.callMethod(ClazzN.from("com.android.okhttp.okio.Okio"), "buffer", sink);
                        XposedHelpers2.setObjectField(body, "buffer", buffer);

                        //com.android.okhttp.internal.http.Http1xStream$FixedLengthSource
//                        Object sourceField = XposedHelpers2.getObjectField(source, "source");
//                        XposedHelpers2.callMethod(buffer, "read", bytes);
                        //读完就废了，所以复制一个给原先的结果

//                        LogUtil.d("readByteArray is ok", bytes + "", sourceField);
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
                        Object response = param.getResult();
                        String contentType = (String) XposedHelpers2.callMethod(response, "header", "Content-Type");
                        if (TextUtils.isEmpty(contentType)) {
                            LogUtil.d("content-type is empty");
                            return;
                        }
                        String guessFileEx = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType);
                        if (TextUtils.isEmpty(guessFileEx) || !Regexs.PIC_EXT.matcher(guessFileEx).find()) {
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
