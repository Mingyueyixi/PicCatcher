package com.pic.testapp.action;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.lu.magic.util.IOUtil;
import com.pic.testapp.util.AppExecutor;
import com.pic.testapp.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @author Lu
 * @date 2024/10/26 23:56
 * @description
 */
public class LoadHttpPicAction implements Action {
    WeakReference<Context> mContextRef;

    @Override
    public void doAction(Context context) {
        mContextRef = new WeakReference<>(context);
        LogUtil.d("doAction");
        AppExecutor.INSTANCE.runOnNetworkIo(() -> {
            LogUtil.d("runOnNetworkIo");
            try {
                doNetworkTask();
            } catch (Throwable e) {
                LogUtil.d(e);
            }
        });
    }

    private InputStream getUrlStream(String picUrl) throws IOException {
        InputStream iStream = null;
        try {
            URL url = new URL(picUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            iStream = conn.getInputStream();
        } catch (Exception e) {
            LogUtil.d(e);
        }
        return iStream;
    }

    private InputStream getOkHttpStream(String picUrl) throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .callTimeout(5000L, TimeUnit.MILLISECONDS)
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                .readTimeout(5000L, TimeUnit.MILLISECONDS)
                .writeTimeout(5000L, TimeUnit.MILLISECONDS);
        LogUtil.d("builder");
        OkHttpClient client = builder.build();
        LogUtil.d("client");
        Response response = client.newCall(new Request.Builder().url(picUrl).build()).execute();
        LogUtil.d("response");
        ResponseBody body = response.body();
        LogUtil.d("body");
        ResponseBody bodyNew = response.peekBody(Long.MAX_VALUE);
//        byte[] bytes = IOUtil.readToBytes(bodyNew.byteStream());

        //返回一个新的
//        BufferedSource peeked = bufferedSource.peek();
//        Buffer buffer = new Buffer();
//        peeked.request(Long.MAX_VALUE);
//        buffer.write(peeked, Math.min(Long.MAX_VALUE, peeked.getBuffer().size()));
//        byte[] bytes = buffer.readByteArray();
//        LogUtil.d("LoadHttpPicAction", "doNetworkTask", "bytes length", bytes.length);
//        InputStream iStream = body.byteStream();
//        bytes = IOUtil.readToBytes(iStream);
//        IOUtil.closeQuietly(peeked);
        return body.byteStream();
    }

    public void doNetworkTask() throws IOException {
//        String picUrl = "https://csdnimg.cn/release/blogv2/dist/pc/img/btnGuideSide1.gif";
        String picUrl = "https://ts2.cn.mm.bing.net/th?id=ORMS.96254be2fd646fe1f697a79490ffc4e9&pid=Wdp&w=612&h=328&qlt=90&c=1&rs=1&dpr=1.25&p=0";
//        String picUrl = "https://192.168.33.66/th?id=ORMS.96254be2fd646fe1f697a79490ffc4e9&pid=Wdp&w=612&h=328&qlt=90&c=1&rs=1&dpr=1.25&p=0";
        LogUtil.d(picUrl);
        InputStream iStream = getUrlStream(picUrl);
//        InputStream iStream = getOkHttpStream(picUrl);
        LogUtil.d(iStream);
        Bitmap bitmap = BitmapFactory.decodeStream(iStream);
        LogUtil.d(bitmap);
//        iStream.close();
        LogUtil.i("LoadHttpPicAction", "doNetworkTask");
        AppExecutor.INSTANCE.runOnUiThread(() -> {
            doUiTask(bitmap);
        });
    }

    private void doUiTask(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(mContextRef.get(), "bitmap is null", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.i("LoadHttpPicAction", "doUiTask");
        Context context = mContextRef.get();
        if (context == null) {
            return;
        }
        ImageView iv = new ImageView(context);
        iv.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT));
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setImageBitmap(bitmap);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(iv)
                .show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = bitmap.getWidth();
        lp.height = bitmap.getHeight();
        window.getDecorView().setPadding(0, 0, 0, 0);
    }
}
