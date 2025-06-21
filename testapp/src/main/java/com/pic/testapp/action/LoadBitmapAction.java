package com.pic.testapp.action;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * @author Lu
 * @date 2024/10/26 19:43
 * @description
 */
public class LoadBitmapAction implements Action {

    @Override
    public void doAction(Context context) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), android.R.mipmap.sym_def_app_icon, opt);
        ImageView iv = new ImageView(context);
        iv.setLayoutParams(new android.view.ViewGroup.LayoutParams(100, 100));
        iv.setImageBitmap(bitmap);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        new AlertDialog.Builder(context)
                .setView(iv)
                .show();
    }
}
