package com.hiep.video.maker.merge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.exoplayer.ExoPlayer;
import com.hiep.video.maker.BaseActivity;
import com.hiep.video.maker.PreviewVideoActivity;
import com.hiep.video.maker.R;
import com.hiep.video.maker.entity.VideoEntity;
import com.hiep.video.maker.merge.rangebar.RangeBar;
import com.hiep.video.maker.player.DemoPlayer;
import com.hiep.video.maker.player.ExtractorRendererBuilder;
import com.hiep.video.maker.system.AppConfig;
import com.hiep.video.maker.util.FileUtil;
import com.hiep.video.maker.util.Logger;
import com.hiep.video.maker.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hiep on 7/25/2016.
 */
public class MergeVideoActivity extends BaseActivity implements DemoPlayer.Listener{
    private static final String TAG=MergeVideoActivity.class.getSimpleName();
    private final int HANDLE_NEXT_VIDEO = 1;
    private final int WHAT_INI_SUCCESS=2;
    private final int WHAT_COPY_AND_CUT_VIDEO=3;
    private final int WHAT_MERGE_VIDEO=4;
    private final int KEY_MERGE=1000;

    private Button mBtnMerge;
    private SurfaceView mSurfaceView;
    private ImageView mPlayerStatus;
    private ViewFlipper mVpListRanger;
    private RecyclerView mListVideo;

    private List<Item> mVideoPaths;
    private DemoPlayer player;
    private ListSelectVideoMergeAdapter adapterSelectVideoMerge;
    private ImageView[] ivFrames;
    private RangeBar[] rangeBars;

    private boolean mPlayerNeedPrepare = true;
    private int mIndex;
    private ProgressDialog progressDialog=null;

    private HashMap<String,ArrayList<Bitmap>> hashMapBitmap;
    private String outputMergeVideo;
    private FFmpeg ffmpeg;

    private DemoPlayer.RendererBuilder getRendererBuilder(Uri contentUri) {
        String userAgent = com.google.android.exoplayer.util.Util.getUserAgent(this, "ExoPlayerDemo");
        return new ExtractorRendererBuilder(this, userAgent, contentUri);
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == HANDLE_NEXT_VIDEO) {
                dismisProgress();
                if (mVideoPaths.size() > (mIndex + 1)) {
                    initVideoView(mIndex + 1);
                    changeConfig(mIndex);
                } else {
                    initVideoView(0);
                    changeConfig(0);
                }
            }else if (msg.what==WHAT_INI_SUCCESS){
                dismisProgress();
                hashMapBitmap.clear();
                hashMapBitmap=null;
                iniListener();
            }else if (msg.what==WHAT_COPY_AND_CUT_VIDEO){
                if (listKeyMerge.size()== mVideoPaths.size()){
                    Logger.d(TAG,"++++++cut video");
                    onMerge(listPathMerge);
                }
            }else if (msg.what==WHAT_MERGE_VIDEO){
                dismisProgress();
                File file=new File(outputMergeVideo);
                if (file.exists()){
                    Logger.d(TAG,"++++++Merge success");
                    gotoPreview(file);
                }else {
                    Logger.d(TAG,"++++++Merge failure");
                    Toast.makeText(MergeVideoActivity.this,"Merge failure",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_video);
        ffmpeg=FFmpeg.getInstance(MergeVideoActivity.this);
        loadFFMpegBinary();
        iniUI();
        iniData(getIntent());
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
        new AlertDialog.Builder(MergeVideoActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MergeVideoActivity.this.finish();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void iniUI() {
        super.iniUI();
        mBtnMerge=(Button)findViewById(R.id.btn_merge_video);
        mSurfaceView=(SurfaceView)findViewById(R.id.surface_view_merge);
        mPlayerStatus=(ImageView)findViewById(R.id.play_status_merge);
        mVpListRanger=(ViewFlipper)findViewById(R.id.vp_list_ranger_merge);
        mListVideo=(RecyclerView)findViewById(R.id.recycler_merge);
    }

    private void iniData(final Intent intent){
        hashMapBitmap=new HashMap<>();
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<VideoMergeEntity> listVideoMerge=(ArrayList<VideoMergeEntity>)intent.getSerializableExtra(AppConfig.EXTRA_VIDEO_MARGE);
                if (listVideoMerge == null)
                    finish();
                mVideoPaths = new ArrayList<Item>();
                // String[] arr = allPath.split(";");
                for (VideoMergeEntity entity : listVideoMerge) {
                    String path=entity.getFilePath();
                    String key=entity.getKey();

                    Item item = new Item();
                    item.setPath(path);
                    item.setStart(0);
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(MergeVideoActivity.this, Uri.fromFile(new File(path)));

                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInMillisec = Long.parseLong(time);

                    ArrayList<Bitmap> bms = hashMapBitmap.get(key);
                    if (bms == null){
                       // Logger.d("get image >> hashMapBitmap: "+hashMapBitmap.size()+" >> path: "+path+" >> key: "+key);
                        bms=new ArrayList<>();
                        long microSeconds=timeInMillisec*1000;
                        bms.add(retriever.getFrameAtTime((long)(microSeconds*0.1),MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                        bms.add(retriever.getFrameAtTime((long)(microSeconds*0.2),MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                        bms.add(retriever.getFrameAtTime((long)(microSeconds*0.3),MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                        bms.add(retriever.getFrameAtTime((long)(microSeconds*0.4),MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                        bms.add(retriever.getFrameAtTime((long)(microSeconds*0.5),MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                        bms.add(retriever.getFrameAtTime((long)(microSeconds*0.6),MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                        bms.add(retriever.getFrameAtTime((long)(microSeconds*0.7),MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                        bms.add(retriever.getFrameAtTime((long)(microSeconds*0.8),MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                        bms.add(retriever.getFrameAtTime((long)(microSeconds*0.9),MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                        bms.add(retriever.getFrameAtTime((long)(microSeconds-1000),MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                        hashMapBitmap.put(key,bms);
                    }

                    item.setEnd((timeInMillisec));
                    item.setMax((timeInMillisec));
                    item.setFrames(bms);

                    mVideoPaths.add(item);
                }
                mHandler.sendEmptyMessage(WHAT_INI_SUCCESS);
            }
        }).start();
    }

    private void iniListener(){
        mSurfaceView.setOnClickListener(this);
        mBtnMerge.setOnClickListener(this);

        mListVideo.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mListVideo.setLayoutManager(linearLayoutManager);
        mVideoPaths.get(0).setSelect(true);
        adapterSelectVideoMerge =new ListSelectVideoMergeAdapter(MergeVideoActivity.this, mVideoPaths, new OnSelectedVideoListener() {
            @Override
            public void actionSelected(int index) {
                changeConfig(index);
                initVideoView(index);
            }
        });

        mListVideo.setAdapter(adapterSelectVideoMerge);

        ivFrames=new ImageView[10];
        ivFrames[0]=(ImageView)findViewById(R.id.ivFrame00);
        ivFrames[1]=(ImageView)findViewById(R.id.ivFrame01);
        ivFrames[2]=(ImageView)findViewById(R.id.ivFrame02);
        ivFrames[3]=(ImageView)findViewById(R.id.ivFrame03);
        ivFrames[4]=(ImageView)findViewById(R.id.ivFrame04);
        ivFrames[5]=(ImageView)findViewById(R.id.ivFrame05);
        ivFrames[6]=(ImageView)findViewById(R.id.ivFrame06);
        ivFrames[7]=(ImageView)findViewById(R.id.ivFrame07);
        ivFrames[8]=(ImageView)findViewById(R.id.ivFrame08);
        ivFrames[9]=(ImageView)findViewById(R.id.ivFrame09);

        for (ImageView iv: ivFrames){
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        setBitmapFrame(mVideoPaths.get(0).getFrames());
        iniRangerBar();

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                //Logger.d(TAG, "surfaceCreated");
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                //Logger.d(TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                //Logger.d(TAG, "surfaceDestroyed");
            }
        });
        initVideoView(0);
    }

    private void setBitmapFrame(ArrayList<Bitmap> bms){
        if (bms==null)return;
        for (int i=0;i<ivFrames.length;i++){
            Bitmap bm=bms.get(i);
            if (bm!=null){
                ivFrames[i].setImageBitmap(bm);
            }
        }
    }

    private void changeConfig(int index){
        setBitmapFrame(mVideoPaths.get(index).getFrames());
        mVpListRanger.setDisplayedChild(index);
        adapterSelectVideoMerge.setSelectVideo(index);
    }

    private long timeStart=0;
    private void iniRangerBar(){
        rangeBars=new RangeBar[mVideoPaths.size()];
        mVpListRanger.removeAllViews();
        for (int i=0;i<mVideoPaths.size();i++){
            View v = getLayoutInflater().inflate(R.layout.item_ranger_bar_merge, null);
            mVpListRanger.addView(v);
            rangeBars[i]=(RangeBar)v;
        }

        timeStart = System.currentTimeMillis();

        for (int i=0;i<mVideoPaths.size();i++){
            final Item item=mVideoPaths.get(i);
            final int index=i;
            float max=item.getMax() / 100;

            rangeBars[i].setTickCount((int)max+1);
            rangeBars[i].setConnectingLineColor(Color.parseColor("#66F5A623"));
            rangeBars[i].setBarColor(Color.TRANSPARENT);

            rangeBars[i].setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
                @Override
                public void onIndexChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex) {
                    //Logger.d(TAG,"++++> up");
                    if (leftPinIndex > rightPinIndex ){
                        item.setStart(leftPinIndex * 100);
                    }

                    if (leftPinIndex == item.getMax() / 100) {
                        item.setStart(item.getMax());
                    } else{
                        item.setStart(leftPinIndex * 100);
                    }

                    if (rightPinIndex == item.getMax() / 100) {
                        item.setEnd(item.getMax());
                    } else {
                        item.setEnd(rightPinIndex * 100);
                    }
                    upSeekVideo(index);
                }

                @Override
                public void onMove(RangeBar rangeBar, int leftPinIndex, int rightPinIndex) {
                    //Logger.d(TAG,"++++> onMove");
                    if (leftPinIndex > rightPinIndex ){
                        item.setStart(leftPinIndex * 100);
                    }

                    if (leftPinIndex == item.getMax() / 100) {
                        item.setStart(item.getMax());
                    } else{
                        item.setStart(leftPinIndex * 100);
                    }

                    if (rightPinIndex == item.getMax() / 100) {
                        item.setEnd(item.getMax());
                    } else {
                        item.setEnd(rightPinIndex * 100);
                    }
                    long delta = System.currentTimeMillis()- timeStart;

                    if(delta>500) {
                        timeStart = System.currentTimeMillis();
                        moveSeekToVideo(index);
                    }
                }

                @Override
                public void onWarning() {
                    Toast.makeText(MergeVideoActivity.this,"Không thể cắt ngắn hơn nữa !",Toast.LENGTH_SHORT).show();
                }
            });
        }
        mVpListRanger.setDisplayedChild(0);
    }

    private void initVideoView(final int index) {
        if (player != null)
            player.release();
        mHandler.removeMessages(HANDLE_NEXT_VIDEO);
        mIndex = index;
        final Item item = mVideoPaths.get(index);
        if (item.getStart() == item.getEnd()) {
            mHandler.sendEmptyMessageDelayed(HANDLE_NEXT_VIDEO, 0);
            return;
        }
        player = new DemoPlayer(getRendererBuilder(Uri.parse(item.getPath())));
        player.addListener(this);
        player.seekTo(item.getStart());
        if (player != null && mPlayerNeedPrepare == true)
            player.prepare();

        player.setSurface(mSurfaceView.getHolder().getSurface());
        player.setPlayWhenReady(true);
        mHandler.sendEmptyMessageDelayed(HANDLE_NEXT_VIDEO, item.getEnd() - item.getStart());
    }

    private boolean mVideoPaused = false;
    private long currentPostion=0;

    private void pauseResumeVideo() {
        if (player != null && player.getPlayerControl() != null) {
            if (player.getPlayerControl().isPlaying()) {
                player.getPlayerControl().pause();
                currentPostion=player.getCurrentPosition();
                mPlayerStatus.setVisibility(View.VISIBLE);
                mHandler.removeMessages(HANDLE_NEXT_VIDEO);
            } else {
                player.getPlayerControl().start();
                mPlayerStatus.setVisibility(View.INVISIBLE);
                resumeVideo();
            }
        }
        mVideoPaused = !mVideoPaused;
    }

    private void resumeVideo(){
        if (currentPostion>0){
            if (player!=null){
                mPlayerStatus.setVisibility(View.INVISIBLE);
                mVideoPaused=false;
                if (!player.getPlayerControl().isPlaying()) {
                    player.getPlayerControl().start();

                }
                final Item item = mVideoPaths.get(mIndex);
                player.seekTo(currentPostion);
                player.prepare();
                mHandler.sendEmptyMessageDelayed(HANDLE_NEXT_VIDEO, item.getEnd() - currentPostion);
            }
        }
    }

    private void moveSeekToVideo(final int index){
        mHandler.removeMessages(HANDLE_NEXT_VIDEO);
        if (player!=null){
            if (player.getPlayerControl().isPlaying()) {
                player.getPlayerControl().pause();
                mPlayerStatus.setVisibility(View.VISIBLE);
                mVideoPaused=true;
            }

            mIndex = index;
            final Item item = mVideoPaths.get(index);
            player.seekTo(item.getStart());
        }

    }

    private void upSeekVideo(final int index){
        mHandler.removeMessages(HANDLE_NEXT_VIDEO);
        if (player!=null){
            if (!player.getPlayerControl().isPlaying()) {
                player.getPlayerControl().start();
                mPlayerStatus.setVisibility(View.INVISIBLE);
                mVideoPaused=false;
            }

            mIndex = index;
            final Item item = mVideoPaths.get(index);
            player.seekTo(item.getStart());
            player.prepare();
            mHandler.sendEmptyMessageDelayed(HANDLE_NEXT_VIDEO, item.getEnd() - item.getStart());
        }

    }


    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                break;
            case ExoPlayer.STATE_ENDED:
                if (mVideoPaths.size() > (mIndex + 1)) {
                    initVideoView(mIndex + 1);
                    changeConfig(mIndex);
                } else {
                    initVideoView(0);
                    changeConfig(0);
                }
                break;
            case ExoPlayer.STATE_IDLE:
                break;
            case ExoPlayer.STATE_PREPARING:
                break;
            case ExoPlayer.STATE_READY:
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(Exception e) {
        mPlayerNeedPrepare = true;
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentPostion>0){
            if (player!=null){
                mPlayerStatus.setVisibility(View.INVISIBLE);
                mVideoPaused=false;
                if (!player.getPlayerControl().isPlaying()) {
                    player.getPlayerControl().start();

                }
                final Item item = mVideoPaths.get(mIndex);
                player.seekTo(currentPostion);
                player.prepare();
                mHandler.sendEmptyMessageDelayed(HANDLE_NEXT_VIDEO, item.getEnd() - currentPostion);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.surface_view_merge:
                pauseResumeVideo();
                break;
            case R.id.btn_merge_video:
                clickMerge();
                break;

        }
    }
    private ArrayList<Integer> listKeyMerge=new ArrayList<>();
    private ArrayList<String> listPathMerge=new ArrayList<>();

    private void clickMerge(){
        if (player != null)
            player.release();
        mHandler.removeMessages(HANDLE_NEXT_VIDEO);
        showProgress();

        for (int i = 0; i < mVideoPaths.size(); i++) {
            Item item = mVideoPaths.get(i);
            String tempPath = FileUtil.getSlideVideo() + "/merge_temp_" + System.currentTimeMillis() + ".mp4";
            if (item.getStart() == 0 && item.getEnd() == item.getMax()) {
                String cmdCopy="-i " + item.getPath() + " -c copy -y " + tempPath;
                execFFmpegBinary(cmdCopy.split(" "),i);
            } else {
                String cmd="-ss " + Util.formatDuration2((int)  mVideoPaths.get(0).getStart()) + " -i "+mVideoPaths.get(0).getPath() +" -t " + Util.formatDuration2((int)  mVideoPaths.get(0).getEnd()) + " -c copy -copyts " + tempPath;
                execFFmpegBinary(cmd.split(" "),i);
            }
            listPathMerge.add(tempPath);
        }

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
        outputMergeVideo = FileUtil.getMyVideo() + "/merge-"+System.currentTimeMillis()+".mp4";
        String cmd="-f concat -safe 0 -i " + FileUtil.getSlideVideo() + "/input.txt -c copy -y " + outputMergeVideo;
        Logger.d(TAG,"++++++ Started command onFailure: cmd "+cmd);
        execFFmpegBinary(cmd.split(" "),KEY_MERGE);
    }

    private void gotoPreview(File file){
        VideoEntity videoEntity =new VideoEntity();
        videoEntity.setId(0);
        videoEntity.setCreateTime(file.lastModified());
        Logger.d(TAG,"Create time: "+videoEntity.getCreateTime());
        videoEntity.setFilePath(file.getAbsolutePath());
        videoEntity.setFileName(file.getName());

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(MergeVideoActivity.this, Uri.fromFile(file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Logger.d(TAG,"Duration time: "+time);
        videoEntity.setDuration(Long.parseLong(time));

        Intent intent=new Intent(MergeVideoActivity.this,PreviewVideoActivity.class);
        intent.putExtra(AppConfig.EXTRA_VIDEO_ENTITY,videoEntity);
        startActivity(intent);
    }


    private void execFFmpegBinary(final String[] command,final  int key) {

        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Logger.d(TAG,"Started command onFailure: ffmpeg "+s);
                }

                @Override
                public void onSuccess(String s) {
                    Logger.d(TAG,"Started command onSuccess: ffmpeg "+s);
                }

                @Override
                public void onProgress(String s) {

                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    Logger.d(TAG,"Started command onSuccess: onFinish >> key: "+key);
                    if (key < mVideoPaths.size()){
                        listKeyMerge.add(key);
                        mHandler.sendEmptyMessage(WHAT_COPY_AND_CUT_VIDEO);
                    }else if (key==KEY_MERGE){
                        mHandler.sendEmptyMessage(WHAT_MERGE_VIDEO);
                    }
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }


    private void showProgress(){
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismisProgress(){
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        if (player.getPlayerControl().isPlaying()) {
            player.getPlayerControl().pause();
            currentPostion=player.getCurrentPosition();
            mPlayerStatus.setVisibility(View.VISIBLE);
            mVideoPaused=true;
        }
        mHandler.removeMessages(HANDLE_NEXT_VIDEO);

        super.onPause();
    }

    public class Item {
        private long start;
        private long end;
        private long max;
        private String path;
        private boolean isSelect=false;
        private ArrayList<Bitmap> frames;

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        public long getMax() {
            return max;
        }

        public void setMax(long max) {
            this.max = max;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public ArrayList<Bitmap> getFrames() {
            return frames;
        }

        public void setFrames(ArrayList<Bitmap> frames) {
            this.frames = frames;
        }
    }
}
