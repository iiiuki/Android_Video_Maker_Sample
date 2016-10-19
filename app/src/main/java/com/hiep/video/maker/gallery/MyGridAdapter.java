package com.hiep.video.maker.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.hiep.video.maker.R;

import java.lang.ref.WeakReference;
import java.util.List;


public class MyGridAdapter extends BaseAdapter {
    private static final String TAG = MyGridAdapter.class.getSimpleName();
    Context context;
    GridView gridView;
    LayoutInflater inflater;
    List<GridViewItem> items;
    Bitmap placeHolder;

    public MyGridAdapter(Context paramContext, List<GridViewItem> paramList, GridView paramGridView) {
        this.items = paramList;
        this.inflater = ((LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        this.gridView = paramGridView;
        this.placeHolder = BitmapFactory.decodeResource(paramContext.getResources(), R.drawable.empty_photo);
        this.context = paramContext;
    }

    public static boolean cancelPotentialWork(long paramLong, ImageView paramImageView) {
        BitmapWorkerTask localBitmapWorkerTask = getBitmapWorkerTask(paramImageView);
        if (localBitmapWorkerTask != null) {
            long l = localBitmapWorkerTask.data;
            if ((l == 0L) || (l != paramLong))
                localBitmapWorkerTask.cancel(true);
        } else {
            return true;
        }
        return false;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView paramImageView) {
        if (paramImageView != null) {
            Drawable localDrawable = paramImageView.getDrawable();
            if ((localDrawable instanceof AsyncDrawable))
                return ((AsyncDrawable) localDrawable).getBitmapWorkerTask();
        }
        return null;
    }

    public int getCount() {
        return this.items.size();
    }

    public Object getItem(int paramInt) {
        return this.items.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    @SuppressLint({"NewApi"})
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.gallery_grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textPath = (TextView) convertView.findViewById(R.id.textView_path);
            viewHolder.textCount = (TextView) convertView.findViewById(R.id.textViewCount);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.textContainer = convertView.findViewById(R.id.grid_item_text_container);
            viewHolder.selectedCount = (TextView) convertView.findViewById(R.id.textViewSelectedItemCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String folderName = ((GridViewItem) this.items.get(position)).getFolderName();
        if (folderName == null || folderName.length() == 0) {
            if (viewHolder.textContainer.getVisibility() == View.VISIBLE) {
                viewHolder.textContainer.setVisibility(View.INVISIBLE);
            }
            if (((GridViewItem) this.items.get(position)).selectedItemCount > 0) {
                viewHolder.selectedCount.setText(""+((GridViewItem) this.items.get(position)).selectedItemCount);
                if (viewHolder.selectedCount.getVisibility() == View.INVISIBLE) {
                    viewHolder.selectedCount.setVisibility(View.VISIBLE);
                }
            } else if (viewHolder.selectedCount.getVisibility() == View.VISIBLE) {
                viewHolder.selectedCount.setVisibility(View.INVISIBLE);
            }
        } else {
            if (viewHolder.textContainer.getVisibility() == View.INVISIBLE) {
                viewHolder.textContainer.setVisibility(View.VISIBLE);
            }
            viewHolder.textPath.setText(((GridViewItem) this.items.get(position)).getFolderName());
            viewHolder.textCount.setText(((GridViewItem) this.items.get(position)).count);
            if (viewHolder.selectedCount.getVisibility() == View.VISIBLE) {
                viewHolder.selectedCount.setVisibility(View.INVISIBLE);
            }
        }
        loadBitmap((long) position, viewHolder.imageView, (GridViewItem) this.items.get(position));
        return convertView;
    }

    public void loadBitmap(long paramLong, ImageView paramImageView, GridViewItem paramGridViewItem) {
        if (cancelPotentialWork(paramLong, paramImageView)) {
            BitmapWorkerTask localBitmapWorkerTask = new BitmapWorkerTask(paramImageView, paramGridViewItem);
            paramImageView.setImageDrawable(new AsyncDrawable(this.context.getResources(), this.placeHolder, localBitmapWorkerTask));
            Long[] arrayOfLong = new Long[1];
            arrayOfLong[0] = Long.valueOf(paramLong);
            localBitmapWorkerTask.execute(arrayOfLong);
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources paramResources, Bitmap paramBitmap, MyGridAdapter.BitmapWorkerTask paramBitmapWorkerTask) {
            super(paramBitmap);
            this.bitmapWorkerTaskReference = new WeakReference(paramBitmapWorkerTask);
        }

        public MyGridAdapter.BitmapWorkerTask getBitmapWorkerTask() {
            return (MyGridAdapter.BitmapWorkerTask) this.bitmapWorkerTaskReference.get();
        }
    }

//    class BitmapWorkerTask extends MyAsyncTask2<Long, Void, Bitmap> {
    class BitmapWorkerTask extends AsyncTask<Long, Void, Bitmap> {
        private long data;
        private final WeakReference<ImageView> imageViewReference;
        private GridViewItem item;

        public BitmapWorkerTask(ImageView imageView, GridViewItem item) {
            this.data = 0;
            this.imageViewReference = new WeakReference(imageView);
            this.item = item;
        }

        protected Bitmap doInBackground(Long... params) {
            this.data = params[0].longValue();
            return this.item.getImage();
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (this.imageViewReference != null && bitmap != null) {
                ImageView imageView = (ImageView) this.imageViewReference.get();
                if (this == MyGridAdapter.getBitmapWorkerTask(imageView) && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class ViewHolder {
        ImageView imageView;
        TextView selectedCount;
        View textContainer;
        TextView textCount;
        TextView textPath;
    }
}
