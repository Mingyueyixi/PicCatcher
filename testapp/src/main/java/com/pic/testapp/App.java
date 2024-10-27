package com.pic.testapp;

import android.app.Application;
import android.content.Context;

/**
 * @author Lu
 * @date 2024/10/26 20:11
 * @description
 */
public class App extends Application {
    private static App instance;


    public static App getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        instance = this;
        super.attachBaseContext(base);
    }

}
