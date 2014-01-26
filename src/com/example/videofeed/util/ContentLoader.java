package com.example.videofeed.util;

import java.util.ArrayList;

import android.os.AsyncTask;

public class ContentLoader {
	private VideoListAdapter mAdapter;
	public ContentLoader(VideoListAdapter adapter) {
		mAdapter = adapter;
	}

	public void load(String rootUrl) {
		new ConnectTask(this).execute(rootUrl);
	}
	
	public void onLoaded(ArrayList<String> list) {
		mAdapter.addAll(list);
	}

	private class ConnectTask extends AsyncTask<String, Integer, ArrayList<String>> {
		ContentLoader mCl;
		public ConnectTask(ContentLoader cl) {
			mCl = cl;
		}
		
		@Override
		protected ArrayList<String> doInBackground(String... urls) {
			return Util.getContentList(urls[0]);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			mCl.onLoaded(result);
		}
	}
}
