//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hiep.video.maker.util;

import android.os.Environment;
import android.os.StatFs;


import java.io.File;

public class SDCardUtil {
    public SDCardUtil() {
    }


    public static boolean diskSpaceAvailable() {
        File mSDCardDirectory = getESD();
        if (mSDCardDirectory == null) {
            return false;
        }
        StatFs fs = new StatFs(mSDCardDirectory.getAbsolutePath());
        return fs.getAvailableBlocks() > 1 ? true : false;
    }

    public static File getESD() {
        return Environment.getExternalStorageDirectory();
    }

    public static String getESDString() {
        return getESD().toString();
    }

    public static boolean getExternalStorageCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static long getSdcardFreeSize() {
        String var0 = Environment.getExternalStorageState();
        long var1 = 0L;
        if (var0.equals(Environment.MEDIA_MOUNTED)) {
            StatFs var3 = new StatFs(getESD().getPath());
            var1 = (long) var3.getBlockSize() * (long) var3.getAvailableBlocks();
        }

        return var1;
    }
}
