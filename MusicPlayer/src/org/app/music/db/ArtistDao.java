package org.app.music.db;

import org.app.music.bean.Artist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ArtistDao {
	private MusicDB dbHpler;

	public ArtistDao(Context context) {
		dbHpler = new MusicDB(context);
	}

	/**
	 * 判断歌手是否存在，存在返回id
	 * */
	public int isExist(String name) {
		int id = -1;
		SQLiteDatabase db = dbHpler.getReadableDatabase();
		Cursor cr = db.rawQuery("SELECT " + DBData.ARTIST_ID + " FROM "
				+ DBData.ARTIST_TABLENAME + " WHERE " + DBData.ARTIST_NAME
				+ "=?", new String[] { name });
		if (cr.moveToNext()) {
			id = cr.getInt(0);
		}
		cr.close();
		db.close();
		return id;
	}

	/**
	 * 添加
	 * */
	public long add(Artist artist) {
		SQLiteDatabase db = dbHpler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DBData.ARTIST_NAME, artist.getName());
		values.put(DBData.ARTIST_PICPATH, artist.getPicPath());
		long rs = db
				.insert(DBData.ARTIST_TABLENAME, DBData.ARTIST_NAME, values);
		db.close();
		return rs;
	}

	/**
	 * 删除
	 * */
	public int delete(int id) {
		SQLiteDatabase db = dbHpler.getWritableDatabase();
		int rs = db.delete(DBData.ARTIST_TABLENAME, DBData.ARTIST_ID + "=?",
				new String[] { String.valueOf(id) });
		db.close();
		return rs;
	}
}