package com.hiep.video.maker.util;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by PingPingStudio on 11/30/2015.
 */

public class BitmapUtil {
    public final static String TAG = BitmapUtil.class.getSimpleName();
    public static Bitmap readBimapFromSDCard(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        return bitmap;
    }

    public static Bitmap alPhaBitmap (Bitmap bitmap, int cons){
        Bitmap transBitmap = Bitmap.createBitmap(bitmap);
        Canvas canvas = new Canvas(transBitmap);
        canvas.drawARGB(0, 0, 0, 0);

        Paint transparentpainthack = new Paint();

        transparentpainthack.setAlpha(cons);

        canvas.drawBitmap(transBitmap, 0, 0, transparentpainthack);
        return  transBitmap;
    }
    public static Bitmap resize(Bitmap paramBitmap, int paramInt1, int paramInt2) {
        Bitmap localBitmap = Bitmap.createScaledBitmap(paramBitmap, paramInt1, paramInt2, true);
        if (!paramBitmap.isRecycled())
            paramBitmap.recycle();
        return localBitmap;
    }
    public static Bitmap zoomImageWithHeight(Bitmap paramBitmap, double paramDouble) {
        int i = paramBitmap.getWidth();
        int j = paramBitmap.getHeight();
        float f = (float) paramDouble / j;
        Matrix localMatrix = new Matrix();
        localMatrix.postScale(f, f);
        Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, i, j, localMatrix, true);
        if ((paramBitmap != null) && (!paramBitmap.isRecycled()))
            paramBitmap.recycle();
        return localBitmap;
    }
    public static Bitmap zoomImageWithWidth(Bitmap paramBitmap, double paramDouble) {
        int i = paramBitmap.getWidth();
        int j = paramBitmap.getHeight();
        float f = (float) paramDouble / i;
        Matrix localMatrix = new Matrix();
        localMatrix.postScale(f, f);
        Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, i, j, localMatrix, true);
        if ((paramBitmap != null) && (!paramBitmap.isRecycled()))
            paramBitmap.recycle();
        return localBitmap;
    }
    public static void safeReleaseBitmap(Bitmap paramBitmap) {
        if (paramBitmap == null)
            return;
        if (!paramBitmap.isRecycled())
            paramBitmap.recycle();
    }
    public static File saveBitmapNoCompression(Bitmap paramBitmap, String paramString) {
        try{
            if (paramBitmap == null)
                throw new IllegalArgumentException("Image should not be null");
            if (paramString == null)
                throw new IllegalArgumentException("File path should not be null");
            File localFile = new File(paramString);
            if (!localFile.exists())
                localFile.createNewFile();
            FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
            if ((paramBitmap != null) && (localFileOutputStream != null)) {
                BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream, 4096);
                paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, localBufferedOutputStream);
                localBufferedOutputStream.close();
                localFileOutputStream.close();
            }
            return localFile;
        }catch (IOException e){
            return null;
        }

    }

    public static Bitmap getResizedBitmap(int targetW, int targetH, String imagePath) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //inJustDecodeBounds = true <-- will not load the bitmap into memory
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        return (bitmap);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
// RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // Rotates the bitmap by the specified degree.
    // If a new bitmap is created, the original bitmap is recycled.
    public static Bitmap rotate(Bitmap b, int degrees, Matrix m) {
        if (degrees != 0 && b != null) {
            if (m == null) {
                m = new Matrix();
            }
            m.setRotate(degrees,
                    (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

        return output;
    }

    public static byte[] convertBitmap2bytes(Bitmap bm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static File saveBitmap(Bitmap bmp, String dstFilePath) throws IOException {
        if (bmp == null) {
            throw new IllegalArgumentException("Image should not be null");
        }
        if (dstFilePath == null) {
            throw new IllegalArgumentException("File path should not be null");
        }
        File file = new File(dstFilePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        if ((bmp != null) && (fos != null)) {
            BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
            bmp.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            bos.close();
            fos.close();
        }
        return file;
    }

    public static Bitmap drawableToBitmap(NinePatchDrawable paramNinePatchDrawable, int paramInt1, int paramInt2) {
        if (paramNinePatchDrawable.getOpacity() != -1) ;
        for (Bitmap.Config localConfig = Bitmap.Config.ARGB_8888; ; localConfig = Bitmap.Config.RGB_565) {
            Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, localConfig);
            Canvas localCanvas = new Canvas(localBitmap);
            paramNinePatchDrawable.setBounds(0, 0, paramInt1, paramInt2);
            paramNinePatchDrawable.draw(localCanvas);
            return localBitmap;
        }
    }

    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float roundPx;
            float top;
            float bottom;
            float left;
            float right;
            float dst_left;
            float dst_top;
            float dst_right;
            float dst_bottom;

            if (width <= height) {
                roundPx = (float) (width / 2);
                top = 0;
                bottom = (float) width;
                left = 0;
                right = (float) width;
                height = width;
                dst_left = 0;
                dst_top = 0;
                dst_right = (float) width;
                dst_bottom = (float) width;
            } else {
                roundPx = (float) (height / 2);
                float clip = (float) ((width - height) / 2);
                left = clip;
                right = (float) width - clip;
                top = 0.0f;
                bottom = (float) height;
                width = height;
                dst_left = 0.0f;
                dst_top = 0.0f;
                dst_right = (float) height;
                dst_bottom = (float) height;
            }
            Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(output);
            Paint paint = new Paint();
            Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
            Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
            RectF rectF = new RectF(dst);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
//            paint.setColor(0x33BDBDBE);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, src, dst, paint);
            if (output != bitmap) {
                safeReleaseBitmap(bitmap);
                return output;
            }
        }
        return null;
    }


    public static int getClosestResampleSize(int cx, int cy, int maxDim) {
        int max = Math.max(cx, cy);
        int resample = 1;
        for (resample = 1; resample < Integer.MAX_VALUE; resample++) {
            if (resample * maxDim > max) {
                resample--;
                break;
            }
        }

        if (resample > 0) {
            return resample;
        }
        return 1;
    }

    ///////////////////////////////////////////////////////
    public static float get1DResizeFactor(Bitmap paramBitmap, int paramInt1, int paramInt2){
        float f1;
        float f2;
        if (paramBitmap != null) {
            f1 = paramBitmap.getWidth();
            f2 = paramBitmap.getHeight();
            switch (paramInt1){
                case 1:
                    if (f1<=paramInt2)return paramInt2 / f1;
                case 2:
                    if (f2 <= paramInt2) return paramInt2 / f2;
            }
        }
        return 1.0F;
    }
    public static Bitmap resizeBitmap(Bitmap paramBitmap, float paramFloat){
        if (paramBitmap == null)return null;
        int weight = (int)(paramFloat * paramBitmap.getWidth());
        int height = (int)(paramFloat * paramBitmap.getHeight());
        try{
            Bitmap localBitmap = Bitmap.createScaledBitmap(paramBitmap, weight,height, true);
            if (paramBitmap != localBitmap) safeReleaseBitmap(paramBitmap);
            return localBitmap;
        }
        catch (OutOfMemoryError localOutOfMemoryError){
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = Integer.valueOf(weight);
            arrayOfObject[1] = Integer.valueOf(height);
            Log.e(TAG, String.format("resizeBitmap OutOfMemoryError occur with weight=%s, height=%s", arrayOfObject));
        }
        return null;
    }
    public static File saveStickerBitmap(Bitmap paramBitmap, String paramString)
            throws IOException {
        if (paramBitmap == null)
            throw new IllegalArgumentException("Image should not be null");
        if (paramString == null)
            throw new IllegalArgumentException("File path should not be null");

        File localFile = new File(paramString);
        if (!localFile.exists())
            localFile.createNewFile();
        FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
        if ((paramBitmap != null) && (localFileOutputStream != null)) {
            BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream, 4096);
            paramBitmap.compress(Bitmap.CompressFormat.PNG, 70, localBufferedOutputStream);
            localBufferedOutputStream.close();
            localFileOutputStream.close();
        }
        return localFile;
    }
    public static File resizeBitmap(Bitmap paramBitmap, String paramString, float paramFloat)
            throws IOException {
        Bitmap localBitmap = resizeBitmap(paramBitmap, paramFloat);
        File localFile = null;
        if (localBitmap != null){
            localFile = saveStickerBitmap(localBitmap, paramString);
            localBitmap.recycle();
        }
        return localFile;
    }
    private static BitmapFactory.Options getResampling(int cx, int cy, int max) {
        float scaleVal = 1.0f;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        if (cx > cy) {
            scaleVal = (float) max / (float) cx;
        } else if (cy > cx) {
            scaleVal = (float) max / (float) cy;
        } else {
            scaleVal = (float) max / (float) cx;
        }
        bfo.outWidth = (int) (cx * scaleVal + 0.5f);
        bfo.outHeight = (int) (cy * scaleVal + 0.5f);
        return bfo;
    }

    public static Bitmap resampleImage(String path, int maxDim){

        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);

        BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
        optsDownSample.inSampleSize = getClosestResampleSize(bfo.outWidth, bfo.outHeight, maxDim);

        Bitmap bmpt = BitmapFactory.decodeFile(path, optsDownSample);

        Matrix m = new Matrix();

        if (bmpt.getWidth() > maxDim || bmpt.getHeight() > maxDim) {
            BitmapFactory.Options optsScale = getResampling(bmpt.getWidth(), bmpt.getHeight(), maxDim);
            m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
                    (float) optsScale.outHeight / (float) bmpt.getHeight());
        }

        int sdk = new Integer(Build.VERSION.SDK).intValue();
        if (sdk > 4) {
            int rotation = getExifRotation(path);
            if (rotation != 0) {
                m.postRotate(rotation);
            }
        }

        return Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(), bmpt.getHeight(), m, true);
    }
    public static Bitmap resampleImage(Bitmap bmpt, int maxDim) {
        Matrix m = new Matrix();
        if (bmpt.getWidth() > maxDim || bmpt.getHeight() > maxDim) {
            BitmapFactory.Options optsScale = getResampling(bmpt.getWidth(), bmpt.getHeight(), maxDim);
            m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
                    (float) optsScale.outHeight / (float) bmpt.getHeight());
        }

        return Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(), bmpt.getHeight(), m, true);
    }
    public static BitmapFactory.Options getBitmapDims(String path) throws Exception {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);
        return bfo;
    }

    public static int getExifRotation(String imgPath) {
        try {
            ExifInterface exif = new ExifInterface(imgPath);
            String rotationAmount = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(rotationAmount)) {
                int rotationParam = Integer.parseInt(rotationAmount);
                switch (rotationParam) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        return 0;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                    default:
                        return 0;
                }
            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        if (initialSize <= 8) {
            int roundedSize = 1;
            if (roundedSize < initialSize) {
                roundedSize = roundedSize << 1;
            }
        }
        int roundedSize = ((initialSize + 7) / 8) * 8;
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = (double) options.outWidth;
        double h = (double) options.outHeight;
        int lowerBound = (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (int) Math.min(Math.floor(w / minSideLength), (int) Math.floor(w / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        }
        if (minSideLength != -1) {
            return upperBound;
        }
        return lowerBound;
    }

    public static Bitmap decodePath(String path) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(path)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inInputShareable = true;
            bitmap = BitmapFactory.decodeFile(path, options);
        }
        return bitmap;
    }
    public static boolean isBitmap(String paramString){
        if (TextUtils.isEmpty(paramString))return false;
        BitmapFactory.Options localOptions;
        if (new File(paramString).exists()){
            localOptions = new BitmapFactory.Options();
            localOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(paramString, localOptions);
            if ((localOptions.outHeight <= 0) || (localOptions.outWidth <= 0))return false;
            else return true;
        }
        return false;
    }
    public static Bitmap viewToBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    public static Bitmap getCommentIconBitmap(Context context,String filename){
        String path="comment_icon/"+filename+".png";
        InputStream istr = null;
        AssetManager assetManager = context.getAssets();
        try {
            istr = assetManager.open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }
    public static String saveBitmapToLocal(String path,Bitmap bm) {
        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return path;
    }


    public static Bitmap scaleBitmap(Bitmap bitmap, int maxwidth){
        int w = maxwidth;
        int h = (int)((float)bitmap.getHeight() * ((float)w / (float)bitmap.getWidth()));

        if(bitmap.getWidth() < bitmap.getHeight()){
            h =maxwidth;
            w =( h * bitmap.getWidth()) /bitmap.getHeight() ;

            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);

        }else if (bitmap.getWidth() > bitmap.getHeight()){
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        }else{
            bitmap = Bitmap.createScaledBitmap(bitmap, w, w, true);
        }
//
        return bitmap;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap flipVBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
