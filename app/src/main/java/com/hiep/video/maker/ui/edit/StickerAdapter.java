package com.hiep.video.maker.ui.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hiep.video.maker.R;
import com.hiep.video.maker.system.Config;
import com.hiep.video.maker.ui.edit.widget.StickerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hiep on 11/30/2015.
 */

public class StickerAdapter extends BaseAdapter {
    public static final String TAG = StickerAdapter.class.getSimpleName();
    private Context mContext;
    private List<Integer> mStickerList;
    public StickerAdapter(Context var1) {
        this.mContext = var1;
        this.mStickerList =  new ArrayList<>();
    }

    public int getCount() {
        return this.mStickerList != null && !this.mStickerList.isEmpty()?this.mStickerList.size():0;
    }

    public Object getItem(int var1) {
        return this.mStickerList != null && !this.mStickerList.isEmpty()?this.mStickerList.get(var1):null;
    }

    public long getItemId(int var1) {
        return (long)var1;
    }

    public View getView(int postition, View view, ViewGroup viewGroup) {
        final StickerView stickerView;
        if(view == null) {
            view = ((LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_list_sticker, viewGroup, false);
            stickerView = (StickerView)view.findViewById(R.id.ivSticker);
            view.setTag(stickerView);
        } else {
            stickerView = (StickerView)view.getTag();
        }

        stickerView.getLayoutParams().width = Config.SCREENWIDTH/6;
        stickerView.getLayoutParams().height = Config.SCREENWIDTH/6;
        if(this.isEnabled(postition)) {
            Glide.with(mContext).load(mStickerList.get(postition)).asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        }

                        @Override
                        public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                            stickerView.setImageBitmap(arg0);                    }
                    });
        }
        return view;
    }

    public void setData(List<Integer> var1) {
        this.mStickerList = var1;
    }

}
