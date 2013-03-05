package com.lm.musicplayerdemo;

/**
 * 歌词内容类,用来管理一行歌词的内容 
 * 一行歌词包括歌词显示时间和歌词内容两部分
 * 
 * @author android
 */
public class LyricContent {
	/**
	 * 歌词内容
	 */
	private String mLyric;

	/**
	 * 歌词时间
	 */
	private int mLyricTime;

	/**
	 * @return 歌词内容
	 */
	public String getLyric() {
		return mLyric;
	}

	/**
	 * 设置歌词
	 * 
	 * @param lyric
	 *            歌词内容
	 */
	public void setLyric(String lyric) {
		this.mLyric = lyric;
	}

	/**
	 * @return 歌词时间
	 */
	public int getLyricTime() {
		return mLyricTime;
	}

	/**
	 * 设置歌词时间
	 * 
	 * @param lyricTime
	 *            歌词时间
	 */
	public void setLyricTime(int lyricTime) {
		this.mLyricTime = lyricTime;
	}

}
