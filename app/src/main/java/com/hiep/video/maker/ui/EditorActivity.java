package com.hiep.video.maker.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hiep.video.maker.BaseActivity;
import com.hiep.video.maker.MainActivity;
import com.hiep.video.maker.R;
import com.hiep.video.maker.SelectImageActivity;
import com.hiep.video.maker.asyncloader.IDoBackGround;
import com.hiep.video.maker.asyncloader.IHandler;
import com.hiep.video.maker.mirror.PromoMirrorView;
import com.hiep.video.maker.system.Config;
import com.hiep.video.maker.ui.adapter.MyAdapter;
import com.hiep.video.maker.ui.edit.StickerFragment;
import com.hiep.video.maker.ui.edit.entity.ItemView;
import com.hiep.video.maker.ui.widget.StickerViewEdit;
import com.hiep.video.maker.util.BitmapUtil;
import com.hiep.video.maker.util.FileUtil;
import com.hiep.video.maker.util.LayoutUtil;
import com.hiep.video.maker.util.Logger;
import com.hiep.video.maker.util.UtiLibs;

import java.util.ArrayList;


/**
 * Created by hiep on 6/10/2016.
 */
public class EditorActivity extends BaseActivity {
    private static final String TAG = EditorActivity.class.getSimpleName();
    public static final int TAB_SIZE = 10;
    public static final int FILTER_RESULT = 1000;
    private RelativeLayout mContentMainALl, mContentRootView;
    private FrameLayout frameLayoutMirror;
    private PromoMirrorView mirrorView;

    private StickerFragment stickerFragment;
    private TextFragment textFragment;
    private BackgroundFragment backgroundFragment;
    private FilterFragment filterFragment;

    private StickerViewEdit mCurrentView;

    private boolean checkSave = false;
    private String filePathNew = "";
    private String filePathSave="";
    private Bitmap bitmapMain;
    private Bitmap bitmapBlur;

    private int posStyle = 0;
    //////////////UI
    private LinearLayout llSave,llBackMain;
    private TextView mTvCountFrame;
    private ImageView ivBackgroundBlur;
    private View[] viewMenuBottomList;
    private ViewFlipper mViewFlipperEditor;

    private LinearLayout mLlListStyle;
    private View mViewOLdStyle;
    private RecyclerView recyclerView_Border,recyclerView_Filter;

    private ImageView mBorderImage,mFlterImage;
    private SeekBar mSbSize,mSbBlur;

    private int indexFrame=0;
    private ArrayList<String> listPathFrames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        iniUI();
        iniEditor();
    }

    @Override
    public void iniUI() {
        llSave=(LinearLayout)findViewById(R.id.llSave);
        llSave.setOnClickListener(this);
        mTvCountFrame=(TextView)findViewById(R.id.tv_frame_count);
        llBackMain=(LinearLayout)findViewById(R.id.llBackMain);
        llBackMain.setOnClickListener(this);

        frameLayoutMirror=(FrameLayout)findViewById(R.id.frameLayoutMirror);
        mContentMainALl = (RelativeLayout) findViewById(R.id.rlMainALl);
        mContentMainALl.getLayoutParams().width = Config.SCREENWIDTH;
        mContentMainALl.getLayoutParams().height = Config.SCREENWIDTH;

        mContentRootView = (RelativeLayout) findViewById(R.id.rlMain);

        mViewFlipperEditor = (ViewFlipper) this.findViewById(R.id.view_flipper_editor);
        mViewFlipperEditor.setDisplayedChild(7);

        mLlListStyle=(LinearLayout)findViewById(R.id.llListStyle);
        recyclerView_Border=(RecyclerView)findViewById(R.id.recyclerView_Border);
        //recyclerView_Filter=(RecyclerView)findViewById(R.id.recyclerView_Filter);

        ivBackgroundBlur=(ImageView)findViewById(R.id.ivBackgroundBlur);
        mSbSize=(SeekBar)findViewById(R.id.sb_size);
        mSbBlur=(SeekBar)findViewById(R.id.sb_blur);

        addAdmobBaner(R.id.llBottomAds);
        //showAdmobFullScreend();;
    }

    private void iniEditor(){
        getData();
        addStyleMirror();
        blurBackground();
        initBackgroundUI();
        initFilterUI();
        //addFilter();
        addBorder();
        initEmojiUI();
        initAddTextUI();
    }

    public void clearViewFlipper() {
        this.mViewFlipperEditor.setDisplayedChild(7);
        this.setTabMenuBottom(-1);
    }

    private void setTabMenuBottom(int tab) {
        if (this.viewMenuBottomList == null) {
            this.viewMenuBottomList = new View[TAB_SIZE];
            this.viewMenuBottomList[0] = this.findViewById(R.id.iv_mirror);
            this.viewMenuBottomList[1] = this.findViewById(R.id.iv_blur);
            this.viewMenuBottomList[2] = this.findViewById(R.id.iv_filter);
            this.viewMenuBottomList[3] = this.findViewById(R.id.iv_editor);
            this.viewMenuBottomList[4] = this.findViewById(R.id.iv_background);
            this.viewMenuBottomList[5] = this.findViewById(R.id.iv_border);
            this.viewMenuBottomList[6] = this.findViewById(R.id.iv_emoji);
            this.viewMenuBottomList[7] = this.findViewById(R.id.iv_addtext);
            this.viewMenuBottomList[8] = this.findViewById(R.id.iv_flip);
            this.viewMenuBottomList[9] = this.findViewById(R.id.iv_rotate);
        }

        for (int i = 0; i < this.viewMenuBottomList.length; ++i) {
            this.viewMenuBottomList[i].setBackgroundResource(R.color.app_square);
        }

        if (tab >= 0) {
            this.viewMenuBottomList[tab].setBackgroundResource(R.color.collage_purple);
        }
    }

    private void getData() {
        Intent mIntent = getIntent();
        if (mIntent != null) {
            listPathFrames=(ArrayList<String>)mIntent.getSerializableExtra(SelectImageActivity.EXTAR_PHOTO_PATH_LIST);
            if (listPathFrames!=null){
                nextFrame();
            }else {
                finish();
            }
        }else {
            finish();
        }
    }

    private void nextFrame(){
        filePathNew = listPathFrames.get(indexFrame);
        Logger.d(indexFrame+". filePathNew: "+filePathNew);
        if (filePathNew != null) {
            addBackground(filePathNew);
        }
        indexFrame++;
        mTvCountFrame.setText(indexFrame+"/"+listPathFrames.size());
    }

    FrameLayout.LayoutParams layoutMirrorParams;
    private void addBackground(String filePath) {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap =BitmapUtil.resampleImage(filePath, Config.SCREENWIDTH);
        if (bitmap != null) {
            myApplication.setBitmapOld(bitmap);
            // Add MirrorView to RootView
            layoutMirrorParams = new FrameLayout.LayoutParams(Config.SCREENWIDTH-200,Config.SCREENWIDTH-200);
            layoutMirrorParams.leftMargin = 0;
            layoutMirrorParams.topMargin = 0;
            layoutMirrorParams.gravity = Gravity.CENTER;
            frameLayoutMirror.setLayoutParams(layoutMirrorParams);

            bitmapMain = bitmap;

            addMirrorToLayout(bitmapMain,0);
            ivBackgroundBlur.setImageBitmap(bitmapMain);

            mFlterImage= LayoutUtil.createImageView(this, 0, 0, Config.SCREENWIDTH, Config.SCREENWIDTH);
            mContentRootView.addView(mFlterImage);
            mBorderImage = LayoutUtil.createImageView(this, 0, 0, Config.SCREENWIDTH, Config.SCREENWIDTH);
            mContentRootView.addView(mBorderImage);


            mSbSize.setMax(Config.SCREENWIDTH);
            mSbSize.setProgress(Config.SCREENWIDTH-200);
            mSbSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress%2!=0){
                        progress=progress+1;
                    }
                    layoutMirrorParams.height = progress;
                    layoutMirrorParams.width = progress;
                    frameLayoutMirror.setLayoutParams(layoutMirrorParams);
                    frameLayoutMirror.invalidate();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } else {
            Toast.makeText(EditorActivity.this, "Images error", Toast.LENGTH_SHORT).show();
        }
    }

    public void addMirrorToLayout(Bitmap bm,int mode){
        mirrorView=new PromoMirrorView(this, Config.SCREENWIDTH,Config.SCREENWIDTH, bm, mode);
        mirrorView.setIsClick(false);
        mirrorView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        frameLayoutMirror.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        frameLayoutMirror.addView(mirrorView, layoutParams);
        mirrorView.post(new Runnable() {
            public void run() {
                mirrorView.startAnimator();
            }
        });
    }


    ////ADD UI style mirror
    private void addStyleMirror(){
        int height_rec = (Config.SCREENHEIGHT / 2 - Config.SCREENWIDTH / 2) / 2;
        for (int i = 0; i < 9; i++) {
            final int k = i;
            final View vItem = View.inflate(this, R.layout.item_style_mirror, null);
            RelativeLayout mainIcon = (RelativeLayout) vItem.findViewById(R.id.mainicon);

            final ImageView imgBg = (ImageView) vItem.findViewById(R.id.imgBg);
            imgBg.getLayoutParams().width = Config.SCREENWIDTH / 6;
            imgBg.getLayoutParams().height = height_rec;


            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.height = Config.SCREENWIDTH / 7;
            lp.width = Config.SCREENWIDTH / 7;
            lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            PromoMirrorView mirrorViewTmp = new PromoMirrorView(this, Config.SCREENWIDTH / 6, Config.SCREENWIDTH / 6, bitmapMain, k);
            mirrorViewTmp.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            mirrorViewTmp.setIsClick(false);
            mirrorViewTmp.setClickable(false);
            mainIcon.addView(mirrorViewTmp);


            mirrorViewTmp.setLayoutParams(lp);

            View mView = new View(this);
            mainIcon.addView(mView);
            mView.getLayoutParams().height = height_rec + height_rec / 3;
            mView.getLayoutParams().width = Config.SCREENWIDTH / 6;
            mView.setBackgroundColor(Color.TRANSPARENT);
            mView.setClickable(true);

            if (k == 0) {
                mViewOLdStyle = vItem;
                ((ImageView) mViewOLdStyle.findViewById(R.id.imgBg)).setBackgroundColor(getResources().getColor(R.color.collage_purple));
            }

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mViewOLdStyle == null)
                        mViewOLdStyle = vItem;
                    else if (mViewOLdStyle == vItem)
                        return;
                    ((ImageView) mViewOLdStyle.findViewById(R.id.imgBg)).setBackgroundColor(getResources().getColor(R.color.trgb_262626));
                    mViewOLdStyle = vItem;
                    ((ImageView) mViewOLdStyle.findViewById(R.id.imgBg)).setBackgroundColor(getResources().getColor(R.color.collage_purple));

                    if (bitmapMain != null) {
                        handlerDoWork(new IHandler() {
                            @Override
                            public void doWork() {
                                posStyle = k;
                                mirrorView.setCurrentModeIndex(k);
                                mirrorView.invalidate();
                            }
                        });
                    }
                }
            });

            mLlListStyle.addView(vItem);
        }
        mLlListStyle.invalidate();
    }
//    // Add UI filter
//    int[] iconFilterlist;
//    private void addFilter(){
//        iconFilterlist=new int[]{
//                R.drawable.icon_none,R.drawable.ic_boke26,R.drawable.ic_boke21,R.drawable.ic_boke27, R.drawable.ic_boke30,
//                R.drawable.ic_boke28,R.drawable.ic_boke22, R.drawable.ic_boke29,R.drawable.ic_boke23,R.drawable.ic_boke24,
//                R.drawable.ic_boke25,R.drawable.ic_boke1,R.drawable.ic_boke2,R.drawable.ic_boke3,R.drawable.ic_boke4,
//                R.drawable.ic_boke5,R.drawable.ic_boke6,R.drawable.ic_boke7,R.drawable.ic_boke8,
//                R.drawable.ic_boke9,R.drawable.ic_boke10,R.drawable.ic_boke11,R.drawable.ic_boke12,
//                R.drawable.ic_boke13,R.drawable.ic_boke14,R.drawable.ic_boke15,R.drawable.ic_boke16,
//                R.drawable.ic_boke17,R.drawable.ic_boke18,R.drawable.ic_boke19,R.drawable.ic_boke20
//        };
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
//        int colorFlipper = this.getResources().getColor(R.color.trgb_262626);
//        int colorBtnFooter = this.getResources().getColor(R.color.collage_purple);
//        MyAdapter collageAdapter= new MyAdapter(iconFilterlist, new MyAdapter.CurrentCollageIndexChangedListener() {
//            public void onIndexChanged(int index) {
//                Logger.d(TAG,"---- filter >> index: "+index);
//                if (index>0){
//                    mFlterImage.setImageResource(iconFilterlist[index]);
//                    mFlterImage.setAlpha(0.1f);
//                }else if (index==0){
//                    mFlterImage.setImageResource(android.R.color.transparent);
//                }
//            }
//        }, colorFlipper, colorBtnFooter, false, true);
//        recyclerView_Filter.setLayoutManager(linearLayoutManager);
//        recyclerView_Filter.setAdapter(collageAdapter);
//        recyclerView_Filter.setItemAnimator(new DefaultItemAnimator());
//    }

    /// Blur
    private void blurBackground(){
        mSbBlur.setMax(100);
        mSbBlur.setProgress(0);
        mSbBlur.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getId() == R.id.sb_blur) {
                    float blur = (float) seekBar.getProgress();
                    updateBlur(blur);
                }
            }
        });
    }
    private void updateBlur(final  float blur){
        showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (blur>0){
                    //bitmapBlur = BlurBitmap.blurNatively(bitmapMain,(int) blur, false);
                    handler.sendEmptyMessage(0);
                }else {
                    handler.sendEmptyMessage(1);
                }
            }
        }).start();
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dismissLoading();
            switch (msg.what){
                case 0:
                    if (bitmapBlur!=null)
                    ivBackgroundBlur.setImageBitmap(bitmapBlur);
                    break;
                case 1:
                    ivBackgroundBlur.setImageBitmap(bitmapMain);
                    break;
            }
        }
    };

    /// Add UI boder
    int[] iconBorderlist;
    private void addBorder(){
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
                Logger.d(TAG,"---- filter >> index: "+index);
                if (index>0){
                    Glide.with(EditorActivity.this).load(iconBorderlist[index]).asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                }

                                @Override
                                public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                                    mBorderImage.setImageBitmap(arg0);                    }
                            });
                }else if (index==0){
                    mBorderImage.setImageResource(android.R.color.transparent);
                }
            }
        }, colorFlipper, colorBtnFooter, false, true);

        recyclerView_Border.setLayoutManager(linearLayoutManager);
        recyclerView_Border.setAdapter(collageAdapter);
        recyclerView_Border.setItemAnimator(new DefaultItemAnimator());
    }
    // Add UI Background
    public void initBackgroundUI() {
        backgroundFragment = new BackgroundFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.frame_background, backgroundFragment, "background");
        fragmentTransaction.hide(backgroundFragment);
        fragmentTransaction.commit();
    }

    public void changeBackgroundState() {
        if (!backgroundFragment.getShowFragment()){
            backgroundFragment = (BackgroundFragment) getSupportFragmentManager().findFragmentByTag("background");
            backgroundFragment.show();
        }else {
            return;
        }
    }
    // Add UI Filter
    public void initFilterUI() {
        filterFragment = new FilterFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.frame_filter, filterFragment, "Filter");
        fragmentTransaction.hide(filterFragment);
        fragmentTransaction.commit();
    }

    public void changeFilterState() {
        if (!filterFragment.getShowFragment()){
            filterFragment = (FilterFragment) getSupportFragmentManager().findFragmentByTag("Filter");
            filterFragment.show();
        }else {
            return;
        }
    }
    // Add UI Emoji
    public void initEmojiUI() {
        stickerFragment = new StickerFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.frame_emoji, stickerFragment, "Emoji");
        fragmentTransaction.hide(stickerFragment);
        fragmentTransaction.commit();
    }

    public void changeStickerState() {
        if (!stickerFragment.getShowFragment()){
            stickerFragment = (StickerFragment) getSupportFragmentManager().findFragmentByTag("Emoji");
            stickerFragment.show();
        }else {
            return;
        }
    }
    // Add UI Text
    public void initAddTextUI() {
        textFragment = new TextFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.frame_text, textFragment, "Text");
        fragmentTransaction.hide(textFragment);
        fragmentTransaction.commit();
    }

    public void changeAddTextState() {
        if (!textFragment.getShowFragment()){
            textFragment = (TextFragment) getSupportFragmentManager().findFragmentByTag("Text");
            textFragment.show();
        }else {
            return;
        }
    }


    // add Sticjker
    public void addStickerView(int resource) {
        final StickerViewEdit stickerViewEdit = new StickerViewEdit(this);
        try {
            stickerViewEdit.setImageResource(resource);
            stickerViewEdit.setOperationListener(new StickerViewEdit.OperationListener() {
                @Override
                public void onDeleteClick() {
                    mContentRootView.removeView(stickerViewEdit);
                }

                @Override
                public void onEdit(StickerViewEdit stickerViewEdit) {
                    mCurrentView.setInEdit(false);
                    mCurrentView = stickerViewEdit;
                    mCurrentView.setInEdit(true);
                }

                @Override
                public void onTop(StickerViewEdit stickerViewEdit) {

                }
            });

            final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mContentRootView.addView(stickerViewEdit, lp);
            setCurrentEdit(stickerViewEdit);

        } catch (Exception e) {
            Logger.d(TAG, "-- error resize bitmap  in addStickerView function");
        }
    }

    private void setCurrentEdit(StickerViewEdit stickerViewEdit) {
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }
        mCurrentView = stickerViewEdit;
        stickerViewEdit.setInEdit(true);
    }

    // Add Text
    public void addStickerText( EditText edtMain) {
        if (edtMain.getText().toString().length() != 0) {
            edtMain.setBackgroundColor(this.getResources().getColor(R.color.app_tranparent));
            edtMain.setCursorVisible(false);
            edtMain.setSelectAllOnFocus(false);
            edtMain.setError(null);
            edtMain.setSelected(false);
            edtMain.clearComposingText();

            Bitmap bitmap = Bitmap.createBitmap(edtMain.getWidth(), edtMain.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            edtMain.layout(0, 0, edtMain.getWidth(), edtMain.getHeight());
            edtMain.draw(canvas);

            StickerViewEdit stickerViewEdit = new StickerViewEdit(this, false);

            stickerViewEdit.setBitmap(BitmapUtil.alPhaBitmap(bitmap, 1), UtiLibs.getStatusBarHeight(this));
            final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mContentRootView.addView(stickerViewEdit, lp);
            final ItemView itemView = new ItemView(stickerViewEdit, bitmap);
            setCurrentEdit(stickerViewEdit);

            stickerViewEdit.setOperationListener(new StickerViewEdit.OperationListener() {
                @Override
                public void onDeleteClick() {
                    mContentRootView.removeView(itemView.view);
                }

                @Override
                public void onEdit(StickerViewEdit stickerViewEdit) {
                    mCurrentView.setInEdit(false);
                    mCurrentView = stickerViewEdit;
                    mCurrentView.setInEdit(true);
                }

                @Override
                public void onTop(StickerViewEdit stickerViewEdit) {
                    mContentRootView.removeView(itemView.view);
                    mContentRootView.addView(stickerViewEdit, lp);
                }
            });

        }
    }
    // Add background
    public void updateBackground(int resource){
        if (resource==-1){
            ivBackgroundBlur.setImageBitmap(bitmapMain);
        }else {
            Glide.with(EditorActivity.this).load(resource).asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        }

                        @Override
                        public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                            ivBackgroundBlur.setImageBitmap(arg0);                    }
                    });
            //ivBackgroundBlur.setImageResource(resource);
        }
    }
    // AddFilter
    public void updateFilter(int resource){
        Glide.with(EditorActivity.this).load(resource).asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    }

                    @Override
                    public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                        mFlterImage.setImageBitmap(arg0);
                        mFlterImage.setAlpha(0.2f);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id=v.getId();
        switch (id){
            //Menu Bottom
            case R.id.iv_mirror:
                this.setTabMenuBottom(0);
                mViewFlipperEditor.setDisplayedChild(0);
                break;
            case R.id.iv_blur:
                this.setTabMenuBottom(1);
                mViewFlipperEditor.setDisplayedChild(1);
                break;
            case R.id.iv_filter:
                this.setTabMenuBottom(2);
                mViewFlipperEditor.setDisplayedChild(2);
                changeFilterState();
                break;
            case R.id.iv_editor:
                this.setTabMenuBottom(3);
                mViewFlipperEditor.setDisplayedChild(7);
                break;
            case R.id.iv_background:
                this.setTabMenuBottom(4);
                mViewFlipperEditor.setDisplayedChild(3);
                changeBackgroundState();
                break;
            case R.id.iv_border:
                this.setTabMenuBottom(5);
                mViewFlipperEditor.setDisplayedChild(4);
                break;
            case R.id.iv_emoji:
                this.setTabMenuBottom(6);
                mViewFlipperEditor.setDisplayedChild(5);
                changeStickerState();
                break;
            case R.id.iv_addtext:
                this.setTabMenuBottom(7);
                mViewFlipperEditor.setDisplayedChild(6);
                changeAddTextState();
                break;
            case R.id.iv_flip:
                this.setTabMenuBottom(8);
                mViewFlipperEditor.setDisplayedChild(7);
                flipVImage();
                break;
            case R.id.iv_rotate:
                this.setTabMenuBottom(9);
                mViewFlipperEditor.setDisplayedChild(7);
                rotateImage();
                break;
            case R.id.llBackMain:
                onBackPressed();
                break;
            case R.id.llSave:
                saveContent();
                break;
        }
    }


    private Bitmap bitmapTmp;
    private void updateStyle(int k) {
        bitmapTmp = Bitmap.createBitmap(bitmapMain);
        mirrorView.setSourceBitmap(bitmapTmp);
        setSoureInvalidate(k);
    }
    private void setSoureInvalidate(int style) {
        mirrorView.setCurrentModeIndex(style);
        mirrorView.invalidate();
    }
    private void rotateImage() {
        if (bitmapTmp == null) {
            if (bitmapMain == null) {
                return;
            }
            bitmapMain = BitmapUtil.rotateBitmap(bitmapMain, 90);

            mirrorView.setSourceBitmap(bitmapMain);
            setSoureInvalidate(posStyle);

        } else {
            bitmapMain = BitmapUtil.rotateBitmap(bitmapMain, 90);
            bitmapTmp = BitmapUtil.rotateBitmap(bitmapTmp, 90);

            mirrorView.setSourceBitmap(bitmapTmp);
            setSoureInvalidate(posStyle);
        }


    }
    private void flipVImage() {
        Logger.e(TAG, "---- flipVImage = " + bitmapTmp);
        if (bitmapTmp == null) {
            if (bitmapMain == null) {
                return;
            }
            bitmapMain = BitmapUtil.flipVBitmap(bitmapMain);

            mirrorView.setSourceBitmap(bitmapMain);
            setSoureInvalidate(posStyle);

        } else {
            bitmapMain = BitmapUtil.flipVBitmap(bitmapMain);
            bitmapTmp = BitmapUtil.flipVBitmap(bitmapTmp);

            mirrorView.setSourceBitmap(bitmapTmp);
            setSoureInvalidate(posStyle);
        }
    }

    private void saveContent() {
        showLoading();
        filePathSave = FileUtil.getImageInput()+"/" +(indexFrame-1)+ ".jpg";
        Logger.d("filePathSave: "+filePathSave);
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }
        doBackGround(new IDoBackGround() {
            @Override
            public void onDoBackGround(boolean isCancelled) {
                Bitmap bitmap = Bitmap.createBitmap(mContentRootView.getWidth(), mContentMainALl.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                mContentMainALl.draw(canvas);
                filePathSave =BitmapUtil.saveBitmapToLocal(filePathSave, bitmap);
            }

            @Override
            public void onComplelted() {
                checkSave = true;
                updateMedia(filePathSave);
                dismissLoading();
                if (indexFrame==listPathFrames.size()){
                    Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    nextFrame();
                }

            }
        });
    }

    private void startEditor(){
//        Intent mIntent = new Intent(this, FilterActivity.class);
//        myApplication.setBitmapNew(bitmapMain);
//        startActivityForResult(mIntent, FILTER_RESULT);
    }

    @Override
    public void onBackPressed() {
        int displayChild=mViewFlipperEditor.getDisplayedChild();
        if (displayChild!=7){
            clearViewFlipper();
        }else {
            super.onBackPressed();
        }
    }

}
