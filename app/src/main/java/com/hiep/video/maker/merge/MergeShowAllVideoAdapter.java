package com.hiep.video.maker.merge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hiep.video.maker.R;
import com.hiep.video.maker.entity.VideoEntity;
import com.hiep.video.maker.util.Util;

import java.util.List;

/**
 * Created by Hiep on 7/14/2016.
 */
public class MergeShowAllVideoAdapter extends ArrayAdapter<VideoEntity> {
    final int resource;
    final LayoutInflater inflater;
    final Context context;

    public MergeShowAllVideoAdapter(Context context, int resource, List<VideoEntity> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final VideoEntity item = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(context).load(item.getFilePath()).into(holder.mCoverImage);
        holder.mNameText.setText(item.getFileName());
        holder.mTimeText.setText(Util.convertDuration(item.getDuration()));
        return convertView;
    }

    class ViewHolder {
        ImageView mCoverImage;
        TextView mTimeText;
        TextView mNameText;

        public ViewHolder(View view) {
            mCoverImage=(ImageView)view.findViewById(R.id.iv_item_marge_video_cover);
            mNameText=(TextView)view.findViewById(R.id.tv_item_marge_video_name);
            mTimeText=(TextView)view.findViewById(R.id.tv_item_marge_video_duration);
        }
    }
}
