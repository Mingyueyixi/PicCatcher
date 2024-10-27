package com.pic.testapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Lu
 * @date 2024/10/26 20:24
 * @description
 */
public class LocalStorage {
    public static SharedPreferences getDefault() {
        return App.getInstance().getSharedPreferences("appDefaultConfig", Context.MODE_PRIVATE);
    }

    public interface Key {
        String X5_WEB_VIEW_LAST_URL = "x5WebViewLastUrl";
        String WEB_VIEW_LAST_URL = "WebViewLastUrl";
    }
}
