//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hiep.video.maker.util;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.hiep.video.maker.system.VideoMaker;

public class ScreenUtil {
    private static final String TAG = ScreenUtil.class.getSimpleName();
    private static ScreenUtil mInstance = null;
    private DisplayMetrics mMetrics = null;
    private int mScreenHeight;
    private int mScreenWidth;
    private WakeLock mWakeLock = null;

    private ScreenUtil() {
        this.mMetrics = new DisplayMetrics();
        Display display = ((WindowManager) VideoMaker.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.mScreenWidth = display.getWidth();
        this.mScreenHeight = display.getHeight();
        display.getMetrics(this.mMetrics);
    }

    public static ScreenUtil getInstance() {
        if(mInstance == null) {
            mInstance = new ScreenUtil();
        }

        return mInstance;
    }

    public int getHeight() {
        return this.mScreenHeight;
    }

    public int getWidth() {
        return this.mScreenWidth;
    }

    public void screenOff() {
        try {
            if(this.mWakeLock != null) {
                this.mWakeLock.release();
                this.mWakeLock.setReferenceCounted(true);
            }
        } catch (RuntimeException var2) {
            Log.i("ScreenUtil", "wake lock count < 1");
        }

        this.mWakeLock = null;
    }

    public void screenOn() {
        this.mWakeLock = ((PowerManager)VideoMaker.context.getSystemService(Context.POWER_SERVICE)).newWakeLock(805306378, TAG);
        this.mWakeLock.setReferenceCounted(false);
        this.mWakeLock.acquire();
    }
}
