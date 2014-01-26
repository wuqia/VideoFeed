package com.example.videofeed.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
			ArrayList<String> list = new ArrayList<String>();
			try {
				Document doc = Jsoup.parse(new URL(urls[0]), 2000);
				Elements result = doc.select("a");
				L.d(result.size());
				for (Element link : result) {
					String href = link.attr("href");
					if(href.matches(".*\\.mp4")) {
						list.add(urls[0] + href);
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return list;
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
