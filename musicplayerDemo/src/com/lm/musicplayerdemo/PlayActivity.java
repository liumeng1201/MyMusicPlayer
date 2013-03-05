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

	private boolean isplaying = false;

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
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicService传递操作数
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_PLAY);
		startService(intent);
	}

	/**
	 * 暂停播放,向MusicService发送暂停播放音乐的请求
	 */
	private void pause() {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicService传递操作数
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_PAUSE);
		startService(intent);
	}

	/**
	 * 停止播放,向MusicService发送停止播放音乐的请求
	 */
	private void stop() {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicService传递操作数
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_STOP);
		startService(intent);
	}

	/**
	 * 下一首,向MusicService发送播放下一首音乐的请求
	 */
	private void next() {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicService传递操作数
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_NEXT);
		startService(intent);
	}

	/**
	 * 上一首,向MusicService发送播放上一首音乐的请求
	 */
	private void previous() {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicService传递操作数
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_PREVIOUS);
		startService(intent);
	}

	/**
	 * 进度条操作,向MusicService发送进度条操作的请求
	 * 
	 * @param progress
	 *            进度条数值
	 */
	private void progresschange(int progress) {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicService传递操作数
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_PROGRESS_CHANGE);
		intent.putExtra(Util.MUSIC_PROGRESS, progress);
		startService(intent);
	}

	/**
	 * @return 是否正在播放音乐
	 */
	private boolean isPlaying() {
		return isplaying;
	}

	/**
	 * 设置播放状态
	 * 
	 * @param state
	 *            播放状态
	 */
	private void setPlayingState(boolean state) {
		this.isplaying = state;
	}
}
