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

	/**
	 * 音乐文件的路径
	 */
	private String mPath;

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
		mPath = intent.getStringExtra(Util.MUSIC_PATH);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (!isPlaying()) {
			
		}
	}

	/**
	 * 播放音乐,向MusicService发送播放音乐的请求
	 */
	private void play() {
		
	}

	/**
	 * 暂停播放,向MusicService发送暂停播放音乐的请求
	 */
	private void pause() {

	}

	/**
	 * 停止播放,向MusicService发送停止播放音乐的请求
	 */
	private void stop() {

	}

	/**
	 * @return 是否正在播放音乐
	 */
	private boolean isPlaying() {
		return true;
	}
}
