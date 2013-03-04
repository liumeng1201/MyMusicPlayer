package org.app.music.db;

import org.app.music.bean.Song;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SongDao {
	private MusicDB dbHpler;

	public SongDao(Context context) {
		dbHpler = new MusicDB(context);
	}

	/**
	 * 返回所有歌曲路径用'$[string]$'分隔
	 * */
	public String getFilePathALL() {
		SQLiteDatabase db = dbHpler.getReadableDatabase();
		StringBuffer sb = new StringBuffer();
		Cursor cr = db.rawQuery("SELECT " + DBData.SONG_FILEPATH + " FROM "
				+ DBData.SONG_TABLENAME + " ORDER BY " + DBData.SONG_ID
				+ " DESC", null);
		while (cr.moveToNext()) {
			sb.append("$")
					.append(cr.getString(cr
							.getColumnIndex(DBData.SONG_FILEPATH))).append("$");
		}
		cr.close();
		db.close();
		return sb.toString();
	}

	/**
	 * 添加
	 * */
	public long add(Song song) {
		SQLiteDatabase db = dbHpler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBData.SONG_DISPLAYNAME, song.getDisplayName());
		values.put(DBData.SONG_FILEPATH, song.getFilePath());
		values.put(DBData.SONG_LYRICPATH, song.getLyricPath());
		values.put(DBData.SONG_MIMETYPE, song.getMimeType());
		values.put(DBData.SONG_NAME, song.getName());
		values.put(DBData.SONG_ALBUMID, song.getAlbum().getId());
		values.put(DBData.SONG_NETURL, song.getNetUrl());
		values.put(DBData.SONG_DURATIONTIME, song.getDurationTime());
		values.put(DBData.SONG_SIZE, song.getSize());
		values.put(DBData.SONG_ARTISTID, song.getArtist().getId());
		values.put(DBData.SONG_PLAYERLIST, song.getPlayerList());
		values.put(DBData.SONG_ISDOWNFINISH, song.isDownFinish());
		values.put(DBData.SONG_ISLIKE, song.isLike());
		values.put(DBData.SONG_ISNET, song.isNet());
		long rs = db.insert(DBData.SONG_TABLENAME, DBData.SONG_NAME, values);
		db.close();
		return rs;
	}
}