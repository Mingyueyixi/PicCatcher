package com.pic.testapp.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

/**
 * @author Lu
 * @date 2024/10/26 15:12
 * @description
 */
public abstract class BindingFragment<VB extends ViewBinding> extends BaseFragment {
    protected VB mBinding;

    @Override
    protected int getLayoutResId() {
        return View.NO_ID;
    }

    @Override
    protected void onRootViewCreate(FrameLayout rootView) {
        super.onRootViewCreate(rootView);
        mBinding = onCreateBinding(getLayoutInflater(), rootView);
        mRootView.addView(mBinding.getRoot());
    }

    protected abstract VB onCreateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    @Override
    protected abstract void initView();

    @Override
    protected abstract void initData();
}
