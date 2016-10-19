package com.hiep.video.maker.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hiep.video.maker.R;
import com.hiep.video.maker.entity.VideoEntity;
import com.hiep.video.maker.system.VideoMaker;
import com.hiep.video.maker.ui.adapter.MyRecylceAdapterBase;
import com.hiep.video.maker.util.Logger;
import com.hiep.video.maker.widget.SquareRelativeLayout;

import java.util.ArrayList;

/**
 * Created by anh on 7/23/2016.
 */
public class VideoEffectAdapter extends MyRecylceAdapterBase<VideoEffectAdapter.ViewHolder> implements View.OnClickListener{
    private static final String TAG =VideoEffectAdapter.class.getSimpleName();
    private RecyclerView recylceView;
    private ArrayList<VideoEntity> videoEntities;
    private IndexChangedListener indexChangedListener;
    int selectedPosition=-1;
    int colorDefault;
    int colorSelected;

    public VideoEffectAdapter( ArrayList<VideoEntity> videoEntities,IndexChangedListener indexChangedListener, int colorDefault, int colorSelected) {
        this.videoEntities= videoEntities;
        this.indexChangedListener=indexChangedListener;
        this.colorDefault = colorDefault;
        this.colorSelected = colorSelected;
    }

    @Override
    public int getItemCount() {
        return this.videoEntities.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recylceView = recyclerView;
    }
    @Override
    public void onBindViewHolder(VideoEffectAdapter.ViewHolder viewHolder, int selectedPosition) {
        viewHolder.setItem(this.videoEntities.get(selectedPosition));
        if(this.selectedPosition == selectedPosition) {
            viewHolder.itemView.setBackgroundColor(this.colorSelected);
        } else {
            viewHolder.itemView.setBackgroundColor(this.colorDefault);
        }

    }
    @Override
    public VideoEffectAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_video_effect, (ViewGroup)null);
        VideoEffectAdapter.ViewHolder viewHolder = new VideoEffectAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
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

        this.indexChangedListener.onIndexChanged(this.videoEntities.get(position));
        this.selectedPosition = position;
        view.setBackgroundColor(this.colorSelected);

    }

    public interface IndexChangedListener {
        void onIndexChanged(VideoEntity videoEntity);
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        ImageView imageView;
        LinearLayout background;
        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView)view.findViewById(R.id.iv_item_video_effect);
            this.background=(LinearLayout)view.findViewById(R.id.srl_item_video_effect);
        }
        public void setItem(VideoEntity videoEntity) {
            if (videoEntity.getId()==-1){
                imageView.setImageResource(R.drawable.icon_none);
            }else {
                Glide.with(VideoMaker.context).load(videoEntity.getFilePath()).asBitmap()
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
}
