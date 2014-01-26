package com.example.videofeed.util;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.videofeed.R;


public class VideoListAdapter extends ArrayAdapter<String> {
	private ImageLoader mImageLoader = new ImageLoader();

	public VideoListAdapter(Context context, int resource, ListView listView) {
		super(context, resource);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = li.inflate(R.layout.listitem, null);
		}
		else {
			final View videoContainer = convertView.findViewById(R.id.videoContainer);
			if(videoContainer != null) {
				final VideoView vv = (VideoView) convertView.findViewById(R.id.videoview);
				vv.stopPlayback();
				final FrameLayout layout = (FrameLayout)convertView.findViewById(R.id.container);
				layout.removeView(videoContainer);
			}
		}
		final TextView loadingTextView = (TextView) convertView.findViewById(R.id.loadingtext);
		loadingTextView.setVisibility(View.GONE);
		final ImageView snapshotImageView = (ImageView) convertView.findViewById(R.id.snapshot);
		snapshotImageView.setImageResource(0);
		snapshotImageView.setVisibility(View.GONE);
		final String path = getItem(position);
		mImageLoader.cancel(snapshotImageView);
		mImageLoader.load(snapshotImageView, path);
		Uri uri = Uri.parse(path);
		final TextView des = (TextView) convertView.findViewById(R.id.textview);
		des.setText(uri.getPath());
		return convertView;
	}
}
