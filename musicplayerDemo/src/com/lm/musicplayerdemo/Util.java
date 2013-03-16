package com.lm.musicplayerdemo;

public class Util {
	// action
	public static final String MUSIC_ACTION_DURATION = "music.duration";
	public static final String MUSIC_ACTION_CURRENTTIME = "music.currenttime";
	public static final String MUSIC_ACTION_PROGRESS = "music.progress";
	public static final String MUSIC_ACTION_ARTIST = "music.artisr";
	public static final String MUSIC_ACTION_TITLE = "music.title";
	public static final String MUSIC_ACTION_ALBUM = "music.album";
	public static final String MUSIC_ACTION_PATH = "music.path";
	public static final String MUSIC_ACTION_ISPLAYING = "music.isplaying";

	// intent key
	public static final String KEY_CURRENTTIME = "currenttime";
	public static final String KEY_DURATION = "duration";
	public static final String KEY_TITLE = "title";
	public static final String KEY_ARTIST = "artist";
	public static final String KEY_ALBUM = "album";
	public static final String KEY_PROGRESS = "progress";
	public static final String KEY_PATH = "path";
	public static final String KEY_ISPLAYING = "isplaying";
	public static final String KEY_LYRIC_PATH = "lyricpath";

	// handler message.what
	public static final int msg_current = 1;
	public static final int msg_durtion = 2;
	public static final int msg_title = 3;
	public static final int msg_artist = 4;
	public static final int msg_album = 5;
	public static final int msg_progress = 6;
	public static final int msg_isplaying = 7;
	public static final int msg_update_lyric = 8;

	// operate number
	public static final String OPERATE_NUMBER = "operate_number";
	public static final int MUSIC_OP_PLAY = 1;
	public static final int MUSIC_OP_PAUSE = 2;
	public static final int MUSIC_OP_STOP = 3;
	public static final int MUSIC_OP_NEXT = 4;
	public static final int MUSIC_OP_PREVIOUS = 5;
	public static final int MUSIC_OP_PROGRESS_CHANGE = 6;
	public static final int MUSIC_OP_UPDATE_DATA = 7;

	public static final boolean IS_MUSIC_PLAYINT = false;

	// service name
	public static final String MUSIC_SERVICE = "com.lm.musicplayer.MUSIC_SERVICE";

	/**
	 * 将时间从int型转换为字符串,并以xx:xx的格式返回
	 * 
	 * @param time
	 *            时间数值
	 * @return xx:xx格式的时间
	 */
	public static String timeToString(int time) {
		String timeStr = null;
		int minute = 0;
		int second = 0;
		time = time / 1000;
		minute = time / 60;
		second = time % 60;
		if ((minute >= 0) && (minute <= 9)) {
			timeStr = "0" + minute + ":";
		} else {
			timeStr = minute + ":";
		}
		if ((second >= 0) && (second <= 9)) {
			timeStr += "0" + second;
		} else {
			timeStr += second + "";
		}
		return timeStr;
	}
}
