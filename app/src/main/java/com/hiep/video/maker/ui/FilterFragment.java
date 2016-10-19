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
import com.hiep.video.maker.ui.adapter.FilterAdapter;
import com.hiep.video.maker.util.Logger;

import java.util.ArrayList;

/**
 * Created by hiep on 11/30/2015.
 */
public class FilterFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = FilterFragment.class.getSimpleName();

    /*--Du lieu--*/
    int listOverlay[] = {R.drawable.icon_none,
            R.drawable.overlay_1,R.drawable.overlay_2,R.drawable.overlay_3,R.drawable.overlay_4,R.drawable.overlay_5,R.drawable.overlay_6,
            R.drawable.overlay_7,R.drawable.overlay_8,R.drawable.overlay_9,R.drawable.overlay_10,R.drawable.overlay_11,R.drawable.overlay_12,
            R.drawable.overlay_13, R.drawable.overlay_14, R.drawable.overlay_15, R.drawable.overlay_16, R.drawable.overlay_17, R.drawable.overlay_18,
            R.drawable.overlay_19, R.drawable.overlay_20, R.drawable.overlay_21
    };

    int listTexture[] = {R.drawable.icon_none,
            R.drawable.texture_01,R.drawable.texture_02,R.drawable.texture_03,R.drawable.texture_04,R.drawable.texture_05,R.drawable.texture_06,R.drawable.texture_07,
            R.drawable.texture_08, R.drawable.texture_09,R.drawable.texture_10,R.drawable.texture_11,R.drawable.texture_12,
            R.drawable.texture_13,R.drawable.texture_14,R.drawable.texture_15,R.drawable.texture_16
    };

    int listGrad[] = {R.drawable.icon_none,
            R.drawable.grad1,R.drawable.grad2, R.drawable.grad3, R.drawable.grad4, R.drawable.grad5, R.drawable.grad16, R.drawable.grad17,
            R.drawable.grad6, R.drawable.grad7, R.drawable.grad8,R.drawable.grad9, R.drawable.grad14, R.drawable.grad15,
            R.drawable.grad10, R.drawable.grad11, R.drawable.grad12, R.drawable.grad13, R.drawable.grad18, R.drawable.grad19, R.drawable.grad20
    };
    private ArrayList<ObItemBackground> obBackgroundList;

    private Context mContext;
    private EditorActivity editorActivity;
    private LinearLayout llIcon;
    private RecyclerView recyclerView_filter;
    private FilterAdapter filterAdapter;
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
        View root = inflater.inflate(R.layout.fragment_filter, container, false);
        if (root != null) {
            initView(root);
            return root;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    private void initView(View view) {
        llIcon=(LinearLayout)view.findViewById(R.id.llIcon);
        recyclerView_filter=(RecyclerView)view.findViewById(R.id.recyclerView_filter);


        iniData();
        initIcon();
        iniAdapter();
    }

    private void iniData(){
        obBackgroundList=new ArrayList<>();
        obBackgroundList.add(new ObItemBackground(R.drawable.overlay_thumb,listOverlay));
        obBackgroundList.add(new ObItemBackground(R.drawable.texture_thumb,listTexture));
        obBackgroundList.add(new ObItemBackground(R.drawable.grap_thumb,listGrad));
        //obBackgroundList.add(new ObItemBackground(R.mipmap.lib_brightness,null));
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
                    if (k<3){
                        v.setBackgroundColor(getResources().getColor(R.color.collage_purple));
                        int[] listData=obBackgroundList.get(k).listData;
                        if (listData!=null){
                            filterAdapter.setData(listData);
                            filterAdapter.setSelect(k);
                            filterAdapter.notifyDataSetChanged();
                        }
                    }else {

                    }
                }
            });

            llIcon.addView(imageView);
        }
    }
    private void iniAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        filterAdapter =new FilterAdapter(mContext,obBackgroundList.get(mIconPosition).listData, new FilterAdapter.CurrentCollageIndexChangedListener() {
            public void onIndexChanged(int index) {
                Logger.d(TAG,"---- index: "+index);
                if (index>0){
                    int resource=obBackgroundList.get(mIconPosition).listData[index];
                    editorActivity.updateFilter(resource);
                }else {
                    editorActivity.updateFilter(android.R.color.transparent);
                }
            }
        });

        recyclerView_filter.setLayoutManager(linearLayoutManager);
        recyclerView_filter.setAdapter(filterAdapter);
        recyclerView_filter.setItemAnimator(new DefaultItemAnimator());
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
