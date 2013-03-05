package com.lm.musicplayerdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌词读取类
 * 
 * @author android
 */
public class LyricRead {
	/**
	 * 歌词list,包含歌词文件中所有歌词行
	 */
	private List<LyricContent> mLyricList;

	/**
	 * 一行歌词,包括歌词显示时间和歌词内容
	 */
	private LyricContent mLyricContent;

	public LyricRead() {
		mLyricList = new ArrayList<LyricContent>();
		mLyricContent = new LyricContent();
	}

	/**
	 * @return 歌词文件中所有歌词行的list
	 */
	public List<LyricContent> getLyricContent() {
		return mLyricList;
	}

	/**
	 * 歌词读取方法,负责从歌词文件中读取歌词内容并保存到歌词list中
	 * 
	 * @param file
	 *            歌词文件路径
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void readLyric(String file) throws FileNotFoundException,
			IOException {
		// 临时变量,用来保存从歌词文件中读取出的一行内容
		String tempLyric = "";

		// 打开一个文件,并从该文件中获取输入流
		File lrcfile = new File(file);
		FileInputStream mFileInputStream = new FileInputStream(lrcfile);
		InputStreamReader mInputStreamReader = new InputStreamReader(
				mFileInputStream, "UTF-8");
		BufferedReader mBufferReader = new BufferedReader(mInputStreamReader);
		// 一行一行读取文件
		while ((tempLyric = mBufferReader.readLine()) != null) {
			// 将歌词中的[]符号进行替换
			tempLyric = tempLyric.replace("[", "");
			tempLyric = tempLyric.replace("]", "@");
			// 分割歌词,分别获得歌词显示时间和歌词内容
			String splitLyric_data[] = tempLyric.split("@");
			if (splitLyric_data.length > 1) {
				mLyricContent.setLyric(splitLyric_data[1]);
				mLyricContent.setLyricTime(transTimeStr(splitLyric_data[0]));

				mLyricList.add(mLyricContent);

				mLyricContent = new LyricContent();
			}
		}

		mBufferReader.close();
		mInputStreamReader.close();
	}

	/**
	 * @param timeStr
	 *            当前歌词的显示时间
	 * @return 当前歌词要在什么时间显示,单位为毫秒
	 */
	public int transTimeStr(String timeStr) {
		timeStr = timeStr.replace(":", ".");
		timeStr = timeStr.replace(".", "@");

		String timeData[] = timeStr.split("@");

		int minute = Integer.parseInt(timeData[0]);
		int second = Integer.parseInt(timeData[1]);
		int millisecond = Integer.parseInt(timeData[2]);

		int currenttime = (minute * 60 + second) * 1000 + millisecond * 10;

		return currenttime;
	}
}
