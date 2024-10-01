package com.pic.catcher;

import android.app.Application;

import androidx.annotation.NonNull;

import com.lu.lposed.api2.XposedHelpers2;
import com.lu.magic.util.log.LogUtil;
import com.lu.magic.util.log.SimpleLogger;
import com.pic.catcher.base.CustomLifecycle;
import com.pic.catcher.base.CustomLifecycleOwner;
import com.pic.catcher.ui.JsonMenuManager;

import org.jetbrains.annotations.NotNull;

public final class App extends Application implements CustomLifecycleOwner {
    public static final Companion Companion = new Companion();
    public static App instance;

    public void onCreate() {
        super.onCreate();

        Companion.setInstance(this);
        JsonMenuManager.Companion.updateMenuListFromRemote(this);
    }

    @NonNull
    @Override
    public CustomLifecycle.State getCurrentState() {
        return CustomLifecycle.State.CREATED;
    }

    @Override
    public void addObserver(@NonNull CustomLifecycle life) {

    }

    @Override
    public void removeObserver(@NonNull CustomLifecycle life) {

    }


    public static final class Companion {
        private Companion() {
        }

        public final App getInstance() {
            App app = instance;
            if (app != null) {
                return app;
            }
            return null;
        }

        public final void setInstance(@NotNull App app) {
            instance = app;
        }

    }
}
