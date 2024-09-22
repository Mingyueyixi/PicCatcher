package com.pic.catcher.plugin;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Looper;

import com.lu.magic.util.AppUtil;
import com.lu.magic.util.IOUtil;
import com.lu.magic.util.log.LogUtil;
import com.lu.magic.util.thread.AppExecutor;
import com.pic.catcher.util.FileUtils;
import com.pic.catcher.util.Md5Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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
            String fileName = file.getName();
            File exportFile = new File(exportDir, fileName);
            exportDir.mkdirs();
            FileUtils.copyFile(file, exportFile);
            LogUtil.d("exportBitmapFile: " + exportFile.getAbsolutePath());
        });

    }

    public void exportBitmap(Bitmap bitmap) {
        runOnIo(() -> {
            if (bitmap == null || bitmap.isRecycled()) {
                return;
            }
            String tempName = bitmap.hashCode() + ".webp";
            FileOutputStream tempStream = null;
            try {
                exportTempDir.mkdirs();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream);
                byte[] bitmapBytes = byteArrayOutputStream.toByteArray();
                String md5 = Md5Util.get(bitmapBytes);
                String exportFileName = md5 + ".webp";
                File exportFile = new File(exportDir, exportFileName);
                if (exportFile.exists()) {
                    exportFile.deleteOnExit();
                }
                tempStream = new FileOutputStream(exportFile);
                IOUtil.writeByByte(bitmapBytes, tempStream);
                LogUtil.d("exportBitmap: ", exportFile.getAbsolutePath());
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
}
