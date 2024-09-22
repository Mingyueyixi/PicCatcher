package com.pic.catcher.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Mingyueyixi
 * @date 2024/9/28 19:28
 * @description
 */
public class BaseFragment extends Fragment implements CustomLifecycleOwner{

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.CREATED);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.RESUMED);
    }

    @Override
    public void onStart() {
        super.onStart();
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.STARTED);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.PAUSED);
    }

    @Override
    public void onStop() {
        super.onStop();
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.STOPPED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCustomLifecycleOwnerDelegate.setCurrentState(CustomLifecycle.State.DESTROYED);
    }


}
