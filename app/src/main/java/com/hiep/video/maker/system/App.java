package com.hiep.video.maker.system;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.hiep.video.maker.music.OnPlayAudioListener;

/**
 * Created by Hiep on 7/14/2016.
 */
public class App extends MultiDexApplication {
    public Bitmap bitmapNew;
    public Bitmap bitmapOld;

    private static GoogleAnalytics analytics;
    private static Tracker tracker;
    public static GoogleAnalytics analytics() {
        return analytics;
    }
    public static Tracker tracker() {
        return tracker;
    }


    private AudioManager mAudioManager;
    private MediaPlayer mAudioPlayer;
    @Override
    public void onCreate() {
        super.onCreate();
        VideoMaker.create(getApplicationContext());
        analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker("");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    public Bitmap getBitmapNew() {
        return bitmapNew;
    }

    public App setBitmapNew(Bitmap bitmapNew) {
        this.bitmapNew = bitmapNew;
        return this;
    }

    public Bitmap getBitmapOld() {
        return bitmapOld;
    }

    public App setBitmapOld(Bitmap bitmapOld) {
        this.bitmapOld = bitmapOld;
        return this;
    }

    public void requestAudioFocus() {
        mAudioManager.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
        }
    };
    public void startAudio(final String filepath, final OnPlayAudioListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    stopAudio();
                    requestAudioFocus();

                    mAudioPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(filepath));
                    if (callback != null) {
                        mAudioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                callback.finish();
                            }
                        });
                    }
                    if (mAudioPlayer != null) {
                        mAudioPlayer.start();
                        callback.duration(mAudioPlayer.getDuration());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopAudio() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mAudioPlayer != null) {
                    mAudioPlayer.release();
                }
            }
        }).start();
    }

    public void pauseAudio() {
        if (mAudioPlayer != null && mAudioPlayer.isPlaying()) {
            mAudioPlayer.pause();
        }
    }

    public void resumeAudio() {
        if (mAudioPlayer != null && !mAudioPlayer.isPlaying()) {
            mAudioPlayer.start();
        }
    }
}
