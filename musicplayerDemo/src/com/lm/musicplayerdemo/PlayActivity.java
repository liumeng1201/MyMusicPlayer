package com.lm.musicplayerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PlayActivity extends Activity {
	private String mTitle;
	private String mArtist;
	private int mCurrentTime;
	private int mDuration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mTitle = intent.getStringExtra(Util.MUSIC_TITLE);
		mArtist = intent.getStringExtra(Util.MUSIC_ARTIST);
		mCurrentTime = intent.getIntExtra(Util.MUSIC_CURRENTTIME, 0);
		mDuration = intent.getIntExtra(Util.MUSIC_DURATION, 0);
	}

}
