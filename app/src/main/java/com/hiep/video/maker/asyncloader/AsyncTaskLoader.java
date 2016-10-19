package com.hiep.video.maker.asyncloader;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by hiep on 6/10/2016.
 */
public class AsyncTaskLoader extends AsyncTask<AsyncCallBack, Integer, Boolean> {

    public static AsyncTaskLoader asyncTaskLoader;
    public static AsyncTaskLoader getInstand() {
        if (asyncTaskLoader == null) {
            asyncTaskLoader = new AsyncTaskLoader();
        }
        return asyncTaskLoader;
    }

    AsyncCallBack[] _params;
    @Override
    protected Boolean doInBackground(AsyncCallBack... params) {
        this._params = params;
        int count = params.length;
        for(int i = 0; i < count; i++){
            params[i].workToDo();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        int count = this._params.length;
        for(int i = 0; i < count; i++){
            this._params[i].onComplete();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        int count = this._params.length;
        for(int i = 0; i < count; i++){
            this._params[i].onCancelled();
        }
    }

    @Override
    protected void onCancelled(Boolean result) {
        // TODO Auto-generated method stub
        super.onCancelled(result);
        int count = this._params.length;
        for(int i = 0; i < count; i++){
            this._params[i].onCancelled(result);
        }
    }
}
