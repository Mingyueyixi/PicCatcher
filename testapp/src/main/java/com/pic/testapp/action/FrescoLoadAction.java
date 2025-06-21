package com.pic.testapp.action;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.pic.testapp.R;

/**
 * @author Lu
 * @date 2025/4/26 14:50
 * @description
 */
public class FrescoLoadAction implements Action {
    @Override
    public void doAction(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_fresco_load, null);
        new AlertDialog.Builder(context)
                .setView(dialogView)
                .show();
        SimpleDraweeView simpleDraweeView = dialogView.findViewById(R.id.sdv_fresco_load);
        simpleDraweeView.post(() -> simpleDraweeView.setImageURI("https://assets.msn.cn/staticsb/statics/latest/channel-store/coachmark-bg-2.png"));
    }
}
