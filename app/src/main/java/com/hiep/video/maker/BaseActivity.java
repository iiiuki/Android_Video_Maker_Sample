package com.hiep.video.maker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.hiep.video.maker.asyncloader.AsyncCallBack;
import com.hiep.video.maker.asyncloader.AsyncTaskLoader;
import com.hiep.video.maker.asyncloader.IDoBackGround;
import com.hiep.video.maker.asyncloader.IHandler;
import com.hiep.video.maker.music.OnPlayAudioListener;
import com.hiep.video.maker.system.App;
import com.hiep.video.maker.system.Config;
import com.hiep.video.maker.util.FileUtil;
import com.hiep.video.maker.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Hiep on 7/14/2016.
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener,ActivityCompat.OnRequestPermissionsResultCallback {
    protected static final int REQUEST_CAMERA_PERMISSION = 200;
    protected static final int REQUEST_READ_EXTERNAL_STORAGE = 222;
    private ProgressDialog mProgressDialog;
    private InterstitialAd mInterstitialAdmob;
    private AdView adViewAdmob;
    protected  App myApplication;
    public AsyncTaskLoader asyncTaskLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        asyncTaskLoader = new AsyncTaskLoader();
        myApplication = (App) getApplicationContext();
    }

    protected boolean isPremissionWrireExternalStorage(){
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = BaseActivity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    protected boolean isPremissionCamera(){
        String permission = "android.permission.CAMERA";
        int res = BaseActivity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    public void iniUI(){

    }



    private long mLastClickTime = 0;
    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

    }
    public void showLoading() {
        handlerDoWork(new IHandler() {
            @Override
            public void doWork() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                } else {
                    mProgressDialog = new ProgressDialog(BaseActivity.this);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setMessage("Processing...");
                    mProgressDialog.show();
                }
            }
        });
    }

    public void dismissLoading() {
        handlerDoWork(new IHandler() {
            public void doWork() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        });
    }
    public void doBackGround(final IDoBackGround mIDoBackGround) {
        IHandler mIHandler = new IHandler() {
            @Override
            public void doWork() {
                asyncTaskLoader = new AsyncTaskLoader();
                asyncTaskLoader.execute(new AsyncCallBack(null) {
                    @Override
                    public void workToDo() {
                        super.workToDo();
                        mIDoBackGround.onDoBackGround(asyncTaskLoader.isCancelled());
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        mIDoBackGround.onComplelted();
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        super.onCancelled();
                    }

                    @Override
                    public void onCancelled(boolean result) {
                        // TODO Auto-generated method stub
                        super.onCancelled(result);
                    }
                });
            }
        };
        this.handlerDoWork(mIHandler);
    }

    public void addAdmobBaner(int i) {
        final LinearLayout admob = (LinearLayout) findViewById(i);
        adViewAdmob = new com.google.android.gms.ads.AdView(this);
        adViewAdmob.setAdSize(AdSize.BANNER);
        adViewAdmob.setAdUnitId("*****************************");
        AdRequest adrequest = (new AdRequest.Builder()).build();
        adViewAdmob.loadAd(adrequest);
        adViewAdmob.setAdListener(new com.google.android.gms.ads.AdListener() {

            public void onAdClosed() {
                super.onAdClosed();
                Logger.e("AdView", "onAdClosed");
            }

            public void onAdFailedToLoad(int j) {
                super.onAdFailedToLoad(j);

                Logger.e("AdView", "onAdFailedToLoad");

            }

            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Logger.e("AdView", "onAdLeftApplication");
            }

            public void onAdLoaded() {
                super.onAdLoaded();
                Logger.e("AdView", "onAdLoaded");
            }

            public void onAdOpened() {
                super.onAdOpened();
                Logger.e("AdView", "onAdOpened");
            }
        });
        admob.addView(adViewAdmob);
    }

    public void showAdmobFullScreend() {
        mInterstitialAdmob = new com.google.android.gms.ads.InterstitialAd(this);
        mInterstitialAdmob.setAdUnitId("***********************************");
        mInterstitialAdmob.setAdListener(new com.google.android.gms.ads.AdListener() {

            public void onAdClosed() {
                super.onAdClosed();
            }

            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                displayInterstitial();
            }

            public void onAdLoaded() {
                super.onAdLoaded();
                displayInterstitial();
            }

            public void onAdOpened() {
                super.onAdOpened();
            }
        });
        AdRequest adrequest = (new AdRequest.Builder()).build();
        mInterstitialAdmob.loadAd(adrequest);
    }


    public void displayInterstitial() {
        handlerDoWork(new IHandler() {
            @Override
            public void doWork() {
                if (mInterstitialAdmob.isLoaded())
                    mInterstitialAdmob.show();
            }
        });
    }

    public void handlerDoWork(IHandler mIHandler) {
        Message message = mIHandlerHandler.obtainMessage(0, mIHandler);
        mIHandlerHandler.sendMessage(message);
    }

    final Handler mIHandlerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            IHandler mIHandler = (IHandler) msg.obj;
            mIHandler.doWork();
        }
    };


    /*Show detail app*/
    public void showDetailApp(String package_name) {
        try {
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + package_name));
            startActivity(marketIntent);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void sentToInstaShare(String imagePath, String Title) {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.instagram.android");
            try {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), imagePath, "", Title)));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            shareIntent.setType("image/jpeg");

            startActivity(shareIntent);
        } else {
            // bring user to the market to download the app.
            // or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + "com.instagram.android"));
            startActivity(intent);
        }
    }

    public void sentToFaceShare(String imagePath, String Title) {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.facebook.katana");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));

            shareIntent.putExtra(Intent.EXTRA_TEXT, Config.linkApp + getPackageName());
            try {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), imagePath, "", Title)));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            shareIntent.setType("image/jpeg");

            startActivity(shareIntent);
        } else {
            // bring user to the market to download the app.
            // or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + "com.facebook.katana"));
            startActivity(intent);
        }
    }

    public void updateMedia(String filepath) {
        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{filepath}, null, null);
    }




    @Override
    protected void onResume() {
        if (adViewAdmob != null)
            adViewAdmob.resume();
        super.onResume();
        FileUtil.createVideoFolders();
    }

    @Override
    protected void onPause() {
        if (adViewAdmob != null)
            adViewAdmob.pause();

        myApplication.pauseAudio();
        super.onPause();
    }

    @Override
    protected void onStop() {
        myApplication.pauseAudio();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (adViewAdmob != null)
            adViewAdmob.destroy();
        super.onDestroy();
    }
    public int dp(float value) {
        float density = getResources().getDisplayMetrics().density;
        return (int)Math.ceil(density * value);
    }
}
