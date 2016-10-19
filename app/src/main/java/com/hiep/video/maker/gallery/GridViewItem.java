//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hiep.video.maker.gallery;

import android.app.Activity;
import android.graphics.Bitmap;

public class GridViewItem {
    Activity context;
    String count;
    private String folderName;
    long imageIdForThumb;
    private boolean isDirectory;
    int orientation;
    int selectedItemCount = 0;

    public GridViewItem(Activity var1, String var2, String var3, boolean var4, long var5, int var7) {
        this.folderName = var2;
        this.isDirectory = var4;
        this.count = var3;
        this.context = var1;
        this.imageIdForThumb = var5;
        this.orientation = var7;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public Bitmap getImage() {
        return GalleryUtility.getThumbnailBitmap(this.context, this.imageIdForThumb, this.orientation);
    }

    public boolean isDirectory() {
        return this.isDirectory;
    }
}
