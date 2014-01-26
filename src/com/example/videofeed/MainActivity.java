package com.example.videofeed;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.videofeed.util.ContentLoader;
import com.example.videofeed.util.L;
import com.example.videofeed.util.SystemUiHider;
import com.example.videofeed.util.VideoListAdapter;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		final ListView listView = (ListView) findViewById(R.id.listview);

		final VideoListAdapter adapter = new VideoListAdapter(this,
				android.R.layout.simple_list_item_1, listView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LayoutInflater li = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final ImageView snapshotImageView = (ImageView) view.findViewById(R.id.snapshot);
				final View videoviewContainer = li.inflate(R.layout.view_video, null);
				final FrameLayout layout = (FrameLayout)view.findViewById(R.id.container);
				final FrameLayout videocover = (FrameLayout)videoviewContainer.findViewById(R.id.videocover);
				layout.addView(videoviewContainer, 0);
				
				final VideoView vv = (VideoView) videoviewContainer.findViewById(R.id.videoview);
				final TextView loadingTextView = (TextView) view.findViewById(R.id.loadingtext);
				loadingTextView.setVisibility(View.VISIBLE);
				loadingTextView.setText("loading...");
				
				vv.setOnErrorListener(new OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						loadingTextView.setText("Error loading video...");
						return true;
					}
				});
				vv.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction() == MotionEvent.ACTION_UP) {
							L.d("onTouch ");
							if(vv.isPlaying()) {
								vv.pause();
								vv.clearFocus();
							}
							else {
								vv.requestFocus();
								vv.start();
							}
						}
						return true;
					}
				});
				vv.setOnPreparedListener(new OnPreparedListener() {
					   @Override
	                   public void onPrepared(MediaPlayer mp)
	                   {
	                	   mp.seekTo(50);
	                	   snapshotImageView.setVisibility(View.GONE);
	                	   loadingTextView.setVisibility(View.GONE);
	                	   videocover.setVisibility(View.GONE);
	                	   mp.start();
	                   }
                }); 
				
				try {
					final String path = adapter.getItem(position);
					Uri uri = Uri.parse(path);
					vv.setVideoURI(uri);
				}
				catch(Throwable t) {
					L.d(t.getMessage());
					vv.setEnabled(false);
				}
			}
			
		});
		ContentLoader loader = new ContentLoader(adapter);
		loader.load("http://tangohacks.com/videofeed/");
	}
}
