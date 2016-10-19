package com.hiep.video.maker.ui.edit.entity;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by hiep on 11/30/2015.
 */
public class ItemView {
    public View view;
    public Bitmap bm;
    public String text;
    public int textColor;
    public ItemView(View v, Bitmap bm){
        this.view=v;
        this.bm=bm;
    }
}
