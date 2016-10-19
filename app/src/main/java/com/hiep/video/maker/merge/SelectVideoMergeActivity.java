package com.hiep.video.maker.merge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hiep.video.maker.BaseActivity;
import com.hiep.video.maker.R;
import com.hiep.video.maker.entity.VideoEntity;
import com.hiep.video.maker.system.AppConfig;
import com.hiep.video.maker.util.FileUtil;
import com.hiep.video.maker.util.Logger;
import com.hiep.video.maker.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hiep on 7/25/2016.
 */
public class SelectVideoMergeActivity extends BaseActivity {
    private static final String TAG=SelectVideoMergeActivity.class.getSimpleName();
    private static final int WHAT_GET_ALL_MY_VIDEO_SUCCESS=100;
    private static final int WHAT_GET_ALL_MY_VIDEO_FAILURE=101;
    private static final int WHAT_COPY_VIDEO=102;
    private GridView mGvAllVideo;
    private Button mBtnNext;
    private RecyclerView mRecyclerSelect;

    private ArrayList<VideoEntity> listMyVideos;
    private MergeShowAllVideoAdapter adapter;
    private ProgressDialog progressDialog=null;

    private List<VideoEntity> videoVideoSelect;
    private ListSelectVideoAdapter adapterVideoSelect;

    private ArrayList<VideoMergeEntity> listVideoMerge;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismisProgress();
            switch (msg.what){
                case WHAT_GET_ALL_MY_VIDEO_SUCCESS:
                    setData();
                    break;
                case WHAT_GET_ALL_MY_VIDEO_FAILURE:
                    Toast.makeText(SelectVideoMergeActivity.this,"Get all video failure",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    break;
                case WHAT_COPY_VIDEO:
                    if (listVideoMerge!=null && listVideoMerge.size()>1){
                        Intent intent=new Intent(SelectVideoMergeActivity.this,MergeVideoActivity.class);
                        intent.putExtra(AppConfig.EXTRA_VIDEO_MARGE,listVideoMerge);
                        startActivity(intent);
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video_merge);
        getMyvideos();
        iniUI();
    }

    @Override
    public void iniUI() {
        super.iniUI();
        mGvAllVideo=(GridView)findViewById(R.id.gv_my_video);
        mBtnNext=(Button)findViewById(R.id.btn_marge_next);
        mRecyclerSelect=(RecyclerView)findViewById(R.id.recycler_marge_video_select);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerSelect.setLayoutManager(linearLayoutManager);

        videoVideoSelect = new ArrayList<>();
        adapterVideoSelect=new ListSelectVideoAdapter(SelectVideoMergeActivity.this, videoVideoSelect);
        mRecyclerSelect.setAdapter(adapterVideoSelect);

        mBtnNext.setOnClickListener(this);
    }

    private void getMyvideos(){
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    File dir = new File(FileUtil.getMyVideo());
                    if (dir.isDirectory()){
                        listMyVideos=new ArrayList<>();
                        String[] children = dir.list();
                        for (int i = 0; i < children.length; i++){
                            try{
                                File childFile= new File(dir, children[i]);
                                VideoEntity videoEntity=new VideoEntity();
                                videoEntity.setId(i);
                                videoEntity.setCreateTime(childFile.lastModified());
                                Logger.d(TAG,"Create time: "+videoEntity.getCreateTime());
                                videoEntity.setFilePath(childFile.getAbsolutePath());
                                videoEntity.setFileName(childFile.getName());

                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                retriever.setDataSource(SelectVideoMergeActivity.this, Uri.fromFile(childFile));
                                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                Logger.d(TAG,"Duration time: "+time);
                                videoEntity.setDuration(Long.parseLong(time));
                                listMyVideos.add(videoEntity);
                            }catch (Exception e){
                                continue;
                            }
                        }
                        Util.sort(listMyVideos);
                        handler.sendEmptyMessage(WHAT_GET_ALL_MY_VIDEO_SUCCESS);
                    }else {
                        handler.sendEmptyMessage(WHAT_GET_ALL_MY_VIDEO_FAILURE);
                    }
                }catch (Exception e){
                    Logger.e(TAG,e.getMessage());
                    handler.sendEmptyMessage(WHAT_GET_ALL_MY_VIDEO_FAILURE);
                }
            }
        }).start();
    }

    private void setData(){
        adapter=new MergeShowAllVideoAdapter(SelectVideoMergeActivity.this,R.layout.video_merge_item_all_video,listMyVideos);
        mGvAllVideo.setAdapter(adapter);
        mGvAllVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (videoVideoSelect.size() <7){
                    videoVideoSelect.add(listMyVideos.get(i));
                    adapterVideoSelect.notifyDataSetChanged();
                }else {
                    Toast.makeText(SelectVideoMergeActivity.this,"Max 7 video",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_marge_next:
                clickMargeNext();
                break;
        }
    }

    private void clickMargeNext(){
        if (videoVideoSelect.size() >1){
            showProgress();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUtil.deleteFileInDir(FileUtil.getSlideVideo());
                    listVideoMerge=new ArrayList<>();
                    for (int i = 0; i < videoVideoSelect.size(); i++) {
                        String outputPath=FileUtil.getSlideVideo()+"/marge_"+i+".mp4";
                        String inputPath=videoVideoSelect.get(i).getFilePath();
                        FileUtil.copyFile(inputPath,outputPath);
                        listVideoMerge.add(new VideoMergeEntity(inputPath,outputPath));
                    }
                    handler.sendEmptyMessage(WHAT_COPY_VIDEO);
                }
            }).start();
        }else {
            Toast.makeText(SelectVideoMergeActivity.this,"Min 2 video",Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgress(){
        progressDialog=new ProgressDialog(this,R.style.StyledDialog);
        progressDialog.setMessage("Processing");
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
}
