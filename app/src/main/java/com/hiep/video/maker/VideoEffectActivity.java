package com.hiep.video.maker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.exoplayer.ExoPlayer;
import com.hiep.video.maker.adapter.VideoEffectAdapter;
import com.hiep.video.maker.entity.VideoEntity;
import com.hiep.video.maker.player.DemoPlayer;
import com.hiep.video.maker.player.ExtractorRendererBuilder;
import com.hiep.video.maker.system.AppConfig;
import com.hiep.video.maker.util.FFmpegCmdUtil;
import com.hiep.video.maker.util.FileUtil;
import com.hiep.video.maker.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by anh on 7/23/2016.
 */
public class VideoEffectActivity extends BaseActivity implements SurfaceHolder.Callback,DemoPlayer.Listener{
    private static final String TAG = VideoEffectActivity.class.getSimpleName();
    private static final int WHAT_GET_ALL_EFFECT_VIDEO_SUCCESS=100;
    private static final int WHAT_GET_ALL_EFFECT_VIDEO_FAILURE=101;

    public static final int HANDLE_SHOW_VIDEO_DURATION = 1;
    private static final int KEY_MERGE = 3;
    private static final int KEY_CMD_ADD_EFFECT_VIDEO = 2;

    private LinearLayout mLlBack;
    private LinearLayout mLlDone;
    private SurfaceView mSvVideo;
    private ImageView mIvStatus;
    private RecyclerView recyclerVideoEffect;
    private ProgressBar mPgDuration;
    private ProgressDialog progressDialog;

    private FFmpeg ffmpeg;
    private DemoPlayer player;
    private Uri contentUri;

    private String videoInput;
    private String videoOutput;
    private ArrayList<VideoEntity> listMyVideos;

    private boolean isLoopVideoEffect=false;
    private String videoEffectMergePath;

    private long durationVideoInput=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect_video);
        videoInput=getIntent().getExtras().getString(AppConfig.EXTRA_VIDEO_EFFECT);
        if (videoInput==null || TextUtils.isEmpty(videoInput)){
            onBackPressed();
        }
        ffmpeg= FFmpeg.getInstance(getApplicationContext());
        iniUI();
        getAllVideoEffectLocal();
        getDurationVideo();
        loadFFMpegBinary();
    }


    @Override
    public void iniUI() {
        super.iniUI();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(null);

        mLlBack=(LinearLayout)findViewById(R.id.ll_video_effect_back);
        mLlDone=(LinearLayout)findViewById(R.id.ll_video_effect_done);
        mSvVideo=(SurfaceView)findViewById(R.id.sv_play_effect_video);
        mIvStatus=(ImageView)findViewById(R.id.iv_play_effect_status);
        mPgDuration=(ProgressBar)findViewById(R.id.pb_preview_video_duration);
        recyclerVideoEffect=(RecyclerView)findViewById(R.id.recycler_effect_video);

        mLlBack.setOnClickListener(this);
        mLlDone.setOnClickListener(this);
        mSvVideo.getHolder().addCallback(this);

        mIvStatus.setVisibility(View.INVISIBLE);
    }

    private void getAllVideoEffectLocal(){
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    File dir = new File(FileUtil.getVideoEffect());
                    if (dir.isDirectory()){
                        listMyVideos=new ArrayList<>();
                        String[] children = dir.list();
                        for (int i = 0; i < children.length; i++){
                            try{
                                File childFile= new File(dir, children[i]);
                                if (childFile.getAbsolutePath().endsWith(".mp4")){
                                    VideoEntity videoEntity=new VideoEntity();
                                    videoEntity.setId(i);
                                    videoEntity.setCreateTime(childFile.lastModified());
                                    Logger.d(TAG,"Create time: "+videoEntity.getCreateTime());
                                    videoEntity.setFilePath(childFile.getAbsolutePath());
                                    videoEntity.setFileName(childFile.getName());

                                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                    retriever.setDataSource(VideoEffectActivity.this, Uri.fromFile(childFile));
                                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                    Logger.d(TAG,"Duration time: "+time);
                                    videoEntity.setDuration(Long.parseLong(time));
                                    listMyVideos.add(videoEntity);
                                }
                            }catch (Exception e){
                                continue;
                            }
                        }
                        handler.sendEmptyMessage(WHAT_GET_ALL_EFFECT_VIDEO_SUCCESS);
                    }else {
                        handler.sendEmptyMessage(WHAT_GET_ALL_EFFECT_VIDEO_FAILURE);
                    }
                }catch (Exception e){
                    Logger.e(TAG,e.getMessage());
                    handler.sendEmptyMessage(WHAT_GET_ALL_EFFECT_VIDEO_FAILURE);
                }
            }
        }).start();
    }


    private void getDurationVideo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(VideoEffectActivity.this, Uri.fromFile(new File(videoInput)));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                durationVideoInput=Long.parseLong(time);
            }
        }).start();
    }

    private void iniVideoEffect() {
        // tạo video empty
        VideoEntity videoEntity =new VideoEntity();
        videoEntity.setId(-1);
        listMyVideos.add(0,videoEntity);
        //

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        int colorFlipper = this.getResources().getColor(R.color.trgb_262626);
        int colorBtnFooter = this.getResources().getColor(R.color.collage_purple);
        VideoEffectAdapter collageAdapter= new VideoEffectAdapter(listMyVideos, new VideoEffectAdapter.IndexChangedListener() {
            public void onIndexChanged(VideoEntity entity) {
                if (entity.getId()==-1){
                    FileUtil.deleteFile(videoOutput);
                    if (new File(videoInput).exists()){
                        contentUri=Uri.parse(videoInput);
                        preparePlayer();
                    }
                    return;
                }else {
                    long duarationVideoEffect=entity.getDuration();
                    Logger.d(TAG, "duarationVideoEffect "+duarationVideoEffect);
                    Logger.d(TAG, "durationVideoInput "+durationVideoInput);
                    if (duarationVideoEffect>=durationVideoInput) {
                        addVideoToVideo(videoInput, entity.getFilePath());
                    }else {
                        int value=(int) (durationVideoInput/duarationVideoEffect);
                        Logger.d(TAG, "Merge, value "+value);
                        ArrayList<String> paths=new ArrayList<>();
                        for (int i=0;i<=value;i++){
                            paths.add(entity.getFilePath());
                        }
                        Logger.d(TAG, "Merge, size paths:  "+paths.size());
                        onMerge(paths);
                    }
                }
            }
        }, colorFlipper, colorBtnFooter);

        recyclerVideoEffect.setLayoutManager(linearLayoutManager);
        recyclerVideoEffect.setAdapter(collageAdapter);
        recyclerVideoEffect.setItemAnimator(new DefaultItemAnimator());
    }

    private void onMerge(ArrayList<String> filePath){
        //Merge
        FileUtil.deleteFile(FileUtil.getSlideVideo() + "/input.txt");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(FileUtil.getSlideVideo() + "/input.txt");
            for (int i = 0; i < filePath.size(); i++) {
                writer.write("file '" + filePath.get(i) + "'");
                writer.println();
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        videoEffectMergePath = FileUtil.getMyVideo() + "/merge-"+System.currentTimeMillis()+".mp4";
        String cmd="-f concat -safe 0 -i " + FileUtil.getSlideVideo() + "/input.txt -c copy -y " + videoEffectMergePath;
        Logger.d(TAG,"++++++ Started command onFailure: cmd "+cmd);
        execFFmpegBinary(cmd.split(" "),KEY_MERGE);
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
        new AlertDialog.Builder(VideoEffectActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VideoEffectActivity.this.finish();
                    }
                })
                .create()
                .show();
    }

    public  void addVideoToVideo(String linkvideo, String linkEffect){
        videoOutput =  FileUtil.getSlideVideo() + "/effect_video_"+System.currentTimeMillis()+".mp4";
        execFFmpegBinary(FFmpegCmdUtil.cmdAddVideoToVideo(linkvideo,linkEffect,videoOutput),KEY_CMD_ADD_EFFECT_VIDEO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoInput!=null && !TextUtils.isEmpty(videoInput)){
            contentUri=Uri.parse(videoInput);
            preparePlayer();
        }
    }

    private void execFFmpegBinary(final String[] command,final  int key) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    if (key==KEY_CMD_ADD_EFFECT_VIDEO){
                        Toast.makeText(VideoEffectActivity.this,"Add video effect failure",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSuccess(String s) {
                    if (key==KEY_CMD_ADD_EFFECT_VIDEO){
                        if (new File(videoOutput).exists()){
                            contentUri=Uri.parse(videoOutput);
                            preparePlayer();
                        }

                        if (new File(videoEffectMergePath).exists()){
                            FileUtil.deleteFile(videoEffectMergePath);
                        }
                    }else if (key==KEY_MERGE){
                        addVideoToVideo(videoInput, videoEffectMergePath);
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
                    if (!progressDialog.isShowing()) {
                        progressDialog.setMessage("Processing...");
                        progressDialog.show();
                    }
                }

                @Override
                public void onFinish() {
                    Logger.d(TAG, "Finished command : ffmpeg "+command);
                    if (key==KEY_CMD_ADD_EFFECT_VIDEO){
                        progressDialog.dismiss();
                    }
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
            case R.id.ll_video_effect_back:
                onBackPressed();
                break;
            case R.id.ll_video_effect_done:
                Intent intent =new Intent(VideoEffectActivity.this,FrameVideoActivity.class);
                if (videoOutput!=null && new File(videoOutput).exists()){
                    intent.putExtra(AppConfig.EXTRA_VIDEO_FRAME,videoOutput);
                }else {
                    intent.putExtra(AppConfig.EXTRA_VIDEO_FRAME,videoInput);
                }
                startActivity(intent);
                break;
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
                case WHAT_GET_ALL_EFFECT_VIDEO_SUCCESS:
                    if (progressDialog!=null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    iniVideoEffect();
                    break;
                case WHAT_GET_ALL_EFFECT_VIDEO_FAILURE:
                    if (progressDialog!=null && progressDialog.isShowing()){
                        progressDialog.dismiss();
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
                        if (!isFinishing() && (videoOutput==null || !new File(videoOutput).exists())) {
                            contentUri = Uri.parse(videoInput);
                            preparePlayer();
                        }else if(!isFinishing() && videoOutput!=null &&new File(videoOutput).exists()){
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
