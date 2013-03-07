package com.lm.musicplayerdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
	 * 专辑名
	 */
	private String mAlbum;

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
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		Intent intent = getIntent();
		if (intent != null) {
			mTitle = intent.getStringExtra(Util.KEY_TITLE);
			mArtist = intent.getStringExtra(Util.KEY_ARTIST);
			mCurrentTime = intent.getIntExtra(Util.KEY_CURRENTTIME, 0);
			mDuration = intent.getIntExtra(Util.KEY_DURATION, 0);
			mPath = intent.getStringExtra(Util.KEY_PATH);
		}
	}

	/**
	 * 初始化,注册要接收的广播
	 */
	private void init() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Util.MUSIC_ACTION_CURRENTTIME);
		filter.addAction(Util.MUSIC_ACTION_DURATION);
		filter.addAction(Util.MUSIC_ACTION_TITLE);
		filter.addAction(Util.MUSIC_ACTION_ARTIST);
		filter.addAction(Util.MUSIC_ACTION_ALBUM);

		// 注册广播接收者
		registerReceiver(musicserviceReceiver, filter);
	}

	private void setMusicSource(String path) {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicServer传递音乐文件的路径
		intent.putExtra(Util.KEY_PATH, path);
		startService(intent);
	}

	/**
	 * 播放音乐,向MusicService发送播放音乐的请求
	 */
	private void play() {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicService传递操作数
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_OP_PLAY);
		startService(intent);
	}

	/**
	 * 暂停播放,向MusicService发送暂停播放音乐的请求
	 */
	private void pause() {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicService传递操作数
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_OP_PAUSE);
		startService(intent);
	}

	/**
	 * 停止播放,向MusicService发送停止播放音乐的请求
	 */
	private void stop() {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicService传递操作数
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_OP_STOP);
		startService(intent);
	}

	/**
	 * 下一首,向MusicService发送播放下一首音乐的请求
	 */
	private void next() {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicService传递操作数
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_OP_NEXT);
		startService(intent);
	}

	/**
	 * 上一首,向MusicService发送播放上一首音乐的请求
	 */
	private void previous() {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		// 向MusicService传递操作数
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_OP_PREVIOUS);
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
		intent.putExtra(Util.OPERATE_NUMBER, Util.MUSIC_OP_PROGRESS_CHANGE);
		intent.putExtra(Util.MUSIC_ACTION_PROGRESS, progress);
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

	/**
	 * 设置当前的播放时间
	 * 
	 * @param current
	 *            当前的播放时间
	 */
	private void setCurrentTime(int current) {
		this.mCurrentTime = current;
		setSeekbarProgress(mCurrentTime);
	}

	/**
	 * 设置歌曲总时间
	 * 
	 * @param duration
	 *            歌曲总时间
	 */
	private void setDuration(int duration) {
		this.mDuration = duration;
		setSeekbarMax(mDuration);
	}

	/**
	 * 设置歌曲名
	 * 
	 * @param title
	 *            歌曲名
	 */
	private void setTitle(String title) {
		this.mTitle = title;
	}

	/**
	 * 设置歌手名
	 * 
	 * @param artist
	 *            歌手名
	 */
	private void setArtist(String artist) {
		this.mArtist = artist;
	}

	/**
	 * 设置专辑名
	 * 
	 * @param album
	 *            专辑名
	 */
	private void setAlbum(String album) {
		this.mAlbum = album;
	}

	/**
	 * 设置进度条的当前位置
	 * 
	 * @param progress
	 *            进度条进度
	 */
	private void setSeekbarProgress(int progress) {

	}

	/**
	 * 设置进度条的最大值
	 * 
	 * @param max
	 *            进度条最大值
	 */
	private void setSeekbarMax(int max) {

	}

	/**
	 * 用于接收从MusicService发送过来的广播信息
	 */
	protected BroadcastReceiver musicserviceReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(Util.MUSIC_ACTION_CURRENTTIME)) {
				// 当前播放时间
				int current = intent.getIntExtra(Util.KEY_CURRENTTIME, 0);
				setCurrentTime(current);
			} else if (action.equals(Util.MUSIC_ACTION_DURATION)) {
				// 歌曲总时间
				int duration = intent.getIntExtra(Util.KEY_DURATION, 0);
				setDuration(duration);
			} else if (action.equals(Util.MUSIC_ACTION_TITLE)) {
				// 歌曲名
				String title = intent.getStringExtra(Util.KEY_TITLE);
				setTitle(title);
			} else if (action.equals(Util.MUSIC_ACTION_ARTIST)) {
				// 歌手名
				String artist = intent.getStringExtra(Util.KEY_ARTIST);
				setArtist(artist);
			} else if (action.equals(Util.MUSIC_ACTION_ALBUM)) {
				// 专辑名
				String album = intent.getStringExtra(Util.KEY_ALBUM);
				setAlbum(album);
			}
		}
	};
}
