package com.hiep.video.maker;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.hiep.video.maker.adapter.MyVideoAdapter;
import com.hiep.video.maker.entity.VideoEntity;
import com.hiep.video.maker.system.AppConfig;
import com.hiep.video.maker.util.FileUtil;
import com.hiep.video.maker.util.Logger;
import com.hiep.video.maker.util.Util;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Hiep on 7/14/2016.
 */
public class MyVideoActivity extends BaseActivity {
    private static final String TAG=MyVideoActivity.class.getSimpleName();
    private static final int WHAT_GET_ALL_MY_VIDEO_SUCCESS=100;
    private static final int WHAT_GET_ALL_MY_VIDEO_FAILURE=101;

    private GridView mGvMyVideo;

    private ArrayList<VideoEntity> listMyVideos;
    private MyVideoAdapter myVideoAdapter;

    private boolean isStartAddGif=false;
    private boolean isStartAddSticker=false;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_GET_ALL_MY_VIDEO_SUCCESS:
                    iniData();
                    break;
                case WHAT_GET_ALL_MY_VIDEO_FAILURE:

                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);
        if (getIntent()!=null){
            isStartAddGif=getIntent().getBooleanExtra(AppConfig.EXTRA_IS_ADD_GIF,false);
            if (!isStartAddGif){
                isStartAddSticker=getIntent().getBooleanExtra(AppConfig.EXTRA_IS_ADD_STICKER,false);
            }
        }
        getMyvideos();
        mGvMyVideo=(GridView)findViewById(R.id.gv_my_video);
    }

    private void getMyvideos(){
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
                             retriever.setDataSource(MyVideoActivity.this, Uri.fromFile(childFile));
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

    private void iniData(){
        myVideoAdapter=new MyVideoAdapter(getApplicationContext(),R.layout.video_item,listMyVideos);
        mGvMyVideo.setAdapter(myVideoAdapter);
        mGvMyVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (!isStartAddGif && !isStartAddSticker){
                    Intent intent =new Intent(MyVideoActivity.this,PreviewVideoActivity.class);
                    intent.putExtra(AppConfig.EXTRA_VIDEO_ENTITY,listMyVideos.get(i));
                    startActivity(intent);
                }else if (isStartAddGif &&!isStartAddSticker ){
                    Intent intent =new Intent(MyVideoActivity.this,AddGifActivity.class);
                    intent.putExtra(AppConfig.EXTRA_VIDEO_ENTITY,listMyVideos.get(i));
                    startActivity(intent);
                }else {
                    Intent intent =new Intent(MyVideoActivity.this,AddStickerActivity.class);
                    intent.putExtra(AppConfig.EXTRA_VIDEO_ENTITY,listMyVideos.get(i));
                    startActivity(intent);
                }

            }
        });
    }
}
