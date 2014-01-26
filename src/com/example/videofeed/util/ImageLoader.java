package com.example.videofeed.util;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.externalutils.DiskLruImageCache;
import com.example.videofeed.util.Util.L;

public class ImageLoader {
	final private DiskLruImageCache diskCache;
	final private LruCache<String, Bitmap> memCache = new LruCache<String, Bitmap>(50);
	final private HashMap<ImageView, ConnectTask> taskTracker = new HashMap<ImageView, ConnectTask>();
	
	public ImageLoader(Context context) {
		diskCache = new DiskLruImageCache(context, "zoroVideoFeed", 20*1024*1024, 70);
	}
	
	public void onLoaded(String path, Bitmap bmp, ImageView image, TextView text) {
		if(bmp != null) {
			memCache.put(path, bmp);
			diskCache.put(String.valueOf(path.hashCode()), bmp);
			image.setImageBitmap(bmp);
			image.setVisibility(View.VISIBLE);
			text.setVisibility(View.GONE);
			synchronized (taskTracker) {
				taskTracker.remove(image);
			}
		}
		else {
			text.setVisibility(View.VISIBLE);
			text.setText("Error loading image preview...");
		}
	}
	
	public void load(String path, ImageView image, TextView text) {
		// cancel previous task associated with this imageview
		cancel(image);
		Bitmap bmp = null;
		if(memCache.get(path) == null) {
			// TODO: put diskCache access into a background task
			if(!diskCache.containsKey(String.valueOf(path.hashCode()))) {
				ConnectTask task = new ConnectTask(path, this, image, text);
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
		text.setVisibility(View.GONE);
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
		private final ImageLoader mImageLoader;
		private final String mPath;
		private final ImageView mImage;
		private final TextView mText;
		public ConnectTask(String path, ImageLoader imageLoader, ImageView image, TextView text) {
			mImageLoader = imageLoader;
			mPath = path;
			mImage = image;
			mText = text;
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
			mImageLoader.onLoaded(mPath, result, mImage, mText);
		}
	}
}
