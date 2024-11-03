package com.pic.catcher.util;

import java.util.regex.Pattern;

/**
 * @author Lu
 * @date 2024/10/13 15:03
 * @description 正则
 */
public interface Regexs {
    /**
     * 图片后缀
     */
    Pattern PIC_EXT = Pattern.compile("jpg|jpeg|png|gif|webp|png|tif|ico|bmp|jng");
    /**
     * 图片链接
     */
    Pattern PIC_URL = Pattern.compile("\\.(jpg|jpeg|png|gif|webp|png|tif|ico|bmp|jng)");
}
