package com.hiep.video.maker.asyncloader;

/**
 * Created by anh on 6/10/2016.
 */
public abstract class IAsyncLoaderCallBack {
    public abstract void workToDo();
    public abstract void onComplete();
    public abstract void onCancelled();
    public abstract void onCancelled(boolean result);
}
