//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hiep.video.maker.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.os.Debug;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

import java.text.DecimalFormat;

public class GalleryUtility {
    public GalleryUtility() {
    }

    public static Bitmap getThumbnailBitmap(Context var0, long var1, int var3) {
        Bitmap var4 = Thumbnails.getThumbnail(var0.getContentResolver(), var1, 1, (Options) null);
        Bitmap var5 = null;
        if (var4 != null) {
            var5 = rotateImage(var4, var3);
            if (var5 == null) {
                return var4;
            }

            if (var5 != var4) {
                var4.recycle();
                return var5;
            }
        }

        return var5;
    }

    public static void logHeap() {
        Double var0 = Double.valueOf(Double.valueOf((double) Debug.getNativeHeapAllocatedSize()).doubleValue() / Double.valueOf(1048576.0D).doubleValue());
        Double var1 = Double.valueOf(Double.valueOf((double) Debug.getNativeHeapSize()).doubleValue() / 1048576.0D);
        Double var2 = Double.valueOf(Double.valueOf((double) Debug.getNativeHeapFreeSize()).doubleValue() / 1048576.0D);
        DecimalFormat var3 = new DecimalFormat();
        var3.setMaximumFractionDigits(2);
        var3.setMinimumFractionDigits(2);
        Log.d("tag", "debug. =================================");
        Log.d("tag", "debug.heap native: allocated " + var3.format(var0) + "MB of " + var3.format(var1) + "MB (" + var3.format(var2) + "MB free)");
        Log.d("tag", "debug.memory: allocated: " + var3.format(Double.valueOf((double) (Runtime.getRuntime().totalMemory() / 1048576L))) + "MB of " + var3.format(Double.valueOf((double) (Runtime.getRuntime().maxMemory() / 1048576L))) + "MB (" + var3.format(Double.valueOf((double) (Runtime.getRuntime().freeMemory() / 1048576L))) + "MB free)");
    }

    private static Bitmap rotateImage(Bitmap var0, int var1) {
        Matrix var2 = new Matrix();
        if (var1 == 90) {
            var2.postRotate(90.0F);
        } else if (var1 == 180) {
            var2.postRotate(180.0F);
        } else if (var1 == 270) {
            var2.postRotate(270.0F);
        }

        Bitmap var4 = null;
        if (var1 != 0) {
            var4 = Bitmap.createBitmap(var0, 0, 0, var0.getWidth(), var0.getHeight(), var2, true);
        }

        return var4;
    }
}
