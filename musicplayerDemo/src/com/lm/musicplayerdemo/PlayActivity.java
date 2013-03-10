package com.lm.musicplayerdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayActivity extends Activity {
	// 歌曲名
	private String mTitle;
	// 歌手名
	private String mArtist;
	// 专辑名
	private String mAlbum;
	// 当前播放时间
	private int mCurrentTime;
	// 总时间
	private int mDuration;

	// 音乐文件的路径
	private String mPath;

	private boolean isplaying = false;

	// 对Activity中的各个控件进行声明
	private ImageButton btnPlay;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private TextView tvTitle;
	private TextView tvArtist;
	private TextView tvCurrentTime;
	private TextView tvDuration;
	private SeekBar seekbar;

	/**
	 * 播放控制按钮点击事件监听
	 */
	private OnClickListener playcontrolListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.play:
				if (isPlaying()) {
					pause();
				} else {
					play();
				}
				break;
			case R.id.next:
				next();
				break;
			case R.id.previous:
				previous();
				break;
			}
		}
	};

	/**
	 * seekbar进度条改变监听
	 */
	private OnSeekBarChangeListener seekbarChangeListener = new OnSeekBarChangeListener() {
		int temp = 0;

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			progresschange(temp);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if (fromUser) {
				temp = progress;
			}
		}
	};

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
			} else if (action.equals(Util.MUSIC_ACTION_ISPLAYING)) {
				// 歌曲播放状态
				boolean playstate = intent.getBooleanExtra(Util.KEY_ISPLAYING,
						false);
				setPlayingState(playstate);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();

		initBroadcastReceiver();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// 获取启动该Activity的intent,并从中获取与歌曲相关的信息
		Intent intent = getIntent();
		if (intent != null) {
			mTitle = intent.getStringExtra(Util.KEY_TITLE);
			mArtist = intent.getStringExtra(Util.KEY_ARTIST);
			mCurrentTime = intent.getIntExtra(Util.KEY_CURRENTTIME, 0);
			mDuration = intent.getIntExtra(Util.KEY_DURATION, 0);
			mPath = intent.getStringExtra(Util.KEY_PATH);
		}

		if (mPath != null) {
			setMusicSource(mPath);
			play();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			init();
			setSongInfo();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			init();
			setSongInfo();
		}
	}

	/**
	 * 为各个控件设置值
	 */
	private void setSongInfo() {
		if (mTitle != null && tvTitle != null) {
			tvTitle.setText(mTitle);
		}
		if (mArtist != null && tvArtist != null) {
			tvArtist.setText(mArtist);
		}
		if (tvCurrentTime != null) {
			tvCurrentTime.setText(Util.timeToString(mCurrentTime));
		}
		if (tvDuration != null) {
			tvDuration.setText(Util.timeToString(mDuration));
		}
		if (seekbar != null) {
			seekbar.setProgress(mCurrentTime);
			seekbar.setMax(mDuration);
		}
	}

	/**
	 * 初始化Activity主界面,并获取各个控件的实例
	 */
	private void init() {
		setContentView(R.layout.play_activity);

		btnPlay = (ImageButton) findViewById(R.id.play);
		btnNext = (ImageButton) findViewById(R.id.next);
		btnPrevious = (ImageButton) findViewById(R.id.previous);
		tvTitle = (TextView) findViewById(R.id.music_title);
		tvArtist = (TextView) findViewById(R.id.music_artist);
		tvCurrentTime = (TextView) findViewById(R.id.currenttime);
		tvDuration = (TextView) findViewById(R.id.duration);
		seekbar = (SeekBar) findViewById(R.id.seekbar);

		btnPlay.setOnClickListener(playcontrolListener);
		btnNext.setOnClickListener(playcontrolListener);
		btnPrevious.setOnClickListener(playcontrolListener);

		seekbar.setOnSeekBarChangeListener(seekbarChangeListener);
	}

	/**
	 * 初始化广播接收者,注册要接收的广播
	 */
	private void initBroadcastReceiver() {
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
		tvCurrentTime.setText(Util.timeToString(current));
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
		tvDuration.setText(Util.timeToString(duration));
	}

	/**
	 * 设置歌曲名
	 * 
	 * @param title
	 *            歌曲名
	 */
	private void setTitle(String title) {
		this.mTitle = title;
		tvTitle.setText(title);
	}

	/**
	 * 设置歌手名
	 * 
	 * @param artist
	 *            歌手名
	 */
	private void setArtist(String artist) {
		this.mArtist = artist;
		tvArtist.setText(artist);
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
		seekbar.setProgress(progress);
	}

	/**
	 * 设置进度条的最大值
	 * 
	 * @param max
	 *            进度条最大值
	 */
	private void setSeekbarMax(int max) {
		seekbar.setMax(max);
	}

	/**
	 * 停止音乐服务
	 */
	private void stopMusicService() {
		Intent intent = new Intent();
		intent.setAction(Util.MUSIC_SERVICE);
		stopService(intent);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(musicserviceReceiver);

		// 如果在退出PlayActivity时为播放暂停状态则关闭MusicService
		if (!isPlaying()) {
			stopMusicService();
		}
	}
}
