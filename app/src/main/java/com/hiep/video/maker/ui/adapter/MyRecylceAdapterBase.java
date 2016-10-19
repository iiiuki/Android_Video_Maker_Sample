package com.hiep.video.maker.ui.adapter;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

public class MyRecylceAdapterBase<VH extends ViewHolder> extends Adapter<VH> {
    public MyRecylceAdapterBase() {
    }

    public int getItemCount() {
        return 0;
    }

    public void onBindViewHolder(VH var1, int var2) {
    }

    public VH onCreateViewHolder(ViewGroup var1, int var2) {
        return null;
    }

    public void setSelectedPositinVoid() {
    }
}