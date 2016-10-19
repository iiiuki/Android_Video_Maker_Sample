package com.hiep.video.maker.system;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.hiep.video.maker.util.SharedPreferencesUtil;

import java.io.InputStream;

/**
 * Created by anh on 6/10/2016.
 */
public class Config extends SharedPreferencesUtil {
    public static String linkApp = "Photo had been created: https://play.google.com/store/apps/details?id=";
    public static String ECOTICON = "ecoticon/";
    public static int SCREENWIDTH;
    public static int SCREENHEIGHT;
    public static int height_rec;

    public static void init(Activity mActivity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        SCREENWIDTH = displaymetrics.widthPixels;
        SCREENHEIGHT = displaymetrics.heightPixels;

        height_rec = (SCREENHEIGHT / 2 - SCREENWIDTH / 2) / 2;
    }

    private static final Config config;

    static {
        config = new Config(VideoMaker.context);
    }

    private Config(Context context) {
        super(context, "video_maker_config");
    }

    public static Config getInstance() {
        return config;
    }

    public static Drawable getDrawableFromAssets(Context mActivity, String path) {
        // load image
        try {
            // get input stream
            InputStream ims = mActivity.getAssets().open(path);
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            return d;
        } catch (Exception ex) {
            return null;
        }
    }

}
