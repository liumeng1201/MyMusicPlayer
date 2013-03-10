package com.lm.musicplayerdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

/**
 * 歌曲信息
 * 
 * @author liumeng
 */
public class SongInfo {
	// 歌曲文件路径
	private String filepath;
	// 歌曲名 4~33byte
	private String title;
	// 歌手名 34~63byte
	private String artist;
	// 专辑名 61~93byte
	private String album;
	// 备注 98~125byte
	private String comment;
	// 三个保留位 126,127,128
	private byte r1, r2, r3;
	// 歌曲文件最后128字节是否合法,合法的话则可以获取歌曲相关信息
	private boolean valid;

	public SongInfo(String filepath) {
		this.filepath = filepath;
		if (filepath != null) {
			getInfo();
		}
	}

	private void getInfo() {
		File f = new File(filepath);
		// 使用RandomAccessFile来读取mp3文件的最后128字节的数据
		RandomAccessFile ran = null;
		byte[] data = new byte[128];
		try {
			ran = new RandomAccessFile(f, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// 定位到文件的最后128字节,并将最后128字节的信息读取到data数组中
		try {
			ran.seek(ran.length() - 128);
			ran.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 判断data是否是128个字节,不是则抛出异常
		if (data.length != 128) {
			throw new RuntimeException("数据长度不合法:" + data.length);
		} else {
			// 开始逐个读取信息并格式化成String
			if (new String(data, 0, 3).equalsIgnoreCase("TAG")) {
				valid = true;

				try {
					title = new String(data, 3, 30, "GBK").trim();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				try {
					artist = new String(data, 33, 30, "GBK").trim();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				try {
					album = new String(data, 63, 30, "GBK").trim();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				try {
					comment = new String(data, 97, 28, "GBK").trim();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else {
				valid = false;
				title = "未知";
				artist = "未知";
				album = "未知";
				comment = "未知";
			}
		}
	}

	// 是否可以获取歌曲相关信息
	private boolean isValid() {
		return valid;
	}

	// 获取歌曲名
	public String getTitle() {
		return title;
	}

	// 获取歌手名
	public String getArtist() {
		return artist;
	}

	// 获取专辑名
	public String getAlbum() {
		return album;
	}

	// 获取专辑信息
	public String getComment() {
		return comment;
	}
}