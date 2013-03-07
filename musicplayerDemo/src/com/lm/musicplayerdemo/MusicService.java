package com.lm.musicplayerdemo;

import java.io.IOException;

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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class MusicService extends Service implements
		MediaPlayer.OnCompletionListener {
	// MediaPlayer对象
	private MediaPlayer mMediaPlayer;

	// 用来向PlayActivity发送广播
	private Handler mHandler;

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

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		init();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		destory();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// 获取由PlayActivity传递过来的对MediaPlayer的操作数
		int operate = intent.getIntExtra(Util.OPERATE_NUMBER, -1);
		switch (operate) {
		case Util.MUSIC_OP_PLAY:
			// 播放
			if (!isplaying()) {
				play();
			}
			break;
		case Util.MUSIC_OP_PAUSE:
			// 暂停
			if (isplaying()) {
				pause();
			}
			break;
		case Util.MUSIC_OP_STOP:
			// 停止
			stop();
			break;
		case Util.MUSIC_OP_NEXT:
			// 下一首
			next();
			break;
		case Util.MUSIC_OP_PREVIOUS:
			// 上一首
			previous();
			break;
		case Util.MUSIC_OP_PROGRESS_CHANGE:
			// 进度条进度改变
			int progress = intent.getIntExtra(Util.MUSIC_ACTION_PROGRESS, 0);
			progresschange(progress);
			break;
		default:
			break;
		}

		// 获取由PlayActivity传递过来的音乐文件的路径
		String musicpath = intent.getStringExtra(Util.KEY_PATH);
		setMusicPath(musicpath);

		if (mPath != null) {
			setMediaPlayerDataSource(mPath);
		}

		play();

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 对MusicService进行初始化
	 */
	private void init() {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		} else {
			mMediaPlayer.reset();
		}

		initBroadcastSendHandler();
	}

	/**
	 * 初始化向PlayActivity发送数据更新广播,通知其进行数据更新
	 */
	private void initBroadcastSendHandler() {
		if (mHandler == null) {
			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					switch (msg.what) {
					case Util.msg_current:
						// 发送当前时间信息广播
						sendCurrentBroad();
						break;
					case Util.msg_durtion:
						// 发送总时间信息广播
						sendDurationBroad();
						break;
					case Util.msg_title:
						// 发送歌曲名信息广播
						sendTitleBroad();
						break;
					case Util.msg_artist:
						// 发送歌手名信息广播
						sendArtistBroad();
						break;
					case Util.msg_album:
						// 发送专辑信息广播
						sendAlbumBroad();
						break;
					}
				}
			};
		}
	}

	/**
	 * 发送当前歌曲播放时间的广播
	 */
	private void sendCurrentBroad() {
		Intent intent = new Intent(Util.MUSIC_ACTION_CURRENTTIME);
		intent.putExtra(Util.KEY_CURRENTTIME, getCurrentTime());
		sendBroadcast(intent);
		mHandler.sendEmptyMessageDelayed(Util.msg_current, 500);
	}

	/**
	 * 发送当前歌曲总时间的广播
	 */
	private void sendDurationBroad() {
		Intent intent = new Intent(Util.MUSIC_ACTION_DURATION);
		intent.putExtra(Util.KEY_DURATION, getDuration());
		sendBroadcast(intent);
	}

	/**
	 * 发送当前歌曲歌曲名信息的广播
	 */
	private void sendTitleBroad() {
		Intent intent = new Intent(Util.MUSIC_ACTION_TITLE);
		intent.putExtra(Util.KEY_TITLE, getTitle());
		sendBroadcast(intent);
	}

	/**
	 * 发送当前歌曲歌手名信息的广播
	 */
	private void sendArtistBroad() {
		Intent intent = new Intent(Util.MUSIC_ACTION_ARTIST);
		intent.putExtra(Util.KEY_TITLE, getArtist());
		sendBroadcast(intent);
	}

	/**
	 * 发送当前歌曲专辑名信息的广播
	 */
	private void sendAlbumBroad() {
		Intent intent = new Intent(Util.MUSIC_ACTION_ALBUM);
		intent.putExtra(Util.KEY_ALBUM, getAlbum());
		sendBroadcast(intent);
	}

	/**
	 * 设置音乐文件的路径
	 * 
	 * @param path
	 *            音乐文件的路径
	 */
	private void setMusicPath(String path) {
		this.mPath = path;
	}

	/**
	 * 位MediaPlayer设置DataSource
	 * 
	 * @param datasource
	 *            音乐文件路径
	 */
	private void setMediaPlayerDataSource(String datasource) {
		if (mMediaPlayer != null) {
			try {
				mMediaPlayer.setDataSource(datasource);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 播放
	 */
	private void play() {
		if (mMediaPlayer != null) {
			if (!isplaying()) {
				mMediaPlayer.start();
			}
			showNotification();

			sendCurrentBroad();
			sendDurationBroad();
			sendTitleBroad();
			sendArtistBroad();
			sendAlbumBroad();
		}
	}

	/**
	 * 暂停
	 */
	private void pause() {
		if (mMediaPlayer != null) {
			if (isplaying()) {
				mMediaPlayer.pause();
			} else {
				mMediaPlayer.start();
			}
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

		mHandler.removeMessages(Util.msg_current);
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
	 * 进度条改变
	 * 
	 * @param progress
	 *            改变之后的进度条的值
	 */
	private void progresschange(int progress) {
		if (mMediaPlayer != null) {
			mMediaPlayer.seekTo(progress);
		}
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
	 * @return 播放器是否正在播放
	 */
	private boolean isplaying() {
		boolean isplaying = false;
		if (mMediaPlayer != null) {
			isplaying = mMediaPlayer.isPlaying();
		} else {
			isplaying = false;
		}
		return isplaying;
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
	 * @return 当前歌曲总时间
	 */
	private int getDuration() {
		int duration = 0;
		if (mMediaPlayer != null) {
			duration = mMediaPlayer.getDuration();
		}
		return duration;
	}

	/**
	 * @return 歌曲名
	 */
	private String getTitle() {
		return mTitle;
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
	 * @return 歌手名
	 */
	private String getArtist() {
		return mArtist;
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
	 * @return 专辑名
	 */
	private String getAlbum() {
		return mAlbum;
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
		intent.putExtra(Util.MUSIC_ACTION_TITLE, mTitle);
		intent.putExtra(Util.MUSIC_ACTION_ARTIST, mArtist);
		intent.putExtra(Util.MUSIC_ACTION_CURRENTTIME, mCurrentTime);
		intent.putExtra(Util.MUSIC_ACTION_DURATION, mDuration);
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
