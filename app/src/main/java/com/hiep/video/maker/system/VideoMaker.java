package com.hiep.video.maker.system;

import android.content.Context;

/**
 * Created by Hiep on 7/19/2016.
 */
public class VideoMaker {
    public static Context context;
    private static VideoMaker mInstance = null;

    public static VideoMaker create(Context context) {
        if (mInstance == null) {
            mInstance = new VideoMaker(context);
        }
        return mInstance;
    }

    private VideoMaker(Context context) {
        this.context = context;
    }
}
