package com.hiep.video.maker.music;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiep.video.maker.R;
import com.hiep.video.maker.entity.AudioEntity;
import com.hiep.video.maker.system.App;
import com.hiep.video.maker.util.Logger;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<AudioEntity> listAudios;
    private OnAudioSelectListener listener = null;
    private App myApp;

    public MusicAdapter(Context c, ArrayList<AudioEntity> listAudios, App myApp,OnAudioSelectListener listener) {
        mContext = c;
        inflater = LayoutInflater.from(mContext);
        this.listAudios = listAudios;
        this.myApp=myApp;
        this.listener=listener;
    }

    public int getCount() {
        return listAudios.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        MyViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_audio, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        final AudioEntity audioEntity = listAudios.get(position);
        mViewHolder.mTvDuration.setText("");
        mViewHolder.mTvTitle.setText(audioEntity.getAudioTitle());
        if (audioEntity.isPlay()){
            mViewHolder.mIvPlay.setImageResource(R.mipmap.ic_audio_pause);
            mViewHolder.mIvBackground.setBackgroundColor(mContext.getResources().getColor(R.color.collage_purple));
            setStopListener(mViewHolder,audioEntity);
        }else {
            mViewHolder.mIvPlay.setImageResource(R.mipmap.ic_audio_play);
            mViewHolder.mIvBackground.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            setPlayOnClickListener(mViewHolder,audioEntity);
        }

        mViewHolder.mRlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("Click audio");
                myApp.stopAudio();
                audioEntity.setPlay(false);
                listener.onSelectAudio(audioEntity);
            }
        });

        return convertView;
    }

    private void setPlayOnClickListener(final MyViewHolder holder, final AudioEntity item) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myApp.startAudio(item.getAudioPath(), new OnPlayAudioListener() {
                    @Override
                    public void finish() {
                        item.setPlay(false);
                        notifyDataSetChanged();
                        holder.mTvDuration.setText("");
                    }
                    @Override
                    public void duration(final long duration) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.mTvDuration.setText(convertDuration(duration));
                            }
                        });
                    }
                });

                item.setPlay(true);
                notifyDataSetChanged();
            }
        };

        holder.mIvPlay.setOnClickListener(onClickListener);
    }

    private void setStopListener(final MyViewHolder holder, final AudioEntity item) {
        holder.mIvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mTvDuration.setText("");
                myApp.stopAudio();
                item.setPlay(false);
                notifyDataSetChanged();
            }
        });

    }

    public static String convertDuration(long duration) {
        long seconds = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;

        StringBuilder b = new StringBuilder();
        b.append(minutes == 0 ? "0" : minutes < 10 ? String.valueOf("" + minutes) :
                String.valueOf(minutes));
        b.append(":");
        b.append(seconds == 0 ? "00" : seconds < 10 ? String.valueOf("0" + seconds) :
                String.valueOf(seconds));
        return b.toString();

    }

    public class MyViewHolder {
        TextView mTvTitle,mTvDuration;
        ImageView mIvPlay,mIvBackground;
        RelativeLayout mRlSelect;

        public MyViewHolder(View item) {
            mTvTitle = (TextView) item.findViewById(R.id.tv_item_audio_title);
            mIvPlay=(ImageView)item.findViewById(R.id.iv_item_audio_play);
            mTvDuration=(TextView)item.findViewById(R.id.iv_item_audio_duration);
            mIvBackground=(ImageView)item.findViewById(R.id.iv_item_audio_background);
            mRlSelect=(RelativeLayout)item.findViewById(R.id.rl_item_audio_select);
        }
    }

    public interface OnAudioSelectListener {
        public void onSelectAudio(AudioEntity item);
    }
}


