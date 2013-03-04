package org.app.musicplayer;

import java.util.List;

import org.app.music.adapter.ScanAdapter;
import org.app.music.bean.ScanBean;
import org.app.music.tool.Contsant;
import org.app.music.tool.MusicManager;
import org.app.music.tool.XfDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ScanMusicActivity extends Activity {
	private ListView scan_list;
	private MusicManager musicmanager;
	private ScanAdapter adapter;
	private List<ScanBean> datas;
	public static final int SCAN_MUSIC_OK = 1;
	public static final int SCAN_MUSIC_CANCEL = 0;
	private ProgressDialog progressDialog;
	public int resultCode = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_music);
		TextView textview = (TextView) findViewById(R.id.tv_setting_top_title);
		textview.setText(getResources().getString(R.string.scan_titles));
		scan_list = (ListView) findViewById(R.id.lv_scan_music_list);
		musicmanager = new MusicManager(this);
		// 获取音乐媒体库目录
		datas = musicmanager.searchByDirectory();
		adapter = new ScanAdapter(this, datas);
		scan_list.setAdapter(adapter);
		scan_list.setOnItemClickListener(itemClickListener);
		((Button) this.findViewById(R.id.btn_scan_add))
				.setOnClickListener(listener);
		((Button) this.findViewById(R.id.btn_scan_ok))
				.setOnClickListener(listener);
		((ImageButton) (this.findViewById(R.id.bar_setting_top))
				.findViewById(R.id.ibtn_player_back_return))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (v.getId() == R.id.ibtn_player_back_return) {
							if (resultCode != -1) {
								setResult(resultCode);
							}
							finish();
						}
					}
				});
	}

	private OnClickListener listener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_scan_add:
				// 添加扫描目录
				Intent it = new Intent(ScanMusicActivity.this,
						ScanDirectoryActivity.class);
				it.putExtra("rs", adapter.getCheckFilePath());
				startActivityForResult(it, SCAN_MUSIC_OK);
				break;

			case R.id.btn_scan_ok:
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(ScanMusicActivity.this);
					progressDialog
							.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				}
				progressDialog.setTitle("扫描歌曲");
				progressDialog.setMessage("正在扫描歌曲,请稍后...");
				progressDialog.setCancelable(false);
				progressDialog.show();
				new Thread(runnable).start();
				break;
			}

		}
	};
	private Runnable runnable = new Runnable() {

		public void run() {
			musicmanager.scanMusic(adapter.getCheckFilePath(), handler);
		}
	};
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String rs = data.getString("rs");
			switch (msg.what) {
			case 0:
				progressDialog.setMessage(rs);
				break;
			case 1:
				progressDialog.cancel();
				progressDialog.dismiss();
				XfDialog dialog = Contsant.createConfirmDialog(
						ScanMusicActivity.this, "确定", "扫描歌曲结果", rs,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								dialog.dismiss();
								Intent it = new Intent(ScanMusicActivity.this,
										MainActivity.class);
								setResult(1, it);
								finish();
							}
						});
				dialog.show();
				break;
			default:
				break;
			}
		}

	};
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CheckBox cb_scan_item = (CheckBox) view
					.findViewById(R.id.cb_scan_item);
			cb_scan_item.setChecked(!cb_scan_item.isChecked());
		}

	};
}