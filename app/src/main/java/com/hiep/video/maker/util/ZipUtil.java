package com.hiep.video.maker.util;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipUtil {
    private static final String TAG = ZipUtil.class.getSimpleName();

    public ZipUtil() {
    }

    public static void unzipFile(File file, String filePath) throws ZipException, IOException {
        Logger.d(TAG, "upZipFile, folderPath=" + filePath);
        File fileDir = new File(filePath);
        FileUtil.deleteFile(fileDir);
        fileDir.mkdirs();
        byte[] bytes = new byte[51200];
        ZipFile zipFile = new ZipFile(file);

        Enumeration enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            InputStream inputStream=null;
            FileOutputStream fileOutputStream=null;

            ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
            inputStream = zipFile.getInputStream(zipEntry);
            Object[] objects = new Object[3];
            objects[0] = filePath;
            objects[1] = File.separator;
            objects[2] = zipEntry.getName();
            String path = new String(String.format("%s%s%s", objects).getBytes("8859_1"), "GB2312");
            Logger.d(TAG, "upZipFile, Decompression_file=" + path);

            File fileSticker = new File(path);
            fileSticker.delete();

            File fileStickerChild = fileSticker.getParentFile();
            if (!fileStickerChild.exists())
                fileStickerChild.mkdirs();

            if (path.lastIndexOf(File.separator) == -1 + path.length())
                fileSticker.mkdir();

            int count;
            // reading and writing
            fileSticker.createNewFile();
            fileOutputStream = new FileOutputStream(fileSticker);
            while((count = inputStream.read(bytes)) != -1){
                fileOutputStream.write(bytes, 0, count);
            }
            inputStream.close();
            fileOutputStream.close();
        }
    }
    public static File zipFile(File file, String s) {
        return null;
    }
}