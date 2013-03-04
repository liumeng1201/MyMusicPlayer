package com.lm.musicplayerdemo;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service implements
		MediaPlayer.OnCompletionListener {
	// MediaPlayer对象
	private MediaPlayer mMediaPlayer;
	// 音频文件总时间
	private int mDuration;
	// 当前播放时间
	private int mCurrentTime;
	// 要跳转到的时间
	private int mProgress;
	// 歌手
	private String mArtist;
	// 歌名
	private String mTitle;
	// 专辑名
	private String mAlbum;
	// 歌曲文件路径
	private String mPath;

	/**
	 * 对MusicService进行初始化
	 */
	private void init() {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		} else {
			mMediaPlayer.reset();
		}
	}

	/**
	 * 播放
	 */
	private void play() {
		if (mMediaPlayer != null) {
			mMediaPlayer.start();
		}
	}

	/**
	 * 暂停
	 */
	private void pause() {
		if (mMediaPlayer != null) {
			mMediaPlayer.pause();
		}
	}

	/**
	 * 停止
	 */
	private void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
	}

	/**
	 * 下一首
	 */
	private void next() {

	}

	/**
	 * 上一首
	 */
	private void previous() {

	}

	/**
	 * 播放器在进行销毁时调用
	 */
	private void destory() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCompletion(MediaPlayer mp) {
		next();
	}
}
