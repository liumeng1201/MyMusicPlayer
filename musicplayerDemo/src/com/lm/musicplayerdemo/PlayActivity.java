package com.lm.musicplayerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PlayActivity extends Activity {
	/**
	 * 歌曲名
	 */
	private String mTitle;

	/**
	 * 歌手名
	 */
	private String mArtist;

	/**
	 * 当前播放时间
	 */
	private int mCurrentTime;

	/**
	 * 总时间
	 */
	private int mDuration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_activity);

		Intent intent = getIntent();
		mTitle = intent.getStringExtra(Util.MUSIC_TITLE);
		mArtist = intent.getStringExtra(Util.MUSIC_ARTIST);
		mCurrentTime = intent.getIntExtra(Util.MUSIC_CURRENTTIME, 0);
		mDuration = intent.getIntExtra(Util.MUSIC_DURATION, 0);
	}

}
