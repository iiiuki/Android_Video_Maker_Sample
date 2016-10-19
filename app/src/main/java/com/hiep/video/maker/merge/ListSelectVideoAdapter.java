package com.hiep.video.maker.merge;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hiep.video.maker.R;
import com.hiep.video.maker.entity.VideoEntity;

import java.util.List;

public class ListSelectVideoAdapter extends RecyclerView.Adapter<ListSelectVideoAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        ImageView mCloseImage;

        ViewHolder(View itemView) {
            super(itemView);
            mImageView=(ImageView)itemView.findViewById(R.id.iv_marge_video_select);
            mCloseImage=(ImageView)itemView.findViewById(R.id.iv_marge_video_close);
        }
    }

    private Context context;
    List<VideoEntity> videoList;

    public ListSelectVideoAdapter(Context context, List<VideoEntity> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_merge_item_select, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final VideoEntity item = videoList.get(position);

        Glide.with(context).load(item.getFilePath()).into(holder.mImageView);

        holder.mCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoList.remove(position);
                notifyDataSetChanged();
            }
        });

    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }

    /**
     * Here is the key method to apply the animation
     */

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }
}
