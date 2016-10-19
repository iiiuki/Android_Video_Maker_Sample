package com.hiep.video.maker.mirror;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

public class PromoMirrorMode {
    private static final String TAG = "MirrorMode";
    public static final int TOUCH_HORIZONTAL = 1;
    public static final int TOUCH_HORIZONTAL_DIRECT = 6;
    public static final int TOUCH_HORIZONTAL_REVERSE = 4;
    public static final int TOUCH_VERTICAL = 0;
    public static final int TOUCH_VERTICAL_DIRECT = 5;
    public static final int TOUCH_VERTICAL_REVERSE = 3;
    int count;
    private Rect drawBitmapSrc;
    Matrix matrix1;
    Matrix matrix2;
    Matrix matrix3;
    RectF rect1;
    RectF rect2;
    RectF rect3;
    RectF rect4;
    RectF rectTotalArea;
    private RectF srcRect;
    int touchMode;

    public PromoMirrorMode(int paramInt1, RectF paramRectF1, RectF paramRectF2, RectF paramRectF3, Matrix paramMatrix, int paramInt2, RectF paramRectF4) {
        this.count = paramInt1;
        this.srcRect = paramRectF1;
        this.drawBitmapSrc = new Rect();
        this.srcRect.round(this.drawBitmapSrc);
        this.rect1 = paramRectF2;
        this.rect2 = paramRectF3;
        this.matrix1 = paramMatrix;
        this.touchMode = paramInt2;
        this.rectTotalArea = paramRectF4;
    }

    public PromoMirrorMode(int paramInt1, RectF paramRectF1, RectF paramRectF2, RectF paramRectF3, RectF paramRectF4, RectF paramRectF5, Matrix paramMatrix1, Matrix paramMatrix2, Matrix paramMatrix3, int paramInt2, RectF paramRectF6) {
        this.count = paramInt1;
        this.srcRect = paramRectF1;
        this.drawBitmapSrc = new Rect();
        this.srcRect.round(this.drawBitmapSrc);
        this.rect1 = paramRectF2;
        this.rect2 = paramRectF3;
        this.rect3 = paramRectF4;
        this.rect4 = paramRectF5;
        this.matrix1 = paramMatrix1;
        this.matrix2 = paramMatrix2;
        this.matrix3 = paramMatrix3;
        this.touchMode = paramInt2;
        this.rectTotalArea = paramRectF6;
    }

    public Rect getDrawBitmapSrc() {
        return this.drawBitmapSrc;
    }

    public RectF getSrcRect() {
        return this.srcRect;
    }

    public void setSrcRect(RectF paramRectF) {
        this.srcRect.set(paramRectF);
        updateBitmapSrc();
    }

    public void updateBitmapSrc() {
        this.srcRect.round(this.drawBitmapSrc);
    }
}