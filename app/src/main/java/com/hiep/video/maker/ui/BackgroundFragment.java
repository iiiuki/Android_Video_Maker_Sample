package com.hiep.video.maker.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.hiep.video.maker.R;
import com.hiep.video.maker.ui.adapter.BackgroundAdapter;
import com.hiep.video.maker.util.Logger;

import java.util.ArrayList;

/**
 * Created by hiep on 11/30/2015.
 */
public class BackgroundFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = BackgroundFragment.class.getSimpleName();

    /*--Du lieu--*/
    int listBackgroundGo[] = {
            R.drawable.icon_none,R.drawable.bk_go1,R.drawable.bk_go2,R.drawable.bk_go3,R.drawable.bk_go4,R.drawable.bk_go5,
            R.drawable.bk_go6,R.drawable.bk_go7,R.drawable.bk_go8,R.drawable.bk_go9,R.drawable.bk_go10,R.drawable.bk_go11
    };

    int listBackgroundTham[] = {
            R.drawable.icon_none,R.drawable.bk_tham2,R.drawable.bk_tham1,R.drawable.bk_tham3,R.drawable.bk_tham4,R.drawable.bk_tham5,R.drawable.bk_tham6,
            R.drawable.bk_tham7, R.drawable.bk_tham8,R.drawable.bk_tham9,R.drawable.bk_tham10,R.drawable.bk_tham11,
            R.drawable.bk_tham12,R.drawable.bk_tham13,R.drawable.bk_tham14,R.drawable.bk_tham15
    };

    int listBackgroundHeart[] = {
            R.drawable.icon_none,R.drawable.bk_heart1, R.drawable.bk_heart2, R.drawable.bk_heart3, R.drawable.bk_heart4,
            R.drawable.bk_heart5, R.drawable.bk_heart6, R.drawable.bk_heart7,R.drawable.bk_heart8,
            R.drawable.bk_heart9, R.drawable.bk_heart10, R.drawable.bk_heart11, R.drawable.bk_heart12
    };
    private ArrayList<ObItemBackground> obBackgroundList;

    private Context mContext;
    private EditorActivity editorActivity;
    private LinearLayout llIcon;
    private RecyclerView recyclerView_background;
    private BackgroundAdapter backgroundAdapter;
   private int mIconPosition = 0;

    private boolean isShow;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();
        editorActivity=(EditorActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_background, container, false);
        if (root != null) {
            initView(root);
            return root;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    private void initView(View view) {
        llIcon=(LinearLayout)view.findViewById(R.id.llIcon);
        recyclerView_background=(RecyclerView)view.findViewById(R.id.recyclerView_background);
        iniData();
        initIcon();
        iniAdapter();
    }

    private void iniData(){
        obBackgroundList=new ArrayList<>();
        obBackgroundList.add(new ObItemBackground(R.drawable.bk_go1,listBackgroundGo));
        obBackgroundList.add(new ObItemBackground(R.drawable.bk_tham2,listBackgroundTham));
        obBackgroundList.add(new ObItemBackground(R.drawable.bk_heart1,listBackgroundHeart));
    }

    private void initIcon() {
        int size=(int)mContext.getResources().getDimension(R.dimen.height_50dp);
        llIcon.removeAllViews();
        for (int i = 0; i < obBackgroundList.size(); ++i) {
            final int k = i;
            ImageView imageView=new ImageView(mContext);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(size, size);
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(20,20,20,20);
            imageView.setId(i);

            int resource=obBackgroundList.get(i).resource;
            imageView.setImageResource(resource);

            if (i == this.mIconPosition) {
                imageView.setBackgroundColor(this.getResources().getColor(R.color.collage_purple));
            } else {
                imageView.setBackgroundColor(this.getResources().getColor(R.color.trgb_262626));
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < obBackgroundList.size(); ++i) {
                        llIcon.findViewById(i).setBackgroundColor(getResources().getColor(R.color.trgb_262626));
                    }
                    mIconPosition=k;
                    v.setBackgroundColor(getResources().getColor(R.color.collage_purple));
                    int[] listData=obBackgroundList.get(k).listData;
                    backgroundAdapter.setData(listData);
                    backgroundAdapter.notifyDataSetChanged();
                }
            });

            llIcon.addView(imageView);
        }
    }
    private void iniAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        backgroundAdapter =new BackgroundAdapter(mContext,obBackgroundList.get(mIconPosition).listData, new BackgroundAdapter.CurrentCollageIndexChangedListener() {
            public void onIndexChanged(int index) {
                Logger.d(TAG,"---- index: "+index);
                if (index>0){
                    int resource=obBackgroundList.get(mIconPosition).listData[index];
                    editorActivity.updateBackground(resource);
                }else {
                    editorActivity.updateBackground(-1);
                }
            }
        });

        recyclerView_background.setLayoutManager(linearLayoutManager);
        recyclerView_background.setAdapter(backgroundAdapter);
        recyclerView_background.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onClick(View v) {

    }

    public void show() {
        isShow=true;
        FragmentTransaction transaction = ((FragmentActivity) this.mContext).getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.show(this);
        transaction.commit();
    }
    public void hide() {
        isShow=false;
        FragmentTransaction transaction = ((FragmentActivity) this.mContext).getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.hide(this);
        transaction.commitAllowingStateLoss();
    }

    public boolean getShowFragment() {
        return isShow;
    }

    public static class ObItemBackground{
        int resource;
        int[] listData;
        ObItemBackground(int resource,int[] listData){
            this.resource=resource;
            this.listData=listData;
        }
    }
}
