package com.pic.testapp.base;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pic.testapp.R;

/**
 * @author Lu
 * @date 2024/10/26 13:21
 * @description
 */
public class BaseActivity extends AppCompatActivity {

    private BaseFragment mRootFragment;
    private ViewModelProvider mViewModelProvider;

    public <VM extends ViewModel> VM getViewModel(Class<VM> vmClass) {
        if (mViewModelProvider == null) {
            mViewModelProvider = new ViewModelProvider(this);
        }
        return mViewModelProvider.get(vmClass);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_frag_container_layout);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                BaseFragment fragment = getCurrentFragment();
                if (fragment != null && fragment.onBackPressed()) {
                    return;
                }
                if (fragment == null || isOnRootFragment()) {
                    finish();
                    return;
                }
                popCurrentFragment();
            }
        });

    }

    private void popCurrentFragment() {
        getSupportFragmentManager().popBackStack();
    }


    public void addFragment(BaseFragment fragment) {
        if (fragment == null) {
            return;
        }
        if (mRootFragment == null) {
            mRootFragment = fragment;
        }
        getSupportFragmentManager().beginTransaction().add(R.id.act_root_container, fragment).addToBackStack(fragment.getClass().getName()).commitAllowingStateLoss();
    }

    public boolean isOnRootFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.act_root_container) == mRootFragment;
    }

    public BaseFragment getCurrentFragment() {
        return (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.act_root_container);
    }


}
