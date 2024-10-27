package com.pic.testapp.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * @author Lu
 * @date 2024/10/26 13:23
 * @description
 */
public abstract class BaseFragment extends Fragment {
    private ViewModelProvider mViewModelProvider;
    protected FrameLayout mRootView;
    /**
     * 处理返回事件
     *
     * @return
     */
    public boolean onBackPressed() {
        return false;
    }

    public <VM extends ViewModel> VM getViewModel(Class<VM> vmClass) {
        if (mViewModelProvider == null) {
            mViewModelProvider = new ViewModelProvider(this);
        }
        return mViewModelProvider.get(vmClass);
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public void addActivityFragment(BaseFragment fragment) {
        getBaseActivity().addFragment(fragment);
    }


    public boolean isOnRootFragment() {
        return getBaseActivity().isOnRootFragment();
    }

    public BaseFragment getCurrentFragment() {
        return getBaseActivity().getCurrentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int resId = getLayoutResId();
        mRootView = new FrameLayout(getBaseActivity());
        mRootView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mRootView.setClickable(true);

        //resId != Resources.ID_NULL
        if (resId != 0 && resId != View.NO_ID) {
            View.inflate(getContext(), resId, mRootView);
        }
        onRootViewCreate(mRootView);
        return mRootView;
    }

    protected void onRootViewCreate(FrameLayout rootView) {
        mRootView.setBackgroundColor(getResources().getColor(android.R.color.background_light, getContext().getTheme()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    public <T extends View> T findViewById(int id) {
        View view = getView();
        if (view == null) {
            return null;
        }
        return view.<T>findViewById(id);
    }

    protected abstract @LayoutRes int getLayoutResId();

    protected abstract void initView();

    protected abstract void initData();

}
