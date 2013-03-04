package com.lm.musicplayerdemo;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

	private static Notification mNotification;
	private static NotificationManager mNotificationManager;

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
			showNotification();
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
		mNotificationManager.cancelAll();
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

	/**
	 * @return 当前播放到的时间
	 */
	private int getCurrentTime() {
		int current = 0;
		if (mMediaPlayer != null) {
			current = mMediaPlayer.getCurrentPosition();
		}
		return current;
	}

	/**
	 * 显示通知栏
	 */
	private void showNotification() {
		// 获取系统通知栏服务对象
		mNotificationManager = (NotificationManager) (MusicService.this)
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Builder builder = new Notification.Builder(MusicService.this);
		// 设置通知栏要显示的内容
		builder.setSmallIcon(R.drawable.beats_logo_s);
		Bitmap icon = BitmapFactory.decodeResource(getResources(),
				R.drawable.beats_logo_l);
		builder.setLargeIcon(icon);
		builder.setContentTitle(mTitle);
		builder.setContentText(mArtist);
		builder.setAutoCancel(false);
		builder.setWhen(System.currentTimeMillis());

		mCurrentTime = getCurrentTime();
		Intent intent = new Intent(MusicService.this, PlayActivity.class);
		// 设置要传递的数据
		intent.putExtra(Util.MUSIC_TITLE, mTitle);
		intent.putExtra(Util.MUSIC_ARTIST, mArtist);
		intent.putExtra(Util.MUSIC_CURRENTTIME, mCurrentTime);
		intent.putExtra(Util.MUSIC_DURATION, mDuration);
		PendingIntent contentIntent = PendingIntent
				.getActivity(MusicService.this, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		mNotification = builder.getNotification();
		mNotificationManager.notify(0, mNotification);
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
