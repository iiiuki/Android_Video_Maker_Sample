package com.hiep.video.maker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.hiep.video.maker.entity.VideoEntity;
import com.hiep.video.maker.player.DemoPlayer;
import com.hiep.video.maker.player.ExtractorRendererBuilder;
import com.hiep.video.maker.system.AppConfig;
import com.hiep.video.maker.ui.adapter.MyAdapter;
import com.hiep.video.maker.util.BitmapUtil;
import com.hiep.video.maker.util.FFmpegCmdUtil;
import com.hiep.video.maker.util.FileUtil;
import com.hiep.video.maker.util.Logger;

import java.io.File;

/**
 * Created by anh on 7/24/2016.
 */
public class FrameVideoActivity extends BaseActivity implements SurfaceHolder.Callback,DemoPlayer.Listener{
    private static final String TAG = FrameVideoActivity.class.getSimpleName();
    private static final int KEY_CMD_ADD_FRAME_VIDEO = 2;
    public static final int HANDLE_SHOW_VIDEO_DURATION = 1;
    private LinearLayout mLlBack;
    private LinearLayout mLlDone;
    private SurfaceView mSvVideo;
    private ImageView mIvStatus;
    private ImageView mIvFrame;
    private RecyclerView recyclerFrameEffect;
    private ProgressBar mPgDuration;
    private ProgressDialog progressDialog;

    private FFmpeg ffmpeg;
    private DemoPlayer player;
    private Uri contentUri;

    private String videoInput;
    private String videoOutput;
    private int widthVideo,heightVideo;
    private boolean isAddFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_video);
        videoInput=getIntent().getExtras().getString(AppConfig.EXTRA_VIDEO_FRAME);
        if (videoInput==null || TextUtils.isEmpty(videoInput)){
            onBackPressed();
        }
        ffmpeg= FFmpeg.getInstance(getApplicationContext());
        iniUI();
        iniFrame();
        loadFFMpegBinary();
    }

    public void  iniUI(){
        super.iniUI();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(null);

        mLlBack=(LinearLayout)findViewById(R.id.ll_video_frame_back);
        mLlDone=(LinearLayout)findViewById(R.id.ll_video_frame_done);
        mSvVideo=(SurfaceView)findViewById(R.id.sv_play_frame_video);
        mIvStatus=(ImageView)findViewById(R.id.iv_play_frame_status);
        mIvFrame=(ImageView)findViewById(R.id.iv_frame_video);
        mPgDuration=(ProgressBar)findViewById(R.id.pb_preview_video_duration);
        recyclerFrameEffect=(RecyclerView)findViewById(R.id.recycler_frame_video);

        mLlBack.setOnClickListener(this);
        mLlDone.setOnClickListener(this);
        mSvVideo.getHolder().addCallback(this);

        mIvStatus.setVisibility(View.INVISIBLE);
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
        new AlertDialog.Builder(FrameVideoActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FrameVideoActivity.this.finish();
                    }
                })
                .create()
                .show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (videoInput!=null && !TextUtils.isEmpty(videoInput)){
            contentUri=Uri.parse(videoInput);
            preparePlayer();
        }
    }

        /// Add UI boder
    int[] iconBorderlist;
    private void iniFrame(){
        MediaMetadataRetriever retriever = new  MediaMetadataRetriever();
            Bitmap bmp = null;
            try
            {
                retriever.setDataSource(videoInput);
                bmp = retriever.getFrameAtTime();
                widthVideo=bmp.getHeight();
                heightVideo=bmp.getWidth();
                Logger.d("widthVideo: "+widthVideo+" > heightVideo: "+heightVideo);
            }catch (Exception e){

            }
        iconBorderlist=new int[]{
                R.drawable.icon_none,R.drawable.border_1,R.drawable.border_2,R.drawable.border_27,R.drawable.border_28,
                R.drawable.border_3,R.drawable.border_9,R.drawable.border_15,R.drawable.border_21,R.drawable.border_29,
                R.drawable.border_4,R.drawable.border_10,R.drawable.border_16,R.drawable.border_22,R.drawable.border_30,
                R.drawable.border_5,R.drawable.border_11,R.drawable.border_17,R.drawable.border_23,R.drawable.border_31,
                R.drawable.border_6,R.drawable.border_12,R.drawable.border_18,R.drawable.border_24,R.drawable.border_32,
                R.drawable.border_7,R.drawable.border_13,R.drawable.border_19,R.drawable.border_25,R.drawable.border_33,
                R.drawable.border_8,R.drawable.border_14,R.drawable.border_20,R.drawable.border_26,R.drawable.border_34
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        int colorFlipper = this.getResources().getColor(R.color.trgb_262626);
        int colorBtnFooter = this.getResources().getColor(R.color.collage_purple);
        MyAdapter collageAdapter= new MyAdapter(iconBorderlist, new MyAdapter.CurrentCollageIndexChangedListener() {
            public void onIndexChanged(int index) {
                if (index>0){
                    Glide.with(FrameVideoActivity.this).load(iconBorderlist[index]).asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    mIvFrame.setImageResource(android.R.color.transparent);
                                    isAddFrame=false;
                                }

                                @Override
                                public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                                    mIvFrame.setImageBitmap(arg0);
                                    FileUtil.deleteFileInDir(FileUtil.getImageBorder());
                                    Bitmap bm= BitmapUtil.scaleBitmap(arg0,widthVideo,heightVideo);
                                    BitmapUtil.saveBitmapNoCompression(bm,FFmpegCmdUtil.linkBorder);
                                    isAddFrame=true;
                                }
                            });

                }else if (index==0){
                    mIvFrame.setImageResource(android.R.color.transparent);
                    isAddFrame=false;
                }
            }
        }, colorFlipper, colorBtnFooter, false, true);

        recyclerFrameEffect.setLayoutManager(linearLayoutManager);
        recyclerFrameEffect.setAdapter(collageAdapter);
        recyclerFrameEffect.setItemAnimator(new DefaultItemAnimator());
    }

    public  void addFrameToVideo(){
        videoOutput =  FileUtil.getMyVideo() + "/video_maker_"+System.currentTimeMillis()+".mp4";
        execFFmpegBinary(FFmpegCmdUtil.cmdAddBorderToVideo(videoInput,videoOutput),KEY_CMD_ADD_FRAME_VIDEO);
    }

    private void execFFmpegBinary(final String[] command,final  int key) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    if (key==KEY_CMD_ADD_FRAME_VIDEO){
                        Toast.makeText(FrameVideoActivity.this,"Add  frame failure",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSuccess(String s) {
                    if (key==KEY_CMD_ADD_FRAME_VIDEO){
                        if (new File(videoOutput).exists()){
                            gotoPreview(videoOutput);
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
            case R.id.ll_video_frame_back:
                onBackPressed();
                break;
            case R.id.ll_video_frame_done:
                if (isAddFrame){
                    addFrameToVideo();
                }else {
                    gotoPreview(videoInput);
                }
                break;
        }
    }

    private void gotoPreview(String path){
        File childFile= new File(path);
        if (childFile.exists()){
            VideoEntity videoEntity =new VideoEntity();
            videoEntity.setId(0);
            videoEntity.setCreateTime(childFile.lastModified());
            Logger.d(TAG,"Create time: "+videoEntity.getCreateTime());
            videoEntity.setFilePath(childFile.getAbsolutePath());
            videoEntity.setFileName(childFile.getName());

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(FrameVideoActivity.this, Uri.fromFile(childFile));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Logger.d(TAG,"Duration time: "+time);
            videoEntity.setDuration(Long.parseLong(time));

            Intent intent=new Intent(FrameVideoActivity.this,PreviewVideoActivity.class);
            intent.putExtra(AppConfig.EXTRA_VIDEO_ENTITY,videoEntity);
            startActivity(intent);
        }else {
            return;
        }
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
    }

    private void releasePlayer() {
        handler.removeMessages(HANDLE_SHOW_VIDEO_DURATION);
        if (player != null) {
            player.release();
            player = null;
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
                        if (!isFinishing()  && (videoOutput==null  || !new File(videoOutput).exists())) {
                            contentUri = Uri.parse(videoInput);
                            preparePlayer();
                        }else if(!isFinishing()&& videoOutput!=null && new File(videoOutput).exists()){
                            contentUri = Uri.parse(videoOutput);
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
    public void onBackPressed() {
        releasePlayer();
        finish();
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
