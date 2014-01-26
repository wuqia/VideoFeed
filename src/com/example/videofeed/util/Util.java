package com.example.videofeed.util;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

public class Util {
	
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
	
	
	
}
