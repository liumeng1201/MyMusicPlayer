package com.lm.mymusicplayer.service;

import java.io.IOException;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;

import com.lm.mymusicplayer.R;
import com.lm.mymusicplayer.view.MusicPlayActivity;

/**
 * 音乐服务Service,在该Service中完成音乐的播放及播放控制功能
 * 
 * @author liumeng
 */
public class MusicService extends Service implements
		MediaPlayer.OnCompletionListener {
	// 当前播放时间
	private static final String MUSIC_CURRENT = "music.currenttime";
	// 音乐总时间
	private static final String MUSCI_DURATION = "music.duration";
	//
	private static final String MUSIC_UPDATE = "music.update";
	// 音乐列表
	private static final String MUSIC_LIST = "music.list";
	// 播放
	private static final int MUSIC_PLAY = 1;
	// 暂停
	private static final int MUSIC_PAUSE = 2;
	// 停止
	private static final int MUSIC_STOP = 3;
	// 下一个
	private static final int MUSIC_NEXT = 4;
	// 上一个
	private static final int MUSIC_PREVIOUS = 5;
	// 播放进度改变
	private static final int MUSIC_PROGRESS_CHANGE = 6;
	// 歌曲名
	private String _title;
	// 艺术家
	private String _artist;
	// 音乐播放到的位置
	private int _position;
	// 音乐文件的路径
	private String _path;

	private Handler mHandler = null;

	// MediaPlayer对象
	private MediaPlayer mMediaPlayer = null;
	private Uri mUri = null;
	// 当前播放时间
	private int currentTime;
	// 总时间
	private int duration;
	// 通知栏显示当前播放音乐
	public static Notification mNotification;
	public static NotificationManager mNotificationManager;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		// 实例化MediaPlayer
		mMediaPlayer = new MediaPlayer();
		// 设置播放完成时监听
		mMediaPlayer.setOnCompletionListener(this);

		// 显示通知栏
		showNotification();

		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.ANSWER");
		// 注册来电监听
		registerReceiver(PhoneListener, filter);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 清楚通知栏信息
		mNotificationManager.cancelAll();
		if (mMediaPlayer != null) {
			// 停止播放
			mMediaPlayer.stop();
			mMediaPlayer = null;
		}
		if (mHandler != null) {
			// 移除消息
			mHandler.removeMessages(1);
			mHandler = null;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		_title = bundle.getString("_title");
		_artist = bundle.getString("_artist");
		_position = bundle.getInt("_position");
		_path = bundle.getString("_path");

		if ((_path != null) && (mMediaPlayer != null)) {
			try {
				// 设置媒体路径
				mMediaPlayer.setDataSource(_path);
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

		setup();
		init();

		int op = bundle.getInt("_op", -1);
		if (op != -1) {
			switch (op) {
			case MUSIC_PLAY:
				// 播放
				if (mMediaPlayer != null) {
					play();
				}
				break;
			case MUSIC_PAUSE:
				// 暂停
				if (mMediaPlayer.isPlaying()) {
					pause();
				}
				break;
			case MUSIC_STOP:
				// 停止
				stop();
				break;
			case MUSIC_PROGRESS_CHANGE:
				// 进度条改变
				currentTime = bundle.getInt("_progress");
				mMediaPlayer.seekTo(currentTime);
				break;
			}
		}
		showNotification();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCompletion(MediaPlayer mediaplayer) {
		// TODO Auto-generated method stub
		// nextOne();此处代码要修改
		stop();
	}

	/**
	 * 初始化,进行duration设置
	 */
	private void setup() {
		final Intent intent = new Intent();
		intent.setAction(MUSCI_DURATION);
		try {
			if (!mMediaPlayer.isPlaying()) {
				mMediaPlayer.prepare();
			}
			mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mHandler.sendEmptyMessage(1);
				}
			});
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		duration = mMediaPlayer.getDuration();
		intent.putExtra("_duration", duration);
		// 向MusicPlayActivity发送广播,告知当前音频文件总时间
		sendBroadcast(intent);
	}

	/**
	 * 初始化,进行currentTime设置及更新 进行mHandler初始化
	 */
	private void init() {
		final Intent intent = new Intent();
		intent.setAction(MUSIC_CURRENT);
		if (mHandler == null) {
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
						currentTime = mMediaPlayer.getCurrentPosition();
						intent.putExtra("_currentTime", currentTime);
						sendBroadcast(intent);
					}
					mHandler.sendEmptyMessageDelayed(1, 500);
				}
			};
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
			mHandler.removeMessages(1);
		}
	}

	/**
	 * 当音乐播放时,通知栏显示当前播放信息
	 */
	private void showNotification() {
		// 获取通知栏系统服务对象
		mNotificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Builder builder = new Notification.Builder(MusicService.this);
		// 设置通知栏要显示的内容
		builder.setSmallIcon(R.drawable.beats_logo_s);
		builder.setContentTitle(_title);
		builder.setContentText(_artist);
		builder.setAutoCancel(false);
		builder.setWhen(System.currentTimeMillis());

		_position = getCurrentTime();
		Intent intent = new Intent(MusicService.this, MusicPlayActivity.class);
		intent.putExtra("_title", _title);
		intent.putExtra("_artist", _artist);
		intent.putExtra("_path", _path);
		intent.putExtra("_position", _position);
		PendingIntent contentIntent = PendingIntent
				.getActivity(MusicService.this, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		mNotification = builder.getNotification();
		mNotificationManager.notify(0, mNotification);
	}

	/**
	 * @return 当前播放进度
	 */
	private int getCurrentTime() {
		int currenttime = 0;
		if (mMediaPlayer != null) {
			currenttime = mMediaPlayer.getCurrentPosition();
		}
		return currenttime;
	}

	/**
	 * 来电监听
	 */
	protected BroadcastReceiver PhoneListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Intent.ACTION_ANSWER)) {
				TelephonyManager telephonymanager = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				switch (telephonymanager.getCallState()) {
				case TelephonyManager.CALL_STATE_RINGING:
					// 当来电时暂停音乐
					pause();
				case TelephonyManager.CALL_STATE_OFFHOOK:
					// 来电之后恢复音乐播放
					play();
				}
			}
		}
	};
}
