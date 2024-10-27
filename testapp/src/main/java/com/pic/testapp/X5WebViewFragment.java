package com.pic.testapp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pic.testapp.base.BindingFragment;
import com.pic.testapp.databinding.FragWebContainerBinding;
import com.pic.testapp.vm.X5ViewModel;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.URLUtil;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * @author Lu
 * @date 2024/10/26 16:06
 * @description
 */
public class X5WebViewFragment extends BindingFragment<FragWebContainerBinding> {
    private X5ViewModel mViewModel;
    private ProgressDialog progressDialog;
    private WebView mWebView;

    @Override
    protected FragWebContainerBinding onCreateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragWebContainerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        mViewModel = getViewModel(X5ViewModel.class);
        mViewModel.initX5().observe(this, aBoolean -> {
            if (!aBoolean) {
                Toast.makeText(getContext(), "x5内核未启用", Toast.LENGTH_SHORT).show();
            }
            onViewReady();
        });
    }

    private void onViewReady() {
        progressDialog.dismiss();
        mWebView = new WebView(getContext());
        initWebView();
        mBinding.etWebUrl.setText(LocalStorage.getDefault().getString(LocalStorage.Key.X5_WEB_VIEW_LAST_URL, ""));
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
            public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
                super.onPageStarted(webView, url, bitmap);
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
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                mBinding.lpbProgress.setProgress(i);
            }
        });
    }

    private void openUrl(String url) {
        recordLoadUrl(url);
        mWebView.loadUrl(url);
        mBinding.etWebUrl.clearFocus();
    }

    private void recordLoadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url) || URLUtil.isFileUrl(url)) {
            LocalStorage.getDefault().edit().putString(LocalStorage.Key.X5_WEB_VIEW_LAST_URL, url).commit();
        }
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
        progressDialog.dismiss();
        mWebView.destroy();
    }
}
