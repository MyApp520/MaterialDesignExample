package org.smile.mde.ui.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.smile.commonlib.base.BaseActivity;
import com.smile.commonlib.util.MyLog;

import org.smile.mde.R;

import butterknife.BindView;
import butterknife.OnClick;

public class FaceRectActivity extends BaseActivity {

    private final String TAG = "FaceRectActivity";
    @BindView(R.id.iv_photo_left)
    com.bm.library.PhotoView ivPhotoLeft;
    @BindView(R.id.iv_photo_right)
    com.bm.library.PhotoView ivPhotoRight;

    private Activity mActivity;
    private String imageBaseUrl = "http://172.28.0.39:8060/img/face?zpdtUrl=";
    private String zpPrefix = "&zpPrefix=3";
    private Paint mFaceRectPaint;

    @Override
    protected int bindLayout() {
        return R.layout.activity_face_rect;
    }

    @Override
    protected void initView() {
        mActivity = FaceRectActivity.this;
        ivPhotoLeft.enable();
        ivPhotoRight.enable();
    }

    @Override
    protected void initData() {
        String firstBitmap = imageBaseUrl + "/yuntianAlarm/35/2597626/dt-5e597a0d0e254df2beaaa4c00764842d.jpg" + zpPrefix;
        String secondBitmap = imageBaseUrl + "/libFace/4/36078/xt-6719D421BE5B4FC49F59E9FA7F12D4E3.png" + zpPrefix;

        if (!TextUtils.isEmpty(firstBitmap)) {
            Log.e(TAG, "initView: firstBitmap = " + firstBitmap);
            glideLoadPhoto2(ivPhotoLeft, firstBitmap);
        }

        if (!TextUtils.isEmpty(secondBitmap)) {
            glideLoadPhoto(ivPhotoRight, secondBitmap);
        }
    }

    @Override
    protected boolean isUseDagger() {
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            // 沉浸式 全屏模式
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void glideLoadPhoto2(final ImageView targetImageView, String targetUrl) {
        //测试方法
        Glide.with(mActivity)
                .load(targetUrl)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resourceBitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.e(TAG, " resourceBitmap = " + resourceBitmap);
                        Log.e(TAG, "resourceBitmap  width = " + resourceBitmap.getWidth() + ", height = " + resourceBitmap.getHeight());

                        Bitmap faceRectBitmap = drawFaceRect(resourceBitmap);
                        Log.e(TAG, " faceRectBitmap = " + faceRectBitmap);
                        if (faceRectBitmap != null) {
                            Log.e(TAG, "faceRectBitmap  width = " + resourceBitmap.getWidth() + ", height = " + resourceBitmap.getHeight());
                            resourceBitmap.recycle();
                            resourceBitmap = null;
                            targetImageView.setImageBitmap(faceRectBitmap);
                        }
                    }
                });
    }

    /**
     * 绘制人脸框
     *
     * @param bitmap
     */
    public Bitmap drawFaceRect(Bitmap bitmap) {
        //绘制bitmap
        bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        Canvas canvas = new Canvas(bitmap);
        if (mFaceRectPaint == null) {
            mFaceRectPaint = new Paint();
            mFaceRectPaint.setAntiAlias(true);
            mFaceRectPaint.setStrokeWidth(10);
            mFaceRectPaint.setColor(Color.RED);
            mFaceRectPaint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawRect(201,136,449,384, mFaceRectPaint);
        return bitmap;
    }

    private void glideLoadPhoto(final ImageView targetImageView, String targetUrl) {
        // 此方法正常可用的
        Glide.with(mActivity)
                .load(targetUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .crossFade()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        MyLog.e(TAG, TAG + "---图片加载出错onException = " + e);
                        Request glideRequest = null;
                        if (target != null) {
                            glideRequest = target.getRequest();
                        }
                        if (glideRequest != null) {
                            glideRequest.begin();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        MyLog.e(TAG, TAG + "---图片加载成功 isFromMemoryCache = " + isFromMemoryCache);
                        return false;
                    }
                })
                .into(targetImageView);
    }

    @OnClick({R.id.iv_photo_left, R.id.iv_photo_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_photo_left:
                break;
            case R.id.iv_photo_right:
                break;
        }
    }
}
