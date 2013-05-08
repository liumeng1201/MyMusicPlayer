package com.lm.musicplayerdemo.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lm.musicplayerdemo.R;
import com.lm.musicplayerdemo.Util;

public class Test extends Activity {
	ListView listview;
	List<HashMap<String, songBean>> songlist = new ArrayList<HashMap<String, songBean>>();
	private musiclistAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);

		listview = (ListView) findViewById(R.id.musiclist);
		getMusic();
		adapter = new musiclistAdapter(Test.this, songlist);
		listview.setAdapter(adapter);
	}

	void getMusic() {
		ContentResolver cr = this.getContentResolver();
		Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		cursor.moveToFirst();
		while (cursor.moveToNext()) {
			// 歌曲ID
			int id = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
			// 歌曲名
			String title = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
			// 歌手名
			String artist = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
			// 歌曲专辑名
			String album = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
			// 歌曲路径
			String url = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			// 歌曲时长
			int duration = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
			songBean song = new songBean(id, title, artist, album, url,
					duration);
			HashMap<String, songBean> map = new HashMap<String, songBean>();
			map.put("songinfo", song);
			songlist.add(map);
		}
	}
}
