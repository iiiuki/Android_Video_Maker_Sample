package com.hiep.video.maker.merge;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiep.video.maker.R;
import com.hiep.video.maker.util.Util;

import java.util.List;

public class ListSelectVideoMergeAdapter  extends RecyclerView.Adapter<ListSelectVideoMergeAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mLayoutItemMerge;
        ImageView mIvMaker;
        ImageView mIvThumb;
        ImageView mIvStatus;
        TextView mTvDuration;

        ViewHolder(View itemView) {
            super(itemView);
            mIvMaker=(ImageView)itemView.findViewById(R.id.iv_marker_image_merge);
            mIvStatus=(ImageView)itemView.findViewById(R.id.iv_status_image_marge);
            mIvThumb=(ImageView)itemView.findViewById(R.id.iv_thumb_image_merge);
            mTvDuration=(TextView)itemView.findViewById(R.id.tv_duration_merge);
            mLayoutItemMerge=(RelativeLayout)itemView.findViewById(R.id.layout_item_merge);

        }
    }

    private Context context;
    List<MergeVideoActivity.Item> listVideoFilter;
    private OnSelectedVideoListener callback;

    public ListSelectVideoMergeAdapter(Context context, List<MergeVideoActivity.Item> listVideoFilter, OnSelectedVideoListener callback) {
        this.context = context;
        this.listVideoFilter = listVideoFilter;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_merge_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MergeVideoActivity.Item item= listVideoFilter.get(position);
        if (item.isSelect()){
            holder.mLayoutItemMerge.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.mIvMaker.setVisibility(View.GONE);
            holder.mIvStatus.setVisibility(View.VISIBLE);
        }else {
            holder.mIvMaker.setVisibility(View.VISIBLE);
            holder.mLayoutItemMerge.setBackgroundColor(0);
            holder.mIvStatus.setVisibility(View.GONE);
        }
        holder.mTvDuration.setVisibility(View.VISIBLE);
        holder.mTvDuration.setText(Util.convertDuration(item.getMax()));
        loadThumbnail(context,holder,item);

        holder.mLayoutItemMerge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.actionSelected(position);
            }
        });
    }

    public void setSelectVideo(int pos){
        for (int i=0;i<listVideoFilter.size();i++){
            if (i==pos){
                listVideoFilter.get(i).setSelect(true);
            }else {
                listVideoFilter.get(i).setSelect(false);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listVideoFilter.size();
    }


    private void loadThumbnail(final Context context, final ViewHolder holder, final MergeVideoActivity.Item item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap thumb = ThumbnailUtils.createVideoThumbnail(item.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.mIvThumb.setImageBitmap(thumb);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

}