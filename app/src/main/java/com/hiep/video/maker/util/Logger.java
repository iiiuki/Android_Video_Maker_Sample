package com.hiep.video.maker.util;

import android.util.Log;

/**
 * Created by Hiep on 6/23/2016.
 */
public class Logger {
    public static final boolean IS_SHOW_LOG=true;
    public static void d(String Tag,String message){
        if (IS_SHOW_LOG){
            Log.d(Tag,message);
        }
    }
    public static void e(String Tag,String message){
        if (IS_SHOW_LOG){
            Log.e(Tag,message);
        }
    }
    public static void i(String Tag,String message){
        if (IS_SHOW_LOG){
            Log.i(Tag,message);
        }
    }
    public static void w(String Tag,String message){
        if (IS_SHOW_LOG){
            Log.w(Tag,message);
        }
    }

    private static String TAG_DEFAULT="Hiep";

    public static void i(String messahe){
        if (IS_SHOW_LOG){
            i(TAG_DEFAULT,messahe);
        }
    }

    public static void e(String messahe){
        if (IS_SHOW_LOG){
            e(TAG_DEFAULT,messahe);
        }
    }

    public static void d(String messahe){
        if (IS_SHOW_LOG){
            d(TAG_DEFAULT,messahe);
        }
    }
}
