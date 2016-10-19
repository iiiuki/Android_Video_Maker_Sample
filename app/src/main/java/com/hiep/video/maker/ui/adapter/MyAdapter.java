package com.hiep.video.maker.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hiep.video.maker.R;
import com.hiep.video.maker.system.VideoMaker;
import com.hiep.video.maker.util.Logger;

/**
 * Created by hiep on 6/10/2016.
 */
public class MyAdapter extends  MyRecylceAdapterBase<MyAdapter.ViewHolder> implements View.OnClickListener{
    private static final String TAG =MyAdapter.class.getSimpleName();
    int colorDefault;
    int colorSelected;
    MyAdapter.CurrentCollageIndexChangedListener currentIndexlistener;
    public int[] iconList;
    boolean isPattern = false;
    MyAdapter.PatternResIdChangedListener patternResIdListener;
    RecyclerView recylceView;
    View selectedListItem;
    int selectedPosition;
    boolean setSelectedView = true;

    public MyAdapter(int[] iconList, int colorDefault, int colorSelected, boolean var4, boolean setSelectedView) {
        this.iconList = iconList;
        this.colorDefault = colorDefault;
        this.colorSelected = colorSelected;
        this.isPattern = var4;
        this.setSelectedView = setSelectedView;
    }

    public MyAdapter(int[] iconList, MyAdapter.CurrentCollageIndexChangedListener var2, int colorDefault, int colorSelected, boolean var5, boolean var6) {
        this.iconList = iconList;
        this.currentIndexlistener = var2;
        this.colorDefault = colorDefault;
        this.colorSelected = colorSelected;
        this.isPattern = var5;
        this.setSelectedView = var6;
    }
    @Override
    public int getItemCount() {
        return this.iconList.length;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recylceView = recyclerView;
    }
    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int selectedPosition) {
        viewHolder.setItem(this.iconList[selectedPosition]);
        if(this.selectedPosition == selectedPosition) {
            viewHolder.itemView.setBackgroundColor(this.colorSelected);
        } else {
            viewHolder.itemView.setBackgroundColor(this.colorDefault);
        }
    }
    @Override
    public void onClick(View view) {
        int position = this.recylceView.getChildPosition(view);
        android.support.v7.widget.RecyclerView.ViewHolder viewHolder = this.recylceView.findViewHolderForPosition(this.selectedPosition);
        if(viewHolder != null) {
            View itemView = viewHolder.itemView;
            if(itemView != null) {
                itemView.setBackgroundColor(this.colorDefault);
            }
        }

        if(this.selectedListItem != null) {
            Logger.d(TAG, "selectedListItem " + position);
        }

        this.currentIndexlistener.onIndexChanged(position);


        if(this.setSelectedView) {
            this.selectedPosition = position;
            view.setBackgroundColor(this.colorSelected);
            this.selectedListItem = view;
        }

    }
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler, (ViewGroup)null);
        MyAdapter.ViewHolder viewHolder = new MyAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    public void setData(int[] iconList) {
        this.iconList = iconList;
    }

    public void setSelectedPositinVoid() {
        this.selectedListItem = null;
        this.selectedPosition = -1;
    }

    public interface CurrentCollageIndexChangedListener {
        void onIndexChanged(int index);
    }

    public interface PatternResIdChangedListener {
        void onPatternResIdChanged(int id);
    }


    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public ImageView imageView;
        private int item;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView)view.findViewById(R.id.iv_image_recycler);

        }
        public void setItem(int item) {
            this.item = item;
            Glide.with(VideoMaker.context).load(this.item).asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        }

                        @Override
                        public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                            imageView.setImageBitmap(arg0);
                        }
                    });
        }
    }
}
