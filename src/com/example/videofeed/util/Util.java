package com.example.videofeed.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

public class Util {
	
	public static class L {
		public static void d(String s) {
			Log.d("zoro", s);
		}
		
		public static void d(int s) {
			Log.d("zoro", "" + s);
		}
	}
	
	public static Bitmap getFrame(String path, long msec) {
	    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
	    try {                       
	    	retriever.setDataSource(path, new HashMap<String, String>());
	        return retriever.getFrameAtTime(msec);
	    } catch (IllegalArgumentException ex) {
	        ex.printStackTrace();
	    } catch (RuntimeException ex) {
	        ex.printStackTrace();
	    } finally {
	        try {
	            retriever.release();
	        } catch (RuntimeException ex) {
	        }
	    }
	    return null;
	}

	public static ArrayList<String> getContentList(String path) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Document doc = Jsoup.parse(new URL(path), 2000);
			Elements result = doc.select("a");
			L.d(result.size());
			for (Element link : result) {
				String href = link.attr("href");
				if(href.matches(".*\\.mp4")) {
					list.add(path + href);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return list;
	}
	
	
}
