package com.lm.musicplayerdemo.test;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lm.musicplayerdemo.R;
import com.lm.musicplayerdemo.Util;

public class musiclistAdapter extends BaseAdapter {
	private Context context = null;
	private List<HashMap<String, songBean>> list = null;

	public musiclistAdapter(Context context,
			List<HashMap<String, songBean>> list) {
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
			holder.artist = (TextView) convertView
					.findViewById(R.id.musicitemartist);
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
		// 歌手名
		String artist = sb.artist;
		// 歌曲专辑名
		String album = sb.album;
		// 歌曲路径
		String url = sb.url;
		// 歌曲时长
		int duration = sb.duration;

		holder.title.setText(title);
		holder.artist.setText(artist);
		holder.time.setText(Util.timeToString(duration));

		return convertView;
	}
}