package com.hiep.video.maker.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by hiep on 6/11/2016.
 */
public class LayoutUtil {
    public static ImageView createImageView(Context context, int paramInt1, int paramInt2, int paramInt3, int paramInt4){
        ImageView imageView = new ImageView(context);
        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(paramInt3, paramInt4);
        localLayoutParams.leftMargin = paramInt1;
        localLayoutParams.topMargin = paramInt2;
        localLayoutParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(localLayoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }
    public static FrameLayout createLayer(Context context, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        FrameLayout localFrameLayout = new FrameLayout(context);
        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(paramInt3, paramInt4);
        localLayoutParams.leftMargin = paramInt1;
        localLayoutParams.topMargin = paramInt2;
        localLayoutParams.gravity = Gravity.CENTER;
        localFrameLayout.setLayoutParams(localLayoutParams);
        return localFrameLayout;
    }
}
