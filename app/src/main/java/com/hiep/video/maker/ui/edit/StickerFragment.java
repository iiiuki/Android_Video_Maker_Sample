package com.hiep.video.maker.ui.edit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.hiep.video.maker.R;
import com.hiep.video.maker.entity.StickerOb;
import com.hiep.video.maker.system.Config;
import com.hiep.video.maker.ui.EditorActivity;
import com.hiep.video.maker.ui.widget.StickerGridview;
import com.hiep.video.maker.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hiep on 11/30/2015.
 */
public class StickerFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = StickerFragment.class.getSimpleName();
    private ViewPager pagerSticker;
    private LinearLayout llIndicator,llIcon;
    private List<View> listView = new ArrayList();
    private Context mContext;
    private StickerFragment.ViewPagerAdapter viewPagerAdapter;
    private static int mCurrenPage = 0;
    private int mIconPosition = 0;

    private List<Integer> listSticker = new ArrayList<>();
    public List<StickerOb> mListStickerObs = new ArrayList<>();

    private int selectStyle=0;
    public List<StickerOb> mListTMP = new ArrayList<>();

    private int primaryPreselect = Color.parseColor("#ffffff");

    /*--Du lieu sticker--*/
    int lstStickerKitKat[] = {
            R.mipmap.kitkat_emoticon1,R.mipmap.kitkat_emoticon2, R.mipmap.kitkat_emoticon3, R.mipmap.kitkat_emoticon4, R.mipmap.kitkat_emoticon5,
            R.mipmap.kitkat_emoticon6, R.mipmap.kitkat_emoticon7, R.mipmap.kitkat_emoticon8, R.mipmap.kitkat_emoticon9, R.mipmap.kitkat_emoticon10,
            R.mipmap.kitkat_emoticon11, R.mipmap.kitkat_emoticon12, R.mipmap.kitkat_emoticon13, R.mipmap.kitkat_emoticon14, R.mipmap.kitkat_emoticon15,
            R.mipmap.kitkat_emoticon16, R.mipmap.kitkat_emoticon17, R.mipmap.kitkat_emoticon18, R.mipmap.kitkat_emoticon19, R.mipmap.kitkat_emoticon20,
            R.mipmap.kitkat_emoticon21, R.mipmap.kitkat_emoticon22, R.mipmap.kitkat_emoticon23, R.mipmap.kitkat_emoticon24, R.mipmap.kitkat_emoticon25,
            R.mipmap.kitkat_emoticon26, R.mipmap.kitkat_emoticon27, R.mipmap.kitkat_emoticon28, R.mipmap.kitkat_emoticon29, R.mipmap.kitkat_emoticon30,
            R.mipmap.kitkat_emoticon31, R.mipmap.kitkat_emoticon32, R.mipmap.kitkat_emoticon33, R.mipmap.kitkat_emoticon34, R.mipmap.kitkat_emoticon35,
            R.mipmap.kitkat_emoticon36, R.mipmap.kitkat_emoticon37, R.mipmap.kitkat_emoticon38, R.mipmap.kitkat_emoticon39, R.mipmap.kitkat_emoticon40,
            R.mipmap.kitkat_emoticon41, R.mipmap.kitkat_emoticon42, R.mipmap.kitkat_emoticon43,
    };

    int lstStickerSmile[] = {
            R.drawable.emotion_1, R.drawable.emotion_9, R.drawable.emotion_16, R.drawable.emotion_23, R.drawable.emotion_30,
            R.drawable.emotion_2, R.drawable.emotion_10, R.drawable.emotion_17, R.drawable.emotion_24, R.drawable.emotion_31,
            R.drawable.emotion_3, R.drawable.emotion_11, R.drawable.emotion_18, R.drawable.emotion_25, R.drawable.emotion_32,
            R.drawable.emotion_4, R.drawable.emotion_12, R.drawable.emotion_19, R.drawable.emotion_26, R.drawable.emotion_33,
            R.drawable.emotion_5, R.drawable.emotion_13, R.drawable.emotion_20, R.drawable.emotion_27, R.drawable.emotion_34,
            R.drawable.emotion_6, R.drawable.emotion_14, R.drawable.emotion_21, R.drawable.emotion_28, R.drawable.emotion_35,
            R.drawable.emotion_7, R.drawable.emotion_15, R.drawable.emotion_22, R.drawable.emotion_29, R.drawable.emotion_36,
            R.drawable.emotion_8, R.drawable.emotion_37,R.drawable.emotion_38,R.drawable.emotion_39,R.drawable.emotion_40
    };

    int lstTag[] = {
            R.mipmap.tag_ecoticon1, R.mipmap.tag_ecoticon2, R.mipmap.tag_ecoticon3, R.mipmap.tag_ecoticon4, R.mipmap.tag_ecoticon5,
            R.mipmap.tag_ecoticon6, R.mipmap.tag_ecoticon7, R.mipmap.tag_ecoticon8, R.mipmap.tag_ecoticon9, R.mipmap.tag_ecoticon10,
            R.mipmap.tag_ecoticon11, R.mipmap.tag_ecoticon12, R.mipmap.tag_ecoticon13, R.mipmap.tag_ecoticon14, R.mipmap.tag_ecoticon15,
            R.mipmap.tag_ecoticon16, R.mipmap.tag_ecoticon17, R.mipmap.tag_ecoticon18, R.mipmap.tag_ecoticon19, R.mipmap.tag_ecoticon20,
            R.mipmap.tag_ecoticon21, R.mipmap.tag_ecoticon22, R.mipmap.tag_ecoticon23, R.mipmap.tag_ecoticon24, R.mipmap.tag_ecoticon25,
            R.mipmap.tag_ecoticon26, R.mipmap.tag_ecoticon27, R.mipmap.tag_ecoticon28, R.mipmap.tag_ecoticon29, R.mipmap.tag_ecoticon30,
            R.mipmap.tag_ecoticon31, R.mipmap.tag_ecoticon32, R.mipmap.tag_ecoticon33, R.mipmap.tag_ecoticon34, R.mipmap.tag_ecoticon35,
            R.mipmap.tag_ecoticon36, R.mipmap.tag_ecoticon37, R.mipmap.tag_ecoticon38, R.mipmap.tag_ecoticon39, R.mipmap.tag_ecoticon40,
            R.mipmap.tag_ecoticon41, R.mipmap.tag_ecoticon42, R.mipmap.tag_ecoticon43, R.mipmap.tag_ecoticon44, R.mipmap.tag_ecoticon45,
            R.mipmap.tag_ecoticon46, R.mipmap.tag_ecoticon47, R.mipmap.tag_ecoticon48, R.mipmap.tag_ecoticon49, R.mipmap.tag_ecoticon50,
            R.mipmap.tag_ecoticon51, R.mipmap.tag_ecoticon52, R.mipmap.tag_ecoticon53, R.mipmap.tag_ecoticon54, R.mipmap.tag_ecoticon55,
            R.mipmap.tag_ecoticon56, R.mipmap.tag_ecoticon57, R.mipmap.tag_ecoticon58, R.mipmap.tag_ecoticon59, R.mipmap.tag_ecoticon60,
    };


    public StickerFragment(){
        List<Integer> mStickerKitKat = new ArrayList<>();
        List<Integer> mStickerSmile = new ArrayList<>();
        List<Integer> mTag = new ArrayList<>();

        for(Integer n : lstStickerKitKat){
            mStickerKitKat.add(n);
        }

        for(Integer n : lstStickerSmile){
            mStickerSmile.add(n);
        }

        for(Integer n : lstTag){
            mTag.add(n);
        }

        mListStickerObs.add(new StickerOb(R.drawable.emotion_1, "smile_emoticon", mStickerSmile ));

        mListStickerObs.add(new StickerOb(R.mipmap.kitkat_emoticon6, "kitkat_emoticon", mStickerKitKat ));

        mListStickerObs.add(new StickerOb(R.mipmap.tag_ecoticon54, "tag_ecoticon", mTag));
    }

    AdapterView.OnItemClickListener stickerItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
            if(listSticker != null && !listSticker.isEmpty()) {
                int var6 = var3 + 12 * mCurrenPage;
                EditorActivity msgListActivity = (EditorActivity) StickerFragment.this.getActivity();
                msgListActivity.clearViewFlipper();
                if(var6 < listSticker.size()) {
                    int resource=-1;
                    Logger.d(TAG,"----- selectStyle: "+selectStyle);
                    switch (selectStyle){
                        case 0:
                            resource=lstStickerSmile[var6];
                            break;
                        case 1:
                            resource=lstStickerKitKat[var6];
                            break;
                        case 2:
                            resource=lstTag[var6];
                            break;
                    }
                    msgListActivity.addStickerView(resource);

                }
            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this.getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_emojisticker_container, container, false);
        if (root != null) {
            this.initView(root);
            return root;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView(View view) {
        pagerSticker = (ViewPager) view.findViewById(R.id.vpSticker);
        pagerSticker.getLayoutParams().height = (Config.SCREENWIDTH/6)*2;

        llIndicator = (LinearLayout) view.findViewById(R.id.llIndicator);

        llIcon = (LinearLayout) view.findViewById(R.id.llIcon);
        llIcon.getLayoutParams().height = (Config.height_rec *5)  /6;

        viewPagerAdapter = new StickerFragment.ViewPagerAdapter();

        pagerSticker.setAdapter(this.viewPagerAdapter);

        pagerSticker.setOnPageChangeListener(new StickerFragment.PageChangeLister());

    }

    private void initViewpagerIndictor(int size) {
        this.llIndicator.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.leftMargin = (int) this.mContext.getResources().getDimension(R.dimen.sticker_indicator_padding);
        layoutParams.rightMargin = (int) this.mContext.getResources().getDimension(R.dimen.sticker_indicator_padding);

        for (int i = 0; i < size; ++i) {
            ImageView imv = new ImageView(this.mContext);
            imv.setLayoutParams(layoutParams);
            if (i == 0) {
                imv.setBackgroundResource(R.mipmap.indicator_selected);
            } else {
                imv.setBackgroundResource(R.mipmap.indicator_unselected);
            }
            this.llIndicator.addView(imv);
        }
    }

    private void initIcon() {
        llIcon.removeAllViews();
        int height_new = (Config.height_rec*5)/6;


        for (int i = 0; i < this.mListTMP.size(); ++i) {
            final int k = i;
            StickerOb mStickerOb = mListTMP.get(i);

            final View vItemIcon = llIcon.inflate(mContext, R.layout.item_icon, null);

            LinearLayout llSize = (LinearLayout) vItemIcon.findViewById(R.id.llSize);
            llSize.getLayoutParams().height = height_new;
            llSize.getLayoutParams().width = height_new;

            ImageView imvIconThumb = (ImageView) vItemIcon.findViewById(R.id.imgIcon);
            imvIconThumb.getLayoutParams().height = (height_new*3)/4;
            imvIconThumb.getLayoutParams().width = (height_new*3)/4;

            vItemIcon.setId(i);
            if (mStickerOb != null) {
                imvIconThumb.setImageResource(mStickerOb.getThumbnail());
            } else {
                imvIconThumb.setImageResource(R.mipmap.ic_launcher);
            }
            if (i == this.mIconPosition) {
                vItemIcon.setBackgroundColor(this.getResources().getColor(R.color.collage_purple));
            } else {
                vItemIcon.setBackgroundColor(this.getResources().getColor(R.color.trgb_262626));
            }

            vItemIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIconPosition = v.getId();
                    mCurrenPage = 0;
                    pagerSticker.setOnPageChangeListener(null);

                    showSelectedStickerView();
                    for (int i = 0; i < mListTMP.size(); ++i) {
                        llIcon.findViewById(i).setBackgroundColor(getResources().getColor(R.color.trgb_262626));
                    }
                    Logger.d(TAG,"----- k: "+k);
                    selectStyle = k;

                    v.setBackgroundColor(getResources().getColor(R.color.collage_purple));
                }
            });

            llIcon.addView(vItemIcon);
        }

    }

    private void showSelectedStickerView() {
        showSticker();
        viewPagerAdapter.notifyDataSetChanged();
        Log.d(TAG, "-- mCurrenPage = " + mCurrenPage);
        pagerSticker.setCurrentItem(mCurrenPage);
        setNavigationDrable(mCurrenPage);
        pagerSticker.setOnPageChangeListener(new StickerFragment.PageChangeLister());
    }

    private void showSticker() {
        listSticker = mListTMP.get(mIconPosition).getmListSticker();
//        this.mStickerList = StickerManagerImpl.getInstances().getStickerListByStickerPkg(this.stickerPkg.pkgName);
        int size = this.listSticker.size();
        int pos;
        if(size % 12 == 0) {
            pos = size / 12;
        } else {
            pos = 1 + size / 12;
        }

        this.initViewpagerIndictor(pos);
        this.listView.clear();
        this.pagerSticker.removeAllViews();

        for(int i = 0; i < pos; ++i) {
            RelativeLayout rlLayout = new RelativeLayout(this.mContext);
            StickerGridview gvSticker = new StickerGridview(this.mContext);
            gvSticker.setSelector(new ColorDrawable(0));
            gvSticker.setNumColumns(6);
            gvSticker.setColumnWidth(Config.SCREENWIDTH/6);
            rlLayout.addView(gvSticker);
            this.listView.add(rlLayout);
        }
    }

    @Override
    public void onClick(View v) {

    }

    class PageChangeLister extends ViewPager.SimpleOnPageChangeListener {
        PageChangeLister() {
        }
        public void onPageSelected(int pageSelect) {
            Log.d(TAG, "-- onPageSelected position = " + pageSelect);
            StickerFragment.mCurrenPage = pageSelect;
            StickerFragment.this.setNavigationDrable(pageSelect);
        }
    }
    private void setNavigationDrable(int pos) {
        int size = this.llIndicator.getChildCount();
        for (int i = 0; i < size; ++i) {
            if (pos != i) {
                ((ImageView) this.llIndicator.getChildAt(i)).setBackgroundResource(R.mipmap.indicator_unselected);
            } else {
                ((ImageView) this.llIndicator.getChildAt(i)).setBackgroundResource(R.mipmap.indicator_selected);
            }
        }

    }

    class ViewPagerAdapter extends PagerAdapter {
        private int mChildCount = 0;
        ViewPagerAdapter() {
        }
        public void destroyItem(ViewGroup var1, int var2, Object var3) {
            if (var2 < StickerFragment.this.listView.size()) {
                ((ViewPager) var1).removeView((View) StickerFragment.this.listView.get(var2));
            }

        }

        public int getCount() {
            return StickerFragment.this.listView.size();
        }

        public int getItemPosition(Object var1) {
            if (this.mChildCount > 0) {
                this.mChildCount += -1;
                return -2;
            } else {
                return super.getItemPosition(var1);
            }
        }

        public Object instantiateItem(ViewGroup viewGroup, int position) {
            View view = (View) StickerFragment.this.listView.get(position);
            StickerGridview stickerGridview = (StickerGridview) ((RelativeLayout) view).getChildAt(0);
            StickerAdapter stickerAdapter = new StickerAdapter(StickerFragment.this.mContext);
            List<Integer> indexables = new ArrayList<>();

            for (int i = 0; i < 12; ++i) {
                if (i + position * 12 <listSticker.size()) {
                    indexables.add(listSticker.get(i + position * 12));
                }
            }

            stickerAdapter.setData(indexables);
            stickerGridview.setAdapter(stickerAdapter);
            stickerGridview.setOnItemClickListener(stickerItemClickListener);

            ((ViewPager) viewGroup).addView(view, 0);
            return view;
        }

        public boolean isViewFromObject(View var1, Object var2) {
            return var2 == var1;
        }

        public void notifyDataSetChanged() {
            this.mChildCount = this.getCount();
            super.notifyDataSetChanged();
        }
    }

    private boolean isShowFragment=false;

    public boolean getShowFragment(){
        return isShowFragment;
    }
    public void hide() {
        isShowFragment=false;
        FragmentTransaction transaction = ((FragmentActivity) this.mContext).getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.hide(this);
        transaction.commitAllowingStateLoss();
    }
    public void show() {
        isShowFragment=true;
        mCurrenPage = 0;
        this.mIconPosition = 0;

        mListTMP = mListStickerObs;

        if (mIconPosition > -1 + mListTMP.size()) {
            mIconPosition = 0;
            mCurrenPage = 0;
        }

        initIcon();
        pagerSticker.setOnPageChangeListener((ViewPager.OnPageChangeListener) null);
        showSelectedStickerView();
        FragmentTransaction transaction = ((FragmentActivity) this.mContext).getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);//4097
        transaction.show(this);
        transaction.commit();
    }
}
