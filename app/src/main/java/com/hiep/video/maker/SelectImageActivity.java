package com.hiep.video.maker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hiep.video.maker.gallery.GalleryFragment;
import com.hiep.video.maker.merge.SelectVideoMergeActivity;
import com.hiep.video.maker.system.AppConfig;
import com.hiep.video.maker.system.Config;
import com.hiep.video.maker.ui.EditorActivity;
import com.hiep.video.maker.util.FileUtil;
import com.hiep.video.maker.widget.GifImageView;

import java.util.ArrayList;

/**
 * Created by hiep on 6/20/2016.
 */
public class SelectImageActivity extends FragmentActivity implements View.OnClickListener{
    private static final String TAG=SelectImageActivity.class.getSimpleName();
    private Button mBtnSelectImage;
    private Button mBtnMyVideo;
    private Button mBtnMergeVideo;
    private Button mBtnAddGif;
    private Button mBtnAddSticker;

    private GalleryFragment galleryFragment;
    private ProgressDialog progressDialog;

    public static final String EXTAR_PHOTO_PATH_LIST = "photo_id_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.init(SelectImageActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtil.createVideoFolders();
                FileUtil.copyVideoEffect();
            }
        }).start();
        setContentView(R.layout.activity_select_image);
        iniUI();
       // getSongListFromStorage();
    }
    private void iniUI(){
        mBtnSelectImage=(Button)findViewById(R.id.btn_select_image);
        mBtnMyVideo=(Button)findViewById(R.id.btn_my_video);
        mBtnMergeVideo=(Button)findViewById(R.id.btn_merge_video);
        mBtnAddGif=(Button)findViewById(R.id.btn_add_gif);
        mBtnAddSticker=(Button)findViewById(R.id.btn_add_sticker);

        mBtnSelectImage.setOnClickListener(this);
        mBtnMyVideo.setOnClickListener(this);
        mBtnMergeVideo.setOnClickListener(this);
        mBtnAddGif.setOnClickListener(this);
        mBtnAddSticker.setOnClickListener(this);

        this.galleryFragment = (GalleryFragment) getSupportFragmentManager().findFragmentByTag("myFragmentTag");
        if (this.galleryFragment != null) {
            this.galleryFragment.setGalleryListener(createGalleryListener());
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(null);
    }

    private long[] idImageGalleries;
    GalleryFragment.GalleryListener createGalleryListener() {
        return new GalleryFragment.GalleryListener() {
            public void onGalleryCancel() {
                SelectImageActivity.this.getSupportFragmentManager().beginTransaction().hide(SelectImageActivity.this.galleryFragment).commitAllowingStateLoss();
            }

            public void onGalleryOkImageArray(long[] paramArrayOfLong, int[] paramArrayOfInt, boolean paramBoolean) {
                idImageGalleries=paramArrayOfLong;
                copyFileImage();
            }

            public void onGalleryOkImageArrayRemoveFragment(long[] paramArrayOfLong, int[] paramArrayOfInt, boolean paramBoolean) {
            }

            public void onGalleryOkSingleImage(long paramLong, int paramInt, boolean paramBoolean) {
            }
        };
    }

    private void copyFileImage(){

        FileUtil.deleteFileInDir(FileUtil.getImageInput());

        ArrayList<String> pathImageGallery=new ArrayList<>();
        if (idImageGalleries!=null && idImageGalleries.length>0){
            for (long id : idImageGalleries){
                Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(id));
                String path= FileUtil.getRealPathFromURI(SelectImageActivity.this,uri);
                if (path!=null){

                    Log.d(TAG,"----> path: "+path);
                    pathImageGallery.add(path);
                }
            }
        }

        FileUtil.deleteFileInDir(FileUtil.getSlideVideo());

        SelectImageActivity.this.getSupportFragmentManager().beginTransaction().hide(SelectImageActivity.this.galleryFragment).commitAllowingStateLoss();
        Intent localIntent = new Intent(SelectImageActivity.this, EditorActivity.class);
        localIntent.putExtra(EXTAR_PHOTO_PATH_LIST,pathImageGallery);
        SelectImageActivity.this.startActivity(localIntent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_select_image:
                addGalleryFragment();
                break;
            case R.id.btn_my_video:
                startActivity(new Intent(SelectImageActivity.this,MyVideoActivity.class));
                break;
            case R.id.btn_merge_video:
                startActivity(new Intent(SelectImageActivity.this,SelectVideoMergeActivity.class));
                break;
            case R.id.btn_add_gif:
                Intent intent=new Intent(SelectImageActivity.this,MyVideoActivity.class);
                intent.putExtra(AppConfig.EXTRA_IS_ADD_GIF,true);
                startActivity(intent);
                break;
            case R.id.btn_add_sticker:
                Intent intentSticker=new Intent(SelectImageActivity.this,MyVideoActivity.class);
                intentSticker.putExtra(AppConfig.EXTRA_IS_ADD_STICKER,true);
                startActivity(intentSticker);
                break;
        }
    }
    public void addGalleryFragment() {
        FragmentManager fm = getSupportFragmentManager();
        this.galleryFragment = (GalleryFragment) fm.findFragmentByTag("myFragmentTag");
        if (this.galleryFragment == null) {
            this.galleryFragment = new GalleryFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fl_gallery, this.galleryFragment, "myFragmentTag");
            ft.commitAllowingStateLoss();
            this.galleryFragment.setGalleryListener(createGalleryListener());
            findViewById(R.id.fl_gallery).bringToFront();
            return;
        }
        getSupportFragmentManager().beginTransaction().show(this.galleryFragment).commitAllowingStateLoss();
    }

//    public void getSongListFromStorage() {
//        ArrayList<SongEntity> listSongs=new ArrayList<>();
//        //retrieve song info
//        ContentResolver musicResolver = getContentResolver();
//        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
//
//        if (musicCursor != null && musicCursor.moveToFirst()) {
//            //get columns
//            int titleColumn = musicCursor.getColumnIndex
//                    (android.provider.MediaStore.Audio.Media.TITLE);
//            int idColumn = musicCursor.getColumnIndex
//                    (android.provider.MediaStore.Audio.Media._ID);
//            int artistColumn = musicCursor.getColumnIndex
//                    (android.provider.MediaStore.Audio.Media.ARTIST);
//            //add songs to list
//            do {
//                long thisId = musicCursor.getLong(idColumn);
//                String thisTitle = musicCursor.getString(titleColumn);
//                String thisArtist = musicCursor.getString(artistColumn);
//                listSongs.add(new SongEntity(thisId, thisTitle, thisArtist,""));
//            } while (musicCursor.moveToNext());
//        }
//
//        Logger.d(TAG,"listSongs: "+listSongs.size());
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
