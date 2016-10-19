package com.hiep.video.maker.widget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hiep.video.maker.util.Logger;

public class GifImageView extends ImageView {
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private boolean isMoveAction;
    private RelativeLayout.LayoutParams layoutParams;
    private boolean mActionDown;
    private long mActionDownTime;
    private OnClickListener mClickListener;
    private float mLastX;
    private float mLastY;
    private int mMaxLeft;
    private int mMaxTop;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mTouchSlopSquare;

    public GifImageView(Context context) {
        super(context);
        this.mTouchSlopSquare = ViewConfiguration.get(context).getScaledTouchSlop() * ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public GifImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTouchSlopSquare = ViewConfiguration.get(context).getScaledTouchSlop() * ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private int getProperty(int left, int top) {
        return left > 0?(left > top?top:left):0;
    }

    private void init() {
        if(this.mScreenHeight == 0) {
            RelativeLayout relativeLayout= (RelativeLayout)this.getParent();
            this.mScreenWidth = relativeLayout.getWidth();
            this.mScreenHeight = relativeLayout.getHeight();
        }

        if(this.layoutParams == null) {
            int width = this.getWidth();
            int height = this.getHeight();
            this.layoutParams = new RelativeLayout.LayoutParams(width, height);
            this.mMaxLeft = this.mScreenWidth - width;
            this.mMaxTop = this.mScreenHeight - height;
        }
    }

    private int maxDistance(float value) {
        if(value < 0.0F) {
            int var3 = (int)value;
            return (double)(value - (float)var3) <= -0.5D?var3 - 1:var3 + 1;
        } else if(value > 0.0F) {
            int var2 = (int)value;
            return (double)(value - (float)var2) >= 0.5D?var2 + 1:var2 - 1;
        } else {
            return (int)value;
        }
    }

    private void processActionMove(MotionEvent motionEvent) {
        float lastX = this.mLastX;
        float lastY = this.mLastY;
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        int distanceX = this.maxDistance(rawX - lastX);
        int distanceY = this.maxDistance(rawY - lastY);
        if(this.mActionDown) {
            if(distanceX * distanceX + distanceY * distanceY > this.mTouchSlopSquare) {
                this.mActionDown = false;
                this.isMoveAction = true;
                int var12 = distanceX + this.getLeft();
                int var13 = distanceY + this.getTop();
                this.init();
                int var14 = this.getProperty(var12, this.mMaxLeft);
                int var15 = this.getProperty(var13, this.mMaxTop);
                this.layoutParams.setMargins(var14, var15, 0, 0);
                this.setLayoutParams(this.layoutParams);
                this.mLastX = motionEvent.getRawX();
                this.mLastY = motionEvent.getRawY();
                this.invalidate();
            }
        } else if(Math.abs(distanceX) >= 1 || Math.abs(distanceY) >= 1) {
            int left = distanceX + this.getLeft();
            int top = distanceY + this.getTop();
            int var10 = this.getProperty(left, this.mMaxLeft);
            int var11 = this.getProperty(top, this.mMaxTop);
            this.layoutParams.setMargins(var10, var11, 0, 0);
            this.setLayoutParams(this.layoutParams);
            this.mLastX = motionEvent.getRawX();
            this.mLastY = motionEvent.getRawY();
            this.invalidate();
            return;
        }

    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean var2;
        if(motionEvent.getPointerCount() > 1) {
            var2 = super.onTouchEvent(motionEvent);
        } else {
            var2 = true;
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.mActionDownTime = SystemClock.elapsedRealtime();
                    this.mLastX = motionEvent.getRawX();
                    this.mLastY = motionEvent.getRawY();
                    this.mActionDown = true;
                    this.isMoveAction = false;
                    return var2;
                case MotionEvent.ACTION_UP:
                    this.onMoveDoneListener.onDone(getLeft(),getTop());
                    if(!this.isMoveAction && SystemClock.elapsedRealtime() - this.mActionDownTime < (long)TAP_TIMEOUT && this.mClickListener != null) {
                        this.mClickListener.onClick(this);
                        return var2;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.processActionMove(motionEvent);
                    return var2;
                default:
                    return super.onTouchEvent(motionEvent);
            }
        }

        return var2;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mClickListener = onClickListener;
    }


    private OnMoveDoneListener onMoveDoneListener;
    public interface OnMoveDoneListener{
        void onDone(int left,int top);
    }
    public void setOnMoveDoneListener(OnMoveDoneListener onMoveDoneListener){
        this.onMoveDoneListener=onMoveDoneListener;
    }

}