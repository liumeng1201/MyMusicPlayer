package com.lm.musicplayerdemo.test;

public class songBean {
	// 歌曲ID
	int id;
	// 歌曲名
	String title;
	// 歌手名
	String artist;
	// 歌曲专辑名
	String album;
	// 歌曲路径
	String url;
	// 歌曲时长
	int duration;

	public songBean(int id, String title, String artist, String album, String url,
			int duration) {
		this.id = id;
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.url = url;
		this.duration = duration;
	}
}
