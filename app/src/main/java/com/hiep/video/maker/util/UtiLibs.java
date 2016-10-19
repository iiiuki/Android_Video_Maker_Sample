package com.hiep.video.maker.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.File;
import java.util.Random;

public class UtiLibs {

    public static int getRandomIndex(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static Uri getUri(File file){

        if (null != file) {
            return Uri.fromFile(file);
        } else {
            return null;
        }

    }

    /**
     * Delete a file without throwing any exception
     *
     * @param path
     * @return
     */
    public static boolean deleteFileNoThrow(String path) {
        File file;
        try {
            file = new File(path);
        } catch (NullPointerException e) {
            return false;
        }

        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static void showDetailApp(Activity mActivity, String package_name) {
        try {
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + package_name));
            mActivity.startActivity(marketIntent);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    // ------------------------------------------------------------------------
    // Resize bipmap
    // Cho phép rize chiều cao, chiều rộng của 1 bức ảnh.
    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        if (bm == null)
            return bm;

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,matrix, false);

        return resizedBitmap;
    }
    public static int dp(Context m, float value) {
        float density = m.getResources().getDisplayMetrics().density;
        return (int)Math.ceil(density * value);
    }
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
