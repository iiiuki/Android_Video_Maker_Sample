package com.hiep.video.maker.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.hiep.video.maker.R;
import com.hiep.video.maker.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerAdapter extends MyRecylceAdapterBase<ColorPickerAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = ColorPickerAdapter.class.getSimpleName();
    int colorDefault;
    private List<Integer> colorList = new ArrayList<>();
    int colorSelected;
    String[] colors = new String[]{"#FFFFFF", "#EFDECD", "#CD4A4A", "#CC6666", "#BC5D58", "#FF5349", "#FD5E53", "#FD7C6E", "#FDBCB4", "#FF6E4A", "#FFA089", "#EA7E5D", "#B4674D", "#A5694F", "#FF7538", "#FF7F49", "#DD9475", "#FF8243", "#FFA474", "#9F8170", "#CD9575", "#EFCDB8", "#D68A59", "#DEAA88", "#FAA76C", "#FFCFAB", "#FFBD88", "#FDD9B5", "#FFA343", "#EFDBC5", "#FFB653", "#E7C697", "#8A795D", "#FAE7B5", "#FFCF48", "#FCD975", "#FDDB6D", "#FCE883", "#F0E891", "#ECEABE", "#BAB86C", "#FDFC74", "#FDFC74", "#FFFF99", "#C5E384", "#B2EC5D", "#87A96B", "#A8E4A0", "#1DF914", "#76FF7A", "#71BC78", "#6DAE81", "#9FE2BF", "#1CAC78", "#30BA8F", "#45CEA2", "#3BB08F", "#1CD3A2", "#17806D", "#158078", "#1FCECB", "#78DBE2", "#77DDE7", "#80DAEB", "#414A4C", "#199EBD", "#1CA9C9", "#1DACD6", "#9ACEEB", "#1A4876", "#1974D2", "#2B6CC4", "#1F75FE", "#C5D0E6", "#B0B7C6", "#5D76CB", "#A2ADD0", "#979AAA", "#ADADD6", "#7366BD", "#7442C8", "#7851A9", "#9D81BA", "#926EAE", "#CDA4DE", "#8F509D", "#C364C5", "#FB7EFD", "#FC74FD", "#8E4585", "#FF1DCE", "#FF1DCE", "#FF48D0", "#E6A8D7", "#C0448F", "#6E5160", "#DD4492", "#FF43A4", "#F664AF", "#FCB4D5", "#FFBCD9", "#F75394", "#FFAACC", "#E3256B", "#FDD7E4", "#CA3767", "#DE5D83", "#FC89AC", "#F780A1", "#C8385A", "#EE204D", "#FF496C", "#EF98AA", "#FC6C85", "#FC2847", "#FF9BAA", "#CB4154", "#EDEDED", "#DBD7D2", "#CDC5C2", "#95918C", "#232323"};
    MyAdapter.CurrentCollageIndexChangedListener listener;
    RecyclerView recylceView;
    View selectedListItem;
    int selectedPosition;

    public ColorPickerAdapter(MyAdapter.CurrentCollageIndexChangedListener var1, int var2, int var3) {
        this.listener = var1;
        this.colorDefault = var2;
        this.colorSelected = var3;
        this.createColorList();
    }

    private void createColorList() {
        for(int var1 = 0; var1 < this.colors.length; ++var1) {
            this.colorList.add(Integer.valueOf(Color.parseColor(this.colors[var1])));
        }

    }

    public int getItemCount() {
        return this.colorList.size();
    }

    public void onAttachedToRecyclerView(RecyclerView var1) {
        this.recylceView = var1;
    }

    public void onBindViewHolder(ColorPickerAdapter.ViewHolder var1, int var2) {
        var1.setItem(((Integer)this.colorList.get(var2)).intValue());
        if(this.selectedPosition == var2) {
            var1.itemView.setBackgroundColor(this.colorSelected);
        } else {
            var1.itemView.setBackgroundColor(this.colorDefault);
        }
    }

    public void onClick(View var1) {
        int var2 = this.recylceView.getChildPosition(var1);
        android.support.v7.widget.RecyclerView.ViewHolder var3 = this.recylceView.findViewHolderForPosition(this.selectedPosition);
        if(var3 != null) {
            View var6 = var3.itemView;
            if(var6 != null) {
                var6.setBackgroundColor(this.colorDefault);
            }
        }

        if(this.selectedListItem != null) {
            Logger.d(TAG, "selectedListItem " + var2);
        }

        Logger.d(TAG, "onClick " + var2);
        this.listener.onIndexChanged(((Integer)this.colorList.get(var2)).intValue());
        this.selectedPosition = var2;
        var1.setBackgroundColor(this.colorSelected);
        this.selectedListItem = var1;
    }

    public ColorPickerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int var2) {
        View var3 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyler_color, (ViewGroup)null);
        ColorPickerAdapter.ViewHolder var4 = new ColorPickerAdapter.ViewHolder(var3);
        var4.setCurrentCollageIndexChangedListener(this.listener);
        var3.setOnClickListener(this);
        return var4;
    }

    public void setSelectedPositinVoid() {
        this.selectedListItem = null;
        this.selectedPosition = -1;
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public View colorPickerView;
        private int item;
        MyAdapter.CurrentCollageIndexChangedListener viewHolderListener;

        public ViewHolder(View var1) {
            super(var1);
            this.colorPickerView = var1.findViewById(R.id.color_picker_view);
        }

        public void setCurrentCollageIndexChangedListener(MyAdapter.CurrentCollageIndexChangedListener var1) {
            this.viewHolderListener = var1;
        }

        public void setItem(int var1) {
            this.item = var1;
            this.colorPickerView.setBackgroundColor(this.item);
        }
    }
}