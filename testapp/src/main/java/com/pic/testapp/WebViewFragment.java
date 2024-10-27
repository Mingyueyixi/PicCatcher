package com.pic.testapp;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pic.testapp.base.BindingFragment;
import com.pic.testapp.databinding.FragWebContainerBinding;
import com.tencent.smtt.sdk.URLUtil;


/**
 * @author Lu
 * @date 2024/10/26 19:31
 * @description
 */
public class WebViewFragment extends BindingFragment<FragWebContainerBinding> {
    private WebView mWebView;

    @Override
    protected FragWebContainerBinding onCreateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragWebContainerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        mWebView = new WebView(getContext());
        initWebView();
        mBinding.etWebUrl.setText(LocalStorage.getDefault().getString(LocalStorage.Key.WEB_VIEW_LAST_URL, ""));
        mBinding.flWebContainer.addView(mWebView);
        mBinding.tvGo.setOnClickListener(v -> openUrl(mBinding.etWebUrl.getText().toString()));
    }

    private void initWebView() {
        WebSettings setting = mWebView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);
        setting.setAllowUniversalAccessFromFileURLs(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mBinding.etWebUrl.setText(url);
                mBinding.lpbProgress.setVisibility(View.VISIBLE);
                recordLoadUrl(url);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                mBinding.lpbProgress.setVisibility(View.INVISIBLE);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mBinding.lpbProgress.setProgress(newProgress);
            }
        });
    }

    private void recordLoadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url) || URLUtil.isFileUrl(url)) {
            LocalStorage.getDefault().edit().putString(LocalStorage.Key.WEB_VIEW_LAST_URL, url).commit();
        }
    }

    private void openUrl(String url) {
        recordLoadUrl(url);
        mWebView.loadUrl(url);
        mBinding.etWebUrl.clearFocus();
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }
}
