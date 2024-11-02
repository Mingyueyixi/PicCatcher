package com.pic.testapp.vm;

import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pic.testapp.App;
import com.pic.testapp.util.LogUtil;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.utils.LogFileUtils;

import java.util.HashMap;
import java.util.logging.LogManager;

/**
 * @author Lu
 * @date 2024/10/26 20:08
 * @description
 */
public class X5ViewModel extends ViewModel {
    private MutableLiveData<Boolean> mX5InitLiveData;

    public MutableLiveData<Boolean> initX5() {
        if (mX5InitLiveData == null) {
            mX5InitLiveData = new MutableLiveData<>();
        }
        if (QbSdk.isTbsCoreInited()) {
            mX5InitLiveData.postValue(QbSdk.isX5Core());
            LogUtil.d("isTbsCoreInited true");
            return mX5InitLiveData;
        }

        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap<String, Object> map = new HashMap<>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.initX5Environment(App.getInstance(), new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                // 内核初始化完成，可能为系统内核，也可能为系统内核
                LogUtil.d("onCoreInitFinished");
            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖网络动态下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             * @param isX5 是否使用X5内核
             */
            @Override
            public void onViewInitFinished(boolean isX5) {
                LogUtil.d("onViewInitFinished isX5:" + isX5);
                mX5InitLiveData.postValue(QbSdk.isX5Core());

            }
        });
        return mX5InitLiveData;
    }
}
