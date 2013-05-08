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
			// 歌曲专辑名
			String album = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
			// 歌曲路径
			String url = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			// 歌曲时长
			int duration = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
			songBean song = new songBean(id, title, album, url, duration);
			HashMap<String, songBean> map = new HashMap<String, songBean>();
			map.put("songinfo", song);
			songlist.add(map);
		}
	}

	class musiclistAdapter extends BaseAdapter {
		private Context context = null;
		private List<HashMap<String, songBean>> list = null;

		musiclistAdapter(Context context, List<HashMap<String, songBean>> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.musicitem, null);
				holder.title = (TextView) convertView
						.findViewById(R.id.musicitemtitle);
				holder.album = (TextView) convertView
						.findViewById(R.id.musicitemalbum);
				holder.time = (TextView) convertView
						.findViewById(R.id.musicitemtime);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			HashMap<String, songBean> map = list.get(position);
			songBean sb = map.get("songinfo");
			// 歌曲ID
			int id = sb.id;
			// 歌曲名
			String title = sb.title;
			// 歌曲专辑名
			String album = sb.album;
			// 歌曲路径
			String url = sb.url;
			// 歌曲时长
			int duration = sb.duration;

			holder.title.setText(title);
			holder.album.setText(album);
			holder.time.setText(Util.timeToString(duration));

			return convertView;
		}
	}

	class ViewHolder {
		TextView title;
		TextView album;
		TextView time;
	}
}
