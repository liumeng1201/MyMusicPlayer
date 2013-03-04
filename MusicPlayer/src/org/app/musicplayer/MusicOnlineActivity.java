package org.app.musicplayer;

import java.util.List;
import org.app.music.bean.Song;
import org.app.music.tool.Contsant;
import org.app.netmusic.XmlParse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 在线音乐界面
 * 
 * @author 涙星
 */
public class MusicOnlineActivity extends Activity {
	private ListView listview;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_music);
		listview = (ListView) findViewById(R.id.net_list);
		listview.setAdapter(new NetMusicAdapter(this, XmlParse
				.parseWebSongList(this)));

	}

	public class NetMusicAdapter extends BaseAdapter {
		private List<Song> data;
		private Context context;

		public NetMusicAdapter(Context context, List<Song> data) {
			this.context = context;
			this.data = data;

		}

		public int getCount() {
			return data.size();
		}

		public Object getItem(int position) {
			return data.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewholder = null;
			if (convertView == null) {
				viewholder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.net_music_item, null);
				viewholder.tv_web_list_item_number = (TextView) convertView
						.findViewById(R.id.tv_web_list_item_number);
				viewholder.tv_web_list_item_top = (TextView) convertView
						.findViewById(R.id.tv_web_list_item_top);
				viewholder.tv_web_list_item_bottom = (TextView) convertView
						.findViewById(R.id.tv_web_list_item_bottom);
				viewholder.ibtn_web_list_item_download = (Button) convertView
						.findViewById(R.id.ibtn_web_list_item_download);
				convertView.setTag(viewholder);
			} else {
				viewholder = (ViewHolder) convertView.getTag();
			}
			final Song song = data.get(position);

			viewholder.tv_web_list_item_number.setText((position + 1) + "");
			viewholder.tv_web_list_item_number.setBackgroundResource(0);
			viewholder.tv_web_list_item_top.setText(song.getName());
			viewholder.tv_web_list_item_top.setTag(song.getId());
			viewholder.tv_web_list_item_bottom.setText(song.getArtist()
					.getName());
			viewholder.ibtn_web_list_item_download
					.setOnClickListener(new DownloadListener());
			System.out.println("网络歌名有:" + song.getName());
			return convertView;
		}
	}

	public class ViewHolder {
		public TextView tv_web_list_item_number;
		public TextView tv_web_list_item_top;
		public TextView tv_web_list_item_bottom;
		public Button ibtn_web_list_item_download;
	}

	class DownloadListener implements OnClickListener {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ibtn_web_list_item_download:
				if (!Contsant.getNetIsAvailable(MusicOnlineActivity.this)) {
					toast = Contsant.showMessage(toast,
							MusicOnlineActivity.this, "当前没有网络，请检查");
				}
				if (!Contsant.isExistSdCard()) {
					toast = Contsant.showMessage(toast,
							MusicOnlineActivity.this, "请插入SD卡");
				} else {
					DownFile();
				}
				break;

			default:
				break;
			}

		}

	}

	private void DownFile() {
		toast = Contsant.showMessage(toast, MusicOnlineActivity.this, "测试，下载");
	}
}