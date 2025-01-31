package com.pic.catcher.plugin;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import com.lu.magic.util.AppUtil;
import com.lu.magic.util.IOUtil;
import com.lu.magic.util.log.LogUtil;
import com.lu.magic.util.thread.AppExecutor;
import com.pic.catcher.config.ModuleConfig;
import com.pic.catcher.util.FileUtils;
import com.pic.catcher.util.Md5Util;
import com.pic.catcher.util.PicUtil;
import com.pic.catcher.util.Regexs;
import com.pic.catcher.util.http.HttpConnectUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * @author Mingyueyixi
 * @date 2024/9/28 23:37
 * @description 图片导出管理器
 */
public class PicExportManager {
    private static PicExportManager sInstance;
    private File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), AppUtil.getContext().getPackageName());
    private File exportTempDir = new File(exportDir, "temp");

    public File getExportDir() {
        return exportDir;
    }

    public synchronized static PicExportManager getInstance() {
        if (sInstance == null) {
            sInstance = new PicExportManager();
        }
        return sInstance;
    }

    public void exportBitmapFile(File file) {
        runOnIo(() -> {
            if (!file.exists()) {
                return;
            }
            if (ModuleConfig.isLessThanMinSize(file.length())) {
                LogUtil.i("exportBitmapFile, less than size" , file.length());
                return;
            }
            String fileName = file.getName();
            File exportFile = new File(exportDir, fileName);
            exportDir.mkdirs();
            FileUtils.copyFile(file, exportFile);
            LogUtil.i("exportBitmapFile: " + exportFile.getAbsolutePath());
        });

    }


    public void exportBitmap(Bitmap bitmap) {
        runOnIo(() -> {
            if (bitmap == null) {
                return;
            }
            FileOutputStream tempStream = null;
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                synchronized (bitmap) {
                    // 多线程保护，同时将创建文件夹方法放到后面处理，避免异步线程回收bitmap导致compress崩溃
                    if (bitmap.isRecycled()) {
                        LogUtil.i("exportBitmapFile, bitmap is isRecycled， ignore" );
                        return;
                    }
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream);
                }
                exportTempDir.mkdirs();
                byte[] bitmapBytes = byteArrayOutputStream.toByteArray();
                if (ModuleConfig.isLessThanMinSize(bitmapBytes.length)) {
                    LogUtil.i("exportBitmapFile, less than size" , bitmapBytes.length);
                    return;
                }
                String md5 = Md5Util.get(bitmapBytes);
                String exportFileName = md5 + ".webp";
                File exportFile = new File(exportDir, exportFileName);
                if (exportFile.exists()) {
                    return;
                }
                tempStream = new FileOutputStream(exportFile);
                IOUtil.writeByByte(bitmapBytes, tempStream);
                LogUtil.i("exportBitmap: ", exportFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                IOUtil.closeQuietly(tempStream);
            }
        });
    }

    public void runOnIo(Runnable runnable) {
        if (Looper.getMainLooper().isCurrentThread()) {
            AppExecutor.io().execute(runnable);
        } else {
            runnable.run();
        }
    }

    private void exportHttpPicUrlIfNeed(String url, String fileEx) {
        HttpConnectUtil.request("GET", url, null, null, true,
                response -> {
                    if (!Regexs.PIC_EXT.matcher(fileEx).find()) {
                        //图片不知道
                        Map<String, List<String>> headers = response.getHeader();
                        List<String> contentType = headers.get("Content-Type");
                        if (contentType == null || contentType.isEmpty()) {
                            //不能判断是不是图片
                            return null;
                        }
                        String guessFileEx = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType.get(0));
                        if (!Regexs.PIC_EXT.matcher(guessFileEx).find()) {
                            //不是图片
                            return null;
                        }
                        exportByteArray(response.getBody(), fileEx);
                        IOUtil.closeQuietly(response);
                        return null;
                    }
                    byte[] body = response.getBody();
                    exportByteArray(body, fileEx);
                    return null;
                }
        );
    }

    public void exportUrlIfNeed(String url) {
        runOnIo(() -> {
            try {
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                String fileEx = MimeTypeMap.getFileExtensionFromUrl(url);
                fileEx = fileEx.toLowerCase(Locale.ROOT);
                if (!URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
                    exportHttpPicUrlIfNeed(url, fileEx);
                } else if (URLUtil.isFileUrl(url)) {
                    File file = new File(URI.create(url));
                    if (Regexs.PIC_EXT.matcher(fileEx).find()) {
                        exportBitmapFile(file);
                    } else {
                        //判断文件内容是否是图片格式
                        fileEx = PicUtil.detectImageType(file, "");
                        if (Regexs.PIC_EXT.matcher(fileEx).find()) {
                            exportBitmapFile(file);
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.d(e);
            }
        });

    }

    public void exportByteArray(final byte[] dataBytes, String lastName) {
        if (TextUtils.isEmpty(lastName)) {
            lastName = PicUtil.detectImageType(dataBytes, "bin");
        }
        if (!lastName.startsWith(".")) {
            lastName = "." + lastName;
        }
        final String finalLastName = lastName;
        runOnIo(() -> {
            if (dataBytes == null || dataBytes.length == 0) {
                LogUtil.d("exportByteArray: dataBytes is empty");
                return;
            }
            if (ModuleConfig.isLessThanMinSize(dataBytes.length)) {
                LogUtil.i("exportBitmapFile, less than size" , dataBytes.length);
                return;
            }
            FileOutputStream fileOutputStream = null;
            try {
                String fileName = Md5Util.get(dataBytes) + finalLastName;
                File file = new File(this.exportDir, fileName);
                if (file.exists()) {
                    LogUtil.d("exportByteArray: file already exists:", file);
                    return;
                }
                this.exportDir.mkdirs();
                fileOutputStream = new FileOutputStream(file);
                IOUtil.writeByByte(dataBytes, fileOutputStream);
                LogUtil.i("exportByteArray: ", file);
            } catch (Throwable th) {
//                th.printStackTrace();
                LogUtil.d(th);
            } finally {
                IOUtil.closeQuietly(fileOutputStream);
            }
        });
    }


}