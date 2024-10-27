package com.pic.testapp;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.pic.testapp.base.BaseActivity;

import okhttp3.Response;
import okio.Buffer;

/**
 * @author Lu
 * @date 2024/10/26 12:57
 * @description
 */
public class LauncherUiActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment(new MainFragment());
    }
}
