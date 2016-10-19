package com.hiep.video.maker.ui.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hiep.video.maker.R;
import com.hiep.video.maker.system.VideoMaker;
import com.hiep.video.maker.util.Logger;


public class FontPickerAdapter extends MyRecylceAdapterBase<FontPickerAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = FontPickerAdapter.class.getSimpleName();
    int colorDefault;
    int colorSelected;
    MyAdapter.CurrentCollageIndexChangedListener listener;
    RecyclerView recylceView;
    View selectedListItem;
    int selectedPosition;
    String[] fonts;

    public FontPickerAdapter(String[] fonts,MyAdapter.CurrentCollageIndexChangedListener var1, int var2, int var3) {
        this.listener = var1;
        this.colorDefault = var2;
        this.colorSelected = var3;
        this.fonts=fonts;
    }



    public int getItemCount() {
        return this.fonts.length;
    }

    public void onAttachedToRecyclerView(RecyclerView var1) {
        this.recylceView = var1;
    }

    public void onBindViewHolder(FontPickerAdapter.ViewHolder var1, int var2) {
        var1.setItem(this.fonts[var2]);
        if(this.selectedPosition == var2) {
            var1.itemView.setBackgroundColor(this.colorSelected);
        } else {
            var1.itemView.setBackgroundColor(this.colorDefault);
        }
    }

    public void onClick(View view) {
        int position = this.recylceView.getChildPosition(view);
        RecyclerView.ViewHolder viewHolder = this.recylceView.findViewHolderForPosition(this.selectedPosition);
        if(viewHolder != null) {
            View itemView = viewHolder.itemView;
            if(itemView != null) {
                itemView.setBackgroundColor(this.colorDefault);
            }
        }

        if(this.selectedListItem != null) {
            Logger.d(TAG, "selectedListItem " + position);
        }

        Logger.d(TAG, "onClick " + position);
        this.listener.onIndexChanged(position);
        this.selectedPosition = position;
        view.setBackgroundColor(this.colorSelected);
        this.selectedListItem = view;
    }

    public FontPickerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int var2) {
        View var3 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyler_font, (ViewGroup)null);
        FontPickerAdapter.ViewHolder var4 = new FontPickerAdapter.ViewHolder(var3);
        var4.setCurrentCollageIndexChangedListener(this.listener);
        var3.setOnClickListener(this);
        return var4;
    }

    public void setSelectedPositinVoid() {
        this.selectedListItem = null;
        this.selectedPosition = -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtFonr;
        private String nameFont;
        MyAdapter.CurrentCollageIndexChangedListener viewHolderListener;

        public ViewHolder(View var1) {
            super(var1);
            this.txtFonr = (TextView) var1.findViewById(R.id.tv_font);
        }

        public void setCurrentCollageIndexChangedListener(MyAdapter.CurrentCollageIndexChangedListener var1) {
            this.viewHolderListener = var1;
        }

        public void setItem(String nameFont) {
            this.nameFont = nameFont;
            Typeface mTypeface = Typeface.createFromAsset(VideoMaker.context.getAssets(), (new StringBuilder("font/")).append(this.nameFont).toString());
            this.txtFonr.setTypeface(mTypeface);
        }
    }
}