package com.hiep.video.maker.mirror;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class PromoMirrorView extends View {
    private static final String TAG = "PromoMirror";
    final Matrix matrixI = new Matrix();
    float animDelta = 2.0F;
    int animDirection = 1;
    int animHalfTime = 1 + this.animationLimit / 2;
    boolean animate = false;
    int animationCount = 0;
    int animationLimit = 291;
    private Runnable animator = new Runnable() {
        public void run() {
            boolean flag = false;
            PromoMirrorView promoMirrorView = PromoMirrorView.this;
            promoMirrorView.animationCount = (1 + promoMirrorView.animationCount);

            float f = PromoMirrorView.this.animDelta * PromoMirrorView.this.animDirection;
            RectF localRectF = PromoMirrorView.this.mirrorModeList[PromoMirrorView.this.currentModeIndex].getSrcRect();
            if (localRectF.width() < PromoMirrorView.this.width) {
                if (f + localRectF.right >= PromoMirrorView.this.width)
                    PromoMirrorView.this.animDirection = -1;
                if (f + localRectF.left <= 0.0F)
                    PromoMirrorView.this.animDirection = 1;
            }else {
                if (f + localRectF.bottom > PromoMirrorView.this.height)
                    PromoMirrorView.this.animDirection = -1;;
                if (f + localRectF.top > 0.0F)
                   PromoMirrorView.this.animDirection = 1;
            }
            PromoMirrorView.this.moveGrid(localRectF, f, f);
            PromoMirrorView.this.mirrorModeList[PromoMirrorView.this.currentModeIndex].updateBitmapSrc();

            if (PromoMirrorView.this.animationCount >= PromoMirrorView.this.animationLimit)
                PromoMirrorView.this.animate = false;

            if (animationCount < animationLimit){
                flag = true;
            } else{
                animate = false;
            }
            if (flag)
            {
                postDelayed(this, frameDuration);
            }
            postInvalidate();
        }
    };
    int currentModeIndex = 0;

    Bitmap d3Bitmap;
    boolean d3Mode = false;
    int defaultColor = Color.WHITE;
    RectF destRect1;
    RectF destRect1X;
    RectF destRect1Y;
    RectF destRect2;
    RectF destRect2X;
    RectF destRect2Y;
    RectF destRect3;
    RectF destRect4;
    float distance;
    boolean drawSavedImage = false;
    RectF dstRectPaper1;
    RectF dstRectPaper2;
    RectF dstRectPaper3;
    RectF dstRectPaper4;
    int frameDuration = 15;
    Paint framePaint = new Paint();
    int height;
    float initialYPos = 0.0F;
    boolean isMarketCalled = false;
    boolean isTouchStartedLeft;
    boolean isTouchStartedTop;
    boolean isVerticle = false;

    Matrix m1 = new Matrix();
    Matrix m2 = new Matrix();
    Matrix m3 = new Matrix();

    String marketUrl = "market://details?id=com.ndh.square.frame";

    Matrix matrixMirror1 = new Matrix();
    Matrix matrixMirror2 = new Matrix();
    Matrix matrixMirror3 = new Matrix();
    Matrix matrixMirror4 = new Matrix();
    public PromoMirrorMode[] mirrorModeList = new PromoMirrorMode[20];

    PromoMirrorMode modeX5;
    PromoMirrorMode modeX6;
    PromoMirrorMode modeX7;
    PromoMirrorMode modeX8;
    PromoMirrorMode modeX1;
    PromoMirrorMode modeX2;
    PromoMirrorMode modeX3;
    PromoMirrorMode modeX4;

    float mulX = 1.0F;
    float mulY = 1.0F;
    float oldX;
    float oldY;

    int screenHeightPixels;
    int screenWidthPixels;

    Bitmap sourceBitmap;

    RectF srcRect1;
    RectF srcRect2;
    RectF srcRect3;
    RectF srcRectPaper;

    int tMode1;
    int tMode2;
    int tMode3;
    Matrix textMatrix = new Matrix();
    Paint textRectPaint = new Paint(1);
    RectF totalArea1;
    RectF totalArea2;
    RectF totalArea3;
    int touchCountForMarket = 0;
    int width;

    public PromoMirrorView(Context paramContext, int width, int height, Bitmap bitmap, String url) {
        super(paramContext);
        this.sourceBitmap = bitmap;
        this.screenHeightPixels = width;
        this.screenWidthPixels = width;
        this.width = this.sourceBitmap.getWidth();
        this.height = this.sourceBitmap.getHeight();

        createMatrix(width, width);
        createRectX(width, width);
        createRectY(width, width);
        createRectXY(width, width);
        createModes();

        this.framePaint.setAntiAlias(true);
        this.framePaint.setFilterBitmap(true);
        this.framePaint.setDither(true);
        this.textRectPaint.setColor(this.defaultColor);

        if (url != null)
            this.marketUrl = ("market://details?id=" + url);
    }

    public PromoMirrorView(Context paramContext, int width, int height, Bitmap bitmap, int  mode) {
        super(paramContext);

        if (mode!=-1 && mode <20){
            currentModeIndex=mode;
        }

        this.sourceBitmap = bitmap;
        this.screenHeightPixels = width;
        this.screenWidthPixels = width;
        this.width = this.sourceBitmap.getWidth();
        this.height = this.sourceBitmap.getHeight();

        createMatrix(width, width);
        createRectX(width, width);
        createRectY(width, width);
        createRectXY(width, width);
        createModes();

        this.framePaint.setAntiAlias(true);
        this.framePaint.setFilterBitmap(true);
        this.framePaint.setDither(true);
        this.textRectPaint.setColor(this.defaultColor);
    }

    private void createMatrix(int width, int height) {
        this.matrixI.reset();

        this.matrixMirror1.reset();
        this.matrixMirror1.postScale(-1.0F, 1.0F);
        this.matrixMirror1.postTranslate(width, 0.0F);

        this.matrixMirror2.reset();
        this.matrixMirror2.postScale(1.0F, -1.0F);
        this.matrixMirror2.postTranslate(0.0F, height);

        this.matrixMirror3.reset();
        this.matrixMirror3.postScale(-1.0F, -1.0F);
        this.matrixMirror3.postTranslate(width, height);
    }

    private void createModes() {
        int k = 4;
        this.modeX1 = new PromoMirrorMode(2, this.srcRect1, this.destRect1X, this.destRect1X, this.matrixMirror1, this.tMode1, this.totalArea1);
        new PromoMirrorMode(2, this.srcRect1, this.destRect1X, this.destRect2X, this.matrixI, this.tMode1, this.totalArea1);
        int m = 4;
        this.modeX2 = new PromoMirrorMode(2, this.srcRect1, this.destRect2X, this.destRect2X, this.matrixMirror1, m, this.totalArea1);
        this.modeX3 = new PromoMirrorMode(2, this.srcRect2, this.destRect1Y, this.destRect1Y, this.matrixMirror2, this.tMode2, this.totalArea2);
        int n = 3;
        this.modeX4 = new PromoMirrorMode(2, this.srcRect2, this.destRect2Y, this.destRect2Y, this.matrixMirror2, n, this.totalArea2);
        this.modeX5 = new PromoMirrorMode(2, this.srcRect1, this.destRect1X, this.destRect2X, this.matrixMirror4, this.tMode1, this.totalArea1);
        this.modeX6 = new PromoMirrorMode(2, this.srcRect2, this.destRect1Y, this.destRect2Y, this.matrixMirror4, this.tMode2, this.totalArea2);
        this.modeX7 = new PromoMirrorMode(2, this.srcRect1, this.destRect1X, this.destRect1X, this.matrixMirror3, this.tMode1, this.totalArea1);
        this.modeX8 = new PromoMirrorMode(2, this.srcRect2, this.destRect1Y, this.destRect1Y, this.matrixMirror3, this.tMode2, this.totalArea2);

        this.mirrorModeList[0] = this.modeX1;
        this.mirrorModeList[1] = this.modeX2;
        this.mirrorModeList[2] = this.modeX3;
        this.mirrorModeList[3] = this.modeX4;
        this.mirrorModeList[4] = this.modeX5;
        this.mirrorModeList[5] = this.modeX6;
        this.mirrorModeList[6] = this.modeX7;
        this.mirrorModeList[7] = this.modeX8;
        this.mirrorModeList[8] = this.modeX4;
    }

    private void createRectX(int paramInt1, int paramInt2) {
        float f1 = paramInt1 * (this.mulY / this.mulX);
        float f2 = paramInt1 / 2.0F;
        boolean bool = f1 < paramInt2;
        float f3 = 0.0F;
        if (bool) {
            f1 = paramInt2;
            f2 = f1 * (this.mulX / this.mulY) / 2.0F;
            f3 = paramInt1 / 2.0F - f2;
        }
        float f4 = this.initialYPos + (paramInt2 - f1) / 2.0F;
        float f5 = 0.0F;
        float f6 = this.width;
        float f7 = this.height;
        this.destRect1X = new RectF(f3, f4, f2 + f3, f1 + f4);
        float f8 = f3 + f2;
        this.destRect2X = new RectF(f8, f4, f2 + f8, f1 + f4);
        this.totalArea1 = new RectF(f3, f4, f2 + f8, f1 + f4);
        this.tMode1 = 1;
        float  f9 = 0.0F;
        if (this.mulX * this.height <= 2.0F * this.mulY * this.width) {
            f9 = (this.width - this.mulX / this.mulY * this.height / 2.0F) / 2.0F;
            f6 = f9 + this.mulX / this.mulY * this.height / 2.0F;
        }else{
            f5 = ((float)height - (float)(width * 2) * (mulY / mulX)) / 2.0F;
            f6 = f5 + (float)(width * 2) * (mulY / mulX);
            tMode1 = 5;
        }
        this.srcRect1 = new RectF(f9, f5, f6, f7);
        this.srcRectPaper = new RectF(f9, f5, f9 + (f6 - f9) / 2.0F, f7);
        float f10 = f2 / 2.0F;
        this.dstRectPaper1 = new RectF(f3, f4, f10 + f3, f1 + f4);
        float f11 = f3 + f10;
        this.dstRectPaper2 = new RectF(f11, f4, f10 + f11, f1 + f4);
        float f12 = f11 + f10;
        this.dstRectPaper3 = new RectF(f12, f4, f10 + f12, f1 + f4);
        float f13 = f12 + f10;
        this.dstRectPaper4 = new RectF(f13, f4, f10 + f13, f1 + f4);
    }

    private void createRectXY(int i, int j) {
        float f3 = ((float)i * (mulY / mulX)) / 2.0F;
        float f1 = (float)i / 2.0F;
        float f2 = 0.0F;
        float f = initialYPos;
        f = f3;
        if (f3 > (float)j)
        {
            f = j;
            f1 = ((mulX / mulY) * f) / 2.0F;
            f2 = (float)i / 2.0F - f1;
        }
        float f8 = initialYPos + ((float)j - 2.0F * f) / 2.0F;
        float f6 = 0.0F;
        float f4 = 0.0F;
        f3 = width;
        float f7 = height;
        destRect1 = new RectF(f2, f8, f1 + f2, f + f8);
        float f9 = f2 + f1;
        destRect2 = new RectF(f9, f8, f1 + f9, f + f8);
        float f10 = f8 + f;
        destRect3 = new RectF(f2, f10, f1 + f2, f + f10);
        destRect4 = new RectF(f9, f10, f1 + f9, f + f10);
        totalArea3 = new RectF(f2, f8, f1 + f9, f + f10);
        if (mulX * (float)height <= mulY * (float)width)
        {
            f = ((float)width - (mulX / mulY) * (float)height) / 2.0F;
            f1 = f + (mulX / mulY) * (float)height;
            tMode3 = 1;
            f3 = f7;
            f2 = f4;
        } else
        {
            f2 = ((float)height - (float)width * (mulY / mulX)) / 2.0F;
            float f5 = f2 + (float)width * (mulY / mulX);
            tMode3 = 0;
            f = f6;
            f1 = f3;
            f3 = f5;
        }
        srcRect3 = new RectF(f, f2, f1, f3);
    }

    private void createRectY(int i, int j) {
        float f3 = ((float)i * (mulY / mulX)) / 2.0F;
        float f1 = i;
        float f2 = 0.0F;
        float f = initialYPos;
        f = f3;
        if (f3 > (float)j)
        {
            f = j;
            f1 = ((mulX / mulY) * f) / 2.0F;
            f2 = (float)i / 2.0F - f1;
        }
        f3 = initialYPos + ((float)j - 2.0F * f) / 2.0F;
        destRect1Y = new RectF(f2, f3, f1 + f2, f + f3);
        float f4 = f3 + f;
        destRect2Y = new RectF(f2, f4, f1 + f2, f + f4);
        totalArea2 = new RectF(f2, f3, f1 + f2, f + f4);
        f = 0.0F;
        f2 = 0.0F;
        f1 = width;
        f3 = height;
        tMode2 = 0;
        if (mulX * 2.0F * (float)height > mulY * (float)width)
        {
            f2 = ((float)height - ((mulY / mulX) * (float)width) / 2.0F) / 2.0F;
            f3 = f2 + ((mulY / mulX) * (float)width) / 2.0F;
        } else
        {
            f = ((float)width - (float)(height * 2) * (mulX / mulY)) / 2.0F;
            f1 = f + (float)(height * 2) * (mulX / mulY);
            tMode2 = 6;
        }
        srcRect2 = new RectF(f, f2, f1, f3);
    }

    private void drawMode(Canvas canvas, Bitmap paramBitmap, PromoMirrorMode paramPromoMirrorMode, Matrix paramMatrix) {
        canvas.drawBitmap(paramBitmap, paramPromoMirrorMode.getDrawBitmapSrc(), paramPromoMirrorMode.rect1, this.framePaint);
        this.m1.set(paramPromoMirrorMode.matrix1);
        this.m1.postConcat(paramMatrix);
        canvas.concat(this.m1);
        canvas.drawBitmap(paramBitmap, paramPromoMirrorMode.getDrawBitmapSrc(), paramPromoMirrorMode.rect2, this.framePaint);
        if (paramPromoMirrorMode.count == 4) {
            this.m2.set(paramPromoMirrorMode.matrix2);
            this.m2.postConcat(paramMatrix);
            canvas.setMatrix(this.m2);
            canvas.drawBitmap(paramBitmap, paramPromoMirrorMode.getDrawBitmapSrc(), paramPromoMirrorMode.rect3, this.framePaint);
            this.m3.set(paramPromoMirrorMode.matrix3);
            this.m3.postConcat(paramMatrix);
            canvas.setMatrix(this.m3);
            canvas.drawBitmap(paramBitmap, paramPromoMirrorMode.getDrawBitmapSrc(), paramPromoMirrorMode.rect4, this.framePaint);
        }
    }

    private void reset(int paramInt1, int paramInt2, boolean paramBoolean) {
        createMatrix(paramInt1, paramInt2);
        createRectX(paramInt1, paramInt2);
        createRectY(paramInt1, paramInt2);
        createRectXY(paramInt1, paramInt2);
        createModes();
        if (paramBoolean)
            postInvalidate();
    }

    public int getCurrentModeIndex() {
        return currentModeIndex;
    }

    public void setCurrentModeIndex(int currentModeIndex) {
        this.currentModeIndex = currentModeIndex;
    }

    public Bitmap getBitmap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap localBitmap = Bitmap.createBitmap(getDrawingCache());
        setDrawingCacheEnabled(false);
        return localBitmap;
    }

    public PromoMirrorMode getCurrentMirrorMode() {
        return this.mirrorModeList[this.currentModeIndex];
    }

    void moveGrid(RectF rectf, float f, float f1) {
        if (mirrorModeList[currentModeIndex].touchMode == 1 || mirrorModeList[currentModeIndex].touchMode == 4 || mirrorModeList[currentModeIndex].touchMode == 6)
        {
            f1 = f;
            if (mirrorModeList[currentModeIndex].touchMode == 4)
            {
                f1 = f * -1F;
            }
            f = f1;
            if (isTouchStartedLeft)
            {
                f = f1;
                if (mirrorModeList[currentModeIndex].touchMode != 6)
                {
                    f = f1 * -1F;
                }
            }
            f1 = f;
            if (rectf.left + f < 0.0F)
            {
                f1 = -rectf.left;
            }
            f = f1;
            if (rectf.right + f1 >= (float)width)
            {
                f = (float)width - rectf.right;
            }
            rectf.left = rectf.left + f;
            rectf.right = rectf.right + f;
        } else
        if (mirrorModeList[currentModeIndex].touchMode == 0 || mirrorModeList[currentModeIndex].touchMode == 3 || mirrorModeList[currentModeIndex].touchMode == 5)
        {
            f = f1;
            if (mirrorModeList[currentModeIndex].touchMode == 3)
            {
                f = f1 * -1F;
            }
            f1 = f;
            if (isTouchStartedTop)
            {
                f1 = f;
                if (mirrorModeList[currentModeIndex].touchMode != 5)
                {
                    f1 = f * -1F;
                }
            }
            f = f1;
            if (rectf.top + f1 < 0.0F)
            {
                f = -rectf.top;
            }
            f1 = f;
            if (rectf.bottom + f >= (float)height)
            {
                f1 = (float)height - rectf.bottom;
            }
            rectf.top = rectf.top + f1;
            rectf.bottom = rectf.bottom + f1;
            return;
        }
    }
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(this.defaultColor);
        drawMode(canvas, this.sourceBitmap, this.mirrorModeList[this.currentModeIndex], this.matrixI);
        if ((this.d3Mode) && (this.d3Bitmap != null) && (!this.d3Bitmap.isRecycled())) {
            canvas.setMatrix(this.matrixI);
            canvas.drawBitmap(this.d3Bitmap, null, this.mirrorModeList[this.currentModeIndex].rectTotalArea, this.framePaint);
        }
        super.onDraw(canvas);
    }
    @Override
    public void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        Log.e("PromoMirror", "initialYPos " + this.initialYPos);
        int i = paramInt3 - paramInt1;
        int j = paramInt4 - paramInt2;
        this.mulX = 1.0F;
        this.mulY = (j / i);
        reset(i, j, true);
        float f1 = this.width / this.height;
        float f2 = i / (1.33F * j);
        if (f1 < f2){
            //setCurrentModeIndex(2);
        }

        Log.e("PromoMirror", "scaleView " + f2);
        Log.e("PromoMirror", "scaleBtm " + f1);
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    @Override
    public void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(paramInt1, paramInt2);
        int i = MeasureSpec.getSize(paramInt1);
        int j = MeasureSpec.getSize(paramInt2);
        if (i > j) ;
        for (int k = j; ; k = i) {
            if (k <= 0)
                k = Math.min(this.screenWidthPixels, this.screenHeightPixels);
            if (i <= 0)
                i = this.screenWidthPixels;
            if (j <= 0)
                j = this.screenHeightPixels;
            if (j > i)
                j = i;
            setMeasuredDimension(i, j);
            Log.e("PromoMirror", "onMeasure " + k);
            return;
        }
    }

    private boolean isCliCk=false;
    public void setIsClick(boolean isCliCk){
        this.isCliCk=isCliCk;
    }
    @Override
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {

        float f1 = paramMotionEvent.getX();
        float f2 = paramMotionEvent.getY();
        switch (paramMotionEvent.getAction()) {
            case MotionEvent.ACTION_UP:
                postInvalidate();
                return true;
            case MotionEvent.ACTION_DOWN:
                this.touchCountForMarket = (1 + this.touchCountForMarket);
                if (((f2 > getHeight() / 2) || (f1 > getWidth() / 2)) && (!this.isMarketCalled) && (this.touchCountForMarket > 1)) {
                   // Intent localIntent = new Intent("android.intent.action.VIEW");
                   // localIntent.setData(Uri.parse(this.marketUrl));
                   // //getContext().startActivity(localIntent);

                    if (isCliCk) {
                        clickImageMirror.onClick();
                        this.isMarketCalled = true;
                        return true;
                    }
                }
                if (f1 < (float)(getWidth() / 2))
                {
                    isTouchStartedLeft = true;
                } else
                {
                    isTouchStartedLeft = false;
                }
                if (f2 < (float)(getHeight() / 2))
                {
                    isTouchStartedTop = true;
                } else
                {
                    isTouchStartedTop = false;
                }
                oldX = f1;
                oldY = f2;
                return true;
            case MotionEvent.ACTION_MOVE:
                moveGrid(this.mirrorModeList[this.currentModeIndex].getSrcRect(), f1 - this.oldX, f2 - this.oldY);
                this.mirrorModeList[this.currentModeIndex].updateBitmapSrc();
                this.oldX = f1;
                this.oldY = f2;
                postInvalidate();
                return true;
            default:
                return true;
        }
    }

    public void startAnimator() {
        int i = (int)mirrorModeList[currentModeIndex].getSrcRect().width();
        int j = (int)mirrorModeList[currentModeIndex].getSrcRect().height();
        Log.e("PromoMirror", (new StringBuilder("anim width ")).append(mirrorModeList[currentModeIndex].getSrcRect().width()).toString());
        if (i < width)
        {
            animDelta = (float)(width - i) / 135F;
        } else
        {
            animDelta = (float)(height - j) / 135F;
        }
        if (animDelta <= 0.0F)
        {
            animDelta = 0.5F;
        }
        animationCount = 0;
        animate = true;
        removeCallbacks(animator);
        post(animator);
    }

    private ClickImageMirror clickImageMirror;
    public interface ClickImageMirror{
        void onClick();
    }

    public void setClickImageMirror(ClickImageMirror clickImageMirror ){
        this.clickImageMirror=clickImageMirror;
    }

    public PromoMirrorView setSourceBitmap(Bitmap sourceBitmap) {
        this.sourceBitmap = sourceBitmap;
        return this;
    }
}