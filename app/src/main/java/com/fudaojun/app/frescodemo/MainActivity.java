package com.fudaojun.app.frescodemo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SimpleDraweeView mImageView;
    private int mScreenWidth;
    private int mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels; //768
        mScreenHeight = displayMetrics.heightPixels; //1184

        mImageView = (SimpleDraweeView) findViewById(R.id.sdv);
        //测试图片 1200*800

//        rotate(mImageView, RotationOptions.ROTATE_90); //顺时针旋转90
//        rotate(mImageView, RotationOptions.ROTATE_270); //顺时针旋转270 = 逆时针旋转90


        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, com.facebook.imagepipeline.image.ImageInfo imageInfo, Animatable animatable) {
                int viewWidth = imageInfo.getWidth();
                int viewHeight = imageInfo.getHeight();
                Toast.makeText(MainActivity.this, viewWidth + "--" + viewHeight, Toast.LENGTH_SHORT).show();

                mImageView.setLayoutParams(new RelativeLayout.LayoutParams(viewWidth, viewHeight));
            }
        };

//        getImageInfo(controllerListener);

        mImageView.setLayoutParams(new RelativeLayout.LayoutParams(600, 400));
//        cutPic(cutProcess);
//        CutProcess cutProcess = new CutProcess(0, 0.5f, 0.5f, 0.5f);
//        rotateAndcutPic(cutProcess, RotationOptions.ROTATE_270);

        //Builder模式
        new FrescoBuilder(mImageView, getUriForFresco(this, R.mipmap.test_img))
                .cutPic(0f, 0f, 0.5f, 0.5f)
                .setControllerListener(controllerListener)
                .setRotate(RotationOptions.NO_ROTATION)
                .build();
    }

    /**
     * 旋转图片
     *
     * @param rotate
     */
    private void rotate(SimpleDraweeView img, int rotate) {
        RotationOptions rotationOptions = RotationOptions.forceRotation(rotate);
        ImageRequest build = ImageRequestBuilder.newBuilderWithSource(getUriForFresco(this, R.mipmap.test_img))
                .setRotationOptions(rotationOptions)
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(build)
                .build();
        mImageView.setController(controller);
    }

    /**
     * 获得图片宽高
     *
     * @param controllerListener
     */
    private void getImageInfo(ControllerListener<? super ImageInfo> controllerListener) {
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(getUriForFresco(this, R.mipmap.test_img))
                .build();
        mImageView.setController(controller);
    }

    /**
     * 裁剪图片
     * @param processor
     */
    private void cutPic(BasePostprocessor processor) {
        ImageRequest build = ImageRequestBuilder.newBuilderWithSource(getUriForFresco(this, R.mipmap.test_img))
                .setPostprocessor(processor)
                .build();

        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(build)
                .build();

        mImageView.setController(controller);
    }

    /**
     * 旋转+裁剪图片
     * @param processor
     */
    private void rotateAndcutPic(BasePostprocessor processor, int rotate) {
        RotationOptions rotationOptions = RotationOptions.forceRotation(rotate);

        ImageRequest build = ImageRequestBuilder.newBuilderWithSource(getUriForFresco(this, R.mipmap.test_img))
                .setPostprocessor(processor)
                .setRotationOptions(rotationOptions)
                .build();

        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(build)
                .build();

        mImageView.setController(controller);
    }

    /**
     * 获得freso使用的本地图片的uri
     *
     * @param context
     * @param resourseId
     * @return
     */
    public static Uri getUriForFresco(Context context, int resourseId) {
        String packageName = getPackageName(context);
        if (packageName != null) {
            Uri parse = Uri.parse("res://" + packageName + "/" + resourseId);
            return parse;
        } else {
            return null;
        }
    }

    /**
     * 获得包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
