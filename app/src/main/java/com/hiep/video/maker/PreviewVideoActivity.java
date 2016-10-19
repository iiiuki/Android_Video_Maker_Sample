package com.hiep.video.maker;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer.ExoPlayer;
import com.hiep.video.maker.entity.VideoEntity;
import com.hiep.video.maker.player.DemoPlayer;
import com.hiep.video.maker.player.ExtractorRendererBuilder;
import com.hiep.video.maker.system.AppConfig;
import com.hiep.video.maker.util.Logger;

/**
 * Created by Hiep on 7/14/2016.
 */
public class PreviewVideoActivity extends BaseActivity implements SurfaceHolder.Callback,DemoPlayer.Listener,View.OnClickListener {
    private static final java.lang.String TAG = PreviewVideoActivity.class.getSimpleName();
    public static final int HANDLE_SHOW_VIDEO_DURATION = 1;

    private SurfaceView mSvVideo;
    private ImageView mIvVideoStatus;
    private ProgressBar mPbDuration;

    private DemoPlayer player;
    private Uri contentUri;
    private VideoEntity mVideoEntity;

    private boolean isBackPressed=false;
    private boolean isPauseVideo=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_video);
        mVideoEntity = (VideoEntity) getIntent().getSerializableExtra(AppConfig.EXTRA_VIDEO_ENTITY);
        if (mVideoEntity == null) {
            finish();
            return;
        }
        iniUI();
    }

    @Override
    public void iniUI() {
        super.iniUI();
        mSvVideo=(SurfaceView)findViewById(R.id.sv_play_video);
        mIvVideoStatus=(ImageView)findViewById(R.id.iv_play_status);
        mPbDuration=(ProgressBar)findViewById(R.id.pb_preview_video_duration);

        mIvVideoStatus.setVisibility(View.INVISIBLE);
        mSvVideo.getHolder().addCallback(this);
        mSvVideo.setOnClickListener(this);
    }

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
    protected void onResume() {
        super.onResume();
        contentUri = Uri.parse(mVideoEntity.getFilePath());
        preparePlayer();
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
        mIvVideoStatus.setVisibility(View.INVISIBLE);
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
                        mPbDuration.setProgress((int) ((player.getCurrentPosition() * 1000) / player.getDuration()));
                        handler.sendEmptyMessageDelayed(HANDLE_SHOW_VIDEO_DURATION, 100);
                    }
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sv_play_video:
                pauseResumeVideo();
                break;
        }
    }

    private void pauseResumeVideo() {
        if (player != null && player.getPlayerControl() != null) {
            if (player.getPlayerControl().isPlaying()) {
                player.getPlayerControl().pause();
                mIvVideoStatus.setVisibility(View.VISIBLE);
                isPauseVideo=true;
            } else {
                player.getPlayerControl().start();
                mIvVideoStatus.setVisibility(View.INVISIBLE);
                isPauseVideo=false;
            }
        }
    }

    @Override
    public void onStateChanged(final boolean playWhenReady, final int playbackState) {
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
                        if (!isFinishing()) {
                            contentUri = Uri.parse(mVideoEntity.getFilePath());
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
        onBackPressed();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }

    @Override
    protected void onPause() {
        releasePlayer();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        releasePlayer();
        super.onBackPressed();
    }


}
