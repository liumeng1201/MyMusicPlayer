package com.lm.musicplayerdemo.test;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.lm.musicplayerdemo.PlayActivity;
import com.lm.musicplayerdemo.Util;

public class musiclistItemClickListener implements OnItemClickListener {
	private Context context = null;
	private List<HashMap<String, songBean>> list = null;
	private HashMap<String, songBean> map = null;
	private songBean sb = null;

	public musiclistItemClickListener(Context context,
			List<HashMap<String, songBean>> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(context, PlayActivity.class);
		map = list.get(position);
		sb = map.get("songinfo");
		intent.putExtra(Util.KEY_PATH, sb.url);
		System.out.println(sb.url);
		System.out.println(Util.getLrcName(sb.url));
		intent.putExtra(Util.KEY_LYRIC_PATH, Util.getLrcName(sb.url));
		context.startActivity(intent);
	}

}
