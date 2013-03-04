package org.app.musicplayer;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.app.music.adapter.MenuAdapter;
import org.app.music.service.MusicService;
import org.app.music.tool.Contsant;
import org.app.music.tool.Menu;
import org.app.music.tool.Setting;
import org.app.music.tool.XfDialog;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.method.DigitsKeyListener;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 开始进入程序时，直接进入音乐列表，主要功能包括了 1.自动扫描音乐并显示列表里(已解决) 2.支持MP3,M4A格式(已解决) 3.支持换肤(已解决)
 * 4.定时关闭(已解决) 5.夜间+日间模式(未解决) 6.查看音乐详细信息(已解决)
 * 最最主要的是依靠MediaStore这个大类。负责搜集所有音乐的信息,读音乐信息是用MediaStore这个大类读取的。
 * 正确是MediaStore.Audio.Media.XXXX.但是你要上面效果首先要自定义适配器(Adapter)。
 * 在相应的界面定义一个方法，还有就是定义三个变量(id,title,artits)分别代表歌的id，歌的标题，艺术家，先实例化后循环获取它们各自的索引列。
 * 
 * @author 涙星
 */
public class MusicListActivity extends BaseActivity {
	/*** 音乐列表 **/
	private ListView listview;
	private int _ids[];// 存放音乐文件的id数组
	private String _titles[];// 存放音乐文件的标题数组
	private String _artists[]; // 存放音乐艺术家的标题数组
	private String[] _path;// 存放音乐路过的标题数组
	private String[] _times;// 存放总时间的标题数组
	private String[] _album;// 存放专辑的标题数组
	private int _sizes[];// 存放文件大小的标题数组
	private String[] _displayname;// 存放名称的标题数组
	private Menu xmenu;// 自定义菜单
	private int num;// num确定一个标识
	private int c;// 同上
	private LayoutInflater inflater;// 装载布局
	private LayoutParams params;
	private Toast toast;// 提示
	/** 铃声标识常量 **/
	public static final int Ringtone = 0;
	public static final int Alarm = 1;
	public static final int Notification = 2;
	private TextView timers;// 显示倒计时的文字
	private Timers timer;// 倒计时内部对象

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		/** 选择子项点击事件 ***/
		listview = (ListView) findViewById(R.id.local_music_list);
		listview.setOnItemClickListener(new MusicListOnClickListener());
		listview.setOnCreateContextMenuListener(new MusicListContextListener());
		timers = (TextView) findViewById(R.id.timer_clock);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		LoadMenu();
		ShowMp3List();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 设置皮肤背景
		Setting setting = new Setting(this, false);
		String brightness = setting.getValue(Setting.KEY_BRIGHTNESS);
		WindowManager.LayoutParams attributes = getWindow().getAttributes();
		brightnesslevel = attributes.screenBrightness;
		if (brightness != null && brightness.equals("0")) {// 夜间模式
			attributes.screenBrightness = Setting.KEY_DARKNESS;
			getWindow().setAttributes(attributes);
		}
		listview.setBackgroundResource(setting.getCurrentSkinResId());
	}

	/**
	 * 显示MP3信息,其中_ids保存了所有音乐文件的_ID，用来确定到底要播放哪一首歌曲，_titles存放音乐名，用来显示在播放界面，
	 * 而_path存放音乐文件的路径（删除文件时会用到）。
	 */
	private void ShowMp3List() {
		// 用游标查找媒体详细信息
		Cursor cursor = this.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.TITLE,// 标题，游标从0读取
						MediaStore.Audio.Media.DURATION,// 持续时间,1
						MediaStore.Audio.Media.ARTIST,// 艺术家,2
						MediaStore.Audio.Media._ID,// id,3
						MediaStore.Audio.Media.DISPLAY_NAME,// 显示名称,4
						MediaStore.Audio.Media.DATA,// 数据，5
						MediaStore.Audio.Media.ALBUM_ID,// 专辑名称ID,6
						MediaStore.Audio.Media.ALBUM,// 专辑,7
						MediaStore.Audio.Media.SIZE }, null, null, null);// 大小,8
		/** 判断游标是否为空，有些地方即使没有音乐也会报异常。因为游标不稳定。稍有不慎就出错了,其次，如果用户没有音乐告知用户没有音乐且添加进去 */
		if (null != cursor && cursor.getCount() == 0) {
			final XfDialog xfdialog = new XfDialog.Builder(
					MusicListActivity.this).setTitle("提示:")
					.setMessage("你的手机未找到音乐，请添加音乐").create();
			xfdialog.show();
			return;

		}
		/** 将游标移到第一位 **/
		cursor.moveToFirst();
		/** 分别将各个标题数组实例化 **/
		_ids = new int[cursor.getCount()];//
		_titles = new String[cursor.getCount()];
		_artists = new String[cursor.getCount()];
		_path = new String[cursor.getCount()];
		_album = new String[cursor.getCount()];
		_times = new String[cursor.getCount()];
		_displayname = new String[cursor.getCount()];
		_sizes = new int[cursor.getCount()];
		/*** 用循环把信息查找出来 */
		/**
		 * 这里获取路径的格式是_path[i]=c.geString,为什么这么写？是因为MediaStore.Audio.Media.
		 * DATA的关系
		 * 得到的内容格式为/mnt/sdcard/[子文件夹名/]音乐文件名，而我们想要得到的是/sdcard/[子文件夹名]音乐文件名
		 */
		for (int i = 0; i < cursor.getCount(); i++) {
			_ids[i] = cursor.getInt(3);
			_titles[i] = cursor.getString(0);
			_artists[i] = cursor.getString(2);
			_path[i] = cursor.getString(5).substring(4);
			/**************** 以下是为提供音乐详细信息而生成的 ***************************/
			_album[i] = cursor.getString(7);
			_times[i] = toTime(cursor.getInt(1));
			_sizes[i] = cursor.getInt(8);
			_displayname[i] = cursor.getString(4);
			cursor.moveToNext();
			/*** 一直将游标往下走 **/
		}
		listview.setAdapter(new MusicListAdapter(this, cursor));

	}

	/** 音乐列表添加监听器，点击之后播放音乐 */
	public class MusicListOnClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
			playMusic(position);

		}

	}

	private class MusicListContextListener implements
			OnCreateContextMenuListener {

		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo info) {
			SongItemDialog();
			final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) info;
			num = menuInfo.position;

		}

	}

	/**
	 * 初始化菜单
	 */
	private void LoadMenu() {
		xmenu = new Menu(this);
		List<int[]> data1 = new ArrayList<int[]>();
		data1.add(new int[] { R.drawable.btn_menu_scanner, R.string.scan_title });
		data1.add(new int[] { R.drawable.btn_menu_skin, R.string.skin_settings });
		data1.add(new int[] { R.drawable.btn_menu_exit, R.string.menu_exit_txt });

		xmenu.addItem("常用", data1, new MenuAdapter.ItemListener() {

			public void onClickListener(int position, View view) {
				xmenu.cancel();
				if (position == 0) {
					Intent it = new Intent(MusicListActivity.this,
							ScanMusicActivity.class);
					startActivityForResult(it, 1);
				} else if (position == 1) {
					Intent it = new Intent(MusicListActivity.this,
							SkinSettingActivity.class);
					startActivityForResult(it, 2);

				} else if (position == 2) {
					exit();

				}
			}
		});
		List<int[]> data2 = new ArrayList<int[]>();
		data2.add(new int[] { R.drawable.btn_menu_setting,
				R.string.menu_settings });
		data2.add(new int[] { R.drawable.btn_menu_sleep, R.string.menu_time_txt });
		Setting setting = new Setting(this, false);
		String brightness = setting.getValue(Setting.KEY_BRIGHTNESS);
		if (brightness != null && brightness.equals("0")) {// 夜间模式
			data2.add(new int[] { R.drawable.btn_menu_brightness,
					R.string.brightness_title });
		} else {
			data2.add(new int[] { R.drawable.btn_menu_darkness,
					R.string.darkness_title });
		}
		xmenu.addItem("工具", data2, new MenuAdapter.ItemListener() {

			public void onClickListener(int position, View view) {
				xmenu.cancel();
				if (position == 0) {

				} else if (position == 1) {
					Sleep();
				} else if (position == 2) {
					setBrightness(view);
				}
			}
		});
		List<int[]> data3 = new ArrayList<int[]>();
		data3.add(new int[] { R.drawable.btn_menu_about, R.string.about_title });
		xmenu.addItem("帮助", data3, new MenuAdapter.ItemListener() {
			public void onClickListener(int position, View view) {
				xmenu.cancel();
				Intent intent = new Intent(MusicListActivity.this,
						AboutActivity.class);
				startActivity(intent);

			}
		});
		xmenu.create();
	}

	/**
	 * 根据Position播放音乐
	 */
	public void playMusic(int position) {
		Intent intent = new Intent(MusicListActivity.this,
				PlayMusicActivity.class);
		intent.putExtra("_ids", _ids);
		intent.putExtra("_titles", _titles);
		intent.putExtra("_artists", _artists);
		intent.putExtra("position", position);
		startActivity(intent);
		finish();

	}

	/**
	 * 复写菜单方法
	 */
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		menu.add("menu");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, android.view.Menu menu) {
		/** 菜单在哪里显示。参数1是该布局总的ID，第二个位置，第三，四个是XY坐标 **/
		xmenu.showAtLocation(findViewById(R.id.rl_parent_cotent),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		/** 菜单返回true的话就会显示系统自带的，返回false的话就显示自己写的。 **/
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 1) {
			Setting setting = new Setting(this, false);
			this.getWindow().setBackgroundDrawableResource(
					setting.getCurrentSkinResId());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			new XfDialog.Builder(MusicListActivity.this)
					.setTitle("询问:")
					.setMessage("确实要退出吗？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									exit();

								}
							}).setNeutralButton("取消", null).show();
			return false;
		}
		return false;
	}

	/**
	 * 音乐列表适配器且优化
	 */
	public class MusicListAdapter extends BaseAdapter {
		private Context mcontext;// 上下文
		private Cursor mcursor;// 游标

		public MusicListAdapter(Context context, Cursor cursor) {
			mcontext = context;
			mcursor = cursor;
		}

		public int getCount() {
			return mcursor.getCount();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewholder = null;
			if (convertView == null) {
				viewholder = new ViewHolder();
				convertView = LayoutInflater.from(mcontext).inflate(
						R.layout.music_list_item, null);
				viewholder.images = (ImageView) convertView
						.findViewById(R.id.images_album);
				viewholder.music_names = (TextView) convertView
						.findViewById(R.id.musicname);
				viewholder.singers = (TextView) convertView
						.findViewById(R.id.singer);
				viewholder.times = (TextView) convertView
						.findViewById(R.id.time);
				viewholder.song_list_item_menu = (ImageButton) convertView
						.findViewById(R.id.ibtn_song_list_item_menu);
				convertView.setTag(viewholder);
			} else {
				viewholder = (ViewHolder) convertView.getTag();
			}
			mcursor.moveToPosition(position);
			Bitmap bitmap = getArtwork(mcontext, mcursor.getInt(3),
					mcursor.getInt(mcursor
							.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
					true);
			viewholder.images.setImageBitmap(bitmap);
			viewholder.music_names.setText(mcursor.getString(0));
			viewholder.singers.setText(mcursor.getString(2));
			viewholder.times.setText(toTime(mcursor.getInt(1)));
			viewholder.song_list_item_menu
					.setOnClickListener(new MenuListListener());
			return convertView;

		}

	}

	public class ViewHolder {
		public ImageView images;
		public TextView music_names;
		public TextView singers;
		public TextView times;
		public ImageButton song_list_item_menu;
	}

	/**
	 * 时间的转换
	 */
	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		/** 返回结果用string的format方法把时间转换成字符类型 **/
		return String.format("%02d:%02d", minute, second);
	}

	/**
	 * 以下是歌曲放的时候显示专辑图片。和列表不同,播放时图片要大。所以cam那个方法写合适的图片吧
	 */
	public static Bitmap getArtwork(Context context, long song_id,
			long album_id, boolean allowdefault) {
		if (album_id < 0) {

			if (song_id >= 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if (bm != null) {
					return bm;
				}
			}
			if (allowdefault) {
				return getDefaultArtwork(context);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
		if (uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				BitmapFactory.Options options = new BitmapFactory.Options();
				/** 先指定原始大小 **/
				options.inSampleSize = 1;
				/** 只进行大小判断 **/
				options.inJustDecodeBounds = true;
				/** 调用此方法得到options得到图片的大小 **/
				BitmapFactory.decodeStream(in, null, options);
				/** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例 **/
				/** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合 **/
				options.inSampleSize = computeSampleSize(options, 30);
				/** 我们得到了缩放的比例，现在开始正式读入BitMap数据 **/
				options.inJustDecodeBounds = false;
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, options);
			} catch (FileNotFoundException ex) {

				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if (bm == null && allowdefault) {
							return getDefaultArtwork(context);
						}
					}
				} else if (allowdefault) {
					bm = getDefaultArtwork(context);
				}
				return bm;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
				}
			}
		}

		return null;
	}

	private static Bitmap getArtworkFromFile(Context context, long songid,
			long albumid) {
		Bitmap bm = null;
		if (albumid < 0 && songid < 0) {
			throw new IllegalArgumentException(
					"Must specify an album or a song id");
		}
		try {

			BitmapFactory.Options options = new BitmapFactory.Options();

			FileDescriptor fd = null;
			if (albumid < 0) {
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			} else {
				Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					fd = pfd.getFileDescriptor();
				}
			}
			options.inSampleSize = 1;
			// 只进行大小判断
			options.inJustDecodeBounds = true;
			// 调用此方法得到options得到图片的大小
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			// 我们的目标是在800pixel的画面上显示。
			// 所以需要调用computeSampleSize得到图片缩放的比例
			options.inSampleSize = 500;
			// OK,我们得到了缩放的比例，现在开始正式读入BitMap数据
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			// 根据options参数，减少所需要的内存
			bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
		} catch (FileNotFoundException ex) {

		}

		return bm;
	}

	/** 这个函数会对图片的大小进行判断，并得到合适的缩放比例，比如2即1/2,3即1/3 **/
	static int computeSampleSize(BitmapFactory.Options options, int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		if (candidate == 0)
			return 1;
		if (candidate > 1) {
			if ((w > target) && (w / candidate) < target)
				candidate -= 1;
		}
		if (candidate > 1) {
			if ((h > target) && (h / candidate) < target)
				candidate -= 1;
		}
		return candidate;
	}

	private static Bitmap getDefaultArtwork(Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(R.drawable.music), null, opts);
	}

	private static final Uri sArtworkUri = Uri
			.parse("content://media/external/audio/albumart");

	class MenuListListener implements OnClickListener {

		public void onClick(View v) {
			SongItemDialog();

		}

	}

	/**
	 * 用户长按列表或者按住最右边的倒三角形发生的事件
	 */
	private void SongItemDialog() {
		String[] menustring = new String[] { "播放此音乐", "将歌曲设为铃声", "查看该歌曲详情" };
		ListView menulist = new ListView(MusicListActivity.this);
		menulist.setCacheColorHint(Color.TRANSPARENT);
		menulist.setDividerHeight(1);
		menulist.setAdapter(new ArrayAdapter<String>(MusicListActivity.this,
				R.layout.dialog_menu_item, R.id.text1, menustring));
		menulist.setLayoutParams(new LayoutParams(Contsant
				.getScreen(MusicListActivity.this)[0] / 2,
				LayoutParams.WRAP_CONTENT));

		final XfDialog xfdialog = new XfDialog.Builder(MusicListActivity.this)
				.setTitle("你要将文件处理为:").setView(menulist).create();
		xfdialog.show();
		menulist.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				xfdialog.cancel();
				xfdialog.dismiss();
				if (position == 0) {
					playMusic(num);
				} else if (position == 1) {
					SetRing();
				} else if (position == 2) {
					ShowMusicInfo(num);
				}

			}
		});
	}

	/**
	 * 设置铃声
	 */
	private void SetRing() {
		RadioGroup rg_ring = new RadioGroup(MusicListActivity.this);
		rg_ring.setLayoutParams(params);
		final RadioButton rbtn_ringtones = new RadioButton(
				MusicListActivity.this);
		rbtn_ringtones.setText("来电铃声");
		rg_ring.addView(rbtn_ringtones, params);
		final RadioButton rbtn_alarms = new RadioButton(MusicListActivity.this);
		rbtn_alarms.setText("闹铃铃声");
		rg_ring.addView(rbtn_alarms, params);
		final RadioButton rbtn_notifications = new RadioButton(
				MusicListActivity.this);
		rbtn_notifications.setText("通知铃声");
		rg_ring.addView(rbtn_notifications, params);
		new XfDialog.Builder(MusicListActivity.this).setTitle("设置铃声")
				.setView(rg_ring)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						dialog.dismiss();

					}

				}).setNegativeButton("取消", null).show();
	}

	/**
	 * 显示音乐详细信息
	 */
	private void ShowMusicInfo(int location) {
		View view = inflater.inflate(R.layout.song_detail, null);
		((TextView) view.findViewById(R.id.tv_song_title))
				.setText(_titles[num]);
		((TextView) view.findViewById(R.id.tv_song_artist))
				.setText(_artists[num]);
		((TextView) view.findViewById(R.id.tv_song_album)).setText(_album[num]);
		((TextView) view.findViewById(R.id.tv_song_filepath))
				.setText(_path[num]);
		((TextView) view.findViewById(R.id.tv_song_duration))
				.setText(_times[num]);
		((TextView) view.findViewById(R.id.tv_song_format)).setText(Contsant
				.getSuffix(_displayname[num]));
		((TextView) view.findViewById(R.id.tv_song_size)).setText(Contsant
				.formatByteToMB(_sizes[num]) + "MB");
		new XfDialog.Builder(MusicListActivity.this).setTitle("歌曲详细信息:")
				.setNeutralButton("确定", null).setView(view).create().show();
	}

	/**
	 * 休眠方法
	 */
	private void Sleep() {
		final EditText edtext = new EditText(this);
		edtext.setText("5");// 设置初始值
		edtext.setKeyListener(new DigitsKeyListener(false, true));
		edtext.setGravity(Gravity.CENTER_HORIZONTAL);// 设置摆设位置
		edtext.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));// 字体类型
		edtext.setTextColor(Color.BLUE);// 字体颜色
		edtext.setSelection(edtext.length());// 设置选择位置
		edtext.selectAll();// 全部选择
		new XfDialog.Builder(MusicListActivity.this).setTitle("请输入时间")
				.setView(edtext)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						dialog.cancel();
						/** 如果输入小于2或者等于0会告知用户 **/
						if (edtext.length() <= 2 && edtext.length() != 0) {
							if (".".equals(edtext.getText().toString())) {
								toast = Contsant.showMessage(toast,
										MusicListActivity.this,
										"输入错误，你至少输入两位数字");
							} else {
								final String time = edtext.getText().toString();
								long Money = Integer.parseInt(time);
								long cX = Money * 60000;
								timer = new Timers(cX, 1000);
								timer.start();
								toast = Contsant.showMessage(toast,
										MusicListActivity.this, "休眠模式启动!于"
												+ String.valueOf(time)
												+ "t分钟后关闭程序!");
								timers.setVisibility(View.INVISIBLE);
								timers.setVisibility(View.VISIBLE);
								timers.setText(String.valueOf(time));
							}

						} else {
							Toast.makeText(MusicListActivity.this, "请输入几分钟",
									Toast.LENGTH_SHORT).show();
						}

					}
				}).setNegativeButton("取消", null).show();

	}

	/**
	 * 产生一个倒计时
	 */
	private class Timers extends CountDownTimer {

		public Timers(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			if (c == 0) {
				exit();
			} else {
				finish();
				onDestroy();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			timers.setText("" + millisUntilFinished / 1000 / 60 + ":"
					+ millisUntilFinished / 1000 % 60);
			// 假如这个数大于9 说明就是2位数了,可以直接输入。假如小于等于9 那就是1位数。所以前面加一个0
			String abc = (millisUntilFinished / 1000 / 60) > 9 ? (millisUntilFinished / 1000 / 60)
					+ ""
					: "0" + (millisUntilFinished / 1000 / 60);
			String b = (millisUntilFinished / 1000 % 60) > 9 ? (millisUntilFinished / 1000 % 60)
					+ ""
					: "0" + (millisUntilFinished / 1000 % 60);
			timers.setText(abc + ":" + b);
			timers.setVisibility(View.GONE);
		}

	}

	/**
	 * 退出程序方法
	 */
	private void exit() {
		Intent mediaServer = new Intent(MusicListActivity.this,
				MusicService.class);
		stopService(mediaServer);
		finish();
	}

}