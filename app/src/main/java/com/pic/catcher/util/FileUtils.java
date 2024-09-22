package com.pic.catcher.util;

import android.os.Build;

import com.lu.magic.util.IOUtil;
import com.lu.magic.util.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Mingyueyixi
 * @date 2024/9/28 23:51
 * @description
 */
public class FileUtils {
    /**
     * 复制文件
     *
     * @param sourceFilePath 源文件路径
     * @param destFilePath   目标文件路径
     */
    public static void copyFile(InputStream sourceFilePath, OutputStream destFilePath) {
        FileInputStream iStream = null;
        FileOutputStream oStream = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                android.os.FileUtils.copy(iStream, oStream);
            } else {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = sourceFilePath.read(buffer)) > 0) {
                    oStream.write(buffer, 0, length);
                }
                oStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(iStream, oStream);
        }
    }

    public static void copyFile(File sourceFilePath, File destFilePath) {
        if (!sourceFilePath.exists()) {
            return;
        }
        if (destFilePath.exists() && destFilePath.isDirectory()) {
            LogUtil.w("目标文件是目录，无法复制文件:" + destFilePath.getName());
            return;
        }
        try {
            FileInputStream iSteam = new FileInputStream(sourceFilePath);
            FileOutputStream oStream = new FileOutputStream(destFilePath);
            copyFile(iSteam, oStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
