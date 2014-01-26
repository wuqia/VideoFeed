package com.example.videofeed.util;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

public class ImageLoader {
	
	LruCache<String, Bitmap> memCache = new LruCache<String, Bitmap>(50);
	HashMap<ImageView, ConnectTask> taskTracker = new HashMap<ImageView, ConnectTask>();
	
	public void onLoaded(String path, Bitmap bmp, ImageView image) {
		if(bmp != null) {
			memCache.put(path, bmp);
			image.setImageBitmap(bmp);
			image.setVisibility(View.VISIBLE);
			synchronized (taskTracker) {
				taskTracker.remove(image);
			}
		}
	}
	
	public void load(ImageView image, String path) {
		if(memCache.get(path) == null) {
			ConnectTask task = new ConnectTask(path, this, image);
			synchronized (taskTracker) {
				taskTracker.put(image, task);
				task.execute("");
				L.d("task.excute: " + image);
			}
		}
		else {
			image.setImageBitmap(memCache.get(path));
			image.setVisibility(View.VISIBLE);
		}
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
