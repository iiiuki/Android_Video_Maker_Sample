package com.hiep.video.maker.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PingPingStudio on 11/30/2015.
 */
public class StickerOb {
    public int thumbnail = 0;
    public String tyle ="";
    public List<Integer> mListSticker = new ArrayList<>();

    public int getThumbnail() {
        return thumbnail;
    }

    public List<Integer> getmListSticker() {
        return mListSticker;
    }

    public StickerOb setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public StickerOb setmListSticker(List<Integer> mListSticker) {
        this.mListSticker = mListSticker;
        return this;
    }

    public String getTyle() {
        return tyle;
    }

    public StickerOb(int thumbnail, String tyle, List<Integer> mListSticker) {
        this.thumbnail = thumbnail;
        this.tyle = tyle;
        this.mListSticker = mListSticker;
    }


}
