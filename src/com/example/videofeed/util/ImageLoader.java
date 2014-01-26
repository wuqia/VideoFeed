package com.example.videofeed.util;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

public class ImageLoader {
	final private DiskLruImageCache diskCache;
	final private LruCache<String, Bitmap> memCache = new LruCache<String, Bitmap>(50);
	final private HashMap<ImageView, ConnectTask> taskTracker = new HashMap<ImageView, ConnectTask>();
	
	public ImageLoader(Context context) {
		diskCache = new DiskLruImageCache(context, "zoroVideoFeed", 20*1024*1024, CompressFormat.JPEG, 70);
	}
	
	public void onLoaded(String path, Bitmap bmp, ImageView image) {
		if(bmp != null) {
			memCache.put(path, bmp);
			diskCache.put(String.valueOf(path.hashCode()), bmp);
			image.setImageBitmap(bmp);
			image.setVisibility(View.VISIBLE);
			synchronized (taskTracker) {
				taskTracker.remove(image);
			}
		}
	}
	
	public void load(ImageView image, String path) {
		Bitmap bmp = null;
		if(memCache.get(path) == null) {
			if(!diskCache.containsKey(String.valueOf(path.hashCode()))) {
				ConnectTask task = new ConnectTask(path, this, image);
				synchronized (taskTracker) {
					taskTracker.put(image, task);
					task.execute("");
					L.d("task.excute: " + image);
				}
				return;
			}
			else {
				bmp = diskCache.getBitmap(String.valueOf(path.hashCode()));
				memCache.put(path, bmp);
			}
		}
		else {
			bmp = memCache.get(path);
		}
		image.setImageBitmap(bmp);
		image.setVisibility(View.VISIBLE);
	}
	
	public void cancel(ImageView image) {
		synchronized (taskTracker) {
			ConnectTask task = taskTracker.get(image);
			if(task != null && !task.isCancelled()) {
				L.d("task.cancel: " + image);
				task.cancel(true);
			}
		}
	}

	private class ConnectTask extends AsyncTask<String, Integer, Bitmap> {
		ImageLoader mCl;
		String mPath;
		ImageView mIv;
		public ConnectTask(String path, ImageLoader il, ImageView iv) {
			mCl = il;
			mPath = path;
			mIv = iv;
		}
		
		@Override
		protected Bitmap doInBackground(String... urls) {
			return Util.getFrame(mPath, 50);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			mCl.onLoaded(mPath, result, mIv);
		}
	}
}
