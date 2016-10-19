package com.hiep.video.maker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.exoplayer.ExoPlayer;
import com.hiep.video.maker.entity.AudioEntity;
import com.hiep.video.maker.entity.VideoEntity;
import com.hiep.video.maker.music.MusicActivity;
import com.hiep.video.maker.player.DemoPlayer;
import com.hiep.video.maker.player.ExtractorRendererBuilder;
import com.hiep.video.maker.system.AppConfig;
import com.hiep.video.maker.ui.adapter.MyAdapter;
import com.hiep.video.maker.util.BitmapUtil;
import com.hiep.video.maker.util.FFmpegCmdUtil;
import com.hiep.video.maker.util.FileUtil;
import com.hiep.video.maker.util.Logger;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements SurfaceHolder.Callback,DemoPlayer.Listener,View.OnClickListener  {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_CODE_AUDIO = 3;
    public static final int HANDLE_SHOW_VIDEO_DURATION = 1;
    private static final int KEY_CMD_CREATE_VIDEO = 1;
    private static final int KEY_CMD_ADD_AUDIO_VIDEO = 2;
    FFmpeg ffmpeg;
    private String outputSlideVideo=null;
    private String outputMyvideoAudio=null;

    private ProgressDialog progressDialog;

    private  LinearLayout llBackMain,llSave;
    private SurfaceView mSvVideo;
    private ImageView mIvStatus;
    private ProgressBar mPgDuration;
    private Button mBtnSelectAudio;

    private DemoPlayer player;
    private Uri contentUri;
    private boolean isPauseVideo=false;
    private boolean isAddAudioVideo=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_maker_main);
        ffmpeg=FFmpeg.getInstance(getApplicationContext());
        loadFFMpegBinary();
        iniUI();
        createVideoByFrame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (outputSlideVideo!=null && !TextUtils.isEmpty(outputSlideVideo)){
            preparePlayer();
        }
    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .create()
                .show();
    }

    public void iniUI(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(null);

        llBackMain=(LinearLayout)findViewById(R.id.llBackMain);
        llSave=(LinearLayout)findViewById(R.id.llSave);
        mSvVideo=(SurfaceView)findViewById(R.id.sv_play_video);
        mIvStatus=(ImageView)findViewById(R.id.iv_play_status);
        mPgDuration=(ProgressBar)findViewById(R.id.pb_preview_video_duration);
        mBtnSelectAudio=(Button)findViewById(R.id.btn_select_audio);

        llBackMain.setOnClickListener(this);
        llSave.setOnClickListener(this);
        mBtnSelectAudio.setOnClickListener(this);
        mSvVideo.getHolder().addCallback(this);
    }

//    /// Add UI boder
//    int[] iconBorderlist;
//    private void addBorder(){
//MediaMetadataRetriever retriever = new  MediaMetadataRetriever();
//    Bitmap bmp = null;
//    try
//    {
//        retriever.setDataSource(pathVideo);
//        bmp = retriever.getFrameAtTime();
//        widthVideo=bmp.getHeight();
//        heightVideo=bmp.getWidth();
//        Logger.d("widthVideo: "+widthVideo+" > heightVideo: "+heightVideo);
//    }catch (Exception e){
//
//    }
//        iconBorderlist=new int[]{
//                R.drawable.icon_none,R.drawable.border_1,R.drawable.border_2,R.drawable.border_27,R.drawable.border_28,
//                R.drawable.border_3,R.drawable.border_9,R.drawable.border_15,R.drawable.border_21,R.drawable.border_29,
//                R.drawable.border_4,R.drawable.border_10,R.drawable.border_16,R.drawable.border_22,R.drawable.border_30,
//                R.drawable.border_5,R.drawable.border_11,R.drawable.border_17,R.drawable.border_23,R.drawable.border_31,
//                R.drawable.border_6,R.drawable.border_12,R.drawable.border_18,R.drawable.border_24,R.drawable.border_32,
//                R.drawable.border_7,R.drawable.border_13,R.drawable.border_19,R.drawable.border_25,R.drawable.border_33,
//                R.drawable.border_8,R.drawable.border_14,R.drawable.border_20,R.drawable.border_26,R.drawable.border_34
//        };
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
//        int colorFlipper = this.getResources().getColor(R.color.trgb_262626);
//        int colorBtnFooter = this.getResources().getColor(R.color.collage_purple);
//        MyAdapter collageAdapter= new MyAdapter(iconBorderlist, new MyAdapter.CurrentCollageIndexChangedListener() {
//            public void onIndexChanged(int index) {
//                if (index>0){
//                    Glide.with(MainActivity.this).load(iconBorderlist[index]).asBitmap()
//                            .into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                                    isSetBorderVideo=false;
//                                    mIvBorderVideo.setImageResource(android.R.color.transparent);
//                                }
//
//                                @Override
//                                public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
//                                    mIvBorderVideo.setImageBitmap(arg0);
//                                    isSetBorderVideo=true;
//                                    FileUtil.deleteFileInDir(FileUtil.getImageBorder());
//                                    Bitmap bm=BitmapUtil.scaleBitmap(arg0,widthVideo,heightVideo);
//                                    BitmapUtil.saveBitmapNoCompression(bm,FFmpegCmdUtil.linkBorder);
//                                }
//                            });
//
//                }else if (index==0){
//                    mIvBorderVideo.setImageResource(android.R.color.transparent);
//                    isSetBorderVideo=false;
//                }
//            }
//        }, colorFlipper, colorBtnFooter, false, true);
//
//        mRecyclerVideoBorder.setLayoutManager(linearLayoutManager);
//        mRecyclerVideoBorder.setAdapter(collageAdapter);
//        mRecyclerVideoBorder.setItemAnimator(new DefaultItemAnimator());
//    }


    private void createVideoByFrame(){
        int size=new File(FileUtil.getImageInput()).listFiles().length;
        Logger.d("Size input frame: "+size);
        String pathInputImage= FileUtil.getImageInput()+"/%d.jpg";
        outputSlideVideo = FileUtil.getSlideVideo()+ "/slide_video_" +System.currentTimeMillis()+ ".mp4";
        execFFmpegBinary(FFmpegCmdUtil.cmdCreateVideo(size,pathInputImage,outputSlideVideo),KEY_CMD_CREATE_VIDEO);
    }

    public  void addAudioToVideo(String linkvideo, String linkAudio){
        outputMyvideoAudio =  FileUtil.getSlideVideo() + "/audio_video_"+System.currentTimeMillis()+".mp4";
        execFFmpegBinary(FFmpegCmdUtil.cmdAddAudiotoVideo(linkvideo,linkAudio,outputMyvideoAudio),KEY_CMD_ADD_AUDIO_VIDEO);
    }


    private void execFFmpegBinary(final String[] command,final  int key) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    if (key==KEY_CMD_CREATE_VIDEO){
                        Toast.makeText(MainActivity.this,"Create slide video failure",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();
                    }else if (key==KEY_CMD_ADD_AUDIO_VIDEO){
                        Toast.makeText(MainActivity.this,"Add audio to video failure",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSuccess(String s) {
                    if (key==KEY_CMD_CREATE_VIDEO){
                        if (new File(outputSlideVideo).exists()){
                            contentUri=Uri.parse(outputSlideVideo);
                            preparePlayer();
                        }
                    }else if (key==KEY_CMD_ADD_AUDIO_VIDEO){
                        if (new File(outputMyvideoAudio).exists()){
                            contentUri=Uri.parse(outputMyvideoAudio);
                            preparePlayer();
                            isAddAudioVideo=true;
                        }
                    }
                }

                @Override
                public void onProgress(String s) {
                    Logger.d(TAG, "Started command : ffmpeg "+command);
                    progressDialog.setMessage("Processing\n"+s);
                }

                @Override
                public void onStart() {
                    Logger.d(TAG, "Started command : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Logger.d(TAG, "Finished command : ffmpeg "+command);
                    progressDialog.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_select_audio:
                Intent intent=new Intent(MainActivity.this, MusicActivity.class);
                startActivityForResult(intent,REQUEST_CODE_AUDIO);
                break;
            case R.id.llSave:
                if (!isAddAudioVideo){
                    outputMyvideoAudio=outputSlideVideo;
                }
                goToVideoEffect();
                break;
            case R.id.llBackMain:
                onBackPressed();
                break;
        }
    }

    private void goToVideoEffect(){
        File fileMyVideo= new File(outputMyvideoAudio);
        if (!fileMyVideo.exists()){
            return;
        }
        Intent intent =new Intent(MainActivity.this,VideoEffectActivity.class);
        intent.putExtra(AppConfig.EXTRA_VIDEO_EFFECT,outputMyvideoAudio);
        startActivity(intent);
        finish();
    }

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = com.google.android.exoplayer.util.Util.getUserAgent(this, "ExoPlayerDemo");
        return new ExtractorRendererBuilder(this, userAgent, contentUri);
    }
    private void preparePlayer() {
        if (player != null)
            player.release();

        player = new DemoPlayer(getRendererBuilder());
        player.addListener(this);
        player.seekTo(0);
        if (player != null)
            player.prepare();

        player.setSurface(mSvVideo.getHolder().getSurface());
        player.setPlayWhenReady(true);
        mIvStatus.setVisibility(View.INVISIBLE);
        isPauseVideo = false;
    }

    private void releasePlayer() {
        handler.removeMessages(HANDLE_SHOW_VIDEO_DURATION);
        if (player != null) {
            player.release();
            player = null;
            isPauseVideo=false;
        }
    }

    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_SHOW_VIDEO_DURATION:
                    if (player != null) {
                        mPgDuration.setProgress((int) ((player.getCurrentPosition() * 1000) / player.getDuration()));
                        handler.sendEmptyMessageDelayed(HANDLE_SHOW_VIDEO_DURATION, 100);
                    }
                    break;
            }
        }
    };


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (player != null) {
            player.setSurface(surfaceHolder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onStateChanged(final boolean playWhenReady,final int playbackState) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = "playWhenReady=" + playWhenReady + ", playbackState=";
                switch (playbackState) {
                    case ExoPlayer.STATE_BUFFERING:
                        text += "buffering";
                        break;
                    case ExoPlayer.STATE_ENDED:
                        releasePlayer();
                        if (!isFinishing() && outputMyvideoAudio ==null) {
                            contentUri = Uri.parse(outputSlideVideo);
                            preparePlayer();
                        }else if(!isFinishing() && outputMyvideoAudio !=null){
                            contentUri = Uri.parse(outputMyvideoAudio);
                            preparePlayer();
                        }
                        text += "ended";
                        break;
                    case ExoPlayer.STATE_IDLE:
                        text += "idle";
                        break;
                    case ExoPlayer.STATE_PREPARING:
                        text += "preparing";
                        break;
                    case ExoPlayer.STATE_READY:
                        text += "ready";
                        handler.sendEmptyMessage(HANDLE_SHOW_VIDEO_DURATION);
                        break;
                    default:
                        text += "unknown";
                        break;
                }
                Logger.d(TAG, "exoplayer " + text);
            }
        });
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.d("onActivityResult");
        if (data!=null && resultCode==RESULT_OK){
            if (requestCode==REQUEST_CODE_AUDIO){
                AudioEntity audioEntity =(AudioEntity)data.getSerializableExtra("audio_select");
                if (audioEntity!=null){
                    if (player!=null &&  player.getPlayerControl()!=null && player.getPlayerControl().isPlaying()){
                        player.getPlayerControl().pause();
                    }
                    addAudioToVideo(outputSlideVideo,audioEntity.getAudioPath());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        releasePlayer();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        releasePlayer();
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }
}
