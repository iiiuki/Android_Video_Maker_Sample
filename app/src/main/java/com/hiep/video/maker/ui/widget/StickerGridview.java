package com.hiep.video.maker.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by PingPingStudio on 11/30/2015.
 */
public class StickerGridview extends GridView {
    public StickerGridview(Context context) {
        super(context);
    }

    public StickerGridview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StickerGridview(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean onTouchEvent(MotionEvent event) {
        try {
            return super.onTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
