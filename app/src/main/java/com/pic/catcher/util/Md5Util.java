package com.pic.catcher.util;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
    public static String get(String text) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(text.getBytes());
            result = toHexString(digest);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String get(byte[] bytes) {
        return get(new ByteArrayInputStream(bytes));
    }

    public static String get(InputStream inputStream) {
        String result = null;
        try {
            byte[] buffer = new byte[8192];
            int len;
            MessageDigest md = MessageDigest.getInstance("MD5");
            while ((len = inputStream.read(buffer)) != -1) {//分多次读入文件，占用内存比较少
                md.update(buffer, 0, len);
            }
            inputStream.close();

            byte[] digest = md.digest();
            result = toHexString(digest);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String get(File file) {
        String result = null;
        try {
            result = get(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String toHexString(byte[] digest) {
        StringBuilder sb = new StringBuilder();
        String hexStr;
        for (byte b : digest) {
            hexStr = Integer.toHexString(b & 0xFF);//& 0xFF处理负数
            if (hexStr.length() == 1) {//长度等于1，前面进行补0，保证最后的字符串长度为32
                hexStr = "0" + hexStr;
            }
            sb.append(hexStr);
        }

        return sb.toString();
    }
}
