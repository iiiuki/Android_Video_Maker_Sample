package com.hiep.video.maker.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.hiep.video.maker.system.VideoMaker;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by anh on 6/10/2016.
 */
public class FileUtil {
    public final static String VIDEO_DIR = "/iiiuki_make_video";
    public final static String IMAGE_INPUT="/image_input";
    public final static String IMAGE_BORDER="/image_border";
    public final static String SLIDE_VIDEO="/temp_video";
    public final static String VIDEO_EFFECT="/video_effect";
    public final static String MY_VIDEO="/my_video";
    public final static String GIF_IMAGE="/gif_image";


    public static void createVideoFolders() {
        String[] subDataFolder = {IMAGE_INPUT,SLIDE_VIDEO,MY_VIDEO,VIDEO_EFFECT,IMAGE_BORDER,GIF_IMAGE};
        for (int j = 0; j < subDataFolder.length; j++) {
            File file = new File(Environment.getExternalStorageDirectory() + VIDEO_DIR + subDataFolder[j]);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }
    public static String getImageInput() {
        File cacheDir = new File(Environment.getExternalStorageDirectory() + VIDEO_DIR + IMAGE_INPUT);
        if(!cacheDir.exists()) cacheDir.mkdir();
        return Environment.getExternalStorageDirectory() + VIDEO_DIR + IMAGE_INPUT;
    }

    public static String getSlideVideo() {
        File cacheDir = new File(Environment.getExternalStorageDirectory() + VIDEO_DIR + SLIDE_VIDEO);
        if(!cacheDir.exists()) cacheDir.mkdir();
        return Environment.getExternalStorageDirectory() + VIDEO_DIR + SLIDE_VIDEO;
    }

    public static String getMyVideo(){
        File cacheDir = new File(Environment.getExternalStorageDirectory() + VIDEO_DIR + MY_VIDEO);
        if(!cacheDir.exists()) cacheDir.mkdir();
        return Environment.getExternalStorageDirectory() + VIDEO_DIR + MY_VIDEO;
    }

    public static String getVideoEffect(){
        File cacheDir = new File(Environment.getExternalStorageDirectory() + VIDEO_DIR + VIDEO_EFFECT);
        if(!cacheDir.exists()) cacheDir.mkdir();
        return Environment.getExternalStorageDirectory() + VIDEO_DIR + VIDEO_EFFECT;
    }

    public static String getImageBorder(){
        File cacheDir = new File(Environment.getExternalStorageDirectory() + VIDEO_DIR + IMAGE_BORDER);
        if(!cacheDir.exists()) cacheDir.mkdir();

        return Environment.getExternalStorageDirectory() + VIDEO_DIR + IMAGE_BORDER;
    }

    public static String getGifImage(){
        File cacheDir = new File(Environment.getExternalStorageDirectory() + VIDEO_DIR + GIF_IMAGE);
        if(!cacheDir.exists()) cacheDir.mkdir();

        return Environment.getExternalStorageDirectory() + VIDEO_DIR + IMAGE_BORDER;
    }

    public static void copyFile(File sourceFile, File destFile)  {
        try{
            if (!sourceFile.exists()) {
                return;
            }
            FileChannel source = null;
            FileChannel destination = null;
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size());
            }
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }catch (IOException e){

        }
    }

    public static void copyFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }
    public static byte[] getBytes(String path) {
        ByteArrayOutputStream bytearrayoutputstream = null;
        FileInputStream inputStream = null;
        byte[] results = null;
        try {
            inputStream = new FileInputStream(new File(path));
            bytearrayoutputstream = new ByteArrayOutputStream(1024);
            byte[] headers = new byte[1024];
            while (true) {
                int i = inputStream.read(headers);
                if (i == -1)
                    break;
                bytearrayoutputstream.write(headers, 0, i);
            }
            results = bytearrayoutputstream.toByteArray();
            bytearrayoutputstream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
        return results;
    }


    public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        try{
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }finally {
//            if (cursor!=null){
//                cursor.close();
//            }
        }
    }

    public static void deleteFile(File paramFile) {
        if ((paramFile == null) || (!paramFile.canWrite()) || (!paramFile.exists()))
            return;
        if (paramFile.isDirectory()) {
            File[] arrayOfFile = paramFile.listFiles();
            if (arrayOfFile != null) {
                int i = arrayOfFile.length;
                for (int j = 0; j < i; j++)
                    deleteFile(arrayOfFile[j]);
            }
        }
        paramFile.delete();
    }
    public static void deleteFile(String paramString) {
        if (TextUtils.isEmpty(paramString))
            return;
        deleteFile(new File(paramString));
    }

    public static void deleteFileInDir(File paramFile) {
        if ((paramFile == null) || (!paramFile.canWrite()) || (!paramFile.exists()))
            return;
        if (paramFile.isDirectory()) {
            File[] arrayOfFile = paramFile.listFiles();
            if (arrayOfFile != null) {
                int i = arrayOfFile.length;
                for (int j = 0; j < i; j++)
                    deleteFile(arrayOfFile[j]);
            }
        }
    }
    public static void deleteFileInDir(String paramString) {
        if (TextUtils.isEmpty(paramString))
            return;
        deleteFileInDir(new File(paramString));
    }



    public static void deleteFold(String paramString) {
        if (TextUtils.isEmpty(paramString))
            return;
        deleteFile(new File(paramString));
    }

    public static void del(String pathDic){
        File dir = new File(pathDic);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
    }

    public static String getPath(Context context,Uri uri) {
        if (uri == null) {
            return null;
        }
        String result = null;

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        // deprecated:
        // Cursor cursor = managedQuery(uri, projection, null, null, null);

        if (cursor != null) {

            int columnIndex = 0;
            try {
                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(columnIndex);
            } catch (IllegalArgumentException e) {

            } finally {
                try {
                    if (!cursor.isClosed()) {
                        cursor.close();
                    }
                    cursor = null;
                } catch (Exception e) {

                }
            }
        }
        return result;
    }

    public static int copyVideoEffect(){
        File localFile = new File(FileUtil.getVideoEffect());
        if (!localFile.exists())
            localFile.mkdirs();

        try{
            copyFile("video_effect_0.mp4");
            copyFile("gif_money.gif");
            copyFile("grad1.png");
            copyFile("grad2.png");
            return 0;
        }
        catch (IOException localIOException){
            localIOException.printStackTrace();
        }
        return -1;
    }


    private static void copyFile(String fileName) throws IOException {
        InputStream localInputStream =null;
        FileOutputStream fileOutputStream=null;
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = FileUtil.getVideoEffect()+"/";
        arrayOfObject[1] = fileName;
        try {
            localInputStream = VideoMaker.context.getAssets().open("video_effct/"+fileName);
            fileOutputStream= new FileOutputStream(String.format("%s%s", arrayOfObject));
            byte[] buffer = new byte[102400];
            int read;
            while ((read = localInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);
            }
            safeCloseStreame(localInputStream);
            fileOutputStream.flush();
            safeCloseStreame(fileOutputStream);
            return;
        } catch (FileNotFoundException fnfe1) {
        } catch (Exception e) {
        }
    }


    private static void safeCloseStreame(Closeable paramCloseable) {
        if (paramCloseable != null) ;
        try {
            paramCloseable.close();
            return;
        } catch (IOException localIOException) {
        }
    }
}
