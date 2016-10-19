

package com.hiep.video.maker.ui.edit.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.hiep.video.maker.R;
import com.hiep.video.maker.system.VideoMaker;
import com.hiep.video.maker.util.BitmapUtil;
import com.hiep.video.maker.util.ScreenUtil;

import java.lang.ref.SoftReference;
import java.util.HashMap;

@android.widget.RemoteViews.RemoteView
public class StickerView extends ImageView {
    public static final String TAG = StickerView.class.getSimpleName();
    private static HashMap<String, SoftReference<Drawable>> mBitmapCache=new HashMap<>();
    Handler handler = new Handler() {
        public void handleMessage(Message paramMessage) {
            switch (paramMessage.what) {
                case 0:
                    StickerView.this.setImageResource(R.mipmap.ic_launcher);
                    break;
                case 1:
                    StickerView.this.setImageDrawable((Drawable) paramMessage.obj);
                    break;
                default:
                    break;
            }

        }
    };

    private AnimationDrawable mAnimDrawable;
    private boolean mIsAttached;
    private LoadThread mLoadThread;

    public StickerView(Context context) {
        super(context);
    }

    public StickerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void updateAnim() {
        if (this.mAnimDrawable != null)
            this.mAnimDrawable.stop();
        Drawable localDrawable = getDrawable();
        if ((localDrawable instanceof AnimationDrawable)) {
            this.mAnimDrawable = ((AnimationDrawable) localDrawable);
            if (this.mIsAttached)
                this.mAnimDrawable.start();
            return;
        }
        this.mAnimDrawable = null;
    }
    private String path;
    public void asyLoadThumbnail(String filePath) {
        stopLoadThum();
        if (TextUtils.isEmpty(filePath)){
            setImageResource(R.mipmap.ic_launcher);
            return;
        }
        path=filePath;
        SoftReference localSoftReference = (SoftReference)mBitmapCache.get(filePath);
        if (localSoftReference != null){
            Drawable localDrawable = (Drawable)localSoftReference.get();
            if (localDrawable != null){
                setImageDrawable(localDrawable);
                return;
            }
        }
        startLoadThum(filePath);
    }

    public String getPath(){
        return path;
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        super.onAttachedToWindow();
        if (this.mAnimDrawable != null)
            this.mAnimDrawable.start();
        this.mIsAttached = true;
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        super.onDetachedFromWindow();
        if (this.mAnimDrawable != null){
            this.mAnimDrawable.stop();
            this.mAnimDrawable = null;
        }
        this.mIsAttached = false;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if ((hasWindowFocus) && (this.mAnimDrawable != null))
            this.mAnimDrawable.start();
        if (this.mIsAttached&& !this.mIsAttached) {
            while (true){
                if (((hasWindowFocus) || (this.mAnimDrawable == null))){
                    return;
                }else {
                    stopAnim();
                }
            }
        }
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    public void setImageResource(int imageResource) {
        super.setImageResource(imageResource);
        updateAnim();
    }

    public void startAnim() {
        if ((this.mAnimDrawable != null) && (!this.mAnimDrawable.isRunning()))
            this.mAnimDrawable.start();
    }

    protected void startLoadThum(String filePath) {
        this.mLoadThread = new LoadThread(filePath);
        this.mLoadThread.setDaemon(true);
        this.mLoadThread.start();
    }

    public void stopAnim() {
        if ((this.mAnimDrawable != null) && (this.mAnimDrawable.isRunning()))
            this.mAnimDrawable.stop();
    }

    protected void stopLoadThum() {
        if (this.mLoadThread != null){
            this.mLoadThread.interrupt();
            this.mLoadThread = null;
        }
    }

    private class LoadThread extends Thread {
        private String path = null;
        LoadThread(final String p2) {
            this.path=p2;
        }

        public void run() {
            Message message=new Message();
            try {
                BitmapDrawable bitmapdrawable=null;
                int max=Math.max(ScreenUtil.getInstance().getWidth()/4,ScreenUtil.getInstance().getHeight()/4);
                Bitmap bm= BitmapUtil.resampleImage(path,max);
                if (bm==null){
                    message.what=0;
                    handler.sendMessage(message);
                }else {
                    bitmapdrawable = new BitmapDrawable(VideoMaker.context.getResources(),bm);
                    StickerView.mBitmapCache.put(path, new SoftReference(bitmapdrawable));
                    message.what=1;
                    message.obj=bitmapdrawable;
                }
            }catch (Exception e){
                Log.e(TAG,"-- Exception decodePath path: "+path);
                message.what=0;
            }
            handler.sendMessage(message);

        }
    }
}