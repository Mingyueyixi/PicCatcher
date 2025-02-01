package com.pic.catcher.base;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Mingyueyixi
 * @date 2024/9/28 19:25
 * @description BaseActivity
 */
public class BaseActivity extends Activity implements CustomLifecycleOwner{
    private CustomLifecycleOwnerDelegate mCustomLifecycleOwnerDelegate = new CustomLifecycleOwnerDelegate();
    @NonNull
    @Override
    public CustomLifecycle.State getCurrentState() {
        return mCustomLifecycleOwnerDelegate.getCurrentState();
    }

    @Override
    public void addObserver(@NonNull CustomLifecycle life) {
        if (life == null) {
            return;
        }
        mCustomLifecycleOwnerDelegate.addObserver(life);
    }

    @Override
    public void removeObserver(@NonNull CustomLifecycle life) {
        mCustomLifecycleOwnerDelegate.removeObserver(life);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.CREATED);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.STARTED);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.RESUMED);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.PAUSED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.STOPPED);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.DESTROYED);
    }

}
