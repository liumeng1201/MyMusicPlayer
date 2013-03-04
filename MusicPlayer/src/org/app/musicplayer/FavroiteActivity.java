package org.app.musicplayer;

import java.util.HashMap;
import java.util.List;

import org.app.music.tool.Contsant;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FavroiteActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite);
		LoadData();
	}

	private void LoadData() {
		List<HashMap<String, Object>> data = Contsant.getListMusicData();
		SimpleAdapter music_adapter = new SimpleAdapter(this, data,
				R.layout.fav_list_item,
				new String[] { "icon", "title", "icon2" }, new int[] {
						R.id.iv_list_item_icon, R.id.tv_list_item_title,
						R.id.iv_list_item_icon2 });
		ListView listview = (ListView) findViewById(R.id.fav_music_list);
		listview.setAdapter(music_adapter);

	}

}