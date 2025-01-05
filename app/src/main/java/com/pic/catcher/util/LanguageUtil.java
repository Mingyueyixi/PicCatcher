package com.pic.catcher.util;

/**
 * @author Lu
 * @date 2025/1/6 0:47
 * @description
 */
public class LanguageUtil {
    public static boolean isEnglish() {
        // 获取当前设备的语言设置
        return "en".equals(java.util.Locale.getDefault().getLanguage());
    }
}
