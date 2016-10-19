package com.hiep.video.maker.ui.adapter;

import android.content.Context;
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
import com.hiep.video.maker.util.Logger;

/**
 * Created by hiep on 6/10/2016.
 */
public class BackgroundAdapter extends  MyRecylceAdapterBase<BackgroundAdapter.ViewHolder> implements View.OnClickListener{
    private static final String TAG =BackgroundAdapter.class.getSimpleName();

    BackgroundAdapter.CurrentCollageIndexChangedListener currentIndexlistener;
    public int[] iconList;
    BackgroundAdapter.PatternResIdChangedListener patternResIdListener;
    RecyclerView recylceView;
    View selectedListItem;
    int selectedPosition;
    Context context;

    public BackgroundAdapter(Context context,int[] iconList, BackgroundAdapter.CurrentCollageIndexChangedListener var2) {
        this.iconList = iconList;
        this.currentIndexlistener = var2;
        this.context=context;
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
    public void onBindViewHolder(BackgroundAdapter.ViewHolder viewHolder, int selectedPosition) {
        viewHolder.setItem(this.iconList[selectedPosition],context);
    }
    @Override
    public void onClick(View view) {
        int position = this.recylceView.getChildPosition(view);
        RecyclerView.ViewHolder viewHolder = this.recylceView.findViewHolderForPosition(this.selectedPosition);

        if(this.selectedListItem != null) {
            Logger.d(TAG, "selectedListItem " + position);
        }

        this.currentIndexlistener.onIndexChanged(position);

    }
    @Override
    public BackgroundAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_background, (ViewGroup)null);
        BackgroundAdapter.ViewHolder viewHolder = new BackgroundAdapter.ViewHolder(view);
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


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        private int item;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView)view.findViewById(R.id.iv_image_recycler);

        }
        public void setItem(int item,Context context) {
            this.item = item;
            Glide.with(context).load(this.item).asBitmap()
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
