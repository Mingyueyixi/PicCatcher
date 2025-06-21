package com.pic.catcher.util;


import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.lu.magic.util.IOUtil;
import com.lu.magic.util.log.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public abstract class PicUtil {


    public static String detectImageType(File file, String fallback) {
        if (!file.exists()) {
            return fallback;
        }
        String result = fallback;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            result = detectImageType(inputStream, fallback);
        } catch (FileNotFoundException e) {
            LogUtil.d(e);
        } finally {
            IOUtil.closeQuietly(inputStream);
        }
        return result;
    }

    public static String detectImageType(InputStream inputStream, String fallback) {
        byte[] headerBytes = null;
        try {
            headerBytes = new byte[12];
            inputStream.read(headerBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ((headerBytes[0] & 0xFF) == 0xFF && (headerBytes[1] & 0xFF) == 0xD8) {
            return "jpg";
        } else if (headerBytes[0] == (byte) 0x89 && headerBytes[1] == (byte) 0x50 && headerBytes[2] == (byte) 0x4E && headerBytes[3] == (byte) 0x47) {
            return "png";
        } else if (headerBytes[0] == (byte) 0x47 && headerBytes[1] == (byte) 0x49 && headerBytes[2] == (byte) 0x46 && headerBytes[3] == (byte) 0x38) {
            return "gif";
        } else if (isWebP(headerBytes)) {
            return "webp";
        }
        return fallback;
    }

    public static String detectImageType(byte[] data, String fallback) {
        if (data == null || data.length < 8) {
            return fallback;
        }
        return detectImageType(new ByteArrayInputStream(data), fallback);
    }

    public static boolean isWebP(byte[] data) {
        if (data == null || data.length < 12) {
            return false;
        }

        // 检查前4个字节是否为 "RIFF"
        if (data[0] != 'R' || data[1] != 'I' || data[2] != 'F' || data[3] != 'F') {
            return false;
        }

        // 检查第8到第11个字节是否为 "WEBP"
        if (data[8] != 'W' || data[9] != 'E' || data[10] != 'B' || data[11] != 'P') {
            return false;
        }
        return true;
    }

    public static String getImageType(String fileName, byte[] data, String fallback) {
        String result = MimeTypeMap.getSingleton().getExtensionFromMimeType(fileName);
        if (!PicUtil.isPicSuffix(result)) {
            result = detectImageType(data, result);
        }
        if (TextUtils.isEmpty(result)) {
            return fallback;
        }
        if ("jpeg".equalsIgnoreCase(result)) {
            result = "jpg";
        }
        return result;
    }


    public static String getImageType(String fileName, InputStream inputStream, String fallback) {
        String result = MimeTypeMap.getSingleton().getExtensionFromMimeType(fileName);
        if (!PicUtil.isPicSuffix(result)) {
            result = detectImageType(inputStream, result);
        }
        if (TextUtils.isEmpty(result)) {
            result = fallback;
        }
        if ("jpeg".equalsIgnoreCase(result)) {
            result = "jpg";
        }
        return result;
    }

    public static String getImageType(String fileName, File file, String fallback) {
        String result = MimeTypeMap.getSingleton().getExtensionFromMimeType(fileName);
        if (!PicUtil.isPicSuffix(result)) {
            result = detectImageType(file, result);
        }
        if (TextUtils.isEmpty(result)) {
            result = fallback;
        }
        if ("jpeg".equalsIgnoreCase(result)) {
            result = "jpg";
        }
        return result;
    }

    /**
     * 判断是图片后缀
     *
     * @param text
     * @return
     */
    public static boolean isPicSuffix(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return Regexs.PIC_EXT.matcher(text).find();
    }
}